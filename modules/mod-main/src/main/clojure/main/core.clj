(ns main.core
  ;; (:import [com.google.appengine.api.datastore
  ;;           DatastoreService
  ;;           FetchOptions$Builder
  ;;           Query
  ;;           Query$Filter])
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log :only [trace debug info]]
            [compojure.api.sweet :refer :all]
            ;; [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.handler.dump :refer :all] ; ring-devel
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.util.http-response :refer :all]
            [ring.util.response :as r]
            [ring.util.servlet :as servlet]
            ))

(println "loading main.core")

(defn the-docs []
  (swagger-docs
   "/core/swagger.json"
   {:info {:title "Main Core API"
           :description "Core API for main backend"}
    :tags [{:name "foo", :description "core foo stuff"}
           {:name "frob", :description "core frobnication"}]}))

(defapi core-api
  (swagger-ui "/core"
              :swagger-docs "/core/swagger.json")
  (the-docs)

  {:formats [:edn]}

  (context* "/foo" []
    :tags ["foo"]

    (GET* "/bar" []
         (str (format "<h1>FOO BAR</h1>")
              "\n\n<a href='/'>blah blah</a>"))

    (GET* "/baz" []
         (str (format "<h1>FOO BAZ</h1>")
              "\n\n<a href='/'>blah blah</a>"))

    (route/not-found "<h1>Page not found</h1>"))

  (context* "/frob" []
    :tags ["frob"]

    (GET* "/nicate" []
         (str (format "<h1>Frobnication!</h1>")
              "\n\n<a href='/'>blah blah</a>"))

    (route/not-found "<h1>Page not found</h1>"))
  )

(servlet/defservice
  (-> #'core-api
      wrap-params
      wrap-file-info
      ))


