"""
Program 25: List Operations

This program creates a list of 10 numbers and finds sum, max, sort it, and remove duplicates.
"""


def list_operations(numbers: list[int]) -> None:
    print(f"List: {numbers}")
    print(f"Sum: {sum(numbers)}")
    print(f"Max: {max(numbers)}")
    print(f"Sorted list: {sorted(numbers)}")
    print(f"List without duplicates: {list(set(numbers))}")


NUMBERS: list[int] = [12, 5, 8, 3, 12, 7, 9, 5, 1, 8]
list_operations(NUMBERS)