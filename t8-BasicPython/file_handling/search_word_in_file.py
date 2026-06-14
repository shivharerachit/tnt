"""
Program 39: Search Word in File

This program searches a word in a file.
"""


def search_word_in_file(file_name: str, word: str) -> None:
    with open(file_name, "r") as file_pointer:
        content = file_pointer.read()

    if word in content:
        print(f"'{word}' found in {file_name}")
    else:
        print(f"'{word}' not found in {file_name}")


file_name: str = input("Enter file name to search in: ")
path = "sample_files/" + file_name
word: str = input("Enter word to search: ")
search_word_in_file(path, word)