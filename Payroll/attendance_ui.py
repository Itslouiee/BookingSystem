from PyQt5 import QtWidgets, uic
from PyQt5.QtWidgets import QMessageBox
import sqlite3


class AttendanceWindow(QtWidgets.QDialog):
    def __init__(self):
        super().__init__()
        uic.loadUi("attendance.ui", self)  # Load the UI file

        # Load employees into the dropdown
        self.load_employees()

        # Connect buttons
        self.save_attendance_btn.clicked.connect(self.save_attendance)
        self.clear_btn.clicked.connect(self.clear_fields)
        self.back_btn.clicked.connect(self.close)

    # =========================
    # LOAD EMPLOYEES
    # =========================
    def load_employees(self):
        try:
            with sqlite3.connect("payroll.db") as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT id, fullname FROM employees")
                employees = cursor.fetchall()
        except Exception as e:
            QMessageBox.critical(self, "Database Error", str(e))
            return

        self.employee_select.clear()
        for emp in employees:
            self.employee_select.addItem(emp[1], emp[0])  # Display name, store ID

    # =========================
    # SAVE ATTENDANCE
    # =========================
    def save_attendance(self):
        employee_id = self.employee_select.currentData()
        date = self.date_input.date().toString("yyyy-MM-dd")
        time_in = self.time_in_input.time().toString("HH:mm")
        time_out = self.time_out_input.time().toString("HH:mm")
        hours = self.hours_input.text()

        # Validate fields
        if not hours:
            QMessageBox.warning(self, "Error", "Please enter hours worked")
            return

        try:
            with sqlite3.connect("payroll.db") as conn:
                cursor = conn.cursor()
                cursor.execute("""
                    INSERT INTO attendance (employee_id, date, time_in, time_out, hours)
                    VALUES (?, ?, ?, ?, ?)
                """, (employee_id, date, time_in, time_out, hours))
                # Database automatically commits and closes with 'with'
        except Exception as e:
            QMessageBox.critical(self, "Database Error", str(e))
            return

        QMessageBox.information(self, "Success", "Attendance saved!")
        self.clear_fields()

    # =========================
    # CLEAR FIELDS
    # =========================
    def clear_fields(self):
        self.hours_input.clear()
        self.time_in_input.setTime(self.time_in_input.minimumTime())
        self.time_out_input.setTime(self.time_out_input.minimumTime())
        self.date_input.setDate(self.date_input.minimumDate())
        self.employee_select.setCurrentIndex(0)