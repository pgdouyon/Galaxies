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
   :widgets 0
   :mines {:Technium 0
           :Scienine 0}
   :elements {:Technium 0
              :Scientine 0}
   :schools {:Engineering 0
             :Physics 0}
   :workers {:Engineers 0
             :Physicists 0}
   :xcoord (gen-coord)
   :ycoord (gen-coord)})

(defn- homebase [p pop widgets]
  (assoc p
    :pop pop
    :widgets widgets))

(defn- widget-cost [amount]
  (* amount 100))

(defn- element-cost [amount]
  (* amount 1000))

#_(defn- planet-coords [p]
  (str (:xcoord p) "," (:ycoord p)))

(defn- planet-id [p]
  (keyword (:name p)))


;===========================================================================
;Update Methods
;===========================================================================
(defn- alter-attrib [p delta keys]
  (update-in p keys + delta))

(defmacro build-in 
  "Anaphoric macro that captures the 'p' and 'amount' arguments from the calling function.
Used to build new objects by updating the appropriate attributes in the planet object (charging the source
and growing the target)."
  [target source cost-func]
  `(let [cost# (- (#'~cost-func ~'amount))]
     (-> ~'p
       (alter-attrib ~'amount ~target)
       (alter-attrib cost# ~source))))

(defn- build-mines [p amount field]
  (build-in [:mines field] [:widgets] widget-cost))

(defn- build-schools [p amount field]
  (build-in [:schools field] [:elements field] element-cost))


;===========================================================================
;Simulation Methods
;===========================================================================
(defn- grow-attrib [p rate keys]
  (update-in p keys * rate))

(defn- grow-pop [p]
  (grow-attrib p growth-rate [:pop]))

;===========================================================================
;API Methods
;===========================================================================
(defn simulate-year []
  (swap! planet-map (partial map grow-pop)))


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


