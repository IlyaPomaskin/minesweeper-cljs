(ns ms.game
  (:require [ms.cell :as cell]
            [ms.field :as field]))

(defn map-items [map-fn field] (mapv (fn [row] (mapv map-fn row)) field))

(defn finished? [game] (or (:win? game) (:loss? game)))

(defn generate-mines [x-size y-size count]
  (vec (repeatedly count (fn [] [(rand-int x-size) (rand-int y-size)]))))
(defn place-mines [mines field]
  (reduce
    (fn [acc [x y]] (cell/update x y acc cell/set-mine))
    field
    mines))
(defn get-mines-count [x y game]
  (count (field/get-neighbours x y cell/mine? game)))
(defn update-mines-count [field]
  (map-items
    (fn [cell]
      (let [x (:x cell)
            y (:y cell)
            count (get-mines-count x y field)]
        (cell/set-count count cell)))
    field))

(defn create [x-size y-size count]
  { :field (->> (field/generate x-size y-size)
                (place-mines (generate-mines x-size y-size count))
                update-mines-count)
    :win? false
    :loss? false })
(defn set-loss [game]
  (if (field/mine-opened? (:field game))
      (assoc game :loss? true)
      game))
(defn set-win [game]
  (if (not (field/has-moves? (:field game)))
      (assoc game :win? true)
      game))
(defn update-state [game]
  (->> game
       set-loss
       set-win))
(defn open-cells [cells game]
  (update-in
    game
    [:field]
    (fn [field]
      (reduce
        (fn [acc [x y]] (cell/open x y acc))
        field
        cells))))
(defn move [x y game]
  (let [field (:field game)
        cell (cell/get x y field)
        cells-for-open (if (or (cell/mine? cell)
                               (not (cell/empty? cell)))
                         [[x y]]
                         (field/get-empty-neighbours x y field))]
    (if (or (finished? game)
            (cell/flag? cell))
      game
      (->> game
           (open-cells cells-for-open)
           update-state))))
(defn flag [x y game]
  (update-in game [:field] #(cell/switch-flag x y %)))
