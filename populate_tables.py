import random
import subprocess
import os

def get_unique_ingredients(menu_items):
    ingredients = set()
    for item in menu_items:
        for ingredient in menu_items[item]:
            ingredients.add(ingredient)
    return ingredients

def item_ingredient_map():
    Menu_Items = {}
    Menu_Items["Orange Chicken"] = ["Chicken Breaded", "Orange Sauce"]
    Menu_Items["Honey Sesame Chicken"] = ["Chicken Breaded", "String Bean", "Pepper", "Honey Sesame Sauce"]
    Menu_Items["Broccoli Beef"] = ["Beef", "Broccoli", "Soy Sauce"]
    Menu_Items["Honey Walnut Shrimp"] = ["Shrimp", "Honey Sesame Sauce", "Walnut"]
    Menu_Items["Kung Pao Chicken"] = ["Chicken Breast", "Zucchini", "Pepper", "Peanut", "Kung Pao Sauce"]
    Menu_Items["Black Pepper Angus Steak"] = ["Steak", "Onion", "Pepper", "Mushroom", "Black Pepper Sauce"]
    Menu_Items["Grilled Teriyaki Chicken"] = ["Chicken Breast", "Teriyaki Sauce"]
    Menu_Items["Black Pepper Chicken"] = ["Chicken Breast", "Celery", "Onion", "Black Pepper Sauce"]
    Menu_Items["Mushroom Chicken"] = ["Chicken Breast", "Mushroom", "Zucchini", "Soy Sauce"]
    Menu_Items["String Bean Chicken Breast"] = ["Chicken Breast", "String Bean", "Soy Sauce"]
    Menu_Items["SweetFire Chicken Breast"] = ["Chicken Breaded", "Pepper", "Sweetfire Sauce"]
    Menu_Items["Firecracker Chicken Breast"] = ["Chicken Breaded", "Pepper", "Sweetfire Sauce"]
    Menu_Items["Beijing Beef"] = ["Beef", "Onion", "Pepper", "Beijing Sauce"]
    Menu_Items["Fried Rice"] = ["White Rice", "Pea", "Carrot", "Eggs", "Soy Sauce"]
    Menu_Items["Chow Mein"] = ["Noodles", "Celery", "Onion", "Soy Sauce"]
    Menu_Items["White Rice"] = ["White Rice"]
    Menu_Items["Brown Rice"] = ["Brown Rice"]
    Menu_Items["Super Greens"] = ["Broccoli", "Carrot", "Cabbage", "Zucchini"]
    Menu_Items["Bowl"] = ["Bowls", "Napkins", "Silver-Ware", "Bags"]
    Menu_Items["Plate"] = ["Plates", "Napkins", "Silver-Ware", "Bags"]
    Menu_Items["Bigger Plate"] = ["Plates", "Napkins", "Silver-Ware", "Bags"]
    Menu_Items["Family Meal"] = ["Bowls", "Plates", "Napkins", "Silver-Ware", "To-go-boxes", "Bags"]
    Menu_Items["Cub Bowl"] = ["Bowls", "Napkins", "Silver-Ware", "Bags"]
    Menu_Items["Carte"] = ["A-la-carte boxes", "Napkins", "Silver-Ware", "Bags"]
    Menu_Items["Drink"] = ["Cups", "Straws"]

    return Menu_Items
# --------------------------------------------------------------------------------------------
def make_order_data():
    Order_Size = {}
    # string name, int id, float charge
    Order_Size["Bowl"] = ["Bowl", 0, 8.30, "Meal", 1, 1]
    Order_Size["Plate"] = ["Plate", 1, 9.80, "Meal", 1, 2]
    Order_Size["Bigger Plate"] = ["Bigger Plate", 2, 11.30, "Meal", 1, 3]
    Order_Size["Family Meal"] = ["Family Meal", 3, 43.00, "Meal", 6, 9]
    Order_Size["Cub Bowl"] = ["Cub Bowl", 4, 8.25, "Meal", 1, 1]
    Order_Size["Carte"] = ["Carte", 5, 5.20, "Meal", 0, 1]
    Order_Size["Drink"] = ["Drink", 6, 2.30, "Extra", 0, 0]
    # Order_Size["Side"] = ["Side", 7, 2.00, "Extra", 0, 0]
    # Order_Size["Dessert"] = ["Dessert", 8, 2.00, "Extra", 0, 0]

    return Order_Size
# --------------------------------------------------------------------------------------------

def employees_data():
    Employees = {}
    # manager id starts at 1, 0 is reserved for all employees
    Employees["David Wang"] = [0, "David Wang", 1, "972-57-1841", "2003-12-14", "972-572-1841", 25.00, "saladsyay@gmail.com", "shiftytrees"]
    Employees["Simon Song"] = [1, "Simon Song", 0, "829-19-1837", "2003-08-24", "972-572-1842", 10.00, "ssong@gmail.com", "shiftytrees"]
    Employees["Oscar Tsai"] = [2, "Oscar Tsai", 0, "927-47-9172", "2003-02-08", "972-572-1843", 10.00, "otsai@gmail.com", "shiftytrees"]
    Employees["David Cheung"] = [3, "David Cheung", 0, "917-47-9172", "2003-04-20", "972-572-1844", 10.00, "dc8@gmail.com", "shiftytrees"]
    Employees["Yoichiro Nishino"] = [4, "Yoichiro Nishino", 0, "9274-81-2364", "2004-09-04", "972-572-1846", 10.00, "ynishino@gmail.com", "shiftytrees"]

    return Employees

def inventory():
    inventory = {}
    # Supplies
    # Supplies
    inventory["Cups"] = [0, "Cups", 500, 300, 0, 1]
    inventory["Straws"] = [1, "Straws", 500, 300, 0, 1]
    inventory["Napkins"] = [2, "Napkins", 500, 300, 0, 1]
    inventory["Silver-Ware"] = [3, "Silver-Ware", 500, 300, 0, 1]
    inventory["To-go-boxes"] = [4, "To-go-boxes", 500, 300, 0, 1]
    inventory["Bowls"] = [5, "Bowls", 500, 300, 0, 1]
    inventory["Plates"] = [6, "Plates", 500, 300, 0, 1]
    inventory["Bags"] = [7, "Bags", 500, 300, 0, 1]
    inventory["A-la-carte boxes"] = [8, "A-la-carte boxes", 500, 300, 0, 1]

    # Meat
    inventory["Chicken Breaded"] = [9, "Chicken Breaded", 500, 300, 0, 1]
    inventory["Chicken Breast"] = [10, "Chicken Breast", 500, 300, 0, 1]
    inventory["Beef"] = [11, "Beef", 500, 300, 0, 1]
    inventory["Steak"] = [12, "Steak", 500, 300, 0, 1]
    inventory["Shrimp"] = [13, "Shrimp", 500, 300, 0, 1]

    # Vegetables
    inventory["Carrot"] = [14, "Carrot", 500, 300, 0, 1]
    inventory["Broccoli"] = [15, "Broccoli", 500, 300, 0, 1]
    inventory["String Bean"] = [16, "String Bean", 500, 300, 0, 1]
    inventory["Pea"] = [17, "Pea", 500, 300, 0, 1]
    inventory["Mushroom"] = [18, "Mushroom", 500, 300, 0, 1]
    inventory["Zucchini"] = [19, "Zucchini", 500, 300, 0, 1]
    inventory["Peanut"] = [20, "Peanut", 500, 300, 0, 1]
    inventory["Walnut"] = [21, "Walnut", 500, 300, 0, 1]
    inventory["Onion"] = [22, "Onion", 500, 300, 0, 1]
    inventory["Pepper"] = [23, "Pepper", 500, 300, 0, 1]
    inventory["Cabbage"] = [24, "Cabbage", 500, 300, 0, 1]
    inventory["Celery"] = [25, "Celery", 500, 300, 0, 1]
    inventory["Green onion"] = [26, "Green onion", 500, 300, 0, 1]
    inventory["Eggs"] = [27, "Eggs", 500, 300, 0, 1]

    # Starch
    inventory["Noodles"] = [28, "Noodles", 500, 300, 0, 1]
    inventory["White Rice"] = [29, "White Rice", 500, 300, 0, 1]
    inventory["Brown Rice"] = [30, "Brown Rice", 500, 300, 0, 1]

    # Sauces
    inventory["Oil"] = [31, "Oil", 500, 300, 0, 1]
    inventory["Sauce"] = [32, "Sauce", 500, 300, 0,1]
    inventory["Batter"] = [33, "Batter", 500, 300, 0, 1]
    inventory["Teriyaki Chicken"] = [34, "Teriyaki Chicken", 500, 300, 0, 1]
    inventory["Soy Sauce"] = [35, "Soy Sauce", 500, 300, 0, 1]
    inventory["Teriyaki Sauce"] = [36, "Teriyaki Sauce", 500, 300, 0, 1]
    inventory["Orange Sauce"] = [37, "Orange Sauce", 500, 300, 0, 1]
    inventory["Sweetfire Sauce"] = [38, "Sweetfire Sauce", 500, 300, 0, 1]
    inventory["Beijing Sauce"] = [39, "Beijing Sauce", 500, 300, 0, 1]
    inventory["Black Pepper Sauce"] = [40, "Black Pepper Sauce", 500, 300, 0, 1]
    inventory["Kung Pao Sauce"] = [41, "Kung Pao Sauce", 500, 300, 0, 1]
    inventory["Honey Sesame Sauce"] = [42, "Honey Sesame Sauce", 500, 300, 0, 1]

    # Sides
    inventory["Cream Cheese Rangoons"] = [43, "Cream Cheese Rangoons", 500, 300, 0, 1]
    inventory["Spring Roll"] = [44, "Spring Roll", 500, 300, 0, 1]
    inventory["Chicken Egg Roll"] = [45, "Chicken Egg Roll", 500, 300, 0, 1]
    inventory["Apple Pie"] = [46, "Apple Pie", 500, 300, 0, 1]

    return inventory

DB_HOST = "csce-315-db.engr.tamu.edu"
DB_USER = "team_5g"
DB_NAME = "team_5g_db"
DB_PASSWORD = "thindoe99"

def execute_psql_command(sql_command):
    try:
        env = os.environ.copy()
        env["PGPASSWORD"] = DB_PASSWORD

        command = [
            "psql",
            "-h", DB_HOST,
            "-U", DB_USER,
            "-d", DB_NAME,
            "-a",
            "-c", sql_command
        ]
        subprocess.run(command, check=True, text=True, capture_output=True, env=env)
        print("SQL file executed successfully.")
        return env

    except subprocess.CalledProcessError as e:
        print(f"Error executing {sql_command}: {e.stderr}")
        return None
# --------------------------------------------------------------------------------------------
def main():
    SQL_COM = "SET datestyle = 'ISO, MDY';"
    execute_psql_command(SQL_COM)

    employees = employees_data()
    for emp in employees:
        SQL_COM = f"INSERT INTO Employee (employee_name, manager_id, ssn, dob, phone_num, salary, email, pword) VALUES ('{employees[emp][1]}', {employees[emp][2]}, '{employees[emp][3]}', '{employees[emp][4]}', '{employees[emp][5]}', '{employees[emp][6]}', '{employees[emp][7]}', '{employees[emp][8]}');"
        execute_psql_command(SQL_COM)

    menu_items = item_ingredient_map()
    items_in_order = list(menu_items.keys())
    for item in items_in_order:
        if item != "Bowl" and item != "Plate" and item != "Bigger Plate" and item != "Family Meal" and item != "Cub Bowl" and item != "Carte" and item != "Drink":
            price = 0
            if item == "Honey Walnut Shrimp" or item == "Black Pepper Angus Steak":
                price = 1.50
            if item == "White Rice" or item == "Brown Rice" or item == "Super Greens" or item == "Chow Mein" or item == "Fried Rice":
                type = "Sides"
                SQL_COM = f"INSERT INTO Menu_Items (menu_name, charge, Menu_Type, Max_Sides, Max_Entrees) VALUES ('{item}', {price}, '{type}', 0, 0);"
            else:
                type = "Entrees"
                SQL_COM = f"INSERT INTO Menu_Items (menu_name, charge, Menu_Type, Max_Sides, Max_Entrees) VALUES ('{item}', {price}, '{type}', 0, 0);"
            execute_psql_command(SQL_COM)
    
    order_data = make_order_data()
    for order in order_data:
        SQL_COM = f"INSERT INTO Menu_Items (menu_name, charge, Menu_Type, Max_Sides, Max_Entrees) VALUES ('{order_data[order][0]}', {order_data[order][2]}, '{order_data[order][3]}', '{order_data[order][4]}', '{order_data[order][5]}');"
        execute_psql_command(SQL_COM)
    
    sides = ["Cream Cheese Rangoon", "Chicken Egg Roll", "Spring Roll", "Apple Pie"]
    for side in sides:
        type = "Extra"
        SQL_COM = f"INSERT INTO Menu_Items (menu_name, charge, Menu_Type, Max_Sides, Max_Entrees) VALUES ('{side}', 2.00, '{type}', 0, 0);"
        execute_psql_command(SQL_COM)
        
    inventory_list = inventory()
    for inv in inventory_list:
        SQL_COM = f"INSERT INTO Inventory (inven_name, stock_amt, use_per_month, price, employee_id) VALUES ('{inventory_list[inv][1]}', {inventory_list[inv][2]}, {inventory_list[inv][3]}, {inventory_list[inv][4]}, {inventory_list[inv][5]});"
        execute_psql_command(SQL_COM)

    ingredient_ids = list(inventory_list.keys())
    menu_ids = list(menu_items.keys())
    for m_item, ingredients in menu_items.items():
        for ingredient in ingredients:
            ingredient_id = ingredient_ids.index(ingredient) + 1
            menu_id = menu_ids.index(m_item) + 1
            SQL_COM = f"INSERT INTO Inventory_Menu (inventory_name, menu_id, inventory_id) VALUES ('{ingredient}', {menu_id}, {ingredient_id});"
            execute_psql_command(SQL_COM)

if __name__ == "__main__":
    main()