import random
import os
import subprocess
from datetime import datetime, timedelta

def generate_random_timestamp(start_date, end_date):
    random_seconds = random.randint(0, int((end_date - start_date).total_seconds()))
    random_date = start_date + timedelta(seconds=random_seconds)
    return random_date.strftime('%Y-%m-%d %H:%M:%S')

def make_menu_items():
    Menu_Items = {}
    Menu_Items["Orange Chicken"] = ["Chicken Breaded", "Orange Sauce"]
    Menu_Items["Honey Sesame Chicken"] = ["Breaded Chicken", "Green Bean", "Pepper", "Honey Sesame Sauce"]
    Menu_Items["Broccoli Beef"] = ["Beef", "Broccoli", "Soy Sauce"]
    Menu_Items["Honey Walnut Shrimp"] = ["Shrimp", "Honey Sesame Sauce", "Walnut"]
    Menu_Items["Kung Pao Chicken"] = ["Chicken Breast", "Zucchini", "Pepper", "Peanut", "Kung Pao Sauce"]
    Menu_Items["Black Pepper Angus Steak"] = ["Steak", "Onion", "Pepper", "Mushroom", "Black Pepper Sauce"]
    Menu_Items["Grilled Teriyaki Chicken"] = ["Chicken Breast", "Teriyaki Sauce"]
    Menu_Items["Black Pepper Chicken"] = ["Chicken Breast", "Celery", "Onion", "Black Pepper Sauce"]
    Menu_Items["Mushroom Chicken"] = ["Chicken Breast", "Mushroom", "Zucchini", "Soy Sauce"]
    Menu_Items["String Bean Chicken"] = ["Chicken Breast", "String Bean", "Soy Sauce"]
    Menu_Items["SweetFire Chicken"] = ["Chicken Breaded", "Pepper", "SweetFire Sauce"]
    Menu_Items["Firecracker Chicken"] = ["Chicken Breaded", "Pepper", "SweetFire Sauce"]
    Menu_Items["Beijing Beef"] = ["Beef", "Onion", "Pepper", "Beijing Sauce"]
    return Menu_Items

def make_side_items():
    Menu_Items = {}
    Menu_Items["Fried Rice"] = ["Rice", "Pea", "Carrot", "Egg", "Soy Sauce"]
    Menu_Items["Chow Mein"] = ["Noodle", "Celery", "Onion", "Soy Sauce"]
    Menu_Items["White Rice"] = ["White Rice"]
    Menu_Items["Brown Rice"] = ["Brown Rice"]
    Menu_Items["Super Greens"] = ["Broccoli", "Carrot", "Cabbage", "Zucchini"]
    return Menu_Items

def make_order_size():
    Order_Size = {}
    Order_Size["Bowl"] = [1, 1]
    Order_Size["Plate"] = [1, 2]
    Order_Size["Bigger Plate"] = [1, 3]
    Order_Size["Family Meal"] = [6, 9]
    Order_Size["Cub Bowl"] = [0, 1]
    Order_Size["Carte"] = [0, 1]
    return Order_Size

def generate_orders():
    menu_items = make_menu_items()
    side_items = make_side_items()
    order_size = make_order_size()
    
    # Set start_date and end_date to today
    today = datetime.now().replace(hour=9, minute=0, second=0, microsecond=0)
    start_date = today
    end_date = today.replace(hour=21, minute=0, second=0, microsecond=0)
    
    current_sales = 0
    total_orders = []
    
    # Generate a realistic number of orders for a single day
    target_daily_orders = random.randint(150, 250)  # Adjust this range as needed
    
    order_size_mapper = make_order_size()
    
    for _ in range(target_daily_orders):
        date = generate_random_timestamp(start_date, end_date)
        curr_order = []
        order_price = 0.00
        num_items = random.randint(1, 20)
        if num_items > 4:
            num_items = 1
        
        for j in range(num_items):
            order_size_chosen_index = random.randint(1, 70)
            price = 0.00
            if order_size_chosen_index <= 25:
                order_size_chosen = "Bowl"
                price += 8
            elif order_size_chosen_index <= 50:
                order_size_chosen = "Plate"
                price += 10
            elif order_size_chosen_index <= 60:
                order_size_chosen = "Bigger Plate"
                price += 15
            elif order_size_chosen_index <= 61:
                order_size_chosen = "Family Meal"
                price += 43
            elif order_size_chosen_index <= 62:
                order_size_chosen = "Cub Bowl"
                price += 43
            else:
                order_size_chosen = "Carte"
                price += 5
            curr_order.append(order_size_chosen)
            
            num_sides = order_size_mapper[order_size_chosen][0]
            num_entrees = order_size_mapper[order_size_chosen][1]
            
            for i in range(num_sides):
                side = random.choice(list(side_items.keys()))
                curr_order.append(side)
            
            for i in range(num_entrees):
                entree = random.choice(list(menu_items.keys()))
                curr_order.append(entree)
                if entree == "Honey Walnut Shrimp" or entree == "Black Pepper Angus Steak":
                    price += 1.50
            
            addDrink = False
            randomPicker = random.randint(1, 2)
            if randomPicker == 1:
                addDrink = True
                price += 2.00
                curr_order.append("Drink")
            
            randomPicker = random.randint(0, 10)
            randomQuantity = random.randint(1, 5)
            if randomQuantity > 3:
                randomQuantity = 1
            
            if randomPicker == 1:
                curr_order.append("Cream Cheese Rangoon")
                price += 2.00 * randomQuantity
                for i in range(randomQuantity):
                    curr_order.append("Cream Cheese Rangoon")
            elif randomPicker == 2:
                curr_order.append("Chicken Egg Roll")
                price += 2.00 * randomQuantity
                for i in range(randomQuantity):
                    curr_order.append("Chicken Egg Roll")
            elif randomPicker == 3:
                curr_order.append("Spring Roll")
                price += 2.00 * randomQuantity
                for i in range(randomQuantity):
                    curr_order.append("Spring Roll")
            elif randomPicker == 4:
                curr_order.append("Apple Pie")
                price += 2.00 * randomQuantity
                for i in range(randomQuantity):
                    curr_order.append("Apple Pie")
            
            order_price += price
        
        curr_order.append(order_price)
        curr_order.append(date)
        total_orders.append(curr_order)
        current_sales += order_price
    
    return total_orders, current_sales

DB_HOST = "csce-315-db.engr.tamu.edu"
DB_USER = "team_5g"
DB_NAME = "team_5g_db"
DB_PASSWORD = "thindoe99"

def execute_psql_command(sql_command):
    try:
        env = os.environ.copy()
        env["PGPASSWORD"] = DB_PASSWORD
        temp_sql_file_path = "tempfile.sql"
        with open(temp_sql_file_path, 'w') as temp_sql_file:
            temp_sql_file.write(sql_command)
        command = [
            "psql",
            "-h", DB_HOST,
            "-U", DB_USER,
            "-d", DB_NAME,
            "-a",
            "-f", temp_sql_file_path
        ]
        subprocess.run(command, check=True, text=True, capture_output=True, env=env)
        print("SQL file executed successfully.")
        return env
    except subprocess.CalledProcessError as e:
        print(f"Error executing {sql_command}: {e.stderr}")
        return None

menu_prices = {
    "Bowl": 8.3,
    "Plate": 9.8,
    "Bigger Plate": 11.3,
    "Family Meal": 43,
    "Cub Bowl": 8.25,
    "Carte": 5.2,
    "Drink": 2.3,
    "Orange Chicken": 0,
    "Honey Sesame Chicken": 0,
    "Broccoli Beef": 0,
    "Honey Walnut Shrimp": 1.5,
    "Kung Pao Chicken": 0,
    "Black Pepper Angus Steak": 1.5,
    "Grilled Teriyaki Chicken": 0,
    "Black Pepper Chicken": 0,
    "Mushroom Chicken": 0,
    "String Bean Chicken": 0,
    "SweetFire Chicken": 0,
    "Firecracker Chicken": 0,
    "Beijing Beef": 0,
    "Fried Rice": 0,
    "Chow Mein": 0,
    "White Rice": 0,
    "Brown Rice": 0,
    "Super Greens": 0,
    "Cream Cheese Rangoon": 2,
    "Chicken Egg Roll": 2,
    "Spring Roll": 2,
    "Apple Pie": 2
}

def main():
    total_orders, price = generate_orders()
    
    # Generate a realistic number of customers
    total_customer_count = random.randint(int(len(total_orders) * 0.8), len(total_orders))

    # Generate customer data
    customer_data = []
    for i in range(total_customer_count):
        order = total_orders[i]
        date_ordered = order[-1]
        price = order[-2]
        email = "".join(random.choices("abcdefghijklmnopqrstuvwxyz", k=7)) + "@gmail.com"
        pay_options = ['Card', 'Cash', 'Dining Dollars', 'Retail Swipe']
        pay_choice = random.choice(pay_options)
        customer_data.append(f"('{email}', '{pay_choice}', {price})")
    
    SQL_COM = f"INSERT INTO Customer (customer_email, pay_method, paid_amt) VALUES {', '.join(customer_data)};"
    execute_psql_command(SQL_COM)

    # Order history
    order_history_data = []
    for i, order in enumerate(total_orders):
        customer_id = i + 1
        if i >= total_customer_count:
            customer_id = random.randint(1, total_customer_count)
        date_ordered = order[-1]
        price = order[-2]
        order_history_data.append(f"({customer_id}, '{date_ordered}', {price})")

    SQL_COM = f"INSERT INTO Order_History (customer_id, date_time, price) VALUES {', '.join(order_history_data)};"
    execute_psql_command(SQL_COM)

    # Order items
    order_data = []
    for i, order in enumerate(total_orders):
        price = order[-2]
        curr_item = order[:-2]
        for j, item in enumerate(curr_item):
            individual_price = menu_prices[item]
            order_data.append(f"({i + 1}, '{item}', {individual_price})")

    SQL_COM = f"INSERT INTO Order_Items (order_id, order_item, price) VALUES {', '.join(order_data)};"
    execute_psql_command(SQL_COM)

if __name__ == "__main__":
    main()