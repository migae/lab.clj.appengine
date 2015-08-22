# migae

minimal Clojure-on-GAE


# examples

* compojure - minimal compojure on GAE example, one servlet
* swagger - minimal example using [compojure-api](https://github.com/metosin/compojure-api), a wrapper on [Swagger](http://swagger.io/)

# editing

**WARNING** If you are using emacs, you _must_ edit the paths in
  `.dir-locals.el` file in each subproject, and you must install the
  custom edit macro `migae.el`.

1.  edit .dir-locals.el and place it in <proj>/src (e.g. compojure/src/.dir-locals.el)
2.  put (migae.el) in your emacs load path and byte compile
    it (see comments in migae.el for installation instructions)


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

