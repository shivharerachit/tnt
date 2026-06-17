"""
Program 5: Any Exception

This program every exception and prints the error message.
"""

try:
    num1: int = int(input("Enter first number: "))
    num2: int = int(input("Enter second number: "))
    operation: str = input("Enter operation (+, -, *, /): ")

    if operation == "+":
        print("Result: ", num1 + num2)
    elif operation == "-":
        print("Result: ", num1 - num2)
    elif operation == "*":
        print("Result: ", num1 * num2)
    elif operation == "/":
        print("Result: ", num1 / num2)
    else:
        print("Invalid Operator!")

except Exception as e:
    print(e)