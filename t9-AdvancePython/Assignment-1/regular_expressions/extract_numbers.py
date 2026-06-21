"""
Program 1: Extract all numbers from a given string using regular expressions.

This program uses re.findall() to extract every number from a string.
"""

import re

text = "There are 12 apples, 45 oranges, and 300 mangoes."
numbers = re.findall(r"\d+", text)

print("Text:", text)
print("Numbers:", numbers)
