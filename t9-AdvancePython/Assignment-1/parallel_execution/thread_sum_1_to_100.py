"""
Program 2: Create a thread that calculates the sum of numbers from 1 to 100.

This program runs the summation in a separate thread.
"""

import threading


def calculate_sum() -> None:
    total = sum(range(1, 101))
    print("Sum from 1 to 100:", total)


if __name__ == "__main__":
    worker = threading.Thread(target=calculate_sum)
    worker.start()
    worker.join()
