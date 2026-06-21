"""
Program 2: Map method to calculate square of a number.

This program executes a map() method to convert a list of numbers into their squares.
"""

numbers: list[int] = [10, 20, 30, 40, 50]
squared_numbers:list = map(lambda i : i * i, numbers)
for n in squared_numbers:
    print(n)