"""
Program 2: Pytest test cases for is_prime().
"""

from prime_number import is_prime


def test_prime_number():
    assert is_prime(7) is True


def test_non_prime_number():
    assert is_prime(8) is False


def test_numbers_less_than_two():
    assert is_prime(0) is False
    assert is_prime(1) is False
