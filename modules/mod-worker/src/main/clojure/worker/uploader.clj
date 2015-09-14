(ns worker.uploader
  (:import [java.lang IllegalArgumentException RuntimeException]
           [java.io InputStream ByteArrayInputStream]
           [org.apache.commons.io IOUtils]
           [org.apache.commons.fileupload.util Streams]
           [org.apache.commons.fileupload UploadContext
            FileItemIterator
            FileItemStream
            FileUpload])
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log :only [trace debug info]]
            [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [ring.handler.dump :refer :all] ; ring-devel
            [ring.middleware.edn :as redn]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.multipart-params :refer :all]
            [ring.middleware.multipart-params.byte-array :refer [byte-array-store]]
            [ring.util.http-response :refer :all]
            [ring.util.response :as r]
            [ring.util.servlet :as servlet]
            [hiccup.core]
            [hiccup.page :refer [html5]]
            ))

;; (:require [clojure.core :refer :all]
;;           [clojure.string :as str]
;;           [clojure.java.io :as io]
;;           [clojure.tools.reader.edn :as edn]
;;           [compojure.core :refer :all]
;;           [compojure.route :as route]
;;           [ring.handler.dump :as dump]
;;           [ring.util.codec :as codec]
;;           [ring.util.response :as r]
;;           [ring.util.servlet :as servlet]
;;           [ring.util.request :as req] ; DEBUG
;;           [ring.util.codec :refer [assoc-conj]] ; DEBUG
;;           [migae.datastore :as ds]
;;           [migae.mail :as mail]
;;           [cheshire.core :as json]
;;           [clojure.tools.logging :as log :refer [debug info trace]]
;;           [clojure.tools.reader.edn :refer [read read-string]]
;;           [ring.middleware.params :refer [wrap-params]]
;;           [ring.middleware.file-info :refer [wrap-file-info]]

(clojure.core/println "loading mod-worker worker.uploader")

(defn home-page []
  (html5 [:form {:action "/upload" :method "post" :enctype "multipart/form-data"}
          [:input {:name "file" :type "file" :size "20"}]
          [:input {:type "submit" :name "submit" :value "submit"}]]))

(defn stream [s]
  (java.io.PushbackReader.
   (java.io.InputStreamReader.
    (ByteArrayInputStream. s)
    "UTF-8")))

(defn read-stream [s]
  (clojure.edn/read
   {:eof :theend}
   s))

(defn the-docs []
  (swagger-docs
   "/worker/swagger.json"
   {:info {:title "Migae Worker Module Uploader API"
           :description "Uploader API for worker backend"}
    :tags [{:name "upload", :description "upload"}
           {:name "users", :description "user admin"}]}))

(defapi worker-api

  (swagger-ui "/worker"
              :swagger-docs "/worker/swagger.json")
  (the-docs)

  {:formats [:edn]}

  (context* "/work" []
            :tags ["worker"]

   (POST* "/upload" {mp :multipart-params :as req}
      :return String
      ;; :body [edn]  ;; free-form edn
      :summary "upload a file"
          (let [{:keys [filename content-type bytes]} (get mp "file")
                s (stream bytes)]
            (let [edn-seq (repeatedly (partial read-stream s))]
              (dorun (map
                      #(do (if (meta %)
                             (print (str "^" (meta %))))
                           (println %)) ;; FIXME: write record to datastore
                      (take-while (partial not= :theend) edn-seq))))
            (str "uploaded file " filename \newline)
            ))

   (GET* "/:nm" [nm :as uri]
        (log/trace "GOT WORKER!! at " uri)
        (-> (r/response (pr-str (str "Hi, " nm "y")))
            (r/status 200)))

   ;; (rfn {m :request-method, u :uri, ctx :context,  pi :path-info :as req}
   ;;      ;; (dump/handle-dump req))
   ;;      (str "UNCAUGHT REQUEST:" \newline))

   (route/not-found "URL not found")))

(servlet/defservice
  (-> (routes
       worker-api)
      wrap-params
      redn/wrap-edn-params
      (wrap-multipart-params {:store (byte-array-store)})
      ))
