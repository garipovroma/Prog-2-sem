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
  (letfn [(build-diff [rule] (fn [vars & args] (let [diffed-args (mapv #(diff % vars) args)] (rule args diffed-args))))])
  #(Abstract-Operation. %& operation operation-string diff-rule))

(def Negate (build-operation - "negate" (fn [vars & args] (Negate (diff (first args) vars)))))

(comment ":NOTE: it's good but it can be done in abtraction, not to copy-paste it in each declaration")

(defn build-diff [rule] (fn [vars & args] (let [diffed-args (mapv #(diff % vars) args)] (rule args diffed-args))))
(def Add (build-operation + "+" (build-diff #(apply Add %2))))
(def Subtract (build-operation - "-" (build-diff #(apply Subtract %2))))
(declare Multiply)
(def Multiply
  (build-operation * "*"
      (build-diff #(let [f (first %1) df (first %2) g (cond (= (count %1) 1) ONE :else(apply Multiply (rest %1)))
                         dg (cond (= (count %2) 1) ONE :else(apply Multiply (rest %2)))]
                     (Add (Multiply f dg) (Multiply df g))))))
(defn divide-operation
  ([single-arg] (single-arg))
  ([first-arg & other-args] (/ first-arg (double (apply * other-args)))))
(comment ":NOTE: to many code for Divide - My :NOTE: fixed")
(def Divide (build-operation divide-operation "/"
    (build-diff #(let [f (first %1) df (first %2) g (cond (= (count %1) 1) ONE :else(apply Multiply (rest %1)))
                       dg (cond (= (count %2) 1) ONE :else(apply Multiply (rest %2)))]
                   (Divide (Subtract (Multiply df g) (Multiply f dg)) (Multiply g g))))))

(def Sum (build-operation + "sum" (build-diff #(apply Add %2))))
(def Avg (build-operation calc-avg "avg" (build-diff #(Divide (apply Add %2) (Constant (count %1))))))

(def get-object-operation
  {'+ Add '- Subtract '/ Divide '* Multiply 'negate Negate 'sum Sum 'avg Avg})

(def parseObject (build-parser get-object-operation Constant Variable))