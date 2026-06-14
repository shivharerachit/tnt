"""
Program 28: Tuple Access

This program creates a tuple and accesses its elements.
"""


def access_tuple_elements(values: tuple[int, ...]) -> None:
    print(f"Tuple: {values}")
    print(f"First element: {values[0]}")
    print(f"Second element: {values[1]}")
    print(f"Last element: {values[-1]}")


values: tuple[int, ...] = (10, 20, 30, 40, 50)
access_tuple_elements(values)