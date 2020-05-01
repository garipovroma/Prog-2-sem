(defn sizes-checker [args seq]
  (every? (partial == (count (first args))) seq))

(defn vectors-sizes-equals? [args] (sizes-checker args (mapv count args)))
(defn is-vector? [a] (and (every? number? a) (vector? a)))
(defn is-matrix? [a] (and (vector? a) (every? is-vector? a) (vectors-sizes-equals? a)))
(defn is-tensor? [a]
  (or
    (number? a)
    (every? number? a)
    (and (every? vector? a) (vectors-sizes-equals? a) (is-tensor? (apply concat [] a)))
    )
  )

(defn v-apply-by-elements [operator args сheck-function]
  {:pre [(vectors-sizes-equals? args) (every? сheck-function args)]}
  (if (every? number? args) (apply operator args) (apply mapv operator args)))

(defn abstract-operation [checker] (fn [operator] (fn [& args] (v-apply-by-elements operator args checker))))
(def vector-operation (abstract-operation is-vector?))
(def matrix-operation (abstract-operation is-matrix?))

(def v+ (vector-operation +))
(def v- (vector-operation -))
(def v* (vector-operation *))

(defn scalar [& args] {:pre [(every? is-vector? args) (vectors-sizes-equals? args)]} (reduce + (apply mapv * args)))
(defn simple-vect [[x1, y1, z1], [x2, y2, z2]]
  (vector (- (* y1 z2) (* y2 z1)) (- (* x2 z1)  (* x1 z2)) (- (* x1 y2) (* x2 y1))))
(defn vect [& args]
  {:pre [(vectors-sizes-equals? args) (every? is-vector? args) (every? (partial == 3) (mapv count args))]}
  (reduce simple-vect args))
(defn v*s [v & args] (reduce (fn [v' s'] (mapv (partial * s') v')) v args))

(def m+ (matrix-operation v+))
(def m- (matrix-operation v-))
(def m* (matrix-operation v*))
(defn m*s [m & args] {:pre [(is-matrix? m) (every? number? args)]}
  (reduce (fn [m' s] (mapv (fn [x] (v*s x s)) m')) m args))
(defn m*v [m v] {:pre [(is-matrix? m) (is-vector? v)]} (mapv (fn [x] (scalar x v)) m))

(defn transpose [m] {:pre [(is-matrix? m)]} (apply mapv vector m))
(defn simple-prod [a, b] (mapv (fn [x] (m*v (transpose b) x)) a))
(defn m*m [& args] (reduce simple-prod args))

(defn tensor-operation [vector-operation this-operation t]
  (if (is-vector? (first t)) (apply vector-operation t) (v-apply-by-elements this-operation t is-tensor?)))

(defn t+ [& t] (tensor-operation v+ t+ t))
(defn t- [& t] (tensor-operation v- t- t))
(defn t* [& t] (tensor-operation v* t* t))
