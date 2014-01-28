(ns galaxies.key-controls
  (:use seesaw.keymap)
  (:require [galaxies [planets :as planets] [ui :as ui]]))

(defn- simulate-year [e]
  (planets/simulate-year!))

(defn- planet-search [e]
  (ui/planet-search))

(defn planet-map-keymap [frame]
  (mapkey frame "s" simulate-year)
  (mapkey frame "p" planet-search))
