"""
Program 6: Replace multiple spaces in a string with a single space using re.sub().

This program removes extra spaces from a string.
"""

import re

text = "This   is    a    sentence   with   extra spaces."
updated_text = re.sub(r"\s+", " ", text)

print("Original:", text)
print("Updated:", updated_text)
