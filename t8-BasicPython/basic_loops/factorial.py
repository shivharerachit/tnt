"""
Program 14: Factorial of a Number

This program finds the factorial of a number using a loop.
"""


def find_factorial(number: int) -> None:
    factorial = 1

    if number < 0:
        print("Factorial is not defined for negative numbers")
        return

    for value in range(1, number + 1):
        factorial *= value

    print(f"Factorial of {number} is {factorial}")


number: int = int(input("Enter a number: "))
find_factorial(number)