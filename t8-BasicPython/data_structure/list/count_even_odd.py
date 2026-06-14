"""
Program 26: Count Even and Odd Numbers

This program counts even and odd numbers in a list.
"""


def count_even_odd(numbers: list[int]) -> None:
    even_count = 0
    odd_count = 0

    for number in numbers:
        if number % 2 == 0:
            even_count += 1
        else:
            odd_count += 1

    print(f"List: {numbers}")
    print(f"Even count: {even_count}")
    print(f"Odd count: {odd_count}")


NUMBERS: list[int] = [10, 15, 20, 25, 30, 33, 40, 41, 50, 55]
count_even_odd(NUMBERS)