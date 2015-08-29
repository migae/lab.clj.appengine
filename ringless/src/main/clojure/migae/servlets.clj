(ns migae.servlets)

(gen-class :name migae.core
           :extends javax.servlet.http.HttpServlet
           :impl-ns migae.core
           :prefix "core-"
           :exposes-methods {doGet core-doGet})

(gen-class :name migae.reloader
           :implements [javax.servlet.Filter]
           :impl-ns migae.reloader)
