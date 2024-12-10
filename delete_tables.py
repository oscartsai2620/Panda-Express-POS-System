import subprocess
import os
# --------------------------------------------------------------------------------------------
# Database connection parameters
DB_HOST = "csce-315-db.engr.tamu.edu"
DB_USER = "team_5g"
DB_NAME = "team_5g_db"
DB_PASSWORD = "thindoe99"
# --------------------------------------------------------------------------------------------
def execute_psql_command(command):
    try:
        env = os.environ.copy()
        env["PGPASSWORD"] = DB_PASSWORD

        full_command = [
            "psql",
            "-h", DB_HOST,
            "-U", DB_USER,
            "-d", DB_NAME,
            "-c", command
        ]
        subprocess.run(full_command, check=True, text=True, capture_output=True, env=env)
        print(f"Executed command: {command}")
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {e.stderr}")

def get_all_tables():
    try:
        env = os.environ.copy()
        env["PGPASSWORD"] = DB_PASSWORD

        command = [
            "psql",
            "-h", DB_HOST,
            "-U", DB_USER,
            "-d", DB_NAME,
            "-t", "-c", "SELECT tablename FROM pg_tables WHERE schemaname='public';"
        ]
        result = subprocess.run(command, check=True, text=True, capture_output=True, env=env)
        tables = result.stdout.strip().split('\n')
        tables = [table.strip() for table in tables if table.strip()]
        return tables
    except subprocess.CalledProcessError as e:
        print(f"Error fetching tables: {e.stderr}")
        return []
# --------------------------------------------------------------------------------------------
def main():
    tables = get_all_tables()
    for table in tables:
        print(f"Dropping table: {table}")
        execute_psql_command(f"DROP TABLE IF EXISTS {table} CASCADE;")
# --------------------------------------------------------------------------------------------
if __name__ == "__main__":
    main()