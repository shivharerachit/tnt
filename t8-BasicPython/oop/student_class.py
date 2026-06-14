"""
Program 40: Student Class

This program creates a Student class with attributes and displays details.
"""


class Student:
    def __init__(self, name: str, age: int, marks: float) -> None:
        self.name = name
        self.age = age
        self.marks = marks

    def display_details(self) -> None:
        print(f"Name: {self.name}")
        print(f"Age: {self.age}")
        print(f"Marks: {self.marks}")


name: str = input("Enter student name: ")
age: int = int(input("Enter student age: "))
marks: float = float(input("Enter student marks: "))

student = Student(name, age, marks)
student.display_details()