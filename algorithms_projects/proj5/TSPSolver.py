#!/usr/bin/python3

from which_pyqt import PYQT_VER
if PYQT_VER == 'PYQT5':
	from PyQt5.QtCore import QLineF, QPointF
elif PYQT_VER == 'PYQT4':
	from PyQt4.QtCore import QLineF, QPointF
else:
	raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))




import time
import math
import numpy as np
from TSPClasses import *
import heapq
import itertools
from queue import PriorityQueue
import copy



class TSPSolver:
	def __init__( self, gui_view ):
		self._scenario = None

	def setupWithScenario( self, scenario ):
		self._scenario = scenario


	''' <summary>
		This is the entry point for the default solver
		which just finds a valid random tour.  Note this could be used to find your
		initial BSSF.
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of solution, 
		time spent to find solution, number of permutations tried during search, the 
		solution found, and three null values for fields not used for this 
		algorithm</returns> 
	'''
	
	def defaultRandomTour( self, time_allowance=60.0 ):
		results = {}
		cities = self._scenario.getCities()
		ncities = len(cities)
		foundTour = False
		count = 0
		bssf = None
		start_time = time.time()
		while not foundTour and time.time()-start_time < time_allowance:
			# create a random permutation
			perm = np.random.permutation( ncities )
			route = []
			# Now build the route using the random permutation
			for i in range( ncities ):
				route.append( cities[ perm[i] ] )
			bssf = TSPSolution(route)
			count += 1
			if bssf.cost < np.inf:
				# Found a valid route
				foundTour = True
		end_time = time.time()
		results['cost'] = bssf.cost if foundTour else math.inf
		results['time'] = end_time - start_time
		results['count'] = count
		results['soln'] = bssf
		results['max'] = None
		results['total'] = None
		results['pruned'] = None
		return results


	''' <summary>
		This is the entry point for the greedy solver, which you must implement for 
		the group project (but it is probably a good idea to just do it for the branch-and
		bound project as a way to get your feet wet).  Note this could be used to find your
		initial BSSF.
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of best solution, 
		time spent to find best solution, total number of solutions found, the best
		solution found, and three null values for fields not used for this 
		algorithm</returns> 
	'''

	def greedy(self, time_allowance=60.0):
		results = {}
		cities = self._scenario.getCities()
		ncities = len(cities)
		foundTour = False
		count = 0
		bssf = None
		start_time = time.time()


		i = -1
		while not foundTour and time.time() - start_time < time_allowance:
			#
			i = i + 1
			if i >= len(cities):
				break
			route = []
			possible_cities = cities.copy()
			currCity = possible_cities[i]

			route.append(currCity)
			possible_cities.remove(currCity)

			while len(possible_cities) != 0:

				minDist = math.inf
				minCity = None

				for city in possible_cities:
					if currCity.costTo(city) <= minDist:
						minCity = city
						minDist = currCity.costTo(city)


				route.append(minCity)
				possible_cities.remove(minCity)
				currCity = minCity

			if bssf is None:
				bssf = TSPSolution(route)
				count += 1

			elif TSPSolution(route).cost <= bssf.cost:
				bssf = TSPSolution(route)
				count += 1

			if bssf.cost < math.inf:
				foundTour = True

		end_time = time.time()
		results['cost'] = bssf.cost if foundTour else math.inf
		results['time'] = end_time - start_time
		results['count'] = count
		results['soln'] = bssf
		results['max'] = None
		results['total'] = None
		results['pruned'] = None
		return results

	def ourRandom(self, time_allowance=60.0):
		results = {}
		cities = self._scenario.getCities()
		ncities = len(cities)
		foundTour = False
		count = 0
		bssf = None
		start_time = time.time()


		i = -1
		while not foundTour and time.time() - start_time < time_allowance:
			#
			i = i + 1
			if i >= len(cities):
				break
			route = []
			possible_cities = cities.copy()
			currCity = possible_cities[i]

			route.append(currCity)
			possible_cities.remove(currCity)

			while len(possible_cities) != 0:

				list = np.random.permutation(len(possible_cities))
				deadEnd = False

				for i in list:
					if currCity.costTo(possible_cities[i]) < math.inf:
						route.append(possible_cities[i])
						possible_cities.remove(possible_cities[i])
						break
					deadEnd = True
				if deadEnd:
					break

			if bssf is None:
				bssf = TSPSolution(route)
				count += 1

			elif TSPSolution(route).cost <= bssf.cost:
				bssf = TSPSolution(route)
				count += 1

			if bssf.cost < math.inf:
				foundTour = True

		end_time = time.time()
		results['cost'] = bssf.cost if foundTour else math.inf
		results['time'] = end_time - start_time
		results['count'] = count
		results['soln'] = bssf
		results['max'] = None
		results['total'] = None
		results['pruned'] = None
		return results
	
	
	
	''' <summary>
		This is the entry point for the branch-and-bound algorithm that you will implement
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of best solution, 
		time spent to find best solution, total number solutions found during search (does
		not include the initial BSSF), the best solution found, and three more ints: 
		max queue size, total number of states created, and number of pruned states.</returns> 
	'''


	def getMatrix(self):
		cities = self._scenario.getCities()
		ncities = len(cities)
		# n*n matrix, for n cities
		matrix = np.array([[None for i in range(ncities)] for j in range(ncities)])

		for i in range(ncities):
			for j in range(ncities):
				if cities[i] == cities[j]:
					matrix[i][j] = math.inf
				else:
					matrix[i][j] = float(cities[i].costTo(cities[j]))

		return matrix

	def reduce(self, matrix, bound=0):

		for i in range(len(matrix[0])):
			x = matrix[i].min()
			if x < math.inf:
				bound += x
				matrix[i] = matrix[i] - x

		matrix = matrix.T

		for i in range(len(matrix[0])):
			x = matrix[i].min()
			if x < math.inf:
				bound += x
				matrix[i] = matrix[i] - x

		matrix = matrix.T

		return matrix, bound

	def infOut(self, matrix, i, j):
		newMatrix = copy.deepcopy(matrix)

		for k in range(len(newMatrix[i])):
			newMatrix[i][k] = math.inf

		#newMatrix = newMatrix.T

		for k in range(len(newMatrix[j])):
			newMatrix[k][j] = math.inf

		#newMatrix = newMatrix.T

		newMatrix[j][i] = math.inf

		return newMatrix


	def makeChildren(self, node, level):

		"""
		- travel to city --> add cost to bound
		- inf out row, col, transpose entry
		- reduce
		:return:
		"""

		print("Making Children for " + str(node.city._name))

		children = []

		i = node.city._index

		for j in range(len(node.matrix)):
			if node.matrix[i][j] < math.inf:
				newBound = node.bound + node.matrix[i][j]
				newMatrix = self.infOut(node.matrix, i, j)
				newMatrix, newBound = self.reduce(newMatrix, newBound)
				children.append(Node(self.cities[j], newMatrix, newBound, node.level + 1, self.AVG_COST, node.route.copy()))

		return children

	def check(self, node):
		"""
		checks to see if we're done -- i.e. we've traveled to all cities with a path < inf back to city 1.
		:param node: Node to check.
		:return: boolean




		"""
		if node.matrix.min() < math.inf:
			return False
		else:
			node.route.pop(-1) #TODO: I'm thinking this will work cuz python is pass by reference, but we may need to return the node...
			return True




	def getAvgCost(self, matrix):
		total = 0
		n = 0
		for i in range(len(matrix)):
			for j in range(len(matrix[0])):
				if matrix[i][j] < math.inf:
					total += matrix[i][j]
					n += 1

		return total / n








		
	def branchAndBound(self, time_allowance=60.0): #TODO: qLength is persisting across uses... for some reason it doesn't reset to 0.
		results = {}
		queue = PriorityQueue()
		qLength = 0
		maxLength = 0
		total = 0
		pruned = 0
		self.cities = self._scenario.getCities()
		ncities = len(self.cities)
		matrix = self.getMatrix()
		self.AVG_COST = self.getAvgCost(matrix)
		m, b = self.reduce(matrix)
		root = Node(self.cities[0], m, b, 0, self.AVG_COST)
		level = 0

		count = 0
		bssf = self.greedy()['soln']
		start_time = time.time()
		queue.put((root.priority, root))
		qLength += 1

		while root.bound < bssf.cost and time.time() - start_time < time_allowance:
			currNode = queue.get()[1]
			#print("Processing Node: " + str(currNode.city._name))
			qLength -= 1
			print("Queue Length: " + str(qLength))
			if currNode.city._name == "D":
				print("gotcha")
			if (self.check(currNode)):
				if currNode.bound < bssf.cost:
					if len(currNode.route) == ncities:
						bssf = TSPSolution(currNode.route)
						#print("Current BSSF: " + str(bssf.cost))
						count += 1
						continue

			if currNode.bound > bssf.cost:
				pruned += 1
				if qLength == 0:
					break
				continue

			children = self.makeChildren(currNode, level)
			for child in children:
				#print("Checking Children")
				if time.time() - start_time > time_allowance: # Not sure if this is necessary
					print("time expired")
					break
				total += 1

				if child.bound < bssf.cost:
					queue.put((child.priority, child))
					qLength += 1
					print("Queue Length: " + str(qLength))
				else:
					pruned += 1

				if qLength > maxLength:
					maxLength = qLength
					print("Current maxLength: " + str(maxLength))

			if qLength == 0:
				break


		end_time = time.time()
		results['cost'] = bssf.cost
		results['time'] = end_time - start_time
		results['count'] = count
		results['soln'] = bssf
		results['max'] = maxLength  #Max queue length
		results['total'] = total
		results['pruned'] = pruned

		print("End Reached")
		print("Solution: ", bssf.route)
		print("Cost: " + str(bssf.cost))
		print( "total: " + str(total))
		print( "pruned: " + str(pruned))

		return results



	''' <summary>
		This is the entry point for the algorithm you'll write for your group project.
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of best solution, 
		time spent to find best solution, total number of solutions found during search, the 
		best solution found.  You may use the other three field however you like.
		algorithm</returns> 
	'''

	def mate(self, parents):
		pass
	def fancy( self,time_allowance=60.0 ):
		population = []
		for i in range(10):
			solution = self.ourRandom()['soln']
			population.append(solution)

			print("nice")

		print("Let's check this baby out")

		return


class Node:

	def __init__(self, city, matrix, bound, level, avg_cost, route=[]):
		self.matrix = matrix
		self.bound = bound
		# self.priority = np.floor(bound / level) #try different combinations of level and bound
		self.priority = bound - (10*level * avg_cost)
		self.level = level
		self.city = city
		self.route = route
		self.route.append(city)

	#Overloading boolean operators for comparing nodes.
	def __lt__(self, other):
		if self.city._index < other.city._index:
			return True
		else: return False
	def __le__(self, other):
		if self.city._index <= other.city._index:
			return True
		else:
			return False
	def __gt__(self, other):
		if self.city._index > other.city._index:
			return True
		else:
			return False
	def __ge__(self, other):
		if self.city._index >= other.city._index:
			return True
		else:
			return False
	def __eq__(self, other):
		if self.city._index == other.city._index:
			return True
		else:
			return False
	def __ne__(self, other):
		if self.city._index != other.city._index:
			return True
		else:
			return False


	def addCity(self, city):
		self.route.append(city)



















		



