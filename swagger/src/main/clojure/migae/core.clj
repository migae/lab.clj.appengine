(ns migae.core
  (:require [compojure.api.sweet :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :refer :all]
            [ring.util.servlet :as ring]
            [migae.domain :refer :all]
            [schema.core :as s]))

(println "reloading core")

(s/defschema Total {:total Long})

(defn the-docs []
  (swagger-docs
   {:info {:title "Sample Api (New!  Improved!  With Special GAE Enzymes!)"
           :description "<a href='https://github.com/metosin/compojure-api'>Compojure Api</a> sample GAE application"}
    :tags [{:name "math", :description "math with parameters"}
           {:name "echo", :description "request echoes"}
           {:name "pizza", :description "pizza Api it is."}]}))

(defapi core-api
  (swagger-ui)
  (the-docs)

  {:formats [:edn]}

  (context* "/echo" []
    :tags ["echo"]

    (GET* "/request" req
      (ok (dissoc req :body)))
    (GET* "/pizza" []
      :return NewSingleToppingPizza
      :query [pizza NewSingleToppingPizza]
      :summary "get echo of a pizza"
      (ok pizza))
    (PUT* "/anonymous" []
      :return [{:secret Boolean s/Keyword s/Any}]
      :body [body [{:secret Boolean s/Keyword s/Any}]]
      (ok body))
    (GET* "/hello" []
      :return String
      :query-params [name :- String]
      (ok (str "Hello, " name)))
    (POST* "/pizza" []
      :return NewSingleToppingPizza
      :body [pizza NewSingleToppingPizza]
      :summary "post echo of a pizza"
      (ok pizza)))

  (context* "/pizzas" []
    :tags ["pizza"]

    (GET* "/" []
      :return [Pizza]
      :summary "Gets all Pizzas"
      (ok (get-pizzas)))
    (POST* "/" []
      :return Pizza
      :body [pizza NewPizza {:description "new pizza"}]
      :summary "Adds a pizza"
      (ok (add! pizza)))
    (PUT* "/" []
      :return Pizza
      :body [pizza Pizza]
      :summary "Updates a pizza"
      (ok (update! pizza)))
    (GET* "/:id" []
      :return Pizza
      :path-params [id :- Long]
      :summary "Gets a pizza"
      (ok (get-pizza id)))
    (DELETE* "/:id" []
      :path-params [id :- Long]
      :summary "Deletes a Pizza"
      (ok (delete! id)))))

(ring/defservice
  (-> #'core-api
      wrap-params
      ))
