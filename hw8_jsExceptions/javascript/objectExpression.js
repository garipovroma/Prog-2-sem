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
    function() { return new Const(0) },
    function() { return this.value.toString() },
    function() { return this.value.toString() }
);
const variableInd = {
    "x" : 0, "y" : 1, "z" : 2
};
const Variable = function(name) {
    this.name = name;
    this.ind = variableInd[name];
};
createExpression(Variable,
    function(...args) { return args[this.ind] },
    function() { return this.name },
    function(variable) { return (this.name === variable ? new Const(1) : new Const(0))},
    function() { return this.name },
    function() { return this.name }
);

const makeOperator = function(operation, operationString, doDiff, arity) {
    let result = function(...args) {
        this.args = args;
    };
    createExpression(result,
        function(...args_) {return operation(...this.args.map((i) => (i.evaluate(...args_))))},
        function() { return this.args.reduce((accumulator, currentValue) =>
            accumulator + " " + currentValue.toString()) + " " + operationString },
        function(variable) { return doDiff(...this.args, variable) },
        function() { return "(" + operationString + " " + this.args.map(cur => cur.prefix()).join(' ') + ")" },
        function() { return "(" + this.args.map(cur => cur.postfix()).join(' ' ) + " " + operationString + ")" }
    );
    result.prototype.arity = arity;
    return result;
};

const Add = makeOperator(
    (a, b) => (a + b),
    '+',
    (a, b, variable) => new Add(a.diff(variable), b.diff(variable)),
    2
);
const Subtract = makeOperator(
    (a, b) => (a - b),
    '-',
    (a, b, variable) => new Subtract(a.diff(variable), b.diff(variable)),
    2
);
const Negate = makeOperator(
    (x) => -x,
    'negate',
    (a, variable) => new Negate(a.diff(variable)),
    1
);
const Multiply = makeOperator(
    (a, b) => (a * b),
    '*',
    (a, b, variable) => new Add(new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable))),
    2
);
const Divide = makeOperator(
    (a, b) => a / b,
    '/',
    (a, b, variable) => new Divide(new Subtract(new Multiply(a.diff(variable), b), new Multiply(a,
        b.diff(variable))), new Multiply(b, b)),
    2
);
let E = new Const(Math.E);
const Log = makeOperator(
    (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    'log',
    (a, b, variable) => new Divide(new Subtract(new Divide(new Multiply(new Log(E, a), b.diff(variable)), b),
        new Divide(new Multiply(new Log(E, b), a.diff(variable)), a)), new Multiply(new Log(E, a), new Log(E, a))),
    2
);
const Power = makeOperator(
    (a, b) => Math.pow(a, b),
    'pow',
    (a, b, variable) => new Multiply(new Power(a, new Subtract(b, new Const(1))), new Add(new Multiply(b,
        a.diff(variable)), new Multiply(new Multiply(a, new Log(E, a)), b.diff(variable)))),
    2
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
    'negate' : Negate
};

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
    this.getSubstr = () => _source.substring(Math.max(_pos - 5, 0),
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
    if (source.getToken() in operationByString) {
        operation = operationByString[source.getToken()];
    } else {
        throw new CustomError("unexpected token : " + source.getSubstr());
    }
    return operation;
}

function parseArguments(source, parse) {
    let args = [];
    source.nextToken();
    while (source.getToken() !== ')') {
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
    if (args.length === 0) {
        throw new CustomError("unexpected ) found : " + source.getSubstr());
    }
    return args;
}

function parsePrefixExpression(source, parse) {
    source.nextToken();
    let operation = parseOperation(source);
    let args = parseArguments(source, parse);
    if (args.length !== operation.prototype.arity) {
        throw new CustomError("unexpected arity of operation : " + source.getSubstr());
    }
    return new operation(...args);
}

function parsePostfixExpression(source, parse) {
    source.nextToken();
    let args = parseArguments(source, parse);
    let operation = parseOperation(source);
    if (args.length !== operation.prototype.arity) {
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
    let parser = new Parser(buildSource(expression.trim()), parsePrefixExpression);
    return parser.parse();
};

let parsePostfix = function(expression) {
    let parser = new Parser(buildSource(expression.trim()), parsePostfixExpression);
    return parser.parse();
};

//  (/ (negate x) 2)

let str = parsePrefix('(/ (negate x) 2)\n');
console.log(str.toString());
console.log(str.prefix());

let str1 = parsePrefix('(negate x)');
console.log(str1.prefix());