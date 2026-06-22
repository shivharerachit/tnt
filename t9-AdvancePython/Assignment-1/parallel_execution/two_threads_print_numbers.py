"""
Program 1: Create two threads that print numbers from 1 to 5 simultaneously.

This program starts two threads and each thread prints numbers from 1 to 5.
"""

import threading
import time


def print_numbers(thread_name: str) -> None:
    for number in range(1, 6):
        print(f"{thread_name}: {number}")
        time.sleep(0.1)


if __name__ == "__main__":
    thread1 = threading.Thread(target=print_numbers, args=("Thread-1",))
    thread2 = threading.Thread(target=print_numbers, args=("Thread-2",))

    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()
