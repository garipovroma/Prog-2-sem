@javac -d _out RunJS.java ^
    && java -ea --module-path=graal -cp _out RunJS
