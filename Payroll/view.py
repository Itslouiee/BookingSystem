import sqlite3
from PyQt5 import QtWidgets
from PyQt5.QtWidgets import QDialog, QTableWidgetItem
from view_ui import Ui_Dialog


class ViewEmployeesWindow(QDialog):

    def __init__(self):
        super().__init__()

        self.ui = Ui_Dialog()
        self.ui.setupUi(self)

        self.ui.refresh_btn.clicked.connect(self.load_employees)
        self.ui.back_btn.clicked.connect(self.close)

        self.load_employees()

    def load_employees(self):

        conn = sqlite3.connect("payroll.db")
        cursor = conn.cursor()

        cursor.execute("SELECT id, fullname, position, department, salary FROM employees")
        rows = cursor.fetchall()

        table = self.ui.employee_table
        table.setRowCount(0)
        table.setColumnCount(5)

        table.setHorizontalHeaderLabels([
            "ID", "Fullname", "Position", "Department", "Salary"
        ])

        for row_number, row_data in enumerate(rows):

            table.insertRow(row_number)

            for column_number, data in enumerate(row_data):

                table.setItem(
                    row_number,
                    column_number,
                    QTableWidgetItem(str(data))
                )

        conn.close()