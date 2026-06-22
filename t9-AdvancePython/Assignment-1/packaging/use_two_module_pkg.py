"""
Example: import from two_module_pkg
"""

from two_module_pkg import greet, farewell

if __name__ == "__main__":
    print(greet("Alice"))
    print(farewell("Alice"))
