(ns migae.core
  (:require [ring.util.response :refer :all]
            [ring.util.servlet :as ring]
            [ring.handler.dump :refer :all]       ; testing only?
            [ring.middleware.reload :refer :all]  ; testing only! from ring-devel
            [ring.middleware.params :refer [wrap-params]] ; in ring-core
            [ring.middleware.defaults :refer :all])) ; in ring-defaults

;; (println "reloading")

(defn index-page []
  (str "<!DOCTYPE html>"
       "<html><body>"
       "<p>This is a dynamically served index.html page, produced by the core servlet.</p>"
       "<ul>"
       "<li><a href='/dump'>/dump</a></li>"
       "<li><a href='/hi/bob'>/hi/:name</a></li>"
       "<li><a href='/bye/bob'>/bye/:name</a></li>"
       "</ul>"
       "</body></html>"))

(defn handler [request]
  (let [path (:uri request)
        root (re-matches #"^/$" path)
        index (re-matches #"^/index.html$" path)
        dump (re-matches #"^/dump$" path)
        hi (nth (re-matches #"/hi/(.+)" path) 1)
        bye (nth (re-matches #"/bye/(.+)" path) 1)]
    (cond
      dump
      (handle-dump request)
      root
      (-> (response (index-page))
          (content-type "text/html"))
      index
      (-> (response (index-page))
          (content-type "text/html"))
      hi
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str "Hello, " hi)}
      bye
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str "Goodby, " bye)}
      :else
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body "Hello Default"})))

(ring/defservice
  (-> handler
      (wrap-reload {:dirs ["./"]}) ; testing only!
      ;; (wrap-defaults api-defaults)
      ;; wrap-params
      ))
