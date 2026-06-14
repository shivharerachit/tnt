"""
Program 13: Multiplication Table

This program prints the multiplication table of a number.
"""


def print_multiplication_table(number: int) -> None:
    for multiplier in range(1, 11):
        print(f"{number} x {multiplier} = {number * multiplier}")


number: int = int(input("Enter a number: "))
print_multiplication_table(number)