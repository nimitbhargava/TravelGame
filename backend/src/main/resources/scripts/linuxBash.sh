#!/bin/bash
echo "startSyl"
WORDS=$1
/root/miniconda3/bin/python "/root/spring/iaib/backend/src/main/resources/scripts/syllables.py" $WORDS
echo "endSyl"
