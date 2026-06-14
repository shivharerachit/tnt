"""
Program 7: Even or Odd

This program checks whether a number is even or odd.
"""


def check_even_odd(number: int) -> None:
    if number % 2 == 0:
        print(f"{number} is Even")
    else:
        print(f"{number} is Odd")


number: int = int(input("Enter a number: "))
check_even_odd(number)