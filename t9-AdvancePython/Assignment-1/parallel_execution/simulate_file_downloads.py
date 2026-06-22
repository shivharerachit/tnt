"""
Program 4: Create multiple threads to simulate file downloading.

This program uses time.sleep() to simulate download time.
"""

import threading
import time


def download_file(file_name: str, seconds: int) -> None:
    print(f"Starting download: {file_name}")
    time.sleep(seconds)
    print(f"Completed download: {file_name}")


if __name__ == "__main__":
    downloads = [
        ("file_a.zip", 1),
        ("file_b.zip", 2),
        ("file_c.zip", 1),
    ]

    threads = []
    for file_name, seconds in downloads:
        thread = threading.Thread(target=download_file, args=(file_name, seconds))
        threads.append(thread)
        thread.start()

    for thread in threads:
        thread.join()

    print("All downloads completed")
