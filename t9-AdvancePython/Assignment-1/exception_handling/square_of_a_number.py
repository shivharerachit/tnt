"""
Program 3: Square of a number

This program uses try, except, else and finally to read a number from a file and perform its square
"""

try:
    file = open("sample_files/number.txt", "r")
    number: int = int(file.read().strip())

except ValueError:
    print("ValueError, the file does not contain a valid number")

except FileNotFoundError:
    print("FileNotFoundError, could not find sample_files/number.txt")

else:
    print(number * number)

finally:
    if file is not None:
        file.close()