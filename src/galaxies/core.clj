(ns galaxies.core
  (:gen-class)
  (:require [galaxies [planets :as planets] [ui :as ui]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (planets/init!)
  (ui/init))
