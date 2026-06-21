"""
Program 6: Use pdb breakpoints inside a loop.

This program shows how to inspect variable values during loop execution.
"""

import pdb


def sum_even_numbers(numbers):
    total = 0
    for number in numbers:
        pdb.set_trace()
        if number % 2 == 0:
            total += number
    return total


if __name__ == "__main__":
    values = [1, 2, 3, 4, 5, 6]
    print(sum_even_numbers(values))
