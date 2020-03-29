"use strict"

const cnst = (a) => () => a;

const variableIndex = {
    "x" : 0, "y" : 1, "z" : 2
};

const variable = (name) => (...args) => args[variableIndex[name]];
const operation = (f, ...args1) => (...args2) => f(...args1.map(value => value(...args2)));
const add = (x, y) => operation((a, b) => a + b, x, y);
const subtract = (x, y) => operation((a, b) => a - b, x, y);
const multiply = (x, y) => operation((a, b) => a * b, x, y);
const divide = (x, y) => operation((a, b) => a / b, x, y);
const negate = (x) => operation((a) => -a, x);
const pi = operation(cnst(Math.acos(-1)));
const e = operation(cnst(Math.exp(1)));
const median = (...args) => args.sort((a, b) => a - b)[Math.floor(args.length / 2)];
const sum = (...args) => args.reduce((a, b) => a + b);
const avg = (...args) => sum(...args) / (args.length);
const avg5 = (a, b, c, d, e) => operation(avg, a, b, c, d, e);
const med3 = (a, b, c) => operation(median, a, b, c);

const constantByString = { 'pi' : pi, 'e' : e };
const operandsNumber = {
    '+' : 2, '-' : 2, '*' : 2, '/' : 2, 'avg5' : 5, 'med3' : 3, negate : 1
};
const operationByString = {
    '+' : add, '-' : subtract, '*' : multiply, '/' : divide, 'avg5' : avg5, 'med3' : med3, 'negate' : negate
};

function parse(expression) {
    let stack = [];
    const parseSubstring = s => {
        if (s in variableIndex) {
            stack.push(variable(s));
        } else if (s in operationByString) {
            stack.push(operationByString[s](...stack.splice(-operandsNumber[s])));
        } else if (s in constantByString) {
            stack.push(constantByString[s]);
        } else {
            stack.push(cnst(+s));
        }
    };
    expression.trim().split(/\s+/).forEach(parseSubstring);
    return stack.pop();
}

// test part
 // x ^ 2 - 2x + 1
let testExpr = add(subtract(multiply(variable("x"),
    variable("x")), multiply(cnst(2), variable("x"))), cnst(1));
for (let i = 0; i < 10; i++) {
    console.log(testExpr(i));
}