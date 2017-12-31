(ns ms.main
  (:require [rum.core :as rum]
            [ms.game :as game]
            [ms.cell :as cell]
            [clojure.core.match :refer [match]]))

(enable-console-print!)

(defn new-game [] (game/create 10 10 10))

(def GAME (atom (new-game)))
(defn update-game [fn] (swap! GAME fn))

(defn new-game-click []
  (update-game #(new-game)))
(defn cell-click [cell]
  (update-game #(game/move (:x cell) (:y cell) %)))
(defn right-cell-click [cell]
  (update-game #(game/flag (:x cell) (:y cell) %)))

(defn cell-key [cell] (str (:x cell) "_" (:y cell)))
(rum/defc field-cell < { :key-fn cell-key }
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
(rum/defc field-row < { :key-fn (fn [index row] (str index)) }
  [index row]
  [:div (mapv field-cell row)])
(rum/defc field [field]
  [:div (map-indexed field-row field)])

(rum/defc state [game]
  [:div
   [:br]
   (if (:win? game) "Win!")
   (if (:loss? game) "Game over!")])

(rum/defc app [game]
  [:div { :class "app" }
   [[:div { :key "game" } (field (:field game))]
    [:div { :key "state" } (state game)]
    [:br { :key "br" }]
    [:button { :key "new-game"
               :on-click #(new-game-click) } "New game"]]])

(rum/defc app-wrapper < rum/reactive
  [] (app (rum/react GAME)))

(rum/mount (app-wrapper) (js/document.querySelector "#root"))
