;This file will store the logic for defining, creating, and manipulating planet data structures.
(ns src.galaxies.planets
  (:use [clojure.string :only [split]])
  (:require [clojure.java.io :as io]))

;===========================================================================
;Constants
;===========================================================================
(def ^:private ^:const growth-rate 1.1)


;===========================================================================
;Global State!
;===========================================================================
(def ^:private planet-map (atom (hash-map)))

(defn get-planets []
  (vals @planet-map))

(defn- update-planets! [planets]
  (swap! planet-map (partial merge-with merge) planets))


;===========================================================================
;Planet methods
;===========================================================================
(defn- precision-rand [n]
  (let [m (Math/pow 10 n)]
    (double
     (/ (rand-int m) m))))

(defn- gen-coord []
  (precision-rand 4))

(defn- planet
  "Given a name, returns a new planet."
  [name]
  {:name name
   :pop 0
   :xcoord (gen-coord)
   :ycoord (gen-coord)})

(defn- planet-coords [p]
  (str (:xcoord p) "," (:ycoord p)))

(defn- planet-id [p]
  (keyword (planet-coords p)))

(defn- alter-pop [p delta]
  (update-in p [:pop] + delta))

(defn- grow-pop [p]
  (update-in p [:pop] * growth-rate))

;===========================================================================
;Initialization
;===========================================================================
(defn- planet-names!
  "Returns, as a vector, a random permutation of the names in the planet_names.txt file.
A random number of names is returned varying from [min-planets, min-planets + variance-num-planets]"
  []
  (shuffle
   (split (slurp (io/resource "planet_names.txt")) #"\n")))

(defn- new-planet-map
  "Returns a hash-map of Planets with the names given as argument, random coordinates, and populations initialized to 0."
  [names]
  (let [planets (map planet names)]
    (into (hash-map)
          (map (juxt planet-id identity) planets))))
  
(defn init! []
  (let [names (planet-names!)
        planets (new-planet-map names)]
    (update-planets! planets)))


(defn simulate-year []
  (swap! planet-map (partial map grow-pop)))
