(ns ms.field
  (:require [clojure.set]
            [ms.cell :as cell]))

(defn range-vec [size] (vec (range 0 size)))
(def neighbours-offset
  [[-1 -1] [ 0 -1] [ 1 -1]
   [-1  0]         [ 1  0]
   [-1  1] [ 0  1] [ 1  1]])

(defn some-cell? [pred-fn field]
  (some? (some pred-fn (flatten field))))

(defn has-moves? [field]
  (some-cell?
    (fn [cell] (and (cell/close? cell)
                    (not (cell/mine? cell))))
    field))

(defn mine-opened? [field]
  (some-cell?
    (fn [cell] (and (cell/open? cell)
                    (cell/mine? cell)))
    field))

(defn generate [x-size y-size]
  (mapv (fn [y] (mapv (fn [x] (cell/create x y))
                      (range-vec x-size)))
        (range-vec y-size)))
(defn get-neighbours [x y pred-fn field]
  (set (reduce
         (fn [neighbours [x-offset y-offset]]
           (let [x (+ x x-offset)
                 y (+ y y-offset)
                 cell (cell/get x y field)]
             (if (and (some? cell)
                      (pred-fn cell))
               (conj neighbours [x y])
               neighbours)))
         []
         neighbours-offset)))
(defn get-empty-cells [cells x y field]
  (let [neighbours (get-neighbours x y cell/empty? field)
        new-cells (clojure.set/difference neighbours cells)]
    (reduce
      (fn [acc [x y]]
        (clojure.set/union
          acc
          (get-empty-cells (conj acc [x y]) x y field)))
      cells
      new-cells)))
(defn get-contour [cells game]
  (reduce
    (fn [acc [x y]]
      (clojure.set/union
        acc
        (get-neighbours x y (constantly true) game)))
    #{}
    cells))

(defn get-empty-neighbours [x y game]
  (let [cells (get-empty-cells #{[x y]} x y game)
        cells-w-contour (get-contour cells game)]
    (vec cells-w-contour)))
