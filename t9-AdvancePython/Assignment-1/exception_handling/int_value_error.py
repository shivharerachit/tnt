"""
Program 1: Integer Value Error

This program takes a number as a input and handles ValueError if input 
is not a valid integer.
"""

try:
	i = int(input("Enter a number: "))
	print(f"You entered: {i}")
except ValueError:
	print("Invalid input. Please enter a valid integer.")

