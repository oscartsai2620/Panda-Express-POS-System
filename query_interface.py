import subprocess
import os
import pandas as pd
from tabulate import tabulate
# --------------------------------------------------------------------------------------------
# Database connection parameters
DB_HOST = "csce-315-db.engr.tamu.edu"
DB_USER = "team_5g"
DB_NAME = "team_5g_db"
DB_PASSWORD = "thindoe99"
SQL_DIR = "schema/create_tables"
week_order_dict = {}
# --------------------------------------------------------------------------------------------
# Craft Query
# --------------------------------------------------------------------------------------------
# Special Query Functions:
def fetchWeekOrderAmount(sql_command):
    try:
        env = os.environ.copy()
        env["PGPASSWORD"] = DB_PASSWORD  # Set the password in the environment
        command = [
            "psql",
            "-h", DB_HOST,
            "-U", DB_USER,
            "-d", DB_NAME,
            "-c", sql_command
        ]

        result = subprocess.run(command, check=True, text=True, capture_output=True, env=env)
        
        # Parse the result.stdout to extract table data
        output_lines = result.stdout.strip().split('\n')
        
        # Assuming the first line is the header and the rest are data rows
        headers = output_lines[0].split('|')
        headers = [header.strip() for header in headers]
        
        data = []
        for line in output_lines[1:]:
            if '|' in line:
                row = line.split('|')
                row = [item.strip() for item in row]
                data.append(row)
        
        # Create a DataFrame from the parsed data
        table = pd.DataFrame(data, columns=headers)
        
        # Print the table using tabulate
        print(tabulate(table, headers='keys', tablefmt='psql'))
        
        return table

    except subprocess.CalledProcessError as e:
        print(f"Error executing {sql_command}: {e.stderr}")
        return None
# -------------------------------------------------------------------------------------------- 
menu = "1 : Week Orders\n2 : Realistic Sales History\n"
def printMenu():
    # Print Line to Divide Each Query
    print(40 * '-')
    print('''
        1 : Week Orders\n
        2 : Realistic Sales History\n
        3 : Peak Sales Day\n
        4 : Menu Item Inventory\n
        5 : Most Ordered Menu Item of the day\n
        6 : Most Expensive Order of the Week\n
        7 : Top 5 days of the month that has the most sales\n
        8 : Least order item of the week\n
        9 : Top 5 Most Profitable day of the month\n
        10 : Top 5 most expensive order of the year\n
        11 : Top 10 Most ordered Item of the year\n
        12 : Count of bowls, plates, drinks sales of each day\n
        13 : Most common to least common payment method for the week\n  
        14 : Orders in the year that cost more than $100\n
        15 : Profit for each day given a chosen week\n
        ''')
    user_input = int(input("Select which Query to Run: "))
    match user_input:
        case 1:
            SQL_COM = f'''
            SELECT EXTRACT(WEEK FROM date_time) AS week_number, COUNT(*) AS order_count
            FROM Order_History
            GROUP BY week_number
            ORDER BY week_number;
            '''
            fetchWeekOrderAmount(SQL_COM)
            week_input = int(input("Select a week: "))
            SQL_COM = f'''
            SELECT EXTRACT(WEEK FROM date_time) AS week_number, COUNT(*) AS order_count
            FROM Order_History
            WHERE EXTRACT(WEEK FROM date_time) = {week_input}
            GROUP BY week_number
            ORDER BY week_number;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 2:
            SQL_COM = f'''
            SELECT EXTRACT(HOUR FROM date_time) AS order_hour, COUNT(*) AS order_count, SUM(price) AS total_sales 
            FROM Order_History 
            GROUP BY order_hour ORDER BY order_hour;
            '''
            fetchWeekOrderAmount(SQL_COM)
            hour = input("Select an hour of the day out of 24 hrs: ")
            SQL_COM = f'''
            SELECT EXTRACT(HOUR FROM date_time) AS order_hour, COUNT(*) AS order_count, SUM(price) AS total_sales 
            FROM Order_History 
            WHERE EXTRACT(HOUR FROM date_time) = {hour}
            GROUP BY order_hour ORDER BY order_hour;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 3:
            date = input("Select an date of the year format yyyy-mm-dd: ")
            SQL_COM = f'''
            SELECT Order_Day, SUM(price) AS total_price_sum 
            FROM ( SELECT DATE(date_time) AS Order_Day, price 
                    FROM order_history 
                    WHERE DATE(date_time) = '{date}'
                    ORDER BY price DESC LIMIT 10 ) 
            AS top_10_orders
            GROUP BY Order_Day; 
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 4:
            SQL_COM = f'''
            SELECT m.Menu_Name, COUNT(i.Inventory_Id) AS item_count 
            FROM Menu_Items m JOIN Inventory_Menu im ON m.Menu_Id = im.Menu_Id JOIN Inventory i ON im.Inventory_Id = i.Inventory_Id 
            GROUP BY m.Menu_Name 
            ORDER BY item_count DESC;
            '''
            fetchWeekOrderAmount(SQL_COM)
            item = input("Select an item: ")
            SQL_COM = f'''
            SELECT m.Menu_Name, COUNT(i.Inventory_Id) AS item_count 
            FROM Menu_Items m JOIN Inventory_Menu im ON m.Menu_Id = im.Menu_Id JOIN Inventory i ON im.Inventory_Id = i.Inventory_Id 
            WHERE m.Menu_Name = '{item}'
            GROUP BY m.Menu_Name;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 5:
            SQL_COM = f'''
            SELECT o.order_item, COUNT(*) AS item_count 
            FROM Order_Items o JOIN Order_History h ON o.order_id = h.order_id 
            GROUP BY o.order_item 
            ORDER BY item_count DESC;
            '''
            fetchWeekOrderAmount(SQL_COM)

            date = input("Select an date of the year format yyyy-mm-dd: ")
            SQL_COM = f'''
            SELECT o.order_item, COUNT(*) AS item_count 
            FROM Order_Items o JOIN Order_History h ON o.order_id = h.order_id 
            WHERE date(h.date_time) = '{date}'
            GROUP BY o.order_item 
            ORDER BY item_count DESC LIMIT 1;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 6:
            SQL_COM = f'''
            SELECT 
                EXTRACT(WEEK FROM o.date_time) AS week_number, 
                o.order_id, 
                o.price
            FROM 
                Order_History o
            JOIN (
                SELECT 
                    EXTRACT(WEEK FROM date_time) AS week_number, 
                    MAX(price) AS max_price
                FROM 
                    Order_History
                GROUP BY 
                    week_number
            ) AS max_prices
            ON 
                EXTRACT(WEEK FROM o.date_time) = max_prices.week_number 
                AND o.price = max_prices.max_price
            ORDER BY 
                week_number;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 7:
            SQL_COM = f'''
            SELECT 
                EXTRACT(DAY FROM date_time) AS order_day, 
                SUM(price) AS total_price
            FROM 
                Order_History
            GROUP BY 
                order_day
            ORDER BY 
                total_price DESC LIMIT 5;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 8:
            SQL_COM = f'''
            SELECT 
                oi.order_item, 
                EXTRACT(WEEK FROM oh.date_time) AS week_number, 
                COUNT(*) AS frequency
            FROM 
                Order_Items AS oi 
            JOIN 
                Order_History AS oh 
                ON oi.order_id = oh.order_id
            WHERE 
                oi.order_item NOT IN ('Bowl', 'Plate', 'Drink', 'Family Meal', 'Cub Bowl', 'Carte', 'Bigger Plate')
            GROUP BY 
                oi.order_item, week_number
            ORDER BY 
                frequency;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 9:
            SQL_COM = f'''
            SELECT 
                EXTRACT(DAY FROM date_time) AS order_day, 
                SUM(price) AS total_price
            FROM 
                Order_History
            GROUP BY 
                order_day
            ORDER BY 
                total_price DESC LIMIT 5;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 10:
            SQL_COM = f'''
            SELECT date(date_time), order_id, price
            FROM Order_History
            ORDER BY price DESC
            LIMIT 5;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 11:
            SQL_COM = f'''
            SELECT 
                Order_Item,
                COUNT(*) AS frequency
            FROM 
                Order_Items
            GROUP BY 
                Order_Item
            ORDER BY 
                frequency DESC
            LIMIT 10;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 12:
            SQL_COM = f'''
            SELECT DATE(o.Date_Time) AS order_day, SUM(
            CASE 
                WHEN order_item = 'Bowl' THEN 1 ELSE 0 END) AS bowl_sales, SUM(
                CASE 
                    WHEN order_item = 'Plate' THEN 1 ELSE 0 END) AS plate_sales, SUM(
                CASE 
                    WHEN order_item = 'Drink' THEN 1 ELSE 0 END) AS drink_sales 
            FROM Order_Items AS oi INNER JOIN Order_History AS o ON oi.order_id = o.order_id GROUP BY DATE(o.Date_Time) 
            ORDER BY order_day;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 13:
            SQL_COM = f'''
            SELECT EXTRACT(WEEK FROM Date_Time) AS weeks, pay_method, COUNT(pay_method) 
            FROM Customer AS c JOIN Order_History AS oh ON c.customer_id = oh.customer_id 
            GROUP BY pay_method, weeks  
            ORDER BY weeks, count DESC;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 14:
            SQL_COM = f'''
                SELECT DATE(date_time), order_id, price 
                FROM Order_History 
                WHERE  price > 100 
                ORDER BY price DESC;
            '''
            fetchWeekOrderAmount(SQL_COM)
        case 15:
            date = input("Select a date of the year format yyyy-mm-dd: ")
            SQL_COM = f'''
            SELECT 
                DATE(oh.date_time) AS order_day,
                SUM(oh.price) AS total_profit
            FROM order_history oh
            WHERE DATE_TRUNC('week', oh.date_time) = DATE_TRUNC('week', '{date}'::date)
            GROUP BY order_day
            ORDER BY order_day;
            '''
            fetchWeekOrderAmount(SQL_COM)

    
# -------------------------------------------------------------------------------------------- 
def main():
    # Create each Table into the database. 
    # (At this point they do not contain PRIMARY or FOREIGN keys)

    while True:
        printMenu()

    return None

# -------------------------------------------------------------------------------------------- 
if __name__ == "__main__":
    main()