from PyQt5 import QtWidgets, uic

class AddWindow(QtWidgets.QDialog):
    def __init__(self):
        super(AddWindow, self).__init__()
        uic.loadUi("add.ui", self)
        self.show()