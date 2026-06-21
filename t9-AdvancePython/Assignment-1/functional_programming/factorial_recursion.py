"""
Program 5: Recursive function to calculate factorial

This program executes a recursive function to calculate factorial.
"""
def factorial(n: int) -> int:
	if n < 0:
		raise ValueError("factorial() not defined for negative values")
	if n <= 1:
		return 1
	return n * factorial(n - 1)


try:
    n = int(input("Enter a non-negative integer: "))
    print(f"factorial({n}) = {factorial(n)}")
except ValueError:
    print("Please enter a valid non-negative integer.")