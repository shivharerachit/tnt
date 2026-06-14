"""
Program 32: Student Dictionary

This program creates a student dictionary and accesses its values.
"""


def display_student_details(student) -> None:
    print(f"Student dictionary: {student}")
    print(f"Name: {student['name']}")
    print(f"Age: {student['age']}")
    print(f"Marks: {student['marks']}")


STUDENT = {"name": "Pulkit", "age": 14, "marks": 87.5}
display_student_details(STUDENT)