"""
Program 7: Check if a string contains only alphabets using regular expressions.

This program validates that the string contains only letters.
"""

import re

text = "HelloWorld"
pattern = r"^[A-Za-z]+$"

if re.fullmatch(pattern, text):
    print("The string contains only alphabets")
else:
    print("The string contains other characters")
