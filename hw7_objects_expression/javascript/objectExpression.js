"use strict"

function Operator (...args) {
    this.args = args;
}
Operator.prototype.evaluate = function(...args) { return this.operation(
    ...this.args.map((i) => (i.evaluate(...args)))) };
Operator.prototype.toString = function() { return this.args.reduce((accumulator, currentValue) =>
    accumulator + " " + currentValue.toString()) + " " + this.operationString };
Operator.prototype.diff = function(variable) { return this.doDiff(...this.args, variable) };

function Add(left, right) {
    Operator.call(this, left, right);
}
Add.prototype = Object.create(Operator.prototype);
Add.prototype.operation = (a, b) => (a + b);
Add.prototype.operationString = '+';
Add.prototype.doDiff = (a, b, variable) => new Add(a.diff(variable), b.diff(variable));

function Subtract(left, right) {
    Operator.call(this, left, right);
}
Subtract.prototype = Object.create(Operator.prototype);
Subtract.prototype.operation = (a, b) => (a - b);
Subtract.prototype.operationString = '-';
Subtract.prototype.doDiff = (a, b, variable) => new Subtract(a.diff(variable), b.diff(variable));

function Multiply(left, right) {
    Operator.call(this, left, right);
}
Multiply.prototype = Object.create(Operator.prototype);
Multiply.prototype.operation = (a, b) => a * b;
Multiply.prototype.operationString = '*';
Multiply.prototype.doDiff = (a, b, variable) => new Add(
    new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable)));

function Divide(left, right) {
    Operator.call(this, left, right);
}
Divide.prototype = Object.create(Operator.prototype);
Divide.prototype.operation = (a, b) => a / b;
Divide.prototype.operationString = '/';
Divide.prototype.doDiff = (a, b, variable) => new Divide(
    new Subtract(new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable))), new Multiply(b, b));

function Negate(operand) {
    Operator.call(this, operand);
}
Negate.prototype = Object.create(Operator.prototype);
Negate.prototype.operation = (x) => -x;
Negate.prototype.operationString = 'negate';
Negate.prototype.doDiff = (a, variable) => new Negate(a.diff(variable));

function Const(x) {
    this.value = x;
}
Const.prototype.evaluate = function() { return this.value };
Const.prototype.toString = function() { return this.value.toString() };
Const.prototype.diff = function() { return new Const(0) };

const variableInd = {
    'x': 0, 'y': 1, 'z': 2
};

function Variable(name) {
    this.name = name;
    this.ind = variableInd[name];
}
Variable.prototype.evaluate = function(...args) { return args[this.ind] };
Variable.prototype.toString = function() { return this.name };
Variable.prototype.diff = function(variable) { return (this.name === variable ? new Const(1) : new Const(0)) };

const E = new Const(Math.E);

function Log(left, right) {
    Operator.call(this, left, right);
}
Log.prototype = Object.create(Operator.prototype);
Log.prototype.operation = (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a));
Log.prototype.operationString = 'log';
Log.prototype.doDiff = (a, b, variable) => new Divide(new Subtract(new Divide(
    new Multiply(new Log(E, a), b.diff(variable)), b),
    new Divide(new Multiply(new Log(E, b), a.diff(variable)), a)
), new Multiply(new Log(E, a), new Log(E, a)));

function Power(left, right) {
    Operator.call(this, left, right);
}
Power.prototype = Object.create(Operator.prototype);
Power.prototype.operation = (a, b) => Math.pow(a, b);
Power.prototype.operationString = 'pow';
Power.prototype.doDiff = (a, b, variable) => new Multiply(new Power(a, new Subtract(b, new Const(1))), new Add(
    new Multiply(b, a.diff(variable)), new Multiply(new Multiply(a, new Log(E, a)), b.diff(variable)))
);

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
    const parseSubstring = s => {
        if (s in variableInd) {
            stack.push(new Variable(s));
        } else if (s in operationByString) {
            stack.push(new operationByString[s](...stack.splice(-operandsNumber[s])));
        } else {
            stack.push(new Const(+s));
        }
    };
    expression.trim().split(/\s+/).forEach(parseSubstring);
    return stack.pop();
}

