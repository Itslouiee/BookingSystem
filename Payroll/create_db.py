import sqlite3

conn = sqlite3.connect("payroll.db")
cursor = conn.cursor()

# EMPLOYEES TABLE
cursor.execute("""
CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fullname TEXT,
    position TEXT,
    department TEXT,
    salary REAL
)
""")

# ADMIN TABLE
cursor.execute("""
CREATE TABLE IF NOT EXISTS admin (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT,
    password TEXT
)
""")

# ATTENDANCE TABLE
cursor.execute("""
CREATE TABLE IF NOT EXISTS attendance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER,
    date TEXT,
    time_in TEXT,
    time_out TEXT,
    hours REAL
)
""")

# PAYROLL TABLE
cursor.execute("""
CREATE TABLE IF NOT EXISTS payroll (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER,
    date TEXT,
    hours REAL,
    rate REAL,
    gross REAL,
    tax REAL,
    net REAL
)
""")

conn.commit()
conn.close()

print("Database and tables created!")