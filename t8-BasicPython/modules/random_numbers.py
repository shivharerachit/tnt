"""
Program 23: Random Module

This program generates random numbers using the random module.
"""

import random


def generate_random_numbers() -> None:
    print(f"Random integer between 1 and 100: {random.randint(1, 100)}")
    print(f"Random float between 0 and 1: {random.random():.2f}")
    print(f"Random choice from list: {random.choice(['Red', 'Green', 'Blue', 'Yellow'])}")


generate_random_numbers()