"""
Program 1: List Iterator

This program creates an iterator for a list and prints elements using next().
"""


numbers: list[int] = [10, 20, 30, 40, 50]
number_iterator = iter(numbers)

for number in number_iterator:
    print(number)