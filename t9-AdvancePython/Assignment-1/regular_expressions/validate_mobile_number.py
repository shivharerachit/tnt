"""
Program 3: Validate a 10-digit mobile number using regular expressions.

This program checks whether the given mobile number has exactly 10 digits.
"""

import re

mobile_number = "9876543210"
pattern = r"^\d{10}$"

if re.fullmatch(pattern, mobile_number):
    print("Valid mobile number: ", mobile_number)
else:
    print("Invalid mobile number")
