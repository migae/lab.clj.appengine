# ringless

Simple, minimal example of Clojure on GAE using only Java interop
facilities.  You don't want to do this in Real Life; this demo is here
only for pedagogical purposes, to help shed light on the way Ring and
Compojure work in the Servlet context.

To run, from the project root directory execute:

```
$ ./gradlew :ringless:aRun
```

The load `localhost:8080` in your browser.  Click on one of the links.
Edit `core.clj` to make a change, then reload.

If you don't see the change reflected in the browser output, make sure
the changed file has been copied to the servlet context.  In this
case, if you have changed

```
migae/ringless/src/main/clojure/migae/core.clj
```

then it should match:

```
migae/ringless/build/exploded-app/WEB-INF/classes/migae/core.clj
```

If you are using the Emacs hack described in the main README and you
don't see `core.clj` in the right place, make sure you have edited
`migae/ringless/src/.dir-locals.el` so it has the correct paths.

If you are not using the Emacs hack, you'll need to make arrangements
to do something similar.

Also, for reloading to work the `<filter-mapping>` stanza in

```
migae/ringless/src/main/webapp/WEB-INF/web.xml
```

must be correct.
