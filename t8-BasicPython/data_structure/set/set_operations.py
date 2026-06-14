"""
Program 30: Set Operations

This program performs union, intersection, and difference on two sets.
"""


def perform_set_operations(first_set: set[int], second_set: set[int]) -> None:
    print(f"First set: {first_set}")
    print(f"Second set: {second_set}")
    print(f"Union: {first_set.union(second_set)}")
    print(f"Intersection: {first_set.intersection(second_set)}")
    print(f"Difference: {first_set.difference(second_set)}")


first_set: set[int] = {1, 2, 3, 4, 5}
second_set: set[int] = {4, 5, 6, 7, 8}
perform_set_operations(first_set, second_set)