"""
Program 29: Tuple to List

This program converts a tuple into a list and modifies it.
"""


def convert_tuple_to_list(values: tuple[int, ...]) -> None:
    list_values = list(values)
    list_values.append(60)
    list_values[0] = 5

    print(f"Original tuple: {values}")
    print(f"Modified list: {list_values}")


values: tuple[int, ...] = (10, 20, 30, 40, 50)
convert_tuple_to_list(values)