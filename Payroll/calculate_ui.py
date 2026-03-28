import sqlite3
from datetime import date
from PyQt5 import QtWidgets, uic
from PyQt5.QtWidgets import QDialog, QMessageBox


class CalculatePayrollWindow(QDialog):

    def __init__(self):
        super().__init__()
        uic.loadUi("calculate.ui", self)

        # BUTTON CONNECTIONS
        self.calculate_btn.clicked.connect(self.calculate_payroll)
        self.save_payroll_btn.clicked.connect(self.save_payroll)
        self.clear_btn.clicked.connect(self.clear_fields)
        self.back_btn.clicked.connect(self.close)

        # LOAD EMPLOYEES
        self.load_employees()

    # LOAD EMPLOYEE LIST
    def load_employees(self):

        conn = sqlite3.connect("payroll.db")
        cursor = conn.cursor()

        cursor.execute("SELECT id, fullname FROM employees")
        employees = cursor.fetchall()

        self.employee_select.clear()

        for emp in employees:
            self.employee_select.addItem(emp[1], emp[0])

        conn.close()

    # CALCULATE PAYROLL
    def calculate_payroll(self):

        try:
            rate = float(self.rate_input.text())
            hours = float(self.hours_input.text())
            tax_rate = float(self.tax_input.text()) / 100

        except:
            QMessageBox.warning(self, "Error", "Invalid input")
            return

        gross = rate * hours
        tax_amount = gross * tax_rate
        net = gross - tax_amount

        self.gross_input.setText(str(round(gross, 2)))
        self.tax_amount_input.setText(str(round(tax_amount, 2)))
        self.net_input.setText(str(round(net, 2)))

    # SAVE PAYROLL TO DATABASE
    def save_payroll(self):

        employee_id = self.employee_select.currentData()

        if self.gross_input.text() == "":
            QMessageBox.warning(self, "Error", "Please calculate payroll first")
            return

        try:
            rate = float(self.rate_input.text())
            hours = float(self.hours_input.text())
            gross = float(self.gross_input.text())
            tax = float(self.tax_amount_input.text())
            net = float(self.net_input.text())
        except:
            QMessageBox.warning(self, "Error", "Invalid values")
            return

        today = date.today()

        conn = sqlite3.connect("payroll.db")
        cursor = conn.cursor()

        cursor.execute("""
        INSERT INTO payroll (employee_id, date, hours, rate, gross, tax, net)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """, (employee_id, today, hours, rate, gross, tax, net))

        conn.commit()
        conn.close()

        QMessageBox.information(self, "Success", "Payroll saved successfully!")

    # CLEAR INPUT FIELDS
    def clear_fields(self):

        self.rate_input.clear()
        self.hours_input.clear()
        self.tax_input.clear()
        self.gross_input.clear()
        self.tax_amount_input.clear()
        self.net_input.clear()