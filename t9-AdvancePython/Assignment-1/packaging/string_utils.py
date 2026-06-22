"""
Module: string_utils

Two simple utility functions: `to_upper` and `count_vowels`.
"""

def to_upper(s: str) -> str:
    return s.upper()


def count_vowels(s: str) -> int:
    return sum(1 for ch in s.lower() if ch in "aeiou")


if __name__ == "__main__":
    sample = "hello world"
    print("Original:", sample)
    print("Upper:", to_upper(sample))
    print("Vowels:", count_vowels(sample))
