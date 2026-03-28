import sqlite3

conn = sqlite3.connect("payroll.db")
cursor = conn.cursor()

def add_column(column, datatype):
    try:
        cursor.execute(f"ALTER TABLE payroll ADD COLUMN {column} {datatype}")
        print(column + " column added")
    except:
        print(column + " already exists")

add_column("date", "TEXT")
add_column("hours", "REAL")
add_column("rate", "REAL")

conn.commit()
conn.close()

print("Payroll table fixed!")