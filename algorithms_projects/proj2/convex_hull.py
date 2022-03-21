from which_pyqt import PYQT_VER

if PYQT_VER == 'PYQT5':
    from PyQt5.QtCore import QLineF, QPointF, QObject
elif PYQT_VER == 'PYQT4':
    from PyQt4.QtCore import QLineF, QPointF, QObject
else:
    raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))

import time
import numpy as np

# Some global color constants that might be useful
RED = (255, 0, 0)
GREEN = (0, 255, 0)
BLUE = (0, 0, 255)

# Global variable that controls the speed of the recursion automation, in seconds
#
PAUSE = 0.25

class Hull2():

    @staticmethod
    def next(index, points):
        if index == len(points)-1:
            return points[0]
        return points[index+1]

    @staticmethod
    def previous(index, points):
        if index == points[0]:
            return points[-1]
        return points[index-1]

class Hull():

    @staticmethod
    def next(point, points):
        if point not in points:
            return ("point not in Hull")

        index = points.index(point)
        if index < len(points) - 1:
            return points[index + 1]
        if index == len(points) - 1:
            return points[0]

    @staticmethod
    def previous(point, points):
        if point not in points:
            return ("point not in Hull")

        index = points.index(point)
        if index > 0:
            return points[index - 1]
        if index == 0:
            return points[-1]


#
# This is the class you have to complete.
#
class ConvexHullSolver(QObject):

    # Class constructor
    def __init__(self):
        super().__init__()
        self.pause = False

    # Some helper methods that make calls to the GUI, allowing us to send updates
    # to be displayed.

    def showTangent(self, line, color):
        self.view.addLines(line, color)
        if self.pause:
            time.sleep(PAUSE)

    def eraseTangent(self, line):
        self.view.clearLines(line)

    def blinkTangent(self, line, color):
        self.showTangent(line, color)
        self.eraseTangent(line)

    def showHull(self, polygon, color):
        self.view.addLines(polygon, color)
        if self.pause:
            time.sleep(PAUSE)

    def eraseHull(self, polygon):
        self.view.clearLines(polygon)

    def showText(self, text):
        self.view.displayStatusText(text)

    # helper function that merges two hulls using upper and lower tangents
    def merge(self, leftHull, rightHull, upperTangent, lowerTangent):

        hull = []
        if (len(leftHull) == 1 and len(rightHull) == 1):
            return [leftHull[0], rightHull[0]]

        hull.append(upperTangent.p1())
        point = upperTangent.p1()

        if(upperTangent.p1() != lowerTangent.p1()):
            while Hull().next(point, leftHull) != lowerTangent.p1():
                hull.append(Hull().next(point, leftHull))
                point = Hull().next(point, leftHull)
            if lowerTangent.p1() != hull[-1]:
                hull.append(lowerTangent.p1())
        hull.append(lowerTangent.p2())
        point = lowerTangent.p2()
        if (lowerTangent.p2() != upperTangent.p2()):
            while Hull().next(point, rightHull) != upperTangent.p2():

                hull.append(Hull().next(point, rightHull))
                point = Hull().next(point, rightHull)

            if(upperTangent.p2() != hull[-1]):
                hull.append(upperTangent.p2())

        return hull



    def getSlope(self, p1, p2):
        return (p2.y() - p1.y()) / (p2.x() - p1.x())

    def getUpperTangent(self, leftHull, rightHull):

        """
        basic Idea:
        - create a copy of the hulls that we can sort by x-value to get innerPoints
        - do the walking algorithm to get to the upperTangent.
        :param leftHull:
        :param rightHull:
        :return:
        """

        if(len(leftHull) == 1 and len(rightHull) == 1):
            return QLineF(leftHull[0], rightHull[0])

        leftList = leftHull.copy()
        rightList = rightHull.copy()
        leftList.sort(key=lambda x: x.x())

        rightList.sort(key=lambda x: x.x())

        upperTangent = [leftList[-1], rightList[0]]
        stop = False
        while not stop:
            checkLeft = upperTangent[0]
            checkRight = upperTangent[1]
            keepGoing = True
            while keepGoing:

                if(len(leftHull) == 1):
                    break

                slope1 = self.getSlope(upperTangent[0], upperTangent[1])
                upperTangent[0] = Hull().next(upperTangent[0], leftHull)
                slope2 = self.getSlope(upperTangent[0], upperTangent[1])
                if slope2 >= slope1:
                    upperTangent[0] = Hull().previous(upperTangent[0], leftHull)
                    keepGoing = False

            keepGoing = True
            while keepGoing:

                if (len(rightHull) == 1):
                    break

                slope1 = self.getSlope(upperTangent[0], upperTangent[1])
                upperTangent[1] = Hull().previous(upperTangent[1], rightHull)
                slope2 = self.getSlope(upperTangent[0], upperTangent[1])
                if slope2 <= slope1:
                    upperTangent[1] = Hull().next(upperTangent[1], rightHull)
                    keepGoing = False
            if checkLeft == upperTangent[0] and checkRight == upperTangent[1]:
                stop = True

        return QLineF(*upperTangent)

    def getLowerTangent(self, leftHull, rightHull):
        """
        basic Idea:
        - create a copy of the hulls that we can sort by x-value to get innerPoints
        - do the walking algorithm to get to the upperTangent.
        :param leftHull:
        :param rightHull:
        :return:
        """

        if (len(leftHull) == 1 and len(rightHull) == 1):
            return QLineF(leftHull[0], rightHull[0])

        leftList = leftHull.copy()
        rightList = rightHull.copy()

        leftList.sort(key=lambda x: x.x())

        rightList.sort(key=lambda x: x.x())


        lowerTangent = [leftList[-1], rightList[0]]
        stop = False
        while not stop:
            # print("loop 4")
            checkLeft = lowerTangent[0]
            checkRight = lowerTangent[1]
            keepGoing = True
            while keepGoing:

                if (len(leftHull) == 1):
                    break

                slope1 = self.getSlope(lowerTangent[0], lowerTangent[1])
                lowerTangent[0] = Hull().previous(lowerTangent[0], leftHull)
                slope2 = self.getSlope(lowerTangent[0], lowerTangent[1])
                if slope2 <= slope1:
                    lowerTangent[0] = Hull().next(lowerTangent[0], leftHull)
                    keepGoing = False

            keepGoing = True
            while keepGoing:

                if (len(rightHull) == 1):
                    break

                slope1 = self.getSlope(lowerTangent[0], lowerTangent[1])
                lowerTangent[1] = Hull().next(lowerTangent[1], rightHull)
                slope2 = self.getSlope(lowerTangent[0], lowerTangent[1])

                if slope2 >= slope1:
                    lowerTangent[1] = Hull().previous(lowerTangent[1], rightHull)
                    keepGoing = False
            if checkLeft == lowerTangent[0] and checkRight == lowerTangent[1]:
                stop = True

        return QLineF(*lowerTangent)

    # this is my recursive hull finder
    def dncHull(self, points):


        if len(points) == 1:
            return points  # singleton hull

        leftList = [points[i] for i in range(len(points) // 2)]
        rightList = [points[i] for i in range(len(points) // 2, len(points))]

        leftHull = self.dncHull(leftList)
        rightHull = self.dncHull(rightList)

        upperTangent = self.getUpperTangent(leftHull, rightHull)

        lowerTangent = self.getLowerTangent(leftHull, rightHull)

        hull = self.merge(leftHull, rightHull, upperTangent, lowerTangent)  # THIS WORKS!!

        return hull

    def lineMaker(self, hull):
        lines = []
        for point in hull:
            line = QLineF(point, Hull().next(point, hull))
            lines.append(line)

        return lines


    # This is the method that gets called by the GUI and actually executes
    # the finding of the hull
    def compute_hull(self, points, pause, view):
        # print("PROGRAM START")
        self.pause = pause
        self.view = view
        assert (type(points) == list and type(points[0]) == QPointF)
        t1 = time.time()
        points.sort(key=lambda x: x.x())
        t2 = time.time()

        t3 = time.time()

        hull = self.dncHull(points)

        polygon = list(self.lineMaker(hull))

        # TODO: REPLACE THE LINE ABOVE WITH A CALL TO YOUR DIVIDE-AND-CONQUER CONVEX HULL SOLVER
        t4 = time.time()

        # when passing lines to the display, pass a list of QLineF objects.  Each QLineF
        # object can be created with two QPointF objects corresponding to the endpoints
        self.showHull(polygon, RED)
        self.showText('Time Elapsed (Convex Hull): {:3.3f} sec'.format(t4 - t3))

        return
