# migae

Clojure on Google App Engine

This project describes a technique for using Clojure with the Google
App Engine.  It's a technique rather than a library; you don't need
any specialized code just to get running on the GAE, you just need to
know how to structure your code for a Servlet environment and
configure your development environment.  You still get all the groovy
Clojure stuff like Ring and Compojure.

Of course, if you want to use AppEngine services, you'll need a
wrapper on the Google libraries, unless you want to use Java interop
facilities to work with the Google libs directly.  Some libraries are
under (slow) development - the
[Datastore](https://github.com/migae/datastore) lib is furthest along,
and stable enough to at least play with.  A demo will be added to this
project, soon, I hope.

### previous approaches

This project originated as an attempt to improve
[appengine-magic](https://github.com/gcv/appengine-magic), but
eventually I decided to take an entirely different approach.  A major
reason for this is just that Google switched to a `gradle`-based build
a while back, which makes it possible to get a quasi-repl going
without going through any of the contortions appengine-magic was
forced into working with the Ant-based build structure.  I also wanted
to break out each service library into an independent library, and
develop my code on the dev server only.  Also, from the looks of
things the Appengine-magic project has been moribund for quite some
time and it's doesn't seem likely that the original developer will be
getting back to it any time soon.

Most importantly, appengine-magic implements a Jetty-based repl, so
you're not using the official Google dev server if you use that.
Before deploying, you'll have to test everything using the dev server,
which means you'll have to bounce the server to make changes, which is
_very_ cumbersome.  Even if you can tolerate that, given that GAE runs
on a very specialized server (emulated more-or-less faithfully in the
dev server), it seems like a dubious idea to develop code on in some
other environment and then migrate it to the dev server environment.

(Having said all that, I'm grateful to the developer of AEMagic, who
deserves a lot of credit for putting it together and making it
available; I learned a lot from studying that code, and I'm using some
of it to develop migae service libraries.)

You can find various blog posts on the web showing how to get started
with Clojure-on-GAE, but most of the ones I've found are woefully
outdated.  A relatively recent one is at
[Clojure in the cloud. Part 1: Google App Engine](http://flowa.fi/blog/2014/04/25/clojure-gae-howto.html?lang=en).
Unfortunately, the approach described there suffers from some of the
same infelicities as the appengine-magic approach: use an external
server (jetty, ring-server) as a repl, then migrate your code to the
GAE dev server environment.  

With the migae approach, you only ever use the dev server, but you
only rarely need to restart it - the technique described here gives
you a quasi-repl environment, wherein you only need reload your
webpage to see your changes.  So you can develop your servlet logic
with minimal overhead.

# libraries

Not ready for primetime, but you can poke around in
e.g. [datastore](https://github.com/migae/datastore) or other repos in
[migae](https://github.com/migae).

These originated from
[appengine-magic](https://github.com/gcv/appengine-magic), but have
turned into something else.

# examples

Currently these examples only demonstrate Clojure on GAE; they do not
use any of the services libraries (datastore, memcache, etc.)  Demos
for those services are under development.

* [ringless](ringless) - simple servlet using only java interop, no ring/compojure
* [ringish](ringish) -  servlet using [ring](https://github.com/ring-clojure/ring)
* [compojure](compojure) - [compojure](https://github.com/weavejester/compojure) on GAE example, two servlets
* [swagger](swagger) -
[compojure-api-examples](https://github.com/metosin/compojure-api-examples)
on GAE.  [compojure-api](https://github.com/metosin/compojure-api) is
a wrapper on [Swagger](http://swagger.io/)

# running

We use the gradle build system.  From the root dir (not the project
subdir), run `$ ./gradlew tasks` to get a list of tasks.  The
subprojects are defined in `settings.gradle`.  Run project-specific
tasks like so: `$ ./gradlew :<proj>:<task>`.  For example, to run the
compojure project with the dev server, run:

```
$ ./gradlew :compojure:appengineRun
```

You can abbreviate both subprojects and tasks, so `$ ./gradlew :c:aRun` will work too.

For more on using the gradle appengine plugin see link:http://rominirani.com/2014/08/15/gradle-tutorial-part-5-gradle-app-engine-plugin/[Gradle Tutorial : Part 5 : Gradle App Engine Plugin]

# quasi-repl

GAE is basically a servlet container.  Due to security constraints,
GAE prohibits access to anything outside of the runtime context; for
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
servlet definitions (using `gen-class`) from implementations.  And to
make sure they get reloaded we use the standard `ns-tracker` library
in a Java Servlet Filter that intercepts all HTTP requests and reloads
changed code before forwarding requests to handlers.

Oh yeah, the other thing is we use `gradle` rather than `leiningen`.
I love leiningen, but using it doesn't make sense for GAE; the
google-supplied gradle plugin does everything we need, and in some
respects gradle is arguably superior to leiningen (e.g. supporting
multiple subprojects, build flavors, etc.)

## servlet definition

The `gen-class` function allows us to define all of our servlets in a
single Clojure source file, which by convention we're calling
`servlets.clj`.  Here's an example from the [compojure](compojure)
demo:

``` java
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
```

That's all there is to it.  Under AOT compilation, the `:name` clause
names the generated servlet class, and the `:impl-ns` clause names the
Clojure implementation namespace.  Here we've used the same name for
both.  For reasons I don't completely understand, this has the
practical effect of enabling dynamic reloading and evaluation of the
Clojure source code in the implementation namespace, even though that
has the same name as the AOT-compiled class.  In effect, `:impl-ns
foo.bar` seems to be telling the Clojure compiler to arrange for
functions in that namespace to be called (loaded) by Clojure rather
than by the servlet containers's class loader.  Note that if you omit
the `:impl-ns` clause, dynamic reload and eval will not be enabled.

At runtime, public HttpServlet methods will be forwarded to the
implementation namespace.  The only such method is
`service(ServletRequest req, ServletResponse res)`; the other
HttpServlet methods, like `doGet`, are `protected`, but they can be
explicitly forwarded using the `:exposes-methods` key of `gen-class`.
But to use Clojure on GAE (at least with ring/compojure), we are only
interested in the `service` method, so this works great.

If you compile e.g. the [compojure](compojure) demo and look at the
generated class files in
`compojure/build/exploded-app/WEB-INF/classes/migae` you will see
that everything has been AOT-compiled.  But if you copy the Clojure
file for any of the implementation namespaces (e.g. `echo.clj` in
the above example) into the `classes/migae` directory, it becomes
eligible for Clojure runtime loading, even though the generated class
files are on disk.

*CAVEAT* You do have to restart the server if you change your
 `gen-class` code (in `servlets.clj`) or you change the configuration
 files `web.xml` or `appengine-web.xml`.  But that happens rarely.

## servlet implementation

The servlet classes specified by the `gen-class` expressions
exemplified above do not contain any application-specific method
implementations.  But they do contain implementation code to support
Clojure's runtime magic, which means they contain the logic necessary
to forward method calls to the namespace (i.e. class) specified by the
`:impl-ns` clause.  So to complete the implementation of a servlet we
need to provide a Clojure function, in the implementation namespace,
to which the `service` method of the servlet can forward calls.  The
brute force way to do this is `(defn -service [this rqst resp] ...)`,
(see the source of `echo.clj`), but fortunately `ring` provides a
`defservice` macro that makes this much easier:

``` java
;; src/main/clojure/migae/echo.clj
(defroutes echo-routes
...
  )
(ring/defservice
   (-> (routes
        echo-routes)
       (wrap-defaults api-defaults)
       ))
```

In summary, the way it works is roughly:

1.  An http request arrives at GAE.
2.  GAE, being a servlet container, figures out which servlet is needed to service the request.
3.  GAE locates the servlet on disk, loads it and initializes it.
4.  GAE calls the `service` method of the servlet, passing the http request.
5.  The compiled `service` method of the servlet forwards the request
    to the service implementation, which is defined by
    `ring/defservice`.  *This uses the Clojure class loader*, which is
    what makes it possible to reload code.  At least I think that's
    how it works.
6.  The implementation code handles the request and generates a response.

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

For example, if you change

```
migae/ringless/src/main/clojure/migae/core.clj
```

then it be copied to

```
migae/ringless/build/exploded-app/WEB-INF/classes/migae/core.clj
```

This is required because the GAE dev server will only look in
`build/exploded-app/` for files.  Since the `build/` hiearchy is
constructed dynamically at compile/run time, we cannot edit in place -
we have to copy from the `src` tree to the `build/exploded-app` tree.

To verify that everything is working, run the dev server for the
[compojure](compojure) demo subproject (`$ ./gradlew c:aRun`) and load
`localhost:8080` in your browser.  Visit `/echo/hello/bob`, then edit
`echo.clj` and change something visible, e.g. change "Hello" to
"Howdy".  Then reload the webpage and you should see the change
(almost) immediately.

Try adding a route, e.g. in `math.clj` within the `math` context:
```
    (GET "/foo" []
          (str "bar"))
```

Then load `localhost:8080/math/foo` and you should see "bar" in your browser.

Needless to say, before uploading to the GAE servers, you should
disable the filter and run `./gradlew clean` to remove the `.clj` files.

