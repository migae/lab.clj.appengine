# migae

Clojure on Google App Engine

# examples

* [ringless](ringless) - simple servlet using only java interop, no ring/compojure
* [ringish](ringish) -  servlet using [ring](https://github.com/ring-clojure/ring)
* [compojure](compojure) - [compojure](https://github.com/weavejester/compojure) on GAE example, two servlets
* [swagger](swagger) -
[compojure-api-examples](https://github.com/metosin/compojure-api-examples)
on GAE.  [compojure-api](https://github.com/metosin/compojure-api) is
a wrapper on [Swagger](http://swagger.io/)

# quasi-repl

GAE is basically a servlet container.  Due to security constraints,
GAE prevents access to anything outside of the runtime context; for
the dev server under the `gradle` build system, this means
`<projroot>/build/exploded-app/`.  The `gradle` build system creates
this context dynamically, at compile time (`./gradlew clean` deletes
it).

This means that the Clojure runtime running in a GAE app can only load
files within that context; it cannot load from the standard
`<projroot>/src/main/clojure` path, for example.  Furthermore,
servlets must be AOT compiled, since the servlet container will look
for compiled bytecode on disk when it needs to load a servlet.

Fortunately, a sufficiently perverse mind can easily get around these
two problems.  To make changed source code available for reloading by
the Clojure runtime, we arrange to copy files from the source tree to
the runtime context whenever they are edited and saved.  To make sure
that AOT compilation does not interfere with code reloading, we split
servlet definitions from implementations.  And to make sure they get
reloaded we use the standard `ns-tracker` library in a Java Servlet
Filter that intercepts all HTTP requests and reloads changed code
before forwarding requests to handlers.

## servlet definition



``` java
(ns migae.servlets)

(gen-class :name migae.core
           :extends javax.servlet.http.HttpServlet
           :impl-ns migae.core)
```


## editing

Unfortunately, the technique described here only works for emacs.  But
it should be easy enough to adapt it.

**WARNING** If you are using emacs, you _must_ edit the paths in
  `.dir-locals.el` file in each subproject, and you must install the
  custom edit macro `migae.el`.

1.  edit .dir-locals.el and place it in <proj>/src (e.g. compojure/src/.dir-locals.el)
2.  put (migae.el) in your emacs load path and byte compile
    it (see comments in migae.el for installation instructions)
3.  make sure the `<filter-mapping>` stanza in `WEB-INF/web.xml` is enabled

Now whenever you edit a source file listed in `filter.clj`, migae.el
will copy it to `WEB-INF/classes`, and refreshing the webpage will run
the filter, which will reload the source file.  Note that you can
control reloading by changing the `<url-pattern>` of the filter mapping in
`web.xml`.

This is required because the GAE dev server will only look in
`build/exploded-app/` for files.  Since the `build/` hiearchy is
constructed dynamically at compile/run time, we cannot edit in place -
we have to copy from the `src` tree to the `build/exploded-app` tree.

To verify that everything is working, run the dev server for the
swagger subproject (`$ ./gradlew s:aRun`) and load `localhost:8080` in
your browser.  Then edit `core_impl.clj` and change something visible,
e.g. one of the `:description` values.  Then reload the webpage and
you should see the change (almost) immediately.

Try adding a route, e.g. within the `math` context:
```
    (GET* "/foo" []
          (ok "bar"))
```

Needless to say, before uploading to the GAE servers, you should
disable the filter and run `./gradlew clean` to remove the `.clj` files.

# running

We use the gradle build system.  From the root dir (not the project
subdir), run `$ ./gradlew tasks` to get a list of tasks.  The
subprojects are defined in `settings.gradle`.  Run project-specific
tasks like so: `$ ./gradlew :<proj>:<task>`.  For example, to run the
minimal project with the dev server, run:

```
$ ./gradlew :compojure:appengineRun
```

You can abbreviate both subprojects and tasks, so `$ ./gradlew :c:aRun` will work too.

