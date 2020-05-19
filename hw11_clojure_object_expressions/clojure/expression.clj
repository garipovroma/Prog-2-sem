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

(declare ZERO)

(deftype Const [const]
  Expression
  (evaluate [_ _] const)
  (string [_] (format "%.1f" const))
  (diff [_ vars] ZERO))

(def ZERO (Const. 0.0))
(def ONE (Const. 1.0))

(deftype Var [var]
  Expression
  (evaluate [_ vars] (vars (str var)))
  (string [_] (str var))
  (diff [_ vars] (if (= (str var) vars) ONE ZERO))
  )

(defn Constant [const]
  (Const. const))
(defn Variable [var] (Var. var))

(deftype Abstract-Operation [args operation operation-string diff-rule]
  Expression
  (evaluate [_ vars] (apply operation (mapv #(evaluate % vars) args)))
  (string [_]  (str "(" operation-string " " (clojure.string/join " " (mapv #(toString %) args))  ")"))
  (diff [_ vars] (apply diff-rule vars args))
  )

(defn build-operation [operation operation-string diff-rule]
  #(Abstract-Operation. %& operation operation-string diff-rule))

(def Negate (build-operation - "negate" (fn [vars & args] (Negate (diff (first args) vars)))))

(defn diff-all [vars args]
  (mapv #(diff % vars) args))

(def Add
  (build-operation + "+"
    (fn [vars & args]
      (apply Add (diff-all vars args))
      )))

(def Subtract
  (build-operation - "-"
   (fn [vars & args]
    (apply Subtract (diff-all vars args))
     )))

(def Multiply
  (build-operation * "*" (fn [vars & args]
     (let [rst (rest args)]
       (if (= (count args) 1) (diff (first args) vars)
       (Add (apply Multiply (diff (first args) vars) rst)
            (Multiply (diff (apply Multiply rst) vars) (first args))))))))

(defn divide-operation
  ([single-arg] (single-arg))
  ([first-arg & other-args] (/ first-arg (double (apply * other-args)))))
(def Divide (build-operation divide-operation "/"
 (fn [vars & args]
    (let [rst (rest args)]
      (if (== (count args) 1) (diff (first args) vars)
      (Divide
        (Subtract
          (Multiply (diff (first args) vars) (apply Multiply rst))
          (Multiply (first args) (diff (apply Multiply rst) vars)))
        (Multiply (apply Multiply rst) (apply Multiply rst))))))))

(defn build-objects-expression [operation args] (apply operation args))
(defn sum-diff [vars args] (build-objects-expression Add (diff-all vars args)))
(def Sum (build-operation + "sum" (fn [vars & args] (sum-diff vars args))))
(def Avg (build-operation calc-avg "avg"
     (fn [vars & args] (Divide (sum-diff vars args) (Constant (count args))))))

(def get-object-operation
  {'+ Add '- Subtract '/ Divide '* Multiply 'negate Negate 'sum Sum 'avg Avg})

(def parseObject (build-parser get-object-operation Constant Variable))
