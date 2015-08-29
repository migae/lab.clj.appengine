(ns migae.echo
  (:require [clojure.math.numeric-tower :as math]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as rsp]
            [ring.util.servlet :as ring]
            [ring.middleware.defaults :refer :all]))

(defroutes echo-routes
  (context "/echo" []
    (GET "/request/body" req
         (do (println "request")
             (-> (rsp/response (str "<p>" (dissoc req :body) "</p>"))
                 (rsp/content-type "text/html"))))
    (GET "/hello/:name" [name]
         (-> (rsp/response (str "Hello, " name))
             (rsp/content-type "text/html")))
    (route/not-found "<h1>Echo API not found</h1>")))

(ring/defservice
  (-> (routes
       echo-routes)
      (wrap-defaults api-defaults)
      ))

;; or:
;; (def echo-handler
;;   (-> (routes
;;        echo-routes)
;;       (wrap-defaults api-defaults)
;;       ))
;; (ring/defservice echo-handler)
;; explicit equivalent of defservice:
;; (defn -service
;;   [this rqst resp]
;;     (let [request-map  (ring/build-request-map rqst)
;;     response-map (echo-handler request-map)]
;;     (when response-map
;;     (ring/update-servlet-response resp response-map))))
