"""
Program 18: Palindrome Check

This program writes a function to check palindrome for numbers and strings.
"""


def check_palindrome(value: str) -> None:
    if value == value[::-1]:
        print(f"{value} is a Palindrome")
    else:
        print(f"{value} is not a Palindrome")


text: str = input("Enter a string or number: ")
check_palindrome(text)