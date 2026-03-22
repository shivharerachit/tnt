var defaultProducts = [
    { id: 1, name: "Laptop", price: 55000, stock: 5, category: "electronics" },
    { id: 2, name: "T-Shirt", price: 499, stock: 20, category: "clothing" },
    { id: 3, name: "JavaScript Book", price: 799, stock: 8, category: "books" },
    { id: 4, name: "Headphones", price: 2999, stock: 0, category: "electronics" },
    { id: 5, name: "Jeans", price: 1299, stock: 3, category: "clothing" },
    { id: 6, name: "Watch", price: 4999, stock: 2, category: "accessories" },
    { id: 7, name: "HTML Book", price: 499, stock: 12, category: "books" },
    { id: 8, name: "Sunglasses", price: 999, stock: 0, category: "accessories" },
    { id: 9, name: "Keyboard", price: 1599, stock: 4, category: "electronics" },
    { id: 10, name: "Notebook", price: 149, stock: 50, category: "books" }
];

function loadProductsFromStorage() {
    var saved = localStorage.getItem("products");
    if (saved) {
        return JSON.parse(saved);
    } else {
        return defaultProducts;
    }
}

function saveProductsToStorage(productsArray) {
    localStorage.setItem("products", JSON.stringify(productsArray));
}

var products = loadProductsFromStorage();

function fetchProducts() {
    return new Promise(function (resolve) {
        // Wait 1.5 seconds, then "resolve" (return) the products
        setTimeout(function () {
            resolve(products);
        }, 1500);
    });
}

function updateAnalytics() {
    var total = products.length;

    var totalValue = 0;
    for (var i = 0; i < products.length; i++) {
        totalValue += products[i].price * products[i].stock;
    }

    var outOfStock = 0;
    for (var i = 0; i < products.length; i++) {
        if (products[i].stock === 0) {
            outOfStock++;
        }
    }

    document.getElementById("totalProducts").textContent = total;
    document.getElementById("totalValue").textContent = "₹" + totalValue.toLocaleString("en-IN");
    document.getElementById("outOfStock").textContent = outOfStock;
}

function renderProducts(productsToShow) {
    var grid = document.getElementById("productGrid");

    grid.innerHTML = "";

    if (productsToShow.length === 0) {
        grid.innerHTML = '<p class="no-products-msg">No products found.</p>';
        return;
    }

    for (var i = 0; i < productsToShow.length; i++) {
        var product = productsToShow[i];

        var stockLabel = product.stock + " in stock";
        var stockClass = "";
        if (product.stock < 5) {
            stockClass = "low-stock";
            if (product.stock === 0) {
                stockLabel = "Out of Stock";
            } else {
                stockLabel = product.stock + " left (Low!)";
            }
        }

        var card = document.createElement("div");
        card.className = "product-card";

        card.innerHTML =
            "<h3>" + product.name + "</h3>" +
            "<p>Category: " + product.category + "</p>" +
            "<p>Price: ₹" + product.price.toLocaleString("en-IN") + "</p>" +
            "<p class='" + stockClass + "'>" + stockLabel + "</p>" +
            "<button onclick='deleteProduct(" + product.id + ")'>Delete</button>";

        grid.appendChild(card);
    }
}

function applyFiltersAndRender() {
  var searchText     = document.getElementById("searchInput").value.toLowerCase();
  var selectedCategory = document.getElementById("categoryFilter").value;
  var lowStockOnly   = document.getElementById("lowStockFilter").checked;
  var sortOption     = document.getElementById("sortSelect").value;

  var filtered = products;

  if (searchText !== "") {
    filtered = filtered.filter(function(p) {
      return p.name.toLowerCase().indexOf(searchText) !== -1;
    });
  }

  if (selectedCategory !== "all") {
    filtered = filtered.filter(function(p) {
      return p.category === selectedCategory;
    });
  }

  if (lowStockOnly) {
    filtered = filtered.filter(function(p) {
      return p.stock < 5;
    });
  }

  if (sortOption === "priceLow") {
    filtered.sort(function(a, b) { return a.price - b.price; });
  } else if (sortOption === "priceHigh") {
    filtered.sort(function(a, b) { return b.price - a.price; });
  } else if (sortOption === "nameAZ") {
    filtered.sort(function(a, b) { return a.name.localeCompare(b.name); });
  } else if (sortOption === "nameZA") {
    filtered.sort(function(a, b) { return b.name.localeCompare(a.name); });
  }

  renderProducts(filtered);
}