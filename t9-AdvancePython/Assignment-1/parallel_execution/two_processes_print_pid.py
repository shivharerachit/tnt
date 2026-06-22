"""
Program 5: Create two processes that print their Process IDs.

This program starts two processes and prints each process id.
"""

import multiprocessing
import os


def show_pid(process_name: str) -> None:
    print(f"{process_name} PID: {os.getpid()}")


if __name__ == "__main__":
    process1 = multiprocessing.Process(target=show_pid, args=("Process-1",))
    process2 = multiprocessing.Process(target=show_pid, args=("Process-2",))

    process1.start()
    process2.start()

    process1.join()
    process2.join()
