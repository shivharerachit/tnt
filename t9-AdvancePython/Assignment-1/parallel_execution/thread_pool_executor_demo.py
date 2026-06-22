"""
Program 7: Convert a normal function into parallel execution using ThreadPoolExecutor.

This program computes squares in parallel using threads.
"""

from concurrent.futures import ThreadPoolExecutor
import time


def square(number: int) -> int:
    time.sleep(0.2)
    return number * number


if __name__ == "__main__":
    numbers = [1, 2, 3, 4, 5]

    with ThreadPoolExecutor(max_workers=3) as executor:
        results = list(executor.map(square, numbers))

    print("Numbers:", numbers)
    print("Squares:", results)
