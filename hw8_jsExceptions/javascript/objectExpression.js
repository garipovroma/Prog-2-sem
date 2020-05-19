"use strict"
const Expression = function(evaluate, toString, diff, prefix, postfix) {
    this.prototype.evaluate = evaluate;
    this.prototype.toString = toString;
    this.prototype.diff = diff;
    this.prototype.prefix = prefix;
    this.prototype.postfix = postfix;
};

const setMethods = function(that, evaluate, toString, diff, prefix, postfix) {
    that.prototype = Object.create(Expression.prototype);
    Expression.call(that, evaluate, toString, diff, prefix, postfix);
};

const createSimpleExpression = function(body, get, toString, doDiff) {
    const result = body;
    setMethods(result, get, toString, doDiff, toString, toString);
    return result;
};

const Const = createSimpleExpression(
    function(value) { this.value = value; },
    function() { return +this.value },
    function() { return this.value.toString(); },
    () => Zero
);

const variableInd = {
    "x" : 0, "y" : 1, "z" : 2
};

const Variable = createSimpleExpression(
    function(name) { this.name = name, this.ind = variableInd[name]; },
    function(...args) { return args[this.ind]; },
    function() { return this.name; },
    function(variable) { return (this.name === variable ? One : Zero); }
);

const Zero = new Const(0);
const One = new Const(1);

let operationByString = {};
let specialOperations = {};

const stringMethod = (left, right, makeStr, args) => (left + args.map(makeStr).join(' ') + right);

const makeOperator = function(operation, operationString, doDiff, special = false) {
    const result = function(...args) {
        this.args = args;
    };
    setMethods(result,
        function(...args_) {return operation(...this.args.map((i) => (i.evaluate(...args_))))},
        function() { return stringMethod('', operationString, (a) => a.toString(), this.args) },
        function(variable) { return doDiff(variable, ...this.args) },
        function() { return stringMethod('(' + operationString + ' ', ')', (a) => (a.prefix()), this.args) },
        function() { return stringMethod('(', ' ' + operationString + ')', (a) => (a.postfix()), this.args) }
    );
    result.prototype.arity = (operation.length === 0 ? undefined : operation.length);
    operationByString[operationString] = result;
    if (special) {
        specialOperations[operationString] = true;
    }
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
    (variable, ...mas) => buildSumExp(...mas).diff(variable),
    true
);

const Softmax = makeOperator(
    (...mas) => (mas.length === 0 ? 0 : (Math.exp(mas[0])) /
        (mas.reduce((accumulator, currentValue) => (accumulator + Math.exp(currentValue)), 0))),
    'softmax',
    (variable, ...args) => buildSoftMax(...args).diff(variable),
    true
);

const AbstractErrorFactory = function(proto, rule) {
    const innerFactory = function(name, messagePref) {
        const result = function(...args) {
            this.message = messagePref + rule(...args);
        }
        result.prototype = Object.create(proto.prototype);
        result.prototype.name = name;
        result.constructor = proto;
        return result;
    }
    return innerFactory;
}

const CustomErrorFactory = AbstractErrorFactory(Error, (message) => (message));
const ParsingError = CustomErrorFactory('ParsingError', '');
const createParsingErrorMessageSuffix = (pos, substr) => ('at pos = ' + pos + ", token(s) found : >> " + substr + " <<");
const ParsingErrorFactory = AbstractErrorFactory(ParsingError, createParsingErrorMessageSuffix);

const UnexpectedTokenError = ParsingErrorFactory('UnexpectedTokenError', 'unexpected token ');
const UnexpectedEndOfExpressionError = ParsingErrorFactory('UnexpectedEndOfExpressionError', 'unexpected end of expression ');
const UnexpectedArityOfOperationError =  ParsingErrorFactory('UnexpectedArityOfOperationError', 'unexpected arity of operation ');
const BracketNotFoundError = ParsingErrorFactory('BracketNotFoundError', 'bracket(s) expected, but not found ');
const UnexpectedEmptyExpressionError = ParsingErrorFactory('UnexpectedEmptyExpressionError', 'unexpected empty expression ');
const OperationNotFoundError = ParsingErrorFactory('OperationNotFoundError', 'expected operation, but not found ');
const PostfixTypeOfExpressionError = ParsingErrorFactory('PostfixTypeOfExpressionError', 'postfix type of expression expected, but not found ');
const PrefixTypeOfExpressionError = ParsingErrorFactory('PrefixTypeOfExpressionError', 'Prefix type of expression expected, but not found ');

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
    let _sizesDiff = 0;
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
    this.getPos = () => _sizesDiff + _pos + 1;
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
        _sizesDiff = expression.length;
        _source = expression.trim();
        _sizesDiff -= _source.length;
        this.nextToken();
    };
};

function buildSource(expression) {
    const source = new Source();
    source.setSource(expression);
    return source;
}

const createParser = function(mode) {
    const _mode = mode;
    let _source = undefined, tokenToReturn = undefined;
    const test = function(cond, error) {
        const throwingString = (error === BracketNotFoundError ? _source.getSubstr() : ((tokenToReturn === undefined ?
            (_source.getToken() === ')' ? _source.getSubstr() : _source.getToken()) : tokenToReturn)))
        if (cond)
            throw new error(_source.getPos(), throwingString)
        tokenToReturn = undefined;
    };
    const setTokenToReturn = (str) => {tokenToReturn = str};
    const error = function(err) { test(true, err); };
    function beginParse(expression) {
        _source = buildSource(expression);
        test(_source.checkEmpty(), UnexpectedEmptyExpressionError);
        let result = parse();
        test(!_source.endFound(), BracketNotFoundError);
        return result;
    }
    function parse() {
        if (_source.getToken() === '(') {
            let operation = undefined, operationString, specialOperation = false, args = [], posOfOperatorInBracket = -1, pos = 0;
            _source.nextToken();
            while (!_source.endFound() && _source.getToken() !== ')') {
                if (_source.getToken() === '(') {
                    args.push(parse());
                } else if (_source.getToken() in operationByString) {
                    test(operation !== undefined, UnexpectedTokenError);
                    test(operation === undefined && pos !== 0 && _mode === 'prefix', PrefixTypeOfExpressionError);
                    operationString = _source.getToken();
                    operation = operationByString[_source.getToken()];
                    posOfOperatorInBracket = pos;
                    specialOperation = (_source.getToken() in specialOperations);
                } else if (_source.getToken() in variableInd) {
                    args.push(new Variable(_source.getToken()));
                } else if (!isNaN(+_source.getToken())) {
                    args.push(new Const(_source.getToken()));
                } else {
                    error(UnexpectedTokenError);
                }
                pos++;
                _source.nextToken();
            }
            test(_source.getToken() !== ')', BracketNotFoundError);
            test(operation === undefined, OperationNotFoundError);
            test(posOfOperatorInBracket + 1 !== pos && _mode === 'postfix', PostfixTypeOfExpressionError);
            setTokenToReturn(operationString);
            test(operation.prototype.arity !== args.length && !specialOperation, UnexpectedArityOfOperationError);
            return new operation(...args);
        } else {
            if (_source.getToken() in variableInd) {
                return new Variable(_source.getToken());
            } else if (!isNaN(+_source.getToken())) {
                return new Const(_source.getToken());
            } else {
                error(UnexpectedTokenError);
            }
        }
    }
    return beginParse;
};

const parsePrefix = createParser('prefix');
const parsePostfix = createParser('postfix');

