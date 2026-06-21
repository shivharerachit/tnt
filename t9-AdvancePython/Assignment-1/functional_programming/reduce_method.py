"""
Program 4: Reduce method to product of all elements in a list

This program executes a reduce() to find the product of all elements in a list.
"""

from functools import reduce


numbers = [2, 3, 4, 5]
product = reduce(lambda x, y: x * y, numbers)

print(product)
