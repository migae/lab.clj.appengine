(ns migae.servlets)

(gen-class :name migae.echo
           :extends javax.servlet.http.HttpServlet
           :impl-ns migae.echo)

(gen-class :name migae.math
           :extends javax.servlet.http.HttpServlet
           :prefix "math-"              ; just as demo
           :impl-ns migae.math)

(gen-class :name migae.reloader
           :implements [javax.servlet.Filter]
           :impl-ns migae.reloader)
