"""
Program 2: Validate an email address using regular expressions.

This program checks whether the given email address is valid.
"""

import re

email = "example123@gmail.com"
pattern = r"^[\w\.-]+@[\w\.-]+\.\w+$"

if re.fullmatch(pattern, email):
    print(email, " is a valid email address")
else:
    print("Invalid email address")
