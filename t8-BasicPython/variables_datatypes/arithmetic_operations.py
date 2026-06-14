
"""
Program 6: Arithmetic Operations

This program demonstrates basic arithmetic operations in python.
"""

def arithmetic_operations(first_number:float, second_number:float) -> None :
    print(f"Sum\t\t=\t{first_number + second_number:.2f}")
    print(f"Difference\t=\t{first_number - second_number:.2f}")
    print(f"Multiplication\t=\t{first_number * second_number:.2f}")

    if second_number != 0:
        print(f"Division\t=\t{first_number/second_number:.2f}")
    else:
        print("ZERO DIVISION ERROR")


number1:float = float(input("Enter first number: "))
number2:float = float(input("Enter second number: "))

arithmetic_operations(number1, number2)