#!/bin/bash
mkdir -p _out
javac -d _out RunJS.java \
    && java -ea --module-path=graal -cp _out RunJS
