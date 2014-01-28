(defproject galaxies "0.1.0-SNAPSHOT"
  :description "A remake of the popular Stars! game written in Clojure"
  :url "https://github.com/pgdouyon/Galaxies"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [seesaw "1.4.4"]]
  :resource-paths ["resources"]
  :main ^:skip-aot galaxies.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
