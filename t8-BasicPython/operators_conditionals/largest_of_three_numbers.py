"""
Program 9: Largest of Three Numbers

This program finds the largest of three numbers.
"""


def find_largest(first_number: float, second_number: float, third_number: float) -> None:
    if first_number >= second_number and first_number >= third_number:
        largest = first_number
    elif second_number >= first_number and second_number >= third_number:
        largest = second_number
    else:
        largest = third_number

    print(f"Largest number is {largest}")


first_number: float = float(input("Enter first number: "))
second_number: float = float(input("Enter second number: "))
third_number: float = float(input("Enter third number: "))

find_largest(first_number, second_number, third_number)