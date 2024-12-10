import subprocess
import os
# --------------------------------------------------------------------------------------------
# Database connection parameters
DB_HOST = "csce-315-db.engr.tamu.edu"
DB_USER = "team_5g"
DB_NAME = "team_5g_db"
DB_PASSWORD = "thindoe99"
SQL_DIR = "schema/create_tables"
# --------------------------------------------------------------------------------------------
# Function to run .sql Files
def execute_psql_command(sql_file):
    try:
        env = os.environ.copy()
        env["PGPASSWORD"] = DB_PASSWORD

        command = [
            "psql",
            "-h", DB_HOST,
            "-U", DB_USER,
            "-d", DB_NAME,
            "-a",
            "-f", sql_file
        ]
        subprocess.run(command, check=True, text=True, capture_output=True, env=env)
        print("SQL file executed successfully.")
        return env

    except subprocess.CalledProcessError as e:
        print(f"Error executing {sql_file}: {e.stderr}")
        return None
# --------------------------------------------------------------------------------------------
def main():
    # Create each Table into the database. 
    # (At this point they do not contain PRIMARY or FOREIGN keys)
    SQL_DIR = "schema/create_tables"
    for filename in os.listdir(SQL_DIR):
        if filename.endswith(".sql"):
            filepath = os.path.join(SQL_DIR, filename)
            print(f"Executing {filepath}...")
            execute_psql_command(filepath)

    # Add the PRIMARY and FOREIGN Keys into the newly created Tables
    SQL_DIR = "schema/adjust_tables"
    for filename in os.listdir(SQL_DIR):
        if filename.endswith(".sql"):
            filepath = os.path.join(SQL_DIR, filename)
            print(f"Executing {filepath}...")
            execute_psql_command(filepath)

# --------------------------------------------------------------------------------------------
if __name__ == "__main__":
    main()
