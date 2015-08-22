(ns migae.core-impl
  (:require [clojure.math.numeric-tower :as math]
            [compojure.core :refer :all]
            [compojure.response :as r]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer [wrap-params]]))

(defroutes math-routes
  (context
   "/math" []
   (GET "/plus" {params :query-params}  ; query string params
        (let [x (read-string (get params "x"))
              y (read-string (get params "y"))]
          (str (+ x y) "\n")))
   (POST "/minus" [x y :as req]        ; body params
         (str (- (read-string x) (read-string y)) "\n"))
   (GET "/times/:x/:y" [x y]           ; named (path) params
        (str (* (read-string x) (read-string y)) "\n"))
   (GET "/power" {:keys [headers] :as req} ; header params
        (let [x (read-string (get headers "x-x"))
              y (read-string (get headers "x-y"))]
          (str (math/expt x y) "\n")))
    (route/not-found "<h1>Math API not found</h1>")))

(defroutes echo-routes
  (context "/echo" []
    (GET "/request" req
         (dissoc req :body))
    ;; (GET "/pizza" []
    ;;   ;; :return NewSingleToppingPizza
    ;;   ;; :query [pizza NewSingleToppingPizza]
    ;;   ;; :summary "get echo of a pizza"
    ;;      ;; (r/render pizza))
    ;;   (r/render "pizza"))
    ;; (PUT "/anonymous" []
    ;;   ;; :return [{:secret Boolean s/Keyword s/Any}]
    ;;   ;; :body [body [{:secret Boolean s/Keyword s/Any}]]
    ;;   ;; (r/render body))
    ;;   (r/render "body"))
    ;; (GET "/hello/:name" [name]
    ;;   ;; :return String
    ;;   ;; :query-params [name :- String]
    ;;   (r/render (str "Hello, " name)))
    ;; (POST "/pizza" []
    ;;   ;; :return NewSingleToppingPizza
    ;;   ;; :body [pizza NewSingleToppingPizza]
    ;;   ;; :summary "post echo of a pizza"
    ;;   ;; (r/render pizza))
    ;;   (r/render "pizza"))
    (route/not-found "<h1>Echo API not found</h1>")))

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

(def core-handler
  (-> (routes
       math-routes
       echo-routes)
       ;; pizza-routes)
      (wrap-defaults api-defaults)
      ))
