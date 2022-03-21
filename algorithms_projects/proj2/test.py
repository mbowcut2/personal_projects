from convex_hull import Hull
from convex_hull import ConvexHullSolver
from PyQt5.QtCore import QLineF, QPointF, QObject
import numpy as np


def main():
    solver = ConvexHullSolver()
    points = [QPointF(-2, 1), QPointF(-3, -1), QPointF(-1, 0), QPointF(1, 0), QPointF(2, 1), QPointF(3, -1), QPointF(4, 2)]
    # points.sort(key=lambda x: x.x())
    A = points[0]
    B = points[1]
    C = points[2]
    D = points[3]
    E = points[4]
    F = points[5]

    upperTangentTruth = QLineF(A, F)
    lowerTangentTruth = QLineF(B, E)
    leftHull = [B, C, A]
    rightHull = [E, D, F]


    points.sort(key=lambda x: x.x())

    print(solver.dncHull(points))

    #print(solver.merge(leftHull, rightHull, upperTangent, lowerTangent))
    #print([A, B, E, F])  # MERGE WORKSSSSSSS

    return 0


if __name__ == "__main__":
    main()
