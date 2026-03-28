# employeepayroll.py
import sys
import sqlite3
from PyQt5.QtWidgets import QApplication, QDialog, QMessageBox
from PyQt5 import QtWidgets


from login_ui import Ui_Dialog             # Login UI
from admin_ui import Ui_Dialog as Ui_Admin # Admin Dashboard UI
from add_ui import Ui_Dialog as Ui_Add     # Add Employee UI
from view_ui import Ui_Dialog as Ui_View   # View Employees UI
from attendance_ui import AttendanceWindow
from calculate_ui import CalculatePayrollWindow
from records_ui import PayrollRecordsWindow


# =======================
# DATABASE SETUP
# =======================
def init_db():
    conn = sqlite3.connect("payroll.db")
    cursor = conn.cursor()

    # Employees table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS employees (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        fullname TEXT,
        position TEXT,
        department TEXT,
        salary REAL
    )
    """)

    # Admin table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS admin (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT UNIQUE,
        password TEXT
    )
    """)
    cursor.execute("INSERT OR IGNORE INTO admin (username, password) VALUES (?, ?)", ("admin", "admin123"))

    # Attendance table
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

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS payroll (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       employee_id INTEGER,
       date TEXT,
       hours REAL,
       rate REAL,
       gross REAL,
       net REAL
    )
    """)

    conn.commit()
    conn.close()

# Initialize database
init_db()

# =======================
# ADD EMPLOYEE WINDOW
# =======================
class AddEmployeeWindow(QDialog):
    def __init__(self):
        super().__init__()
        self.ui = Ui_Add()
        self.ui.setupUi(self)
        self.ui.save_employee_btn.clicked.connect(self.save_employee)
        self.ui.cancel_btn.clicked.connect(self.close)

    def save_employee(self):
        fullname = self.ui.employee_name_input.text()
        position = self.ui.employee_position_input.text()
        department = self.ui.employee_position_input.text()

        try:
            salary = float(self.ui.employee_rate_input.text())
        except ValueError:
            QMessageBox.warning(self, "Error", "Salary must be a number")
            return

        if not fullname or not position or not department:
            QMessageBox.warning(self, "Error", "Please fill all fields")
            return

        try:
            with sqlite3.connect("payroll.db") as conn:
                cursor = conn.cursor()
                cursor.execute(
                    "INSERT INTO employees(fullname, position, department, salary) VALUES (?, ?, ?, ?)",
                    (fullname, position, department, salary)
                )
        except Exception as e:
            QMessageBox.critical(self, "Database Error", str(e))
            return

        QMessageBox.information(self, "Success", f"Employee {fullname} added!")
        self.close()

# =======================
# VIEW EMPLOYEES WINDOW
# =======================
class ViewEmployeesWindow(QDialog):
    def __init__(self):
        super().__init__()
        self.ui = Ui_View()
        self.ui.setupUi(self)
        self.ui.refresh_btn.clicked.connect(self.load_employees)
        self.ui.back_btn.clicked.connect(self.close)
        self.load_employees()

    def load_employees(self):
        try:
            with sqlite3.connect("payroll.db") as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT id, fullname, position, department, salary FROM employees")
                employees = cursor.fetchall()
        except Exception as e:
            QMessageBox.critical(self, "Database Error", str(e))
            return

        self.ui.employee_table.setRowCount(0)
        self.ui.employee_table.setColumnCount(5)
        self.ui.employee_table.setHorizontalHeaderLabels(["ID","Name","Position","Department","Salary"])

        for row_number, row_data in enumerate(employees):
            self.ui.employee_table.insertRow(row_number)
            for column_number, data in enumerate(row_data):
                self.ui.employee_table.setItem(row_number, column_number, QtWidgets.QTableWidgetItem(str(data)))

# =======================
# ADMIN DASHBOARD WINDOW
# =======================
class AdminDashboard(QDialog):
    def __init__(self):
        super().__init__()
        self.ui = Ui_Admin()
        self.ui.setupUi(self)

        self.ui.add_employee_btn.clicked.connect(self.open_add_employee)
        self.ui.view_employee_btn.clicked.connect(self.open_view_employees)
        self.ui.attendance_btn.clicked.connect(self.open_attendance)
        self.ui.calculate_payroll_btn.clicked.connect(self.calculate_payroll)
        self.ui.payroll_records_btn.clicked.connect(self.payroll_records)
        self.ui.logout_btn.clicked.connect(self.logout)

    def open_add_employee(self):
        self.add_window = AddEmployeeWindow()
        self.add_window.show()

    def open_view_employees(self):
        self.view_window = ViewEmployeesWindow()
        self.view_window.show()

    def open_attendance(self):
        self.attendance_window = AttendanceWindow()
        self.attendance_window.show()

    def calculate_payroll(self):
        self.calculate_window = CalculatePayrollWindow()
        self.calculate_window.show()

    def payroll_records(self):
        self.records_window = PayrollRecordsWindow()
        self.records_window.show()

    def logout(self):
        self.login_window = LoginWindow()
        self.login_window.show()
        self.close()

# =======================
# LOGIN WINDOW
# =======================
class LoginWindow(QDialog):
    def __init__(self):
        super().__init__()
        self.ui = Ui_Dialog()
        self.ui.setupUi(self)
        self.ui.login_btn.clicked.connect(self.login)

    def login(self):
        username = self.ui.username_input.text()
        password = self.ui.password_input.text()

        try:
            with sqlite3.connect("payroll.db") as conn:
                cursor = conn.cursor()
                cursor.execute(
                    "SELECT * FROM admin WHERE username=? AND password=?",
                    (username, password)
                )
                admin = cursor.fetchone()
        except Exception as e:
            QMessageBox.critical(self, "Database Error", str(e))
            return

        if admin:
            self.open_dashboard()
        else:
            self.ui.message_label.setText("Invalid admin account")

    def open_dashboard(self):
        self.dashboard = AdminDashboard()
        self.dashboard.show()
        self.close()

# =======================
# RUN APPLICATION
# =======================
if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = LoginWindow()
    window.show()
    sys.exit(app.exec_())
