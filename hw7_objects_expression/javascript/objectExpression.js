"use strict"

function Expression (evalFunction, toStringFunction, diffFunction) {
    this.evaluate = evalFunction;
    this.toString = toStringFunction;
    this.diff = diffFunction;
}

function BinaryOperator (left, right, operatorString, evalFunction, diffFunction) {
    this.left = left;
    this.right = right;
    this.operatorString = operatorString;
    Expression.call(this, (...args) => evalFunction(
        this.left.evaluate(...args),
        this.right.evaluate(...args)),
        () => (this.left.toString() + " " + this.right.toString() + " " + this.operatorString),
        diffFunction(this.left, this.right));
}
BinaryOperator.prototype = Object.create(Expression.prototype);

let Add = function(left, right) { BinaryOperator.call(this, left, right, '+', (a, b) => a + b,
    (c, d) => (variable) => new Add(c.diff(variable), d.diff(variable))); };
let Subtract = function(left, right) { BinaryOperator.call(this, left, right, '-', (a, b) => a - b,
    (c, d) => (variable) => new Subtract(c.diff(variable), d.diff(variable))); };
let Multiply = function(left, right) { BinaryOperator.call(this, left, right, '*', (a, b) => a * b,
    (c, d) => (variable) => new Add(new Multiply(c.diff(variable), d), new Multiply(c, d.diff(variable)))); };
let Divide = function(left, right) { BinaryOperator.call(this, left, right, '/', (a, b) => a / b,
    (c, d) => (variable) => new Divide(
        new Subtract(new Multiply(c.diff(variable), d), new Multiply(c, d.diff(variable))), new Multiply(d, d))); };

function UnaryOperator (operand, operatorString, evalFunction, diffFunction) {
    this.operand = operand;
    this.operatorString = operatorString;
    Expression.call(this, (...args) => evalFunction(this.operand.evaluate(...args)),
        () => this.operand.toString() + " " + this.operatorString, diffFunction(this.operand));
}
UnaryOperator.prototype = Object.create(Expression.prototype);

let Negate = function(operand) { UnaryOperator.call(this, operand, 'negate',  (x) => -x,
    (c) => (variable) => (new Negate(c.diff(variable)))); };

let Const = function(x) {
    this.value = x;
    Expression.call(this, () => this.value, () => this.value.toString(), () => new Const(0));
};
Const.prototype = Object.create(Expression.prototype);

let variableInd = {
    'x': 0, 'y': 1, 'z': 2
};

let Variable = function(name) {
    this.name = name;
    Expression.call(this, (...args) => (args[variableInd[this.name]]), () => this.name,
        (variable) => (name === variable ? new Const(1) : new Const(0)));
};
Variable.prototype = Object.create(Expression.prototype);

let x = new Variable("x");
console.log(x.evaluate(10));

let a = new Add(new Const(1), new Variable("x"));
console.log(a.evaluate(10, 0, 0));
console.log(a.toString());


// -------------parser functions and constants-------------------

let skip = function (f) {
    return (...args) => {
        let s = args[0];
        let i = args[1];
        while (i < s.length && f(s.charAt(i))) {
            i++;
        }
        return i;
    }
};

let variableNames = ['x', 'y', 'z'];
let operationsPrefixes = ['+', '-', '*', '/'];
let operations = ['+', '-', '*', '/'];
let unaryOperations = ['negate'];
let unaryOperationsPrefixes = ['n'];
let operationByPrefix = {
    '+' : '+', '-' : '-', '*' : '*', '/' : '/'
};
let operandsNumber = {
    '+' : 2, '-' : 2, '*' : 2, '/' : 2
};
let operationByString = {
    '+' : Add, '-' : Subtract, '*' : Multiply, '/' : Divide
};
let pushOperation = function(stack, curOp) {
    stack.push(new operationByString[curOp](...stack.splice(-operandsNumber[curOp])));
};

let isDigit = c => {return ('0' <= c && c <= '9')};  // use it only with 1-characters-strings
let skipWhiteSpaces = skip(c => {return (c === ' ')} );
let skipConst = skip(isDigit);

let check = function(s, t, ind) {
    for (let i = 0; i < t.length; i++) {
        if (s.charAt(i + ind) !== t.charAt(i)) {
            return false;
        }
    }
    return true;
};

// -------------parser functions and constants-------------------

let parse = function(s) {
    s = s.trim();
    let stack = [];
    for (let i = 0; i < s.length; i++) {
        i = skipWhiteSpaces(s, i);
        let negative = false;
        if (s.charAt(i) === '-' && isDigit(s.charAt(i + 1))) {
            negative = true;
            i++;
        }
        if (isDigit(s.charAt(i))) {
            let j = skipConst(s, i);
            let val = +s.substr(i, j - i);
            if (negative) {
                val *= -1;
            }
            stack.push(new Const(val));
            i = j;
            i--;
        } else if (variableNames.includes(s.charAt(i))) {
            stack.push(new Variable(s.charAt(i)));
        } else if (operationsPrefixes.includes(s.charAt(i))) {
            if (check(s, operationByPrefix[s.charAt(i)], i)) {
                let curOp = operationByPrefix[s.charAt(i)];
                pushOperation(stack, curOp);
                let str = operationByPrefix[s.charAt(i)];
                i += str.length - 1;
            }  //  -- else error
        } else if (unaryOperationsPrefixes.includes(s.charAt(i))) {
            switch (s.charAt(i)) {
                case 'n':
                    if (check(s, "negate", i)) {
                        stack.push(new Negate(stack.pop()));
                        i += 5;
                    }
                // otherwise -- error
            }
        }
    }
    return stack.pop();
};
