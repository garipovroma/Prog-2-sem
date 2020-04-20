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

const makeOperator = function(operation, operationString, doDiff, arity) {
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
    result.prototype.arity = arity;
    return result;
};

const Add = makeOperator(
    (a, b) => (a + b),
    '+',
    (variable, a, b) => new Add(a.diff(variable), b.diff(variable)),
    2
);
const Subtract = makeOperator(
    (a, b) => (a - b),
    '-',
    (variable, a, b) => new Subtract(a.diff(variable), b.diff(variable)),
    2
);
const Negate = makeOperator(
    (x) => -x,
    'negate',
    (variable, a) => new Negate(a.diff(variable)),
    1
);
const Multiply = makeOperator(
    (a, b) => (a * b),
    '*',
    (variable, a, b) => new Add(new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable))),
    2
);
const Divide = makeOperator(
    (a, b) => a / b,
    '/',
    (variable, a, b) => new Divide(new Subtract(new Multiply(a.diff(variable), b), new Multiply(a,
        b.diff(variable))), new Multiply(b, b)),
    2
);
let E = new Const(Math.E);
const Log = makeOperator(
    (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    'log',
    (variable, a, b) => new Divide(new Subtract(new Divide(new Multiply(new Log(E, a), b.diff(variable)), b),
        new Divide(new Multiply(new Log(E, b), a.diff(variable)), a)), new Multiply(new Log(E, a), new Log(E, a))),
    2
);
const Power = makeOperator(
    (a, b) => Math.pow(a, b),
    'pow',
    (variable, a, b) => new Multiply(new Power(a, new Subtract(b, new Const(1))), new Add(new Multiply(b,
        a.diff(variable)), new Multiply(new Multiply(a, new Log(E, a)), b.diff(variable)))),
    2
);

function buildSum(...args) {
    if (args.length === 0) {
        return Zero;
    } else if (args.length === 1) {
        return new Power(E, args[0]);
    } else {
        let cur = new Add(new Power(E, args[0]),new Power(E, args[1]));
        for (let i = 2; i < args.length; i++) {
            cur = new Add(cur, new Power(E, args[i]));
        }
        return cur;
    }
}

const Sumexp = makeOperator(
    (...args) => (args.reduce((accumulator, currentValue) => (accumulator + Math.exp(currentValue)), 0)),
    'sumexp',
    (variable, ...mas) => buildSum(...mas).diff(variable),
    undefined
);

let buildSoftmaxDiff = function(...mas) {
    //console.log(mas.map(cur => cur.toString()).join(" "));
    if (mas.length <= 1) {
        return new Const(0);
    } else {
        let ch = new Power(E, mas[0]);
        return new Divide(ch, buildSum(...mas));
    }
};

const Softmax = makeOperator(
    (...mas) => (mas.length === 0 ? 0 : (Math.exp(mas[0])) /
        (mas.reduce((accumulator, currentValue) => (accumulator + Math.exp(currentValue)), 0))),
    'softmax',
    (variable, ...args) => buildSoftmaxDiff(...args).diff(variable),
    undefined
);

let CustomError = function(message) {
    this.message = message;
};
CustomError.prototype = Object.create(Error.prototype);
CustomError.prototype.name = "CustomError";

const operationByString = {
    '+' : Add,
    '-' : Subtract,
    '*' : Multiply,
    '/' : Divide,
    'pow' : Power,
    'log' : Log,
    'negate' : Negate,
    'sumexp' : Sumexp,
    'softmax' : Softmax
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
    this.endFound = () => (_pos === _source.length);
    this.getSubstr = () => "at pos = " + _pos + " : " + _source.substring(Math.max(_pos - 5, 0),
        Math.min(_pos + 5, _source.length - 1));
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

function Parser (source, parseExpression) {
    this.parse = () => {
        if (source.getToken() === '') {
            throw new CustomError("unexpected empty expression");
        }
        let res = null;
        if (source.getToken() === '(') {
            res = _parse(true, true);
        } else {
            res = _parse(false, false);
        }
        if (!source.endFound()) {
            throw new CustomError("unexpected token : " + source.getSubstr());
        }
        return res;
    };
    function _parse(needToken, needBracket) {
        if (needToken) {
            source.getToken();
        }
        if (!needBracket) {
            if (source.getToken() in variableInd) {
                return new Variable(source.getToken());
            } else if (!isNaN(+source.getToken())) {
                return new Const(source.getToken());
            } else {
                throw new CustomError("unexpected token :" + source.getSubstr());
            }
        }
        let res = parseExpression(source, _parse);
        return res;
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
        throw new CustomError("unexpected token : " + source.getSubstr());
    }
    return [operation, specialOperation];
}

function parseArguments(source, parse, condition, f) {
    let args = [];
    while (condition()) {
        if (source.endFound()) {
            throw new CustomError("expected ), but not found : " + source.getSubstr());
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
    if (args.length === 0 && !(specialOperations.includes(source.getToken())) && !f) {
        throw new CustomError("unexpected ) found : " + source.getSubstr());
    }
    return args;
}

function parsePrefixExpression(source, parse) {
    source.nextToken();
    let mas = parseOperation(source);
    let operation = mas[0];
    let f = mas[1];
    source.nextToken();
    let args = parseArguments(source, parse, () => (source.getToken() !== ')'), f);
    if (operation.prototype.arity !== undefined && args.length !== operation.prototype.arity) {
        throw new CustomError("unexpected arity of operation : " + source.getSubstr());
    }
    return new operation(...args);
}

function parsePostfixExpression(source, parse) {
    source.nextToken();
    let args = parseArguments(source, parse, () => (!(source.getToken() in operationByString)));
    let mas = parseOperation(source);
    let operation = mas[0];
    let f = mas[1];
    source.nextToken();
    if (source.getToken() !== ')') {
        throw new CustomError("expected ) , but not found : " + source.getSubstr());
    }
    if (operation.prototype.arity !== undefined && args.length !== operation.prototype.arity) {
        throw new CustomError("unexpected arity of operation : " + source.getSubstr());
    }
    return new operation(...args);
}

function buildSource(expression) {
    let source = new Source();
    source.setSource(expression.trim());
    return source;
}

let parsePrefix = function(expression) {
    let parser = new Parser(buildSource(expression), parsePrefixExpression);
    return parser.parse();
};

let parsePostfix = function(expression) {
    let parser = new Parser(buildSource(expression), parsePostfixExpression);
    return parser.parse();
};
