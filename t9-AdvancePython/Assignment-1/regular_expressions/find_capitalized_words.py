"""
Program 5: Use re.findall() to extract all words starting with a capital letter.

This program finds all capitalized words in a sentence.
"""

import re

sentence = "Alice and Bob are visiting London in June."
capitalized_words = re.findall(r"\b[A-Z][a-zA-Z]*\b", sentence)

print("Sentence:", sentence)
print("Capitalized words:", capitalized_words)
