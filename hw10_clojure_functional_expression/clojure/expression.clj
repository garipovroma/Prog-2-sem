;
; HW-10 - review
;

(defn constant [const-value] (constantly const-value))
(defn variable [variable-name] (fn [var-values] (get var-values variable-name)))
(defn abstract-operation [operator]
  (fn [& args]
    (fn [var-values]
      (apply operator (mapv (fn [cur] (cur var-values)) args))
      )
    )
  )

(comment ":NOTE: for what when you have abstract-operation? - My :NOTE: abstract unary operation function deleted")
(def add (abstract-operation +))
(def subtract (abstract-operation -))
(def multiply (abstract-operation *))
(def divide (abstract-operation #(/ (double %1) (double %2))))
(defn calc-med [& args]
  (nth (sort args) (int (/ (count args) 2)))
  )
(defn calc-avg [& args]
  (/ (apply + args) (count args))
  )
(def negate (abstract-operation -))
(def med (abstract-operation calc-med))
(def avg (abstract-operation calc-avg))

(def get-functional-operation
  {'negate negate '+ add '- subtract '* multiply '/ divide 'med med 'avg avg}
  )


(defn build-parser [get-operation const-func variable-func]
  (fn [input-string]
    (letfn [(recursive-parse [cur-string]
              (cond
                (number? cur-string) (const-func cur-string)
                (symbol? cur-string) (variable-func (str cur-string))
                :else (apply (get get-operation (first cur-string)) (mapv recursive-parse (rest cur-string)))
                ))]
      (recursive-parse (read-string input-string))
      )))

(def parseFunction (build-parser get-functional-operation constant variable))
