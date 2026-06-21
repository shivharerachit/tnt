"""
Program 3: Logical bug demo using pdb.

This program contains a logical bug and a pdb breakpoint to inspect it.
"""

import pdb


def find_largest(numbers):
    largest = numbers[0]
    pdb.set_trace()
    for number in numbers[1:]:
        if number < largest:
            largest = number
    return largest


if __name__ == "__main__":
    values = [12, 45, 7, 89, 23]
    print(find_largest(values))
