;
; HW10 - accepted
;
(def constant constantly)
(defn variable [variable-name] (fn [var-values] (get var-values variable-name)))
(defn abstract-operation [operator]
  (fn [& args]
    (fn [var-values]
      (apply operator (mapv (fn [cur] (cur var-values)) args))
      )
    )
  )
(def add (abstract-operation +))
(def subtract (abstract-operation -))
(def multiply (abstract-operation *))
(def divide (abstract-operation
              (fn ([x] (/ (double x))) ([x & rst] (reduce (fn [a b] (/ (double a) (double b))) x rst)))))
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

;
; HW-11 - review
;

(definterface Expression
  (evaluate [vars])
  (string [])
  (diff [vars])
  )

(defn toString [a] (.string a))
(defn diff [a s] (.diff a s))
(defn evaluate [a d] (.evaluate a d))

(comment ":NOTE: merge or remove prototypes for Constant and Variable  -  My :NOTE: abstract type for them created")

(deftype Abstract-Simple-Expression [evaluate to-string diff element]
  Expression
  (evaluate [_ vars] (evaluate element vars))
  (string [_] (to-string element))
  (diff [_ vars] (diff element vars))
  )

(defn build-simple-expression-element [eval to-string diff]
  (fn [x] (Abstract-Simple-Expression.
            eval to-string diff x)))

(declare ZERO)
(declare ONE)

(def Constant (build-simple-expression-element (fn [f s] f) #(format "%.1f" (double %)) (fn [f s] ZERO)))
(def ZERO (Constant 0.0))
(def ONE (Constant 1.0))

(def Variable (build-simple-expression-element #(%2 (str %1)) #(str %1) #(if (= ( str %1) %2) ONE ZERO)))

(deftype Abstract-Operation [args operation operation-string diff-rule]
  Expression
  (evaluate [_ vars] (apply operation (mapv #(evaluate % vars) args)))
  (string [_]  (str "(" operation-string " " (clojure.string/join " " (mapv #(toString %) args))  ")"))
  (diff [_ vars] (letfn [(build-diff [rule]
                           (fn [vars & args] (let [diffed-args (mapv #(diff % vars) args)] (rule args diffed-args))))]
                   (apply (build-diff diff-rule) vars args))))

(defn build-operation [operation operation-string diff-rule]
  #(Abstract-Operation. %& operation operation-string diff-rule))
(def Negate (build-operation - "negate" #(Negate (first %2))))

(def Add (build-operation + "+" #(apply Add %2)))
(def Subtract (build-operation - "-" #(apply Subtract %2)))
(def Multiply (build-operation * "*"
                               #(let [f (first %1) df (first %2) g (cond (= (count %1) 1) ONE :else(apply Multiply (rest %1)))
                                      dg (cond (= (count %2) 1) ONE :else(apply Multiply (rest %2)))]
                                  (Add (Multiply f dg) (Multiply df g)))))
(defn divide-operation
  ([single-arg] (single-arg))
  ([first-arg & other-args] (/ first-arg (double (apply * other-args)))))
(def Divide (build-operation divide-operation "/"
                             #(let [f (first %1) df (first %2) g (cond (= (count %1) 1) ONE :else(apply Multiply (rest %1)))
                                    dg (cond (= (count %2) 1) ONE :else(apply Multiply (rest %2)))]
                                (Divide (Subtract (Multiply df g) (Multiply f dg)) (Multiply g g)))))
(def Sum (build-operation + "sum" #(apply Add %2)))
(def Avg (build-operation calc-avg "avg" #(Divide (apply Add %2) (Constant (count %1)))))

(def get-object-operation
  {'+ Add '- Subtract '/ Divide '* Multiply 'negate Negate 'sum Sum 'avg Avg})

(def parseObject (build-parser get-object-operation Constant Variable))