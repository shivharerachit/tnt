"""
Program 19: Maximum Number from a List

This program writes a function that returns the maximum number from a list.
"""


def find_maximum(numbers: list[float]) -> float:
    maximum_number = numbers[0]

    for number in numbers:
        if number > maximum_number:
            maximum_number = number

    return maximum_number


numbers: list[float] = [float(value) for value in input("Enter numbers separated by spaces: ").split()]
print(f"Maximum number is {find_maximum(numbers)}")