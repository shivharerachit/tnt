"""
Program 27: Reverse a List

This program reverses a list without using reverse().
"""


def reverse_list(numbers: list[int]) -> None:
    reversed_numbers = []

    for index in range(len(numbers) - 1, -1, -1):
        reversed_numbers.append(numbers[index])

    print(f"Original list: {numbers}")
    print(f"Reversed list: {reversed_numbers}")


NUMBERS: list[int] = [11, 22, 33, 44, 55]
reverse_list(NUMBERS)