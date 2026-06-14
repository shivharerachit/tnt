"""
Program 22: Math Module Operations

This program uses the math module to find square root, power, and factorial.
"""

import math


def math_operations(number: float, exponent: float) -> None:
    print(f"Square root of {number} is {math.sqrt(number):.2f}")
    print(f"{number} to the power of {exponent} is {math.pow(number, exponent):.2f}")
    print(f"Factorial of {int(number)} is {math.factorial(int(number))}")


number: float = float(input("Enter a non-negative number: "))
exponent: float = float(input("Enter the exponent number: "))

math_operations(number, exponent)