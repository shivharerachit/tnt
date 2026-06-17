"""
Program 7: Custom Exception

This program handles custom exception called AgeException and raise it if age is less than 18.
"""

class AgeException(Exception):
    pass

try:
    age: int = int(input("Enter your age: "))

    if age < 18:
        raise AgeException("Age must be at least 18.")

    print("Age accepted: ", age)

except ValueError:
    print("Please enter a valid integer for age.")

except AgeException as e:
    print(f"Invalid Age: {e}")