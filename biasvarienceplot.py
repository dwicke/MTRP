import numpy as np
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison
import matplotlib.pyplot as plt
from matplotlib.ticker import FuncFormatter
import os
import math
import re
from sklearn import linear_model


# some helpful links for the plotting and numpy
# https://s3.amazonaws.com/assets.datacamp.com/blog_assets/Python_Matplotlib_Cheat_Sheet.pdf
# https://s3.amazonaws.com/assets.datacamp.com/blog_assets/Numpy_Python_Cheat_Sheet.pdf

def split_upper(s):
    return " ".join(filter(None, re.split("([A-Z][^A-Z]*)", s)))


# colors = ['blue', '#9370DB', 'orange', 'yellow', 'green']
# titles = ['Static Environment', "Emergent Jobs", "Hard Jobs", "Dynamic Agents", "Sudden Tasks"]
rates = {'jumpshipExp':.05, 'fouragentoneNeighborhood':.25, 'fouragentfourneighborhoodSpreadout':.0625, 'oneagentoneneighborhood':.0625, 'OverlapFourAgentFourNeighborhood':.25, 'disaster1':.25, 'disaster2':.1875, 'sixtyfouragents':.8 }
areas = {'jumpshipExp': 6400, 'fouragentoneNeighborhood':1600, 'fouragentfourneighborhoodSpreadout':1600, 'oneagentoneneighborhood':1600, 'OverlapFourAgentFourNeighborhood':3600, 'disaster1':3600, 'disaster2':3200, 'sixtyfouragents':102400 }
numAgents = {'jumpshipExp':4, 'fouragentoneNeighborhood':4, 'fouragentfourneighborhoodSpreadout':1, 'oneagentoneneighborhood':1, 'OverlapFourAgentFourNeighborhood':4, 'disaster1':4, 'disaster2':4, 'sixtyfouragents':64 }
# 8 is used as the auction method for a single agent experiment
# 13 is equitable partitions with NN
# 6 is just NN when i split the space
# 4 is jumpship with comms with no range
# 14 is range limited bounty hunting
agentToIndex = {'0r':0, '0':0,'8':0, '4r':1, '4':1, '6r':2, '6':2, '13':3, '14r':4, '14':4, '0n':5, '4n':6, '14n':7, '5r':8}
indexToPointChar = {'0':'o', '1':'x', '2':'s', '3':'D', '4':'^', '5':'o', '6':'x', '7':'s', '8':'D'}
indexToName = {'0':'Auction with Rate', '1':'Bounty Hunting with Rate', '2':'NN', '3':'Equitable Paritions', '4':'Bounty Hunting With Comm with Rate', '5':'Auction fixed Bounty', '6':'Bounty Hunting fixed Bounty', '7':'Bounty Hunting With Comm fixed Bounty', '8':'Nearest Neighbor with Task Abandonment'}
myTitle = {'jumpshipExp':'Four Agents 80x80', 'fouragentoneNeighborhood':'Four Agents $\lambda = .25$ A = 40x40', 'fouragentfourneighborhoodSpreadout':'Four Agents Serperate Regions', 'oneagentoneneighborhood':'One Agent $\lambda = .0625$ A = 40x40', 'OverlapFourAgentFourNeighborhood':'Four Agents Piecewise PPP', 'disaster':'Four Agents with Time Varying PPP',  'sixtyfouragents':'Sixty Four Agents' }


intervalToIndex = {'8':0, '9':1, '10':2, '11':3, '12':4, '13':5, '14':6, 
					'40':0,'45':1, '50':2, '55':3, '60':4, '65':5, '70':6}

incrementToIndex = {'0.0':0, '1.0E-4':1, '0.001':2, '0.01':3, '0.1':4, '1.0':5, '100.0':6}
xlabels = [0.0, 0.0001, 0.001, 0.01, 0.1, 1.0, 100.0]

#for i in ["regular", "death", "emergentjobs", "hardjobs", "suddentasks"]:
startDir = '/home/drew/tmp/forpaper3/'

for experiments in os.listdir(startDir):
	
	if 'fouragentfourneighborhoodRandom' in experiments:
		continue

	if 'disaster' not in experiments:
		rate = rates[experiments]
		area = areas[experiments]
		numAgent = numAgents[experiments]
	else:
		rate1 = rates['disaster1']
		rate2 = rates['disaster2']
		area1 = areas['disaster1']
		area2 = areas['disaster2']
		numAgent = numAgents['disaster1']


	for fuelOrNoFuel in os.listdir('{}{}/'.format(startDir,experiments)):
		for regularOrSudden in os.listdir('{}{}/{}/'.format(startDir, experiments, fuelOrNoFuel)):
			# means = np.zeros((9, 6))
			# err = np.zeros((9,6))
			# T = np.zeros((9,6))
			bias = np.zeros((6,7))# there are 6 service times and for each i had 8 different bounty increment valuespoints
			variance = np.zeros((6,7))
			totalError = np.zeros((6,7))

			for interval in os.listdir('{}{}/{}/{}/'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden)):
				for method in os.listdir('{}{}/{}/{}/{}'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden,interval)):
					bountyrate = 0.0
					if 'Fair' in method:
						#print(interval)
						#print(method)
						meanFairness = np.mean(np.loadtxt('{}{}/{}/{}/{}/{}'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden,interval,method)))
						#print(meanFairness)
						#print("bounty rate = {}".format(method.split('_')[1]))
						bias[intervalToIndex[interval]][incrementToIndex[method.split('_')[1]]] = (1.0 - meanFairness)*(1.0 - meanFairness)
					elif 'Var' in method:
						print(interval)
						print(method)
						meanVar = np.mean(np.loadtxt('{}{}/{}/{}/{}/{}'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden,interval,method)))
						variance[intervalToIndex[interval]][incrementToIndex[method.split('_')[1].split('a')[0]]] = meanVar
				#bias[intervalToIndex[interval]]=bias[intervalToIndex[interval]] / np.linalg.norm(bias[intervalToIndex[interval]])
			
			
				#print(normBias)
				norm = np.linalg.norm(bias[intervalToIndex[interval]])
				bias[intervalToIndex[interval]] = bias[intervalToIndex[interval]] / norm
				#plt.plot(bias[intervalToIndex[interval]], label="{}bias".format(interval))
				norm=np.linalg.norm(variance[intervalToIndex[interval]])
				variance[intervalToIndex[interval]] = variance[intervalToIndex[interval]] / norm
				#plt.plot(variance[intervalToIndex[interval]], label=interval)
				totalError[intervalToIndex[interval]] = bias[intervalToIndex[interval]] + variance[intervalToIndex[interval]]
				# plt.show()
			x = np.arange(0,7)
			print(x)
			print(bias.mean(0))
			c1,c2, intercept = np.polyfit(x, bias.mean(0), 2)
			p = plt.plot(x, x*x*c1+x*c2+intercept, label="bias^2")
			c1,c2, intercept = np.polyfit(x, variance.mean(0), 2)
			p = plt.plot(x, x*x*c1+x*c2+intercept, label="variance")
			c1,c2, intercept = np.polyfit(x, totalError.mean(0), 2)
			p = plt.plot(x, x*x*c1+x*c2+intercept, label="total error")
			plt.xticks(x,xlabels)
			plt.xlabel('Bounty Rate')
			plt.ylabel('Normalized Error')
			plt.title('{} with {}'.format(myTitle[experiments], split_upper(fuelOrNoFuel)))
			plt.legend(fontsize="small")
			plt.show()