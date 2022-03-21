#!/usr/bin/python3

from which_pyqt import PYQT_VER

if PYQT_VER == 'PYQT5':
	from PyQt5.QtCore import QLineF, QPointF
elif PYQT_VER == 'PYQT4':
	from PyQt4.QtCore import QLineF, QPointF
else:
	raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))

import math
import time
import random
import numpy as np

# Used to compute the bandwidth for banded version
MAXINDELS = 3

# Used to implement Needleman-Wunsch scoring
MATCH = -3
INDEL = 5
SUB = 1


class GeneSequencing:

	def __init__(self):
		pass

	# This is the method called by the GUI.  _seq1_ and _seq2_ are two sequences to be aligned, _banded_ is a boolean that tells
	# you whether you should compute a banded alignment or full alignment, and _align_length_ tells you
	# how many base pairs to use in computing the alignment

	def diff(self, seq1, seq2, i, j):
		if seq1[i - 1] == seq2[j - 1]:
			return MATCH
		else:
			return SUB

	def show(self, table):
		print(np.array(table))

	def align(self, seq1, seq2, banded, align_length):
		self.banded = banded
		self.MaxCharactersToAlign = align_length
		d = 3

		###################################################################################################
		# your code should replace these three statements and populate the three variables: score, alignment1 and alignment2
		# score = random.random()*100;
		# alignment1 = 'abc-easy  DEBUG:({} chars,align_len={}{})'.format(
		# len(seq1), align_length, ',BANDED' if banded else '')
		# alignment2 = 'as-123--  DEBUG:({} chars,align_len={}{})'.format(
		# len(seq2), align_length, ',BANDED' if banded else '')

		table = []
		path = []

		if len(seq1) > align_length:
			seq1 = seq1[:align_length:]
		if len(seq2) > align_length:
			seq2 = seq2[:align_length:]

		if banded:
			if abs(len(seq1) - len(seq2)) > d:

				return {'align_cost': math.inf, 'seqi_first100': "No Alignment Possible",
					'seqj_first100': "No Alignment Possible"}

			for i in range(len(seq1) + 1):
				table.append([])
				path.append([])
				for j in range(2*d + 1):
					table[i].append(math.inf)
					path[i].append(None)

					if d > j + i: #negative indices
						continue
					if j + i > len(seq2) + d: #indices outside of the seq length
						continue


					if i == 0 and j == d: #the starting index
						table[i][j] = 0
						path[i][j] = "START"

					elif i == 0: #the top row
						table[i][j] = table[i][j - 1] + INDEL
						path[i][j] = "LEFT"

					elif j + i == d: #the leftmost column
						table[i][j] = table[i - 1][j + 1] + INDEL
						path[i][j] = "UP"

					elif j == 0: #first in the row, no LEFT option
						table[i][j] = min(
							[INDEL + table[i - 1][j + 1],
							 self.diff(seq1, seq2, i, i + j - d) + table[i - 1][j]]
						)
						if table[i][j] == INDEL + table[i - 1][j + 1]:
							path[i][j] = "UP"
						else:
							path[i][j] = "DIAG"

					elif j == 2*d: #last in the row, no UP option
						table[i][j] = min(
							[INDEL + table[i][j - 1],
							 self.diff(seq1, seq2, i, i + j - d) + table[i - 1][j]]
						)
						if table[i][j] == INDEL + table[i][j - 1]:
							path[i][j] = "LEFT"
						else:
							path[i][j] = "DIAG"

					else: #middle entries have all three options
						table[i][j] = min(
							[INDEL + table[i][j - 1],
							 INDEL + table[i - 1][j + 1],
							 self.diff(seq1, seq2, i, i + j - d) + table[i - 1][j]]
						)
						if table[i][j] == INDEL + table[i][j - 1]:
							path[i][j] = "LEFT"
						elif table[i][j] == INDEL + table[i - 1][j + 1]:
							path[i][j] = "UP"
						else:
							path[i][j] = "DIAG"

			score = 0
			for value in table[-1][::-1]:
				if value != math.inf:
					score = value
					break

			alignment1 = ""
			alignment2 = ""

			# traverse the path
			i, j = len(seq1), 2 * d
			while i >= 0 or j >= 0:

				if path[i][j] is None:
					j = j - 1
					continue
				elif path[i][j] == "UP":
					alignment1 += seq1[i-1]
					alignment2 += "-"
					i = i - 1
					j = j + 1
				elif path[i][j] == "LEFT":
					alignment1 += "-"
					j = j - 1
				elif path[i][j] == "DIAG":
					alignment1 += seq1[i-1]
					alignment2 += seq2[i + j - d - 1]
					i = i - 1
				elif path[i][j] == "START":
					break


			alignment1 = alignment1[::-1]
			alignment2 = alignment2[::-1]

			return {'align_cost': score, 'seqi_first100': alignment1[:min(100,align_length):], 'seqj_first100': alignment2[:min(100,align_length):]}

		if not banded:
			for i in range(len(seq1) + 1):
				table.append([])
				path.append([])
				for j in range(len(seq2) + 1):
					table[i].append(math.inf)
					path[i].append(None)
					if i == 0 and j == 0:
						table[i][j] = 0
						path[i][j] = "START"

					elif i == 0:
						table[i][j] = table[i][j - 1] + INDEL
						path[i][j] = "LEFT"

					elif j == 0:
						table[i][j] = table[i - 1][j] + INDEL
						path[i][j] = "UP"

					else:
						table[i][j] = min(
							[INDEL + table[i][j - 1],
							 INDEL + table[i - 1][j],
							 self.diff(seq1, seq2, i, j) + table[i - 1][j - 1]]
						)
						if table[i][j] == INDEL + table[i][j - 1]:
							path[i][j] = "LEFT"
						elif table[i][j] == INDEL + table[i - 1][j]:
							path[i][j] = "UP"
						else:
							path[i][j] = "DIAG"
			score = table[-1][-1]

			alignment1 = ""
			alignment2 = ""

			# traverse the path
			i, j = len(seq1), len(seq2)
			while i >= 0 or j >= 0:
				if path[i][j] == "UP":
					alignment1 += seq1[i-1]
					alignment2 += "-"
					i = i - 1
				elif path[i][j] == "LEFT":
					alignment1 += "-"
					alignment2 += seq2[j-1]
					j = j - 1
				elif path[i][j] == "DIAG":
					alignment1 += seq1[i-1]
					alignment2 += seq2[j-1]
					i = i - 1
					j = j - 1
				elif path[i][j] == "START":
					break


			alignment1 = alignment1[::-1]
			alignment2 = alignment2[::-1]

			return {'align_cost': score, 'seqi_first100': alignment1[:min(100,align_length):], 'seqj_first100': alignment2[:min(100,align_length):]}


###################################################################################################



if __name__ == "__main__":
	gs = GeneSequencing()
	seq1 = "abababcdcdcd"
	seq2 = "bababadcdcdc"
	results = gs.align(seq1, seq2, True, 1000)
	print(results["align_cost"], "\n", results["seqi_first100"], "\n", results["seqj_first100"])
