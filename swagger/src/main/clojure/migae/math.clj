(ns migae.math
  (:require [compojure.api.sweet :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :refer :all]
            [ring.util.servlet :as ring]
            [migae.domain :refer :all]
            [schema.core :as s]))

(println "reloading math")

(s/defschema Total {:total Long})

(defn the-docs []
  (swagger-docs "/math/swagger.json"
   {:info {:title "Sample Api (New!  Improved!  With Special GAE Enzymes!)"
           :description "<a href='https://github.com/metosin/compojure-api'>Compojure Api</a> sample GAE application"}
    :tags [{:name "math", :description "math with parameters"}
           {:name "echo", :description "request echoes"}
           {:name "pizza", :description "pizza Api it is."}]}))

(defapi math-api
  (swagger-ui "/math" :swagger-docs "/math/swagger.json")
  (the-docs)

  {:formats [:edn]}

  (context* "/math" []
    :tags ["math"]
    (GET* "/plus" []
      :return Total
      :query-params [x :- Long, y :- Long]
      :summary "x+y with query-parameters"
      (ok {:total (+ x y)}))
    (POST* "/minus" []
      :return Total
      :body-params [x :- Long, y :- Long]
      :summary "x-y with body-parameters"
      (ok {:total (- x y)}))
    (GET* "/times/:x/:y" []
      :return Total
      :path-params [x :- Long, y :- Long]
      :summary "x*y with path-parameters"
      (ok {:total (* x y)}))
    (GET* "/power" []
      :return Total
      :header-params [x :- Long, y :- Long]
      :summary "x^y with header-parameters"
      (ok {:total (long (Math/pow x y))}))))

(ring/defservice
  (-> #'math-api
      wrap-params
      ))
