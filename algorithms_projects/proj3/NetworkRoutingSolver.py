#!/usr/bin/python3


from CS312Graph import *
import time
import q
import numpy as np


class NetworkRoutingSolver:
    def __init__(self):
        pass

    def initializeNetwork(self, network):
        assert (type(network) == CS312Graph)
        self.network = network

    def length(self, n1, n2):

        # calculates the euclidian distance between the nodes.

        return np.sqrt((n1.x() - n2.x()) ** 2 + (n1.y() - n2.y()) ** 2)

    def getShortestPath(self, destIndex):
        self.dest = destIndex
        # TODO: RETURN THE SHORTEST PATH FOR destIndex
        #       INSTEAD OF THE DUMMY SET OF EDGES BELOW
        #       IT'S JUST AN EXAMPLE OF THE FORMAT YOU'LL 
        #       NEED TO USE
        path_edges = []
        total_length = self.dist[destIndex]

        index = destIndex
        success = False
        while True:
            if self.prev[index] is None:
                break
            '''
            path_edges.append( (self.prev[index].loc, self.qArr[index].loc, '{:.0f}'.format(self.length(self.prev[index].loc, self.qArr[index].loc))))
            index = self.qArr.index(self.prev[index])
            '''
            for edge in self.prev[index].neighbors:
                if edge.dest == self.qArr[index]:
                    path_edges.append((edge.src.loc, edge.dest.loc, '{:.0f}'.format(edge.length)))
                if edge.src == self.qArr[self.source]:
                    success = True
            index = self.prev[index].node_id

        if success:
            return {'cost': total_length, 'path': path_edges}

        else:
            print('unsuccessful')
            return {'cost': float('inf'), 'path': path_edges}

    def computeShortestPaths(self, srcIndex, use_heap=False):
        self.source = srcIndex
        self.qArr = []
        self.prev = []

        if use_heap:
            queue = q.HeapQueue(len(self.network.nodes))
        else:
            queue = q.ArrayQueue()

        t1 = time.time()
        # TODO: RUN DIJKSTRA'S TO DETERMINE SHORTEST PATHS.
        #       ALSO, STORE THE RESULTS FOR THE SUBSEQUENT
        #       CALL TO getShortestPath(dest_index)

        for node in self.network.nodes:
            queue.insert(node, 1e6)
            self.qArr.append(node)
            self.prev.append(None)
        queue.dist[srcIndex] = 0
        if use_heap:
            queue.bubble_up(srcIndex)

        while len(queue.H) > 0:
            u = queue.delete_min()
            if u is None:
                break
            for edge in u.neighbors:
                if queue.dist[edge.dest.node_id] > queue.dist[edge.src.node_id] + edge.length:
                    queue.dist[edge.dest.node_id] = queue.dist[edge.src.node_id] + edge.length
                    self.prev[edge.dest.node_id] = edge.src
                    if edge.dest is not None:
                        queue.decrease_key(edge.dest)

        t2 = time.time()
        self.dist = queue.dist
        return (t2 - t1)
