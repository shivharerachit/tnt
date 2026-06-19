"""
Program 7: Processing Large Dataset

This program processes a large dataset using a generator instead of storing all values in a list.
"""

from collections.abc import Iterator

def large_dataset_simulator(n: int) -> Iterator[str]:
	for i in range(1, n + 1):
		yield f"record-{i}"


def process_records(rec: str) -> None:
    processed: str = rec.upper()
    print(processed)


simulated = large_dataset_simulator(1_000_000)

for i in range(10):
    process_records(next(simulated))