"""
Program 17: Square of a Number

This program writes a function to calculate the square of a number.
"""


def calculate_square(number: float) -> None:
    print(f"Square of {number} is {number * number}")


number: float = float(input("Enter a number: "))
calculate_square(number)