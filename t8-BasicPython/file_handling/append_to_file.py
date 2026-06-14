"""
Program 37: Append Data to Existing File

This program appends data to an existing file.
"""


def append_data_to_file(file_name: str, data: str) -> None:
    with open(file_name, "a") as file_pointer:
        file_pointer.write(data)

    print(f"Data appended to {file_name}")


file_name: str = input("Enter file name to append to: ")
path = "sample_files/" + file_name
data: str = input("Enter data to append: ")
append_data_to_file(path, "\n" + data)