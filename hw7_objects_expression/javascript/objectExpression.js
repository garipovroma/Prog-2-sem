"use strict"

let Operator = function(...args) {
    this.args = args;
};
Operator.prototype.evaluate = function(...args) { return this.operation(
    ...this.args.map((i) => (i.evaluate(...args)))) };
Operator.prototype.toString = function() { return ''.concat(
    ...this.args.map((i) => i.toString() + ' '), this.operationString) };
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

let variableInd = {
    'x': 0, 'y': 1, 'z': 2
};

function Variable(name) {
    this.name = name;
};
Variable.prototype.evaluate = function(...args) { return args[variableInd[this.name]] };
Variable.prototype.toString = function() { return this.name };
Variable.prototype.diff = function(variable) { return (this.name === variable ? new Const(1) : new Const(0)) };

let x = new Variable("x");
console.log(x.evaluate(10));

let a = new Add(new Const(2), new Variable("x")).diff();
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
