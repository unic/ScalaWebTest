#!/usr/bin/env bash
docker run -it -p4000:4000 -v`pwd`:/srv/jekyll jekyll/jekyll:latest jekyll serve --watch