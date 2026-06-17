"""
Program 8: Custom Exception

This program handles FileNotFoundError when trying to open a file.
"""

try:
    path = "sample_files/number.txt"
    file = open(path, "r")
    content: str = file.read()
    print(content)

except FileNotFoundError:
    print(f"FileNotFoundError, could not find {path}")

except Exception as e:
    print(f"Error: {e}")