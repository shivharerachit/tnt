"""
Program 1: Lambda Function to calculate square of a number.

This program executes a lambda function to calculate the square of a number.
"""
numbers: list[int] = [10, 20, 30, 40, 50]
square = lambda i : i * i
for n in numbers:
    print(square(n))