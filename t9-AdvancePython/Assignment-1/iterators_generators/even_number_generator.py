"""
Program 5: Even Number Generator

This program creates a generator (function) that yields even numbers from 1 to 50.
"""
from collections.abc import Iterator

def even_number_generator(limit: int) -> Iterator[int]:
    for i in range(limit):
        if(i%2 == 0):
            yield i

for number in even_number_generator(50):
    print(number)