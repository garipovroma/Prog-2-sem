"use strict"

function Expression (evaluate, toString, diff) {
    this.evaluate = evaluate;
    this.toString = toString;
    this.diff = diff;
}

function Const(value) {
    this.value = value;
    Expression.call(this, () => +value, () => value.toString(), () => new Const(0));
}

const variableInd = {
    'x': 0, 'y': 1, 'z': 2
};

function Variable(name) {
    this.name = name;
    this.ind = variableInd[name];
    Expression.call(this, (...args) => (args[this.ind]), () => name, (variable) =>
        (name === variable ? new Const(1) : new Const(0)));
}

function Operator (operation, operationString, doDiff, ...exprs) {
    Expression.call(this,
        (...args) =>
            operation(...exprs.map((i) => (i.evaluate(...args)))),
        () => exprs.reduce((accumulator, currentValue) =>
            accumulator + " " + currentValue.toString()) + " " + operationString,
        (variable) =>
            doDiff(...exprs, variable)
        );
}

function Add(left, right) {
    Operator.call(this, (a, b) => (a + b), '+', (a, b, variable) => new Add(a.diff(variable), b.diff(variable)),
        left, right);
}
function Subtract(left, right) {
    Operator.call(this, (a, b) => (a - b), '-', (a, b, variable) => new Subtract(a.diff(variable), b.diff(variable)),
        left, right);
}
function Multiply(left, right) {
    Operator.call(this, (a, b) => (a * b), '*', (a, b, variable) =>
        new Add(new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable))), left, right);
}
function Divide(left, right) {
    Operator.call(this, (a, b) => a / b, '/', (a, b, variable) => new Divide(
        new Subtract(new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable))), new Multiply(b, b)),
        left, right);
}
function Negate(operand) {
    Operator.call(this, (x) => -x, 'negate', (a, variable) => new Negate(a.diff(variable)),  operand);
}

const E = new Const(Math.E);
function Log(left, right) {
    Operator.call(this, (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)), 'log',
        (a, b, variable) => new Divide(new Subtract(new Divide(new Multiply(new Log(E, a), b.diff(variable)), b),
            new Divide(new Multiply(new Log(E, b), a.diff(variable)), a)), new Multiply(new Log(E, a), new Log(E, a))),
        left, right
    );
}
function Power(left, right) {
    Operator.call(this, (a, b) => Math.pow(a, b), 'pow',
        (a, b, variable) => new Multiply(new Power(a, new Subtract(b, new Const(1))),
            new Add(new Multiply(b, a.diff(variable)), new Multiply(new Multiply(a, new Log(E, a)), b.diff(variable)))),
        left, right);
}

const operandsNumber = {
    '+' : 2,
    '-' : 2,
    '*' : 2,
    '/' : 2,
    'log' : 2,
    'pow' : 2,
    'negate': 1
};
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
    for (let s of expression.trim().split(/\s+/)) {
        if (s in variableInd) {
            stack.push(new Variable(s));
        } else if (s in operationByString) {
            stack.push(new operationByString[s](...stack.splice(-operandsNumber[s])));
        } else {
            stack.push(new Const(+s));
        }
    }
    return stack.pop();
}

