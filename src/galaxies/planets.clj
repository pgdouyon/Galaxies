;This file will store the logic for defining, creating, and manipulating planet data structures.
(ns galaxies.planets
  (:refer-clojure)
  (:use [clojure.string :only [split]])
  (:require [clojure.java.io :as io]))

; ############################## Constants #############################
(def ^:private ^:const GROWTH-RATE 1.1)


; ############################ Global State! ###########################
(def ^:private planet-map (atom (hash-map)))

(defn get-planets []
  (vals @planet-map))

(defn- update-planets! [planets]
  (swap! planet-map (partial merge-with merge) planets))


; ########################## Utility Functions #########################
(defn fmap
  "Maps a function onto all the vals of a map.  A functor for the map coll."
  [m f]
  (into {} (for [[k v] m] [k (f v)])))

; ########################### Planet Creation ###########################
(defn- precision-rand 
  "Return random num between 0 and 1 to n decimal places" 
  [n]
  (let [m (Math/pow 10 n)]
    (double
     (/ (rand-int m) m))))

(defn- gen-coord []
  (precision-rand 4))

(defn- planet
  "Given a name, returns a new planet."
  [name]
  {:name name
   :pop (rand-int 500)
   :widgets (rand-int 100)
   :mines {:Engineering 0
           :Physics 0}
   :elements {:Engineering 0
              :Physics 0}
   :schools {:Engineering 0
             :Physics 0}
   :workers {:Engineering 0
             :Physics 0}
   :xcoord (gen-coord)
   :ycoord (gen-coord)})

(defn- init-homebase [p pop widgets]
  (assoc-in p
    [:pop] pop
    [:widgets] widgets))

(defn- planet-id [p]
  (keyword (:name p)))

#_(defn- planet-coords [p]
  (str (:xcoord p) "," (:ycoord p)))


; ######################### Planet Manipulation ########################
(defn- elem-cost [amount]
  (* amount 1000) 

(defn- widget-cost [amount]
  (* amount 100))

(defn- builder [target source costf]
  (fn [p amt]
    (let [cost (costf amt)]
      (-> p
        (update-in target + amt)
        (update-in source - cost)))))

(defn- build-mines [p amt field]
  (let [target [:mines field]
        source [:widgets]
        build (builder target source widget-cost)]
    (build p amt)))

(defn- build-schools [p amt field]
  (let [target [:schools field]
        source [:elements field]
        build (builder target source elem-cost)]
    (build p amt)))


; ######################### Simulation Methods #########################
(defn- element-yield [p field]
  (let [mine-count (get-in p [:mines field])]
    (* mine-count 5)) 

(defn- sim-pop [p]
  (update-in p [:pop] #(Math/round (* GROWTH-RATE %))))

(defn sim-elements [p]
  (for [field (keys (:elements p))
        :let [yield (element-yield p field)]]
    (update-in p [:elements field] + yield)))

; ############################# API Methods ############################
(defn simulate-year! []
  (let [sim (comp sim-elements sim-pop)]
      (swap! planet-map fmap sim)))


; ########################### Initialization ###########################
(defn- planet-names
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
  (let [names (take 3 (planet-names))
        planets (new-planet-map names)]
    (update-planets! planets)))


