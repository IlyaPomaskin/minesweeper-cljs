(ns ms.game
  (:require [ms.cell :as cell]
            [ms.field :as field]))

(defn map-items [map-fn field] (mapv (fn [row] (mapv map-fn row)) field))

(defn finished? [game] (or (:win? game) (:loss? game)))

(defn generate-mines [x-size y-size count]
  (vec (repeatedly count (fn [] [(rand-int x-size) (rand-int y-size)]))))
(defn place-mines [mines field]
  (reduce
    (fn [acc coords] (cell/update coords acc cell/set-mine))
    field
    mines))
(defn get-mines-count [coords field]
  (count (field/get-neighbours coords cell/mine? field)))
(defn update-mines-count [field]
  (map-items
    (fn [cell]
      (cell/set-count
        (get-mines-count (cell/coords cell) field)
        cell))
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
        (fn [acc coords] (cell/open coords acc))
        field
        cells))))
(defn move [coords game]
  (let [field (:field game)
        cell (cell/get coords field)
        cells-for-open (if (or (cell/mine? cell)
                               (not (cell/empty? cell)))
                         [coords]
                         (field/get-empty-neighbours coords field))]
    (if (or (finished? game)
            (cell/flag? cell))
      game
      (->> game
           (open-cells [coords])
           (open-cells cells-for-open)
           update-state))))
(defn flag [coords game]
  (if (finished? game)
    game
    (update-in game [:field] #(cell/switch-flag coords %))))
