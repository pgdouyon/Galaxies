;This file will store the logic for defining, creating, and manipulating planet data structures.
(ns galaxies.planets
  (:refer-clojure)
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
;Utility Functions
;===========================================================================
(defn fmap
  "Maps a function onto all the vals of a map.  A functor for the map coll."
  [m f]
  (into {} (for [[k v] m] [k (f v)])))

;===========================================================================
;Planet Methods
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

(defn- homebase [p pop widgets]
  (assoc p
    :pop pop
    :widgets widgets))

(defn- planet-id [p]
  (keyword (:name p)))

(defn- widget-cost [amount]
  (* amount 100))

(defn- element-cost [amount]
  (* amount 1000))

(defn- elem-yield [p field]
  (let [mine-count (get-in p [:mines field])]
    (* mine-count 5)))

#_(defn- planet-coords [p]
  (str (:xcoord p) "," (:ycoord p)))


;===========================================================================
;Update Methods
;===========================================================================
(defn- alter-attrib [p keys delta]
  (update-in p keys + delta))

(defn- buildf [target src costf]
  (fn [p amt]
    (let [cost (- (costf amt))]
      (-> p
        (alter-attrib target amt)
        (alter-attrib src cost)))))

(defn- build-mines [p amt field]
  (let [build (buildf [:mines field] [:widgets] widget-cost)]
    (build p amt)))

(defn- build-schools [p amt field]
  (let [build (buildf [:schools field] [:elements field] element-cost)]
    (build p amt)))

;===========================================================================
;Simulation Methods
;===========================================================================
(defn- grow-attrib [p keys rate]
  (update-in p keys * rate))

(defn- grow-pop [p]
  (grow-attrib p [:pop] growth-rate))

(defn grow-elem [yieldf [elem amt]]
  (let [yield (yieldf elem)]
    [elem (+ amt yield)]))

(defn grow-elems [p]
  (let [yieldf (partial elem-yield p)
        growf (partial grow-elem yieldf)
        new-elems (into {} (map growf (:elements p)))]
    (assoc-in p [:elements] new-elems)))


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


