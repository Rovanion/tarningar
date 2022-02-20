(ns ^:figwheel-hooks tarningar.core
  (:require
   [goog.dom     :as gdom]
   [reagent.core :as r  :refer [atom]]
   [reagent.dom  :as rd]))

(defonce state (atom {:tärningar       []
                      :antal-tärningar 1}))

(defn kasta-d6 []
  (+ (rand-int 6) 1))

(defn kasta-d6-obegränsad
  [antal-tärningar]
  (let [kast (repeatedly antal-tärningar kasta-d6)
        antal-sexor (count (filter #(= % 6) kast))
        utan-sexor (filter #(not= % 6) kast)]
    (if (= antal-sexor 0)
      kast
      (into utan-sexor (kasta-d6-obegränsad (* antal-sexor 2))))))

(defn kastknappstryck
  [knapp]
  (swap! state #(assoc % :tärningar (kasta-d6-obegränsad (:antal-tärningar @state)))))

(defn num->tärningstecken
  [number]
  (case number
    1 \⚂
    2 \⚁
    3 \⚂
    4 \⚃
    5 \⚄
    6 \⚅))

(defn some-component []
  [:div
   [:header
    [:h1
     [:button {:on-click #(swap! state update :antal-tärningar dec)} "-"]
     " " (:antal-tärningar @state) " "
     [:button {:on-click #(swap! state update :antal-tärningar inc)} "+"]]
    [:button {:on-click kastknappstryck}
     "Kasta tärningar"]]
   [:div.dices
    (for [[index tärning] (map-indexed list (:tärningar @state))]
      [:span.dice {:key index} (num->tärningstecken tärning)])]])

(defn mount []
  (rd/render [some-component]
             (.-body js/document)))

(mount)

(defn ^:after-load on-reload []
  (mount))
