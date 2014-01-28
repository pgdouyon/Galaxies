(ns galaxies.ui
  (:use [seesaw core mig color graphics])
  (:require [galaxies [planets :as planets] [key-controls :as kc]]))


;===========================================================================
;Paint methods
;===========================================================================
(def planet-radius
  (constantly 5))

(defn- paint-planet [context graphic planet]
  (let [w (.getWidth context)
        h (.getHeight context)
        x (* w (:xcoord planet))
        y (* h (:ycoord planet))]
    (draw graphic
          (circle x y (planet-radius))
          (style :background :green))))

(defn- paint-map [c g]
  (doseq [p (planets/get-planets)]
    (paint-planet c g p)))


;===========================================================================
;Dialogs
;===========================================================================
(defn planet-search "doc-string" []
  ())
;===========================================================================
;UI panels
;===========================================================================
(def ^:private galaxy-map (canvas :id :galaxy-map
                                  :background :black
                                  :paint paint-map))

(def ^:private main-panel (border-panel :center galaxy-map
                                        :id :main-panel))

#_(def ^:private main-panel (mig-panel :constraints ["fill, wrap 3", "[grow][grow]"]
                           :items [[action-panel "span 2 3"]
                                   [map-panel "span 3 3"]
                                   [message-panel "span 2 2"]
                                   [info-panel "span 3 2"]]))

(def gui-main (frame :title "Galaxies!"
                     :content main-panel
                     :minimum-size [800 :by 600]
                     ))

(defn init []
  (-> gui-main pack! show!)
  (kc/planet-map-keymap gui-main))
