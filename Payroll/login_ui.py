# -*- coding: utf-8 -*-

from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import QDialog, QApplication
import admin_ui
import sys


class Ui_Dialog(object):
    def setupUi(self, Dialog):
        self.Dialog = Dialog
        Dialog.setObjectName("Dialog")
        Dialog.resize(661, 382)

        self.label = QtWidgets.QLabel(Dialog)
        self.label.setGeometry(QtCore.QRect(190, 20, 321, 51))
        font = QtGui.QFont()
        font.setFamily("Segoe UI")
        font.setPointSize(18)
        font.setBold(True)
        self.label.setFont(font)
        self.label.setStyleSheet("color: #2c3e50;")
        self.label.setText("COMPANY PAYROLL")

        self.username = QtWidgets.QLabel(Dialog)
        self.username.setGeometry(QtCore.QRect(180, 110, 121, 51))
        font = QtGui.QFont()
        font.setPointSize(12)
        font.setBold(True)
        self.username.setFont(font)
        self.username.setText("Username :")

        self.password = QtWidgets.QLabel(Dialog)
        self.password.setGeometry(QtCore.QRect(180, 190, 111, 31))
        font = QtGui.QFont()
        font.setPointSize(12)
        font.setBold(True)
        self.password.setFont(font)
        self.password.setText("Password :")

        self.username_input = QtWidgets.QLineEdit(Dialog)
        self.username_input.setGeometry(QtCore.QRect(310, 110, 151, 41))
        self.username_input.setPlaceholderText("Enter Username")

        self.password_input = QtWidgets.QLineEdit(Dialog)
        self.password_input.setGeometry(QtCore.QRect(310, 180, 151, 41))
        self.password_input.setEchoMode(QtWidgets.QLineEdit.Password)
        self.password_input.setPlaceholderText("Enter Password")

        self.login_btn = QtWidgets.QPushButton(Dialog)
        self.login_btn.setGeometry(QtCore.QRect(290, 260, 93, 28))
        self.login_btn.setText("Login")

        self.message_label = QtWidgets.QLabel(Dialog)
        self.message_label.setGeometry(QtCore.QRect(170, 310, 311, 31))
        font = QtGui.QFont()
        font.setFamily("MV Boli")
        font.setPointSize(14)
        self.message_label.setFont(font)
        self.message_label.setText("")

        self.login_btn.clicked.connect(self.login_function)

        QtCore.QMetaObject.connectSlotsByName(Dialog)

    def login_function(self):
        username = self.username_input.text()
        password = self.password_input.text()

        if username == "admin" and password == "admin1234":
            self.message_label.setText("Login Successful ✅")

            # Open Admin Dashboard
            self.dashboard = QDialog()
            self.ui = admin_ui.Ui_Dialog()
            self.ui.setupUi(self.dashboard)
            self.dashboard.show()

            # Close Login Window
            self.Dialog.close()

        else:
            self.message_label.setText("Invalid Username or Password ❌")


if __name__ == "__main__":
    app = QApplication(sys.argv)
    Dialog = QDialog()
    ui = Ui_Dialog()
    ui.setupUi(Dialog)
    Dialog.show()
    sys.exit(app.exec_())