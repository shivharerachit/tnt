"""
Program 20: Default Parameters

This program writes a function using default parameters.
"""


def greet(name: str = "Guest") -> None:
    print(f"Hello, {name}!")


name: str = input("Enter your name (press enter to use default): ")

if name:
    greet(name)
else:
    greet()