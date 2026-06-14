"""
Program 38: Copy File Content

This program copies content from one file to another.
"""


def copy_file_content(source_file: str, destination_file: str) -> None:
    with open(source_file, "r") as source_pointer:
        content = source_pointer.read()

    with open(destination_file, "w") as destination_pointer:
        destination_pointer.write(content)

    print(f"Content copied from {source_file} to {destination_file}")


source_file: str = input("Enter source file name: ")
destination_file: str = input("Enter destination file name: ")
source_path = "sample_files/" + source_file
destination_path = "sample_files/" + destination_file
copy_file_content(source_path, destination_path)