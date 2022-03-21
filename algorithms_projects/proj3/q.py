#!/usr/bin/python3

import numpy as np


class Queue:

    def delete_min(self):
        pass

    def decrease_key(self, v):
        pass

    def insert(self, node, dist):
        pass


class ArrayQueue(Queue):

    def __init__(self):
        # self.qArr = []  # nodes in the graph -- key
        self.dist = []  # distances -- value  -- node, dist have matching indices.
        # self.prev = []  # tracks path through the tree by previous node for each node
        self.H = []


    def delete_min(self):
        minimum = 1e6
        minInd = None

        for i in range(len(self.H)):
            if self.dist[self.H[i].node_id] < minimum:
                minimum = self.dist[self.H[i].node_id]
                minInd = i

        if minInd is None:
            self.H = []
            return minInd
        n = self.H.pop(minInd)
        return n

    def decrease_key(self, v):  # this function should do nothing.
        pass

    def insert(self, node, dist):
        # self.qArr.append(node)
        self.dist.append(dist)
        self.H.append(node)



class HeapQueue(Queue):

    def __init__(self, l):
        # self.qArr = []  # nodes in the graph -- key
        self.dist = []  # distances -- value  -- node, dist have matching indices.
        # self.prev = []  # tracks path through the tree by previous node for each node
        self.H = []
        self.pos = [None for _ in range(l)]  # index = node_id, value = position in H

    def swap(self, i, j):
        i = int(i)
        j = int(j)
        self.H[i], self.H[j] = self.H[j], self.H[i]
        #temp = self.H[i]
        #3self.H[i] = self.H[j]
        #self.H[j] = temp

        self.pos[self.H[i].node_id], self.pos[self.H[j].node_id] = self.pos[self.H[j].node_id], self.pos[self.H[i].node_id]
        #temp = self.pos[self.H[i].node_id]
        #self.pos[self.H[i].node_id] = self.pos[self.H[j].node_id]
        #self.pos[self.H[j].node_id] = temp
        # print(1)

    def bubble_up(self, index):  # TODO: This should accpet indices into the heap structure (i.e. values of self.pos)
        while True:
            if index == 0:
                break
            parentInd = int((index - 1) // 2)
            # if index not in range(len(self.H)) or parentInd not in range(len(self.H)):
            # print("uh oh")
            if self.dist[self.H[index].node_id] < self.dist[self.H[parentInd].node_id]:
                self.swap(index, parentInd)
                index = parentInd
            else:
                break

    def sift_down(self, index):
        while True:
            minInd = 0
            single = False
            children = [2 * index + 1, 2 * index + 2]
            if children[0] >= len(self.H):
                break
            if children[1] >= len(self.H):
                minInd = 0
                single = True
            else:
                if self.dist[self.H[children[0]].node_id] < self.dist[self.H[children[1]].node_id]:
                    minInd = 0
                else:
                    minInd = 1
            minChild = children[minInd]
            if not single:
                if self.dist[self.H[children[0]].node_id] < self.dist[self.H[index].node_id]:
                    if self.dist[self.H[children[1]].node_id] < self.dist[self.H[index].node_id]:
                        self.swap(index, minChild)
                        index = minChild
                    else:
                        break
                else:
                    break
            if single:
                if self.dist[self.H[children[0]].node_id] < self.dist[self.H[index].node_id]:
                    self.swap(index, minChild)
                    index = minChild
                else:
                    break

    def delete_min(self):
        root = self.H[0]

        self.pos[root.node_id] = None

        temp = self.H[-1]
        leaf = self.H.pop(-1)
        if len(self.H) == 0:
            return root
        self.H[0] = temp
        self.pos[leaf.node_id] = 0

        self.sift_down(0)

        return root

    def decrease_key(self, n):
        self.bubble_up(self.pos[n.node_id])

    def insert(self, n, d):
        self.H.append(n)
        self.dist.append(d)
        self.pos[n.node_id] = len(self.H) - 1
        self.bubble_up(len(self.H) - 1)
