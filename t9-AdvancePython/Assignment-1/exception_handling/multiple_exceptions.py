"""
Program 4: Multiple Exceptions

This program handles multiple exceptions in a single program.
"""

try:
    num1: int = int(input("Enter first number: "))
    num2: int = int(input("Enter second number: "))
    operation: str = input("Enter operation (+, -, *, /): ")

    if operation == "+":
        print("Result: ", num1 + num2)
    elif operation == "-":
        print("Result: ", num1 - num2)
    elif operation == "*":
        print("Result: ", num1 * num2)
    elif operation == "/":
        print("Result: ", num1 / num2)
    else:
        print("Invalid Operator!")

except ValueError:
    print("ValueError, Input must be a valid integer.")

except ZeroDivisionError:
    print("ZeroDivisionError, second number can not be zero while dividing.")