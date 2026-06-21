"""
Program 3: Filter method to extract even numbers from a list

This program executes a filter() method to extract even numbers from a list.
"""
def is_even(number): 
    return number % 2 == 0

numbers: list[int] = [3, 4, 10, 6, 7, 20, 30, 40, 50]
even_numbers: list[int] = filter(is_even, numbers)
for n in even_numbers:
    print(n)