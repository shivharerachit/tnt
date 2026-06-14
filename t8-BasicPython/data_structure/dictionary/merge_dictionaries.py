"""
Program 34: Merge Two Dictionaries

This program merges two dictionaries.
"""


def merge_dictionaries(first_dictionary: dict[str, int], second_dictionary: dict[str, int]) -> None:
    merged_dictionary = {}
    merged_dictionary.update(first_dictionary)
    merged_dictionary.update(second_dictionary)
    print(f"First dictionary: {first_dictionary}")
    print(f"Second dictionary: {second_dictionary}")
    print(f"Merged dictionary: {merged_dictionary}")


first_dictionary: dict[str, int] = {"a": 1, "b": 2}
second_dictionary: dict[str, int] = {"c": 3, "d": 4}
merge_dictionaries(first_dictionary, second_dictionary)