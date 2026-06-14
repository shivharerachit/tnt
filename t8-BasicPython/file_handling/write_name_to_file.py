"""
Program 35: Write Name to File

This program creates a file and writes a name into it.
"""


def write_name_to_file(file_name: str, name: str) -> None:
    with open(file_name, "w") as file_pointer:
        file_pointer.write(name)

    print(f"Name written to {file_name}")


file_name: str = input("Enter file name: ")
name: str = input("Enter name to write: ")
path = "sample_files/" + file_name
write_name_to_file(path, name)