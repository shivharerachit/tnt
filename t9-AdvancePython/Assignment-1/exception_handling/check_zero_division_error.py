"""
Program 2: Zero Division Error

This program takes two numbers as a input and divides them while handling ZeroDivisionError if divided by zero.
"""

try:
    a = int(input("Enter first number: "))
    b = int(input("Enter second number: "))
    print(f"{a}/{b} = {a/b}")

except ZeroDivisionError:
	print("ZeroDivisionError, second number can not be zero")