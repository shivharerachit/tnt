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

function deleteProduct(productId) {
  products = products.filter(function(p) {
    return p.id !== productId;
  });

  saveProductsToStorage(products);

  applyFiltersAndRender();
  updateAnalytics();
}

document.getElementById("addProductBtn").addEventListener("click", function() {
  var name     = document.getElementById("productName").value.trim();
  var price    = parseFloat(document.getElementById("productPrice").value);
  var stock    = parseInt(document.getElementById("productStock").value);
  var category = document.getElementById("productCategory").value;
  var errorMsg = document.getElementById("formError");

  if (name === "") {
    errorMsg.textContent = "Please enter a product name.";
    return;
  }
  if (isNaN(price) || price <= 0) {
    errorMsg.textContent = "Price must be a number greater than 0.";
    return;
  }
  if (isNaN(stock) || stock < 0) {
    errorMsg.textContent = "Stock cannot be negative.";
    return;
  }
  if (category === "") {
    errorMsg.textContent = "Please select a category.";
    return;
  }

  errorMsg.textContent = "";

  var newProduct = {
    id: Date.now(),
    name: name,
    price: price,
    stock: stock,
    category: category
  };

  products.push(newProduct);

  saveProductsToStorage(products);

  document.getElementById("productName").value  = "";
  document.getElementById("productPrice").value = "";
  document.getElementById("productStock").value = "";
  document.getElementById("productCategory").value = "";

  applyFiltersAndRender();
  updateAnalytics();
});


document.getElementById("searchInput").addEventListener("input", applyFiltersAndRender);
document.getElementById("categoryFilter").addEventListener("change", applyFiltersAndRender);
document.getElementById("lowStockFilter").addEventListener("change", applyFiltersAndRender);
document.getElementById("sortSelect").addEventListener("change", applyFiltersAndRender);

var loadingMsg = document.getElementById("loadingMsg");
loadingMsg.style.display = "block";

fetchProducts().then(function(loadedProducts) {
  products = loadedProducts;
  loadingMsg.style.display = "none";
  applyFiltersAndRender();
  updateAnalytics();
});