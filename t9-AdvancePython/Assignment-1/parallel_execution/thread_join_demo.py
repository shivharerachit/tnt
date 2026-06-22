"""
Program 3: Demonstrate the use of join() method in threading.

This program shows main thread waiting for worker thread completion using join().
"""

import threading
import time


def do_work() -> None:
    print("Worker thread started")
    time.sleep(1)
    print("Worker thread finished")


if __name__ == "__main__":
    thread = threading.Thread(target=do_work)
    thread.start()

    print("Main thread waiting using join()")
    thread.join()
    print("Main thread resumed after worker completion")
