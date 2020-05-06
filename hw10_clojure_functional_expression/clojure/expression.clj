;delay

(defn constant [const-value] (constantly const-value))
(defn variable [variable-name] (fn [var-values] (get var-values variable-name)))
(defn abstract-operation [operator]
  (fn [& args]
    (fn [var-values]
      (apply operator (mapv (fn [cur] (cur var-values)) args))
      )
    )
  )
(defn abstract-unary-operation [operator]
  (fn [arg]
    (fn [var-values]
      (operator (arg var-values))
      )
    )
  )

(def add (abstract-operation +))
(def subtract (abstract-operation -))
(def multiply (abstract-operation *))
(def divide (abstract-operation (fn ([x] (/ (double x))) ([x & rst] (reduce (fn [a b] (/ (double a) (double b))) x rst)))))
(defn my-med [& args]
  (nth (sort args) (int (/ (count args) 2)))
  )
(defn my-avg [& args]
  (/ (apply + args) (count args))
  )
(def negate (abstract-unary-operation -))
(def med (abstract-operation my-med))
(def avg (abstract-operation my-avg))
(def get-operation-by-symbol
  {'negate negate '+ add '- subtract '* multiply '/ divide 'med med 'avg avg}
  )

(defn my-parse [ss]
  (cond
    (number? ss) (constant ss)
    (symbol? ss) (variable (str ss))
    :else (apply (get get-operation-by-symbol (first ss)) (mapv my-parse (rest ss)))
    )
  )
(defn parseFunction [s]
  (my-parse (read-string s))
  )
