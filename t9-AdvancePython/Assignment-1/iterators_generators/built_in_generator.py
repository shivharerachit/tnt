"""
Program 8: Built-in Generator

This program shows an example of a built-in generator and iterate over it.
"""


numbers: list[int] = [10, 20, 30, 40, 50]

print("Using enumerate() built-in generator:")
for index, value in enumerate(numbers):
    print(f"Index: {index}, Value: {value}")

print("\nUsing map() built-in generator:")
squared: list[int] = map(lambda x: x ** 2, numbers)
for value in squared:
    print(value)
