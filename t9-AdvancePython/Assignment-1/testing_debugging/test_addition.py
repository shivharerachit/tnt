"""
Program 1: Pytest test cases for add_two_numbers().
"""

from addition import add_two_numbers


def test_add_two_positive_numbers():
    assert add_two_numbers(2, 3) == 5


def test_add_two_negative_numbers():
    assert add_two_numbers(-2, -3) == -5


def test_add_zero():
    assert add_two_numbers(0, 7) == 7
