"""
Program 4: Fibonacci Generator

This program creates a generator (function) that yields Fibonacci numbers.
"""
from collections.abc import Iterator

def fibonacci_generator(limit: int) -> Iterator[int]:
    first: int = 0
    second: int = 1

    for i in range(limit):
        yield first
        first, second = second, first + second

for number in fibonacci_generator(15):
    print(number)