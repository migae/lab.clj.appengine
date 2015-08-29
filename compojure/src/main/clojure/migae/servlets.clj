(ns migae.servlets)

(gen-class :name migae.echo
           :extends javax.servlet.http.HttpServlet
           ;; using default prefix "-"
           :impl-ns migae.echo)

(gen-class :name migae.math
           :extends javax.servlet.http.HttpServlet
           :prefix "math-"
           :impl-ns migae.math)
