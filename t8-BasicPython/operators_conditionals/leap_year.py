"""
Program 11: Leap Year

This program checks whether a year is a leap year.
"""


def check_leap_year(year: int) -> None:
    if (year % 400 == 0) or (year % 4 == 0 and year % 100 != 0):
        print(f"{year} is a Leap Year")
    else:
        print(f"{year} is not a Leap Year")


year: int = int(input("Enter a year: "))
check_leap_year(year)