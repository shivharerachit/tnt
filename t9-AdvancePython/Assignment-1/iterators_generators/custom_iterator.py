"""
Program 2: Custom Iterator

This program creates a custom iterator class that returns numbers from 1 to N.
"""
from collections.abc import Iterator

class NumberIterator:
    def __init__(self, limit: int) -> None:
        self.limit = limit

    def __iter__(self) -> Iterator[int]:
        return iter(range(1, self.limit + 1))


number_iterator = NumberIterator(5)

for number in number_iterator:
    print(number)