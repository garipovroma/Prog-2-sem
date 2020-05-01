//review

"use strict"

const Expression = function(evaluate, toString, diff, prefix, postfix) {
    this.prototype.evaluate = evaluate;
    this.prototype.toString = toString;
    this.prototype.diff = diff;
    this.prototype.prefix = prefix;
    this.prototype.postfix = postfix;
};

const createExpression = function(that, evaluate, toString, diff, prefix, postfix) {
    that.prototype = Object.create(Expression.prototype);
    Expression.call(that, evaluate, toString, diff, prefix, postfix);
};

const Const = function(value) {
    this.value = value;
};

createExpression(Const,
    function() { return +this.value },
    function() { return this.value.toString() },
    function() { return Zero },
    function() { return this.value.toString() },
    function() { return this.value.toString() }
);
const variableInd = {
    "x" : 0, "y" : 1, "z" : 2
};

const Zero = new Const(0);
const One = new Const(1);

const Variable = function(name) {
    this.name = name;
    this.ind = variableInd[name];
};
createExpression(Variable,
    function(...args) { return args[this.ind] },
    function() { return this.name },
    function(variable) { return (this.name === variable ? One : Zero)},
    function() { return this.name },
    function() { return this.name }
);

const makeOperator = function(operation, operationString, doDiff) {
    let result = function(...args) {
        this.args = args;
    };
    createExpression(result,
        function(...args_) {return operation(...this.args.map((i) => (i.evaluate(...args_))))},
        function() { return (this.args.length === 0? "" : this.args.reduce((accumulator, currentValue) =>
            accumulator + " " + currentValue.toString())) + " " + operationString },
        function(variable) { return doDiff(variable, ...this.args) },
        function() { return "(" + operationString + " " + (this.args.length === 0 ? '' : this.args.map(cur => cur.prefix()).join(' ')) + ")" },
        function() { return "(" + (this.args.length === 0 ? '' : this.args.map(cur => cur.postfix()).join(' ' )) + " " + operationString + ")" }
    );
    result.prototype.arity = (operation.length === 0 ? undefined : operation.length);
    return result;
};

const Add = makeOperator(
    (a, b) => (a + b),
    '+',
    (variable, a, b) => new Add(a.diff(variable), b.diff(variable))
);
const Subtract = makeOperator(
    (a, b) => (a - b),
    '-',
    (variable, a, b) => new Subtract(a.diff(variable), b.diff(variable))
);
const Negate = makeOperator(
    (x) => -x,
    'negate',
    (variable, a) => new Negate(a.diff(variable))
);
const Multiply = makeOperator(
    (a, b) => (a * b),
    '*',
    (variable, a, b) => new Add(new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable)))
);
const Divide = makeOperator(
    (a, b) => a / b,
    '/',
    (variable, a, b) => new Divide(new Subtract(new Multiply(a.diff(variable), b), new Multiply(a,
        b.diff(variable))), new Multiply(b, b))
);

const E = new Const(Math.E);
const Log = makeOperator(
    (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    'log',
    (variable, a, b) => new Divide(new Subtract(new Divide(new Multiply(new Log(E, a), b.diff(variable)), b),
        new Divide(new Multiply(new Log(E, b), a.diff(variable)), a)), new Multiply(new Log(E, a), new Log(E, a)))
);
const Power = makeOperator(
    (a, b) => Math.pow(a, b),
    'pow',
    (variable, a, b) => new Multiply(new Power(a, new Subtract(b, new Const(1))), new Add(new Multiply(b,
        a.diff(variable)), new Multiply(new Multiply(a, new Log(E, a)), b.diff(variable))))
);

const buildSum = (...args) => (args.reduce((accumulator, currentValue) => (new Add(accumulator, currentValue)), Zero));
const buildSumExp = (...args) => buildSum(...args.map((currentValue) => (new Power(E, currentValue))));
const buildSoftMax = (...args) => (args.length === 0 ? Zero : new Divide(new Power(E, args[0]), buildSumExp(...args)));

const Sumexp = makeOperator(
    (...args) => (args.reduce((accumulator, currentValue) => (accumulator + Math.exp(currentValue)), 0)),
    'sumexp',
    (variable, ...mas) => buildSumExp(...mas).diff(variable)
);

const Softmax = makeOperator(
    (...mas) => (mas.length === 0 ? 0 : (Math.exp(mas[0])) /
        (mas.reduce((accumulator, currentValue) => (accumulator + Math.exp(currentValue)), 0))),
    'softmax',
    (variable, ...args) => buildSoftMax(...args).diff(variable)
);

let CustomError = function(message) {
    this.message = message;
};
CustomError.prototype = Object.create(Error.prototype);
CustomError.prototype.name = "CustomError";
CustomError.prototype.constructor = CustomError;

const operationByString = {
    '+' : Add, '-' : Subtract, '*' : Multiply, '/' : Divide, 'pow' : Power, 'log' : Log, 'negate' : Negate,
    'sumexp' : Sumexp, 'softmax' : Softmax
};
const specialOperations = ['softmax', 'sumexp'];

function parse(expression) {
    let stack = [];
    expression = expression.trim();
    for (let s of expression.split(/\s+/)) {
        let cur;
        if (s in variableInd) {
            cur = new Variable(s);
        } else if (s in operationByString) {
            let op = operationByString[s];
            cur = new op(...stack.splice(-op.prototype.arity));
        } else {
            cur = new Const(+s);
        }
        stack.push(cur);
    }
    return stack.pop();
}

const Source = function() {
    let _source = null;
    let _pos = 0;
    let _curToken = '';
    const getCh = () => _source[_pos];
    const hasNext = () =>  (_pos < _source.length);
    const nextChar = () => (hasNext() ? _source[_pos++] : '\0');
    const isWhitespace = () => (_source[_pos] === ' ');
    const skipWhitespaces = () => { while (isWhitespace()) nextChar(); };
    const check = (ch) => (_source[_pos] === ch);
    this.checkEmpty = () => (_source.length === 0);
    this.endFound = () => (_pos === _source.length);
    this.getSubstr = () => _source.substring(Math.max(_pos - 15, 0),
        Math.min(_pos + 10, _source.length));
    this.nextToken = function() {
        skipWhitespaces();
        if (check('(')) {
            _pos++;
            return _curToken = '(';
        }
        if (check(')')) {
            _pos++;
            return _curToken = ')';
        }
        let subStr = [];
        while (hasNext() && !isWhitespace() && !check('(') && !check(')')) {
            subStr.push(getCh());
            nextChar();
        }
        return _curToken = subStr.join('');
    };
    this.getToken = () => _curToken;
    this.setSource = function (expression) {
        _pos = 0;
        _source = expression.trim();
        this.nextToken();
    };
};

function Parser(source, parseExpression, argumentsCondition, orderOfFunctions, contidionsOfError, checkErrors, makeResult) {
    this.parse = () => {
        if (source.checkEmpty()) {
            throw new CustomError("unexpected empty expression");
        }
        let res = null;
        res = _parse(source.getToken() === '(');
        if (!source.endFound()) {
            throw new CustomError("unexpected token : " + source.getSubstr());
        }
        return res;
    };
    function _parse(needBracket) {
        if (!needBracket) {
            if (source.getToken() in variableInd) {
                return new Variable(source.getToken());
            } else if (!isNaN(+source.getToken())) {
                return new Const(source.getToken());
            } else {
                throw new CustomError("unexpected token :" + source.getSubstr());
            }
        }
        return parseExpression(source, _parse, argumentsCondition, orderOfFunctions,
            contidionsOfError, checkErrors, makeResult);
    }
}

function parseOperation(source) {
    let operation = null;
    let specialOperation = false;
    if (source.getToken() in operationByString) {
        if (specialOperations.includes(source.getToken())) {
            specialOperation = true;
        }
        operation = operationByString[source.getToken()];
    } else {
        throw new CustomError("operation expected, but not found : " + source.getSubstr());
    }
    source.nextToken();
    return [operation, specialOperation];
}

function parseArguments(source, parse, condition, previousCall) {
    let args = [];
    while (condition()) {
        if (source.endFound()) {
            throw new CustomError("unexpected end of expression in brackets : " + source.getSubstr());
        }
        if (source.getToken() === '(') {
            args.push(parse(true, true));
        } else if (source.getToken() in variableInd) {
            args.push(new Variable(source.getToken()));
        } else if (!isNaN(+source.getToken())) {
            args.push(new Const(source.getToken()));
        } else {
            throw new CustomError("unexpected token :" + source.getSubstr());
        }
        source.nextToken();
    }
    if (args.length === 0 && !(specialOperations.includes(source.getToken()))
        && previousCall !== undefined && !previousCall[1]) {
        throw new CustomError("unexpected ) found : " + source.getSubstr());
    }
    return args;
}

function parseExpression(source, parse, argumentsCondition, orderOfFunctions, contidionsOfError, checkErrors, makeResult) {
    source.nextToken();
    let firstCall = orderOfFunctions[0](source, parse, argumentsCondition);
    let secondCall = orderOfFunctions[1](source, parse, argumentsCondition, firstCall);
    for (let i = 0; i < contidionsOfError.length; i++) {
        if (contidionsOfError[i](firstCall, secondCall, source)) {
            checkErrors[i](source);
        }
    }
    return makeResult(firstCall, secondCall);
}

function buildSource(expression) {
    let source = new Source();
    source.setSource(expression.trim());
    return source;
}

const prefixOrderOfFunctions = [parseOperation, parseArguments];
const prefixArgumentsParseCondition = function(source) { return () => source.getToken() !== ')'; };
const prefixConditionsOfError = [(firstCall, secondCall) =>
    (firstCall[0].prototype.arity !== undefined && secondCall.length !== firstCall[0].prototype.arity)];
const prefixCheckErrors = [(source) => {throw new CustomError("unexpected arity of operation : " + source.getSubstr())}];
const prefixMakeResult = (firstCall, secondCall) => (new firstCall[0](...secondCall));
const parsePrefix = function(expression) {
    const source = buildSource(expression);
    const parser = new Parser(source, parseExpression, prefixArgumentsParseCondition(source),
        prefixOrderOfFunctions, prefixConditionsOfError, prefixCheckErrors, prefixMakeResult);
    return parser.parse();
};

const postfixArgumentsParseCondition = function(source) { return () => (!(source.getToken() in operationByString)); };
const postfixOrderOfFunctions = [parseArguments, parseOperation];
const postfixConditionsOfError = [(firstCall, secondCall, source) => (source.getToken() !== ')'),
    (firstCall, secondCall) =>
        (secondCall[0].prototype.arity !== undefined && firstCall.length !== secondCall[0].prototype.arity)];
const postfixCheckErrors = [(source) => {throw new CustomError("bracket(s) expected , but not found : " + source.getSubstr())},
    (source) => {throw new CustomError("unexpected arity of operation : " + source.getSubstr())}];
const postfixMakeResult = (firstCall, secondCall) => new secondCall[0](...firstCall);
const parsePostfix = function(expression) {
    const source = buildSource(expression);
    const parser = new Parser(source, parseExpression, postfixArgumentsParseCondition(source),
        postfixOrderOfFunctions, postfixConditionsOfError, postfixCheckErrors, postfixMakeResult);
    return parser.parse();
};
