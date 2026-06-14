"""
Program 31: Remove Duplicates Using Set

This program removes duplicates from a list using set.
"""


def remove_duplicates(numbers: list[int]) -> None:
    print(f"Original list: {numbers}")
    print(f"List without duplicates: {list(set(numbers))}")


numbers: list[int] = [1, 2, 3, 2, 4, 5, 3, 6, 1, 7]
remove_duplicates(numbers)