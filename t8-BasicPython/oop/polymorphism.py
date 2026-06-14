"""
Program 44: Polymorphism

This program demonstrates polymorphism using different classes with the same method name.
"""


class Dog:
    def speak(self) -> None:
        print("Dog barks")


class Cat:
    def speak(self) -> None:
        print("Cat meows")


class Cow:
    def speak(self) -> None:
        print("Cow moos")


def make_animal_speak(animal) -> None:
    animal.speak()


make_animal_speak(Dog())
make_animal_speak(Cat())
make_animal_speak(Cow())