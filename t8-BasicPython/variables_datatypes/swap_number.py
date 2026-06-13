"""
Program 5: Swap Numbers

This program demonstrates how to swap values of two variables.
"""

number1: int = int(input("Enter first number: "))
number2: int = int(input("Enter second number: "))

print(f"Number before swapping:\nnumber1: ", number1, "\tnumber2: ", number2)

number1, number2 = number2, number1

print(f"Number after swapping:\nnumber1: ", number1, "\tnumber2: ", number2)