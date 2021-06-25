print("syllables script start")

import sys
from estnltk.vabamorf.morf import syllabify_word

txt = sys.argv[1]
arr = txt.split("%20")
syllables = {}

for i in range(len(arr)):
    syllables[i] = syllabify_word(arr[i])

print("syllablesDictionary:" + str(syllabify_word(txt)))
