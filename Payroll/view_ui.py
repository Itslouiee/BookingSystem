# -*- coding: utf-8 -*-

from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_Dialog(object):
    def setupUi(self, Dialog):
        Dialog.setObjectName("Dialog")
        Dialog.resize(578, 442)

        self.label = QtWidgets.QLabel(Dialog)
        self.label.setGeometry(QtCore.QRect(160, 10, 281, 71))

        font = QtGui.QFont()
        font.setPointSize(18)
        font.setBold(True)

        self.label.setFont(font)
        self.label.setObjectName("label")

        self.employee_table = QtWidgets.QTableWidget(Dialog)
        self.employee_table.setGeometry(QtCore.QRect(90, 80, 391, 181))
        self.employee_table.setColumnCount(0)
        self.employee_table.setRowCount(0)
        self.employee_table.setObjectName("employee_table")

        self.refresh_btn = QtWidgets.QPushButton(Dialog)
        self.refresh_btn.setGeometry(QtCore.QRect(90, 320, 151, 28))
        self.refresh_btn.setObjectName("refresh_btn")

        self.back_btn = QtWidgets.QPushButton(Dialog)
        self.back_btn.setGeometry(QtCore.QRect(330, 320, 151, 28))
        self.back_btn.setObjectName("back_btn")

        self.retranslateUi(Dialog)
        QtCore.QMetaObject.connectSlotsByName(Dialog)

    def retranslateUi(self, Dialog):
        _translate = QtCore.QCoreApplication.translate
        Dialog.setWindowTitle(_translate("Dialog", "Dialog"))
        self.label.setText(_translate("Dialog", "VIEW EMPLOYEES"))
        self.refresh_btn.setText(_translate("Dialog", "REFRESH"))
        self.back_btn.setText(_translate("Dialog", "BACK"))