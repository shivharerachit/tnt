"""
Program 8: Convert a normal function into parallel execution using ProcessPoolExecutor.

This program computes cubes in parallel using processes.
"""

from concurrent.futures import ProcessPoolExecutor


def cube(number: int) -> int:
    return number * number * number


if __name__ == "__main__":
    numbers = [1, 2, 3, 4, 5]

    with ProcessPoolExecutor(max_workers=3) as executor:
        results = list(executor.map(cube, numbers))

    print("Numbers:", numbers)
    print("Cubes:", results)
