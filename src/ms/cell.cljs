(ns ms.cell
  (:require [clojure.core.match :refer [match]]
            [goog.string :as gstring]))

(def long-space (gstring/unescapeEntities "&nbsp;"))

(defn set-open [cell] (assoc cell :open? true))
(defn set-mine [cell] (assoc cell :mine? true))
(defn set-count [count cell] (assoc cell :count count))
(defn set-next-flag [cell] (update cell :flag? not))

(defn mine? [cell] (true? (:mine? cell)))
(defn open? [cell] (true? (:open? cell)))
(defn flag? [cell] (true? (:flag? cell)))
(defn close? [cell] (not (open? cell)))
(defn empty? [cell] (= 0 (:count cell)))
(defn coords [cell] [(:x cell) (:y cell)])
(defn key [cell] (str (:x cell) "-" (:y cell)))
(defn create [[x y]] { :x x :y y :open? false })
(defn get [[x y] field] (get-in field [y x]))
(defn update [[x y] field fn] (update-in field [y x] fn))
(defn open [coords field] (update coords field set-open))
(defn switch-flag [coords field] (update coords field set-next-flag))

(defn render [cell]
  (match cell
         { :open? false :flag? true } "!"
         { :open? true :mine? true } "X"
         { :open? true :count 0 } long-space
         { :open? true } (:count cell)
         :else long-space))
