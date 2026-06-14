"""
Program 33: Character Frequency

This program counts frequency of characters in a string using a dictionary.
"""


def character_frequency(text: str) -> None:
    frequency: dict[str, int] = {}

    for character in text:
        if character in frequency:
            frequency[character] += 1
        else:
            frequency[character] = 1

    print(f"String: {text}")
    print(f"Character frequency: {frequency}")


text: str = input("Enter a string: ")
character_frequency(text)