(ns migae.servlets)

(gen-class :name migae.core
           :extends javax.servlet.http.HttpServlet
           :impl-ns migae.core
           :prefix "core-"
           :exposes-methods {doGet core-doGet})

;; (gen-class :name migae.math
;;            :extends javax.servlet.http.HttpServlet
;;            :impl-ns migae.math-impl
;;            :prefix "math-"
;;            :exposes-methods {doGet math-doGet})
