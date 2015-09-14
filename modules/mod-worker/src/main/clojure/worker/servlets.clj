(ns worker.servlets)

(gen-class :name worker.uploader
           :extends javax.servlet.http.HttpServlet
           :impl-ns worker.uploader)

(gen-class :name worker.reloader
           :implements [javax.servlet.Filter]
           :impl-ns worker.reloader)
