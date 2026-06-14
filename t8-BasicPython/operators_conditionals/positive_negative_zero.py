"""
Program 8: Positive, Negative, or Zero

This program checks whether a number is positive, negative, or zero.
"""


def check_number_sign(number: float) -> None:
    if number > 0:
        print(f"{number} is Positive")
    elif number < 0:
        print(f"{number} is Negative")
    else:
        print(f"{number} is Zero")


number: float = float(input("Enter a number: "))
check_number_sign(number)