"""
Program 36: Count Words, Lines, and Characters

This program reads a file and counts words, lines, and characters.
"""


def count_file_stats(file_name: str) -> None:
    with open(file_name, "r") as file_pointer:
        content = file_pointer.read()

    lines = content.splitlines()
    words = content.split()

    print(f"Lines: {len(lines)}")
    print(f"Words: {len(words)}")
    print(f"Characters: {len(content)}")


file_name: str = input("Enter file name to read: ")
path = "sample_files/" + file_name
count_file_stats(path)