(ns main.admin
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

(println "loading mod-main main.admin")
;
(defn the-docs []
  (swagger-docs
   "/admin/swagger.json"
   {:info {:title "Main Admin API"
           :description "Admin API for main backend"}
    :tags [{:name "widgets", :description "widget admin"}
           {:name "users", :description "users admin"}]}))

(defapi admin-api
  (swagger-ui "/admin"
              :swagger-docs "/admin/swagger.json")
  (the-docs)

  {:formats [:edn]}

  (context* "/admin" []
            :tags ["widgets"]

    (GET* "/widgets" []
          (log/trace "GET* widgets")
          (str (format "<h1>Widgets</h1>")
               "\n\n<a href='/'>blah blah</a>"))

    (route/not-found "<h1>Page not found</h1>"))

  (context* "/users" []
    :tags ["users"]

    (GET* "/registrants" []
         (str (format "<h1>REGISTRANTS</h1>")
              "\n\n<a href='/'>blah blah</a>"))

    (route/not-found "<h1>Page not found</h1>"))
  )

(servlet/defservice
  (-> #'admin-api
      wrap-params
      wrap-file-info
      ))


