(ns migae.core)

(defn core-doGet [this req rsp]
  (let [path (.getRequestURI req)
        r (.getWriter rsp)]
    (cond
      (.matches path "^/hello/.+")
      (do
        (.setContentType rsp "text/html")
        (.println r "<h1>Hello GAE, from the core servlet path regex '/hello/.+'</h1>")
        )
      (.matches path "^/dump$")
      (do
        (.setContentType rsp "text/html")
        (.println r "<h1>Hello GAE, from the core servlet path '/dump'.</h1>")
        (.println r "<ul>")
        (.println r (str "<li>URL: " (.getRequestURL req) "</li>"))
        (.println r (str "<li>URI: " (.getRequestURI req) "</li>"))
        (.println r (str "<li>servlet path: " (.getServletPath req) "</li>"))
        (.println r (str "<li>path info: " (.getPathInfo req) "</li>"))
        (.println r (str "<li>path translated: " (.getPathTranslated req) "</li>"))
        (.println r "</ul>"))
      :else
      (do
        (.setContentType rsp "text/html")
        (.println r (str "<h1>Hello GAE, the default handler, path: '" path "'.</h1>"))))
))
