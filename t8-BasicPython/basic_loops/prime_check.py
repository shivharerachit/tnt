"""
Program 16: Prime Number Check

This program checks whether a number is prime.
"""


def check_prime(number: int) -> None:
    if number <= 1:
        print(f"{number} is not a Prime Number")
        return

    for divisor in range(2, number):
        if number % divisor == 0:
            print(f"{number} is not a Prime Number")
            return

    print(f"{number} is a Prime Number")


number: int = int(input("Enter a number: "))
check_prime(number)