"""
Program 4: Data Type

This program demonstrates the use of type function to get the type of data/variable.
"""
def print_data_type(*args) -> None:
    for value in args:
        print(value, "\tis of\t", type(value), " type." )
    


STUDENT_NAME: str = "Pulkit"
AGE: int = 14
PERCENTAGE: float = 78.9
IS_PASSED: bool = True

print_data_type(STUDENT_NAME, AGE, PERCENTAGE, IS_PASSED)