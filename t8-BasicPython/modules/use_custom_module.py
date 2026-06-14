"""
Program 24: Create Your Own Module

This program creates a custom module and imports it.
"""

import custom_module


name: str = input("Enter your name: ")
first_number: float = float(input("Enter first number: "))
second_number: float = float(input("Enter second number: "))

custom_module.greet(name)
print(f"Sum is {custom_module.add_numbers(first_number, second_number):.2f}")