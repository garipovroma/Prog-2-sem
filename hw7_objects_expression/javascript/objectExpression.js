"use strict"

const Expression = function(evaluate, toString, diff) {
    this.prototype.evaluate = evaluate;
    this.prototype.toString = toString;
    this.prototype.diff = diff;
};

const createExpression = function(that, evaluate, toString, diff) {
    that.prototype = Object.create(Expression.prototype);
    Expression.call(that, evaluate, toString, diff);
};

const Const = function(value) {
    this.value = value;
};
createExpression(Const,
    function() { return +this.value },
    function() { return this.value.toString() },
    function() { return new Const(0) }
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
    function(variable) { return (this.name === variable ? new Const(1) : new Const(0)) }
);

const makeOperator = function(operation, operationString, doDiff) {
    let result = function(...args) {
        this.args = args;
    };
    createExpression(result,
        function(...args_) {return operation(...this.args.map((i) => (i.evaluate(...args_))))},
        function() { return this.args.reduce((accumulator, currentValue) =>
            accumulator + " " + currentValue.toString()) + " " + operationString },
        function(variable) { return doDiff(...this.args, variable) }
    );
    return result;
};

const Add = makeOperator(
    (a, b) => (a + b),
    '+', (a, b, variable) => new Add(a.diff(variable), b.diff(variable))
);
const Subtract = makeOperator(
    (a, b) => (a - b),
    '-',
    (a, b, variable) => new Subtract(a.diff(variable), b.diff(variable))
);
const Negate = makeOperator(
    (x) => -x,
    'negate',
    (a, variable) => new Negate(a.diff(variable))
);
const Multiply = makeOperator(
    (a, b) => (a * b),
    '*',
    (a, b, variable) => new Add(new Multiply(a.diff(variable), b), new Multiply(a, b.diff(variable)))
);
const Divide = makeOperator(
    (a, b) => a / b,
    '/',
    (a, b, variable) => new Divide(new Subtract(new Multiply(a.diff(variable), b), new Multiply(a,
        b.diff(variable))), new Multiply(b, b))
);
let E = new Const(Math.E);
const Log = makeOperator(
    (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    'log',
    (a, b, variable) => new Divide(new Subtract(new Divide(new Multiply(new Log(E, a), b.diff(variable)), b),
        new Divide(new Multiply(new Log(E, b), a.diff(variable)), a)), new Multiply(new Log(E, a), new Log(E, a)))
);
const Power = makeOperator(
    (a, b) => Math.pow(a, b),
    'pow',
    (a, b, variable) => new Multiply(new Power(a, new Subtract(b, new Const(1))), new Add(new Multiply(b,
        a.diff(variable)), new Multiply(new Multiply(a, new Log(E, a)), b.diff(variable)))));
/*
let x = new Add(new Const(1), new Const(2));
let y = new Add(new Const(2), new Const(2));
let v = new Variable('x');
let z = new Subtract(new Const(2), new Const(2));
console.log(Object.getPrototypeOf(x) === Object.getPrototypeOf(y));
console.log(Object.getPrototypeOf(x) === Object.getPrototypeOf(v));
console.log(Object.getPrototypeOf(x) === Object.getPrototypeOf(z));
*/

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

