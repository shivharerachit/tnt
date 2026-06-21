"""
Program 8: Password validation using regular expressions.

This program checks for minimum length, one digit, and one special character.
"""

import re

password = "Pass@123"
pattern = r"^(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':\"\\|,.<>/?]).{8,}$"

if re.fullmatch(pattern, password):
    print("Valid password")
else:
    print("Invalid password")
