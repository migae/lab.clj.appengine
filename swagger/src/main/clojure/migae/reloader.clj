(ns migae.reloader
  (:import (javax.servlet Filter FilterChain FilterConfig
                          ServletRequest ServletResponse))
  (:require [ns-tracker.core :refer :all]))

(println "ring reloading reloader")

(defn -init [^Filter this ^FilterConfig cfg])

(defn -destroy [^Filter this])

(def modified-namespaces (ns-tracker ["./"]))

(defn -doFilter
  [^Filter this
   ^ServletRequest rqst
   ^ServletResponse resp
   ^FilterChain chain]
  (doseq [ns-sym (modified-namespaces)]
    (require ns-sym :reload))
    (.doFilter chain rqst resp))
