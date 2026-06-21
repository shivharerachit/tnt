"""
Program 2: Function to check whether a number is prime.

This program defines a simple function to check prime numbers.
"""


def is_prime(number):
    if number < 2:
        return False
    for divisor in range(2, int(number ** 0.5) + 1):
        if number % divisor == 0:
            return False
    return True


if __name__ == "__main__":
    print(is_prime(11))
