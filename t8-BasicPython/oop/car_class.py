"""
Program 41: Car Class

This program creates a Car class with a constructor.
"""


class Car:
    def __init__(self, brand: str, model: str, year: int) -> None:
        self.brand = brand
        self.model = model
        self.year = year

    def display_details(self) -> None:
        print(f"Brand: {self.brand}")
        print(f"Model: {self.model}")
        print(f"Year: {self.year}")


brand: str = input("Enter car brand: ")
model: str = input("Enter car model: ")
year: int = int(input("Enter car year: "))

car = Car(brand, model, year)
car.display_details()