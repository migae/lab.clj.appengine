(ns migae.servlets)

(gen-class :name migae.core
           :extends javax.servlet.http.HttpServlet
           :impl-ns migae.core)

(gen-class :name migae.math
           :extends javax.servlet.http.HttpServlet
           :impl-ns migae.math)

(gen-class :name migae.reloader
           :implements [javax.servlet.Filter]
           :impl-ns migae.reloader)

