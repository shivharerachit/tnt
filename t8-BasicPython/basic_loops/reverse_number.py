"""
Program 15: Reverse a Number

This program reverses a number using a loop.
"""


def reverse_number(number: int) -> None:
    if number < 0: 
        sign = -1 
    else: 
        sign = 1
    number = abs(number)
    reversed_number = 0

    while number > 0:
        digit = number % 10
        reversed_number = reversed_number * 10 + digit
        number //= 10

    print(f"Reversed number is {sign * reversed_number}")


number: int = int(input("Enter a number: "))
reverse_number(number)