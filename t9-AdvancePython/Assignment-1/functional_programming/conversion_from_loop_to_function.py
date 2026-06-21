"""
Program 7: Conversion of loop-based program into functional style using map or filter

This program converts a simple loop-based program into a functional style using map or filter.
"""


def loop_version(numbers):
	"""Return squares of even numbers using a loop."""
	result = []
	for n in numbers:
		if n % 2 == 0:
			result.append(n * n)
	return result


def functional_version(numbers):
	"""Return squares of even numbers using filter + map."""
	return list(map(lambda x: x * x, filter(lambda x: x % 2 == 0, numbers)))



s = input("Enter integers separated by spaces (default: 1 2 3 4 5): ").strip()
nums = [int(x) for x in s.split()] if s else [1, 2, 3, 4, 5]
print("Loop version:", loop_version(nums))
print("Functional version:", functional_version(nums))