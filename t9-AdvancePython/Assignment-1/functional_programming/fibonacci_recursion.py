"""
Program 6: Recursive function to calculate Fibonacci

This program executes a recursive function to calculate Fibonacci.
"""
def fibonacci(n: int) -> int:
    if n < 0:
        raise ValueError("fibonacci() not defined for negative values")
    if n <= 1:
        return n
    return fibonacci(n - 1) + fibonacci(n - 2)


try:
    n = int(input("Enter a non-negative integer: "))
    print(f"fibonacci({n}) = {fibonacci(n)}")

except ValueError:
    print("Please enter a valid non-negative integer.")