-- a. Find the top 3 customers who have spent the most on orders using a CTE.
WITH customer_spending AS (
    SELECT customer_id,
    SUM(total_amount) as total_spent
    FROM Orders
    GROUP BY customer_id
)
SELECT *
FROM customer_spending 
ORDER BY total_spent DESC
LIMIT 3;

-- b. Retrieve all orders where the total amount exceeds 500, converting the amount to an integer. Using CAST
SELECT 
    order_id, 
    order_date, 
    payment_method, 
    CAST(total_amount AS SIGNED) AS total_amount_int
FROM Orders
WHERE total_amount > 500;

-- c. Write a CRON job to generate daily sales reports at 11:55 PM

SET GLOBAL event_scheduler = ON;

DROP EVENT IF EXISTS daily_sales_report;

CREATE EVENT daily_sales_report
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE, '23:55:00')
DO
WITH daily_report AS (
    SELECT
        CURDATE() AS report_date,
        COUNT(*) AS total_orders,
        COALESCE(SUM(total_amount), 0) AS total_sales
    FROM Orders
    WHERE DATE(order_date) = CURDATE()
)
SELECT *
FROM daily_report;


-- d. Write a transaction to insert a new customer and an order. If the order fails, roll back the customer creation.

START TRANSACTION;

INSERT INTO Customers (name, email, mobile, age)
VALUES ('Nes Rao', 'nes.rao@example.com', '9876500944', 18);

SET @new_customer_id = LAST_INSERT_ID();

INSERT INTO Orders (customer_id, product_id, quantity, order_date, status, payment_method, total_amount)
VALUES (@new_customer_id, 9999, 1, CURDATE(), 'Pending', 'UPI', 799.00);

COMMIT;

-- If the order INSERT fails, execute:
-- ROLLBACK;


-- e. Rank customers based on their total spending, showing ranks even if values are tied.
WITH customer_spending AS (
    SELECT
        customer_id,
        SUM(total_amount) AS total_spent
    FROM Orders
    GROUP BY customer_id
)
SELECT
    customer_id,
    total_spent,
    RANK() OVER (ORDER BY total_spent DESC) AS spending_rank
FROM customer_spending
ORDER BY spending_rank, customer_id;
