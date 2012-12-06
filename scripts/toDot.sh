#!/bin/sh

for FILE in $(ls *.dot); do nc 130.240.94.110 5010 < $FILE > $FILE.svg; rm $FILE; done
