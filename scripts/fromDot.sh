#!/bin/sh

while [ True ]; do nc.traditional -vvvvv -l -p 5010 -c "/usr/bin/dot -Tsvg"; done
