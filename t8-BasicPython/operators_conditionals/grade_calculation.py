"""
Program 10: Grade Calculation

This program calculates grade based on marks.
"""


def calculate_grade(marks: float) -> None:
    if marks >= 90:
        print("Grade: A")
    elif marks >= 75:
        print("Grade: B")
    elif marks >= 50:
        print("Grade: C")
    else:
        print("Grade: Fail")


marks: float = float(input("Enter marks: "))
calculate_grade(marks)