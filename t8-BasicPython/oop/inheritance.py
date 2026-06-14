"""
Program 42: Inheritance

This program implements inheritance using Person and Employee classes.
"""


class Person:
    def __init__(self, name: str, age: int) -> None:
        self.name = name
        self.age = age

    def display_person_details(self) -> None:
        print(f"Name: {self.name}")
        print(f"Age: {self.age}")


class Employee(Person):
    def __init__(self, name: str, age: int, employee_id: str) -> None:
        super().__init__(name, age)
        self.employee_id = employee_id

    def display_employee_details(self) -> None:
        self.display_person_details()
        print(f"Employee ID: {self.employee_id}")


name: str = input("Enter employee name: ")
age: int = int(input("Enter employee age: "))
employee_id: str = input("Enter employee ID: ")

employee = Employee(name, age, employee_id)
employee.display_employee_details()