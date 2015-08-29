(ns migae.pizza-impl
  (:require [clojure.math.numeric-tower :as math]
            [compojure.core :refer :all]
            [compojure.response :as r]
            [compojure.route :as route]
            [ring.util.response :as rsp]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer [wrap-params]]))

;; (defroutes pizza-routes
;;   (context "/pizzas" []
;;     (GET "/" []
;;       :return [Pizza]
;;       :summary "Gets all Pizzas"
;;       (r/render (get-pizzas)))
;;     (POST "/" []
;;       :return Pizza
;;       :body [pizza NewPizza {:description "new pizza"}]
;;       :summary "Adds a pizza"
;;       (r/render (add! pizza)))
;;     (PUT "/" []
;;       :return Pizza
;;       :body [pizza Pizza]
;;       :summary "Updates a pizza"
;;       (r/render (update! pizza)))
;;     (GET "/:id" []
;;       :return Pizza
;;       :path-params [id :- Long]
;;       :summary "Gets a pizza"
;;       (r/render (get-pizza id)))
;;     (DELETE "/:id" []
;;       :path-params [id :- Long]
;;       :summary "Deletes a Pizza"
;;       (r/render (delete! id)))
;;     (route/not-found "<h1>Echo API not found</h1>")))

;; (def pizza-handler
;;   (-> (routes
;;        pizza-routes)
;;       (wrap-defaults api-defaults)
;;       ))
