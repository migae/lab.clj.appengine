#!/bin/bash

## this monstrosity takes the absolute path produced by fswitch,
## extracts the path relative to the current directory, and uses it to
## construct the target path.

function target {
    P=`pwd`
    ROOT=./build/exploded-app
    tgt=${1##`pwd`/src/main/clojure/}
    tdir=`dirname $tgt`
    tfile=`basename $tgt`
    mkdir -p ${ROOT}/WEB-INF/classes/$tdir
    gcp -rv $1 ${ROOT}/WEB-INF/classes/$tdir
    return 0
}

export -f target

fswatch -0 -e ".*" -i ".*clj$" \
	--event Updated \
	--recursive \
	src/main/clojure \
	--format %p \
    | xargs -0 -n 1 -I {} \
	    bash -c 'target "$@"' - {}
