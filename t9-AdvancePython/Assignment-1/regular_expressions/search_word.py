"""
Program 4: Use re.search() to check whether a word exists in a sentence.

This program checks whether a word is present in the given sentence.
"""

import re

sentence = "Python is a powerful programming language."
word = "powerful"

if re.search(word, sentence):
    print(f"'{word}' found in the sentence")
else:
    print(f"'{word}' not found in the sentence")
