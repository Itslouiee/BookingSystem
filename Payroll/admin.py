from PyQt5 import QtWidgets, uic
import add_ui
import view_ui
import calculate_ui
import records_ui
import login_ui

class AdminWindow(QtWidgets.QDialog):
    def __init__(self):
        super(AdminWindow, self).__init__()
        uic.loadUi("admin.ui", self)

        self.add_btn.clicked.connect(self.open_add)
        self.view_btn.clicked.connect(self.open_view)
        self.attendance_btn.clicked.connect(self.open_attendance)
        self.calculate_btn.clicked.connect(self.open_calculate)
        self.records_btn.clicked.connect(self.open_records)
        self.logout_btn.clicked.connect(self.logout)

    def open_add(self):
        self.window = add_ui.AddWindow()
        self.window.show()

    def open_view(self):
        self.window = view_ui.ViewWindow()
        self.window.show()

    def open_attendance(self):
        QtWidgets.QMessageBox.information(self,"Attendance","Attendance Window")

    def open_calculate(self):
        self.window = calculate_ui.CalculateWindow()
        self.window.show()

    def open_records(self):
        self.window = records_ui.RecordsWindow()
        self.window.show()

    def logout(self):
        self.login = login_ui.LoginWindow()
        self.login.show()
        self.close()

