(ns ms.ui
  (:require [rum.core :as rum]
            [clojure.core.match :refer [match]]
            [ms.game :as game]
            [ms.cell :as cell]))

(def GAME (atom {}))
(defn update [fn] (swap! GAME fn))

(defn new-game-click
  []
  (update #(game/create 10 10 10)))
(defn cell-click
  [cell]
  (update #(game/move (cell/coords cell) %)))
(defn right-cell-click
  [cell]
  (update #(game/flag (cell/coords cell) %)))

(rum/defc field-cell < rum/static { :key-fn cell/key }
  [cell]
  [:button
   { :on-click #(cell-click cell)
     :on-context-menu (fn [e] (do
                                (.preventDefault e)
                                (right-cell-click cell)))
     :class (match cell
                   { :open? true :mine? true } "open mine"
                   { :open? true } "open"
                   :else "close") }
   (cell/render cell)])
(rum/defc field-row < rum/static { :key-fn (fn [index row] (str index)) }
  [index row]
  [:div (mapv field-cell row)])
(rum/defc field < rum/static
  [field]
  [:div (map-indexed field-row field)])

(rum/defc state < rum/static
  [game]
  [:div
   [:br]
   (if (:win? game) "Win!")
   (if (:loss? game) "Game over!")])

(rum/defc app < rum/static
  [game]
  [:div { :class "app" }
   [[:div { :key "game" } (field (:field game))]
    [:div { :key "state" } (state game)]
    [:br { :key "br" }]
    [:button { :key "new-game"
               :on-click #(new-game-click) } "New game"]]])

(rum/defc app-wrapper < rum/static rum/reactive
  []
  (app (rum/react GAME)))
