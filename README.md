# migae

Clojure on Google App Engine

# examples

* [ringless](ringless) - simple servlet using only java interop, no ring/compojure
* ringish -  servlet using [ring](https://github.com/ring-clojure/ring)
* compojure - [compojure](https://github.com/weavejester/compojure) on GAE example, two servlets
* swagger -
[compojure-api-examples](https://github.com/metosin/compojure-api-examples)
on GAE.  [compojure-api](https://github.com/metosin/compojure-api) is
a wrapper on [Swagger](http://swagger.io/)

# quasi-repl



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

