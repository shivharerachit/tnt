"""
Program 43: Encapsulation

This program demonstrates encapsulation using private variables in a Bank class.
"""


class Bank:
    def __init__(self, account_holder: str, balance: float) -> None:
        self.__account_holder = account_holder
        self.__balance = balance

    def display_details(self) -> None:
        print(f"Account Holder: {self.__account_holder}")
        print(f"Balance: {self.__balance}")

    def deposit(self, amount: float) -> None:
        self.__balance += amount


account_holder: str = input("Enter account holder name: ")
balance: float = float(input("Enter balance: "))
deposit_amount: float = float(input("Enter deposit amount: "))

bank = Bank(account_holder, balance)
bank.deposit(deposit_amount)
bank.display_details()