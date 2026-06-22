"""two_module_pkg package exposing greet and farewell"""

from .module1 import greet
from .module2 import farewell

__all__ = ["greet", "farewell"]
