(ns src.galaxies.key-controls
  (:use seesaw.keymap)
  (:require [src.galaxies [planets :as planets] [ui :as ui]]))

(defn- simulate-year [e]
  (planets/simulate-year))

(defn planet-map-keymap [f]
  (mapkey f "s" simulate-year))
