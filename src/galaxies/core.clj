(ns galaxies.core
  (:gen-class)
  (:require [galaxies [planets :as planets]]))
;(:require [galaxies [planets :as planets] [ui :as ui]])

(defn print-planets []
  (println (planets/get-planets)))

(defn simulate-year []
  (planets/simulate-year!)
  (print-planets))

(defn get-input-cmd []
  (let [input (clojure.string/lower-case (read-line))]
    (condp = input
      "s" (simulate-year)
      "p" (print-planets)
      "q" true
      (println "Unknown command, please try again:"))))

(defn main-test []
  (planets/init!)
  (while (not (get-input-cmd))))








(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (planets/init!)
  (while (not (get-input-cmd))))

