"""
Program 6: Calculate squares of numbers using multiprocessing.Process class.

This program creates one process per input number and prints its square.
"""

import multiprocessing


def print_square(number: int) -> None:
    print(f"Square of {number} is {number * number}")


if __name__ == "__main__":
    numbers = [2, 4, 6, 8]
    processes = []

    for number in numbers:
        process = multiprocessing.Process(target=print_square, args=(number,))
        processes.append(process)
        process.start()

    for process in processes:
        process.join()
