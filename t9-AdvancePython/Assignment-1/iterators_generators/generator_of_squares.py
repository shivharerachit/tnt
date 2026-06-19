"""
Program 3: Square Generator

This program creates a generator (function) that yields square numbers up to N.
"""
from collections.abc import Iterator

def square_generator(limit: int) -> Iterator[int]:
    for number in range(1, limit + 1):
        yield number * number

for square in square_generator(5):
    print(square)