;;; Directory Local Variables
;;; See Info node `(emacs) Directory Variables' for more information.

;; WARNING!  Edit these paths so they are correct for your project and system!

((clojure-mode
  (app . "sparky")
  (cljsrc . "/Users/gar/migae/migae/compojure/src/main/clojure")
  ;; gradle:
  (cljdest . "/Users/gar/migae/migae/compojure/build/exploded-app/WEB-INF"))
 ;; maven: use this one:
 ;; (warroot . "/Users/gar/norc.dev/sparky/gae/target/sparky-1.0-SNAPSHOT/WEB-INF"))

 (js-mode
  (app . "sparky")
  (jssrc . "/Users/gar/migae/migae/compojure/src/main/webapp/js")
  (jsdest . "/Users/gar/migae/migae/compojure/build/exploded-app/js"))

 (nxml-mode
  (app . "sparky")
  (nxmlsrc . "/Users/gar/migae/migae/compojure/src/main/webapp")
  (nxmldest . "/Users/gar/migae/migae/compojure/build/exploded-app"))

 (html-mode
  (app . "sparky")
  (htmlsrc . "/Users/gar/migae/migae/compojure/src/main/webapp/qx")
  (htmldest . "/Users/gar/migae/migae/compojure/build/exploded-app/qx")))


