"use strict"

let cnst = (a) => () => a;
let variable = (name) => (...args) => {
    switch (name) {
        case "x":
            return +args[0];
        case "y":
            return +args[1];
        case "z":
            return +args[2];
    }
};

let binaryOperation = (f, a, b) => (...args) => f(a(args[0], args[1], args[2]), b(args[0], args[1], args[2]));
let unaryOperation = (f, a) => (...args) => f(a(args[0], args[1], args[2]));
let constValueOperation = (value) => (cnst(value));

let add = (x, y) => binaryOperation((a, b) => a + b, x, y);
let subtract = (x, y) => binaryOperation((a, b) => a - b, x, y);
let multiply = (x, y) => binaryOperation((a, b) => a * b, x, y);
let divide = (x, y) => binaryOperation((a, b) => a / b, x, y);

let negate = (x) => unaryOperation((a) => -a, x);
let sin = (x) => unaryOperation((a) => Math.sin(a), x);
let cos = (x) => unaryOperation((a) => Math.cos(a), x);

let pi = constValueOperation(Math.acos(-1));
let e = constValueOperation(Math.exp(1));

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
let binaryOperations = ['+', '-', '*', '/'];
let unaryOperationsPrefixes = ['s', 'c', 'n'];
let constantsPrefixes = ['p', 'e'];

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
            stack.push(cnst(val));
            i = j;
            i--;
        } else if (variableNames.includes(s.charAt(i))) {
            stack.push(variable(s.charAt(i)));
        } else if (unaryOperationsPrefixes.includes(s.charAt(i))) {
            switch (s.charAt(i)) {
                case 's':
                    if (check(s, "sin", i)) {
                        stack.push(sin(stack.pop()));
                        i += 2;
                    }
                    continue;
                case 'c':
                    if (check(s, "cos", i)) {
                        stack.push(cos(stack.pop()));
                        i += 2;
                    }
                    continue;
                case 'n':
                    if (check(s, "negate", i)) {
                        stack.push(negate(stack.pop()));
                        i += 5;
                    }
                // otherwise -- error
            }
        } else if (constantsPrefixes.includes(s.charAt(i))) {
            switch (s.charAt(i)) {
                case 'p':
                    if (check(s, "pi", i)) {
                        stack.push(pi);
                        i += 1;
                    }
                    continue;
                case 'e':
                    stack.push(e);
            }
        } else if (binaryOperations.includes(s.charAt(i))) {
            let b = stack.pop();
            let a = stack.pop();
            if (s.charAt(i) === '+') {
                stack.push(add(a, b));
            } else if (s.charAt(i) === '-') {
                stack.push(subtract(a, b));
            } else if (s.charAt(i) === '*') {
                stack.push(multiply(a, b));
            } else if (s.charAt(i) === '/') {
                stack.push(divide(a, b));
            }
        }
    }
    return stack.pop();
};
