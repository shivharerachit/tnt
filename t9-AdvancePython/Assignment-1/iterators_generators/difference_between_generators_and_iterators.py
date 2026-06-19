"""
Program 6: Difference between generators and iterators

This program explains the difference between iterator and generator with a small example.
"""
from collections.abc import Iterator

print("***** Iterator *****")
l: Iterator[str] = iter(['Geeks', 'For', 'Geeks'])
print(next(l))
print(next(l))
print(next(l))

print("\n***** Generator *****")
def sq_numbers(n: int) -> Iterator[int]:
    for i in range(1, n+1):
        yield i*i

a: Iterator[int] = sq_numbers(3)

print("The square of numbers 1, 2, 3 are:")
print(next(a))
print(next(a))
print(next(a))