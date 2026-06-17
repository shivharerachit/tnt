"""
Program 6: Raise ValueError

This program raises ValueError if a number is negative.
"""

try:
    num: int = int(input("Enter a number: "))
    if num < 0:
        raise ValueError("Number cannot be negative")
    print(f"Number is valid: {num}")

except ValueError as e:
    print(f"ValueError, Number must be a valid and positive: {e}")