(defproject minesweeper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [lein-figwheel "0.5.14"]
                 [rum "0.10.8"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.14"]]
  :main ms.main
  :source-paths ["src"]
  :cljsbuild { :builds [{ :id "dev"
                          :source-paths ["src"]
                          :figwheel true
                          :compiler { :main "ms.main"
                                      :asset-path "js/out"
                                      :output-to "resources/public/js/main.js"
                                      :output-dir "resources/public/js/out"
                                      :optimizations :none
                                      :verbose true
                                      :pretty-print true
                                      :source-map true }}
                        { :id "prod"
                          :source-paths ["src"]
                          :compiler { :main "ms.main"
                                      :asset-path "js/out"
                                      :output-to "js/main.js"
                                      :output-dir "js/out"
                                      :optimizations :advanced
                                      :pretty-print false }}]})

