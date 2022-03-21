from TSPSolver import TSPSolver, Node
import numpy as np
from queue import PriorityQueue
import math

class City:

    def __init__(self, index):
        self._index = index



if __name__ == "__main__":
    arr = np.array([[math.inf,math.inf,math.inf],[math.inf,math.inf,math.inf],[math.inf,math.inf,math.inf]])

    solver = TSPSolver(None)

    queue = PriorityQueue()

    #print(solver.reduce(arr, 0))

    #print(solver.infOut(arr, 1, 2))

    #print(np.mean(np.array([[2,2],[2,2],[2,2],[2,2]])))
    node1 = Node(City(0), arr, 27, 2, 12)
    node2 = Node(City(1), arr, 12, 2, 12)

    print(node1 < node2)
    print(node1 <= node2)
    print(node1 > node2)
    print(node1 >= node2)
    print(node1 == node2)
    print(node1 != node2)







