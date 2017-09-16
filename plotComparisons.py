import numpy as np
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison
import matplotlib.pyplot as plt
import os
import math
import re

def split_upper(s):
    return " ".join(filter(None, re.split("([A-Z][^A-Z]*)", s)))


# colors = ['blue', '#9370DB', 'orange', 'yellow', 'green']
# titles = ['Static Environment', "Emergent Jobs", "Hard Jobs", "Dynamic Agents", "Sudden Tasks"]
rates = {'fouragentoneNeighborhood':.25, 'fouragentfourneighborhoodSpreadout':.0625, 'oneagentoneneighborhood':.0625, 'OverlapFourAgentFourNeighborhood':.25, 'disaster1':.25, 'disaster2':.1875, 'sixtyfouragents':.8 }
areas = {'fouragentoneNeighborhood':1600, 'fouragentfourneighborhoodSpreadout':1600, 'oneagentoneneighborhood':1600, 'OverlapFourAgentFourNeighborhood':3600, 'disaster1':3600, 'disaster2':3200, 'sixtyfouragents':102400 }
numAgents = {'fouragentoneNeighborhood':4, 'fouragentfourneighborhoodSpreadout':1, 'oneagentoneneighborhood':1, 'OverlapFourAgentFourNeighborhood':4, 'disaster1':4, 'disaster2':4, 'sixtyfouragents':64 }
# 8 is used as the auction method for a single agent experiment
# 13 is equitable partitions with NN
# 6 is just NN when i split the space
# 4 is jumpship with comms with no range
# 14 is range limited bounty hunting
agentToIndex = {'0':0,'8':0, '4':1, '6':2, '13':3, '14':4}
indexToName = {'0':'Auction', '1':'Bounty Hunting', '2':'NN', '3':'Equitable Paritions', '4':'Bounty Hunting With Comm'}


intervalToIndex = {'8':0, '9':1, '10':2, '11':3, '12':4, '13':5, '14':6, 
					'40':0,'45':1, '50':2, '55':3, '60':4, '65':5, '70':6}

#for i in ["regular", "death", "emergentjobs", "hardjobs", "suddentasks"]:
for experiments in os.listdir('/home/drew/tmp/forpaper/'):
	
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


	for fuelOrNoFuel in os.listdir('/home/drew/tmp/forpaper/{}/'.format(experiments)):
		for regularOrSudden in os.listdir('/home/drew/tmp/forpaper/{}/{}/'.format(experiments, fuelOrNoFuel)):
			means = np.zeros((5, 7))
			err = np.zeros((5,7))
			T = np.zeros((5,7))
			for interval in os.listdir('/home/drew/tmp/forpaper/{}/{}/{}/'.format(experiments, fuelOrNoFuel,regularOrSudden)):
				for method in os.listdir('/home/drew/tmp/forpaper/{}/{}/{}/{}'.format(experiments, fuelOrNoFuel,regularOrSudden,interval)):
					#print("the service time is {} the file is {} and the mean is {}".format(interval, method, np.mean(np.loadtxt('/home/drew/tmp/forpaper/{}/{}/{}/{}/{}'.format(experiments, fuelOrNoFuel,regularOrSudden,interval,method)))))
					if "Time" in method and "15" not in interval:
						loadedTxt = np.loadtxt('/home/drew/tmp/forpaper/{}/{}/{}/{}/{}'.format(experiments, fuelOrNoFuel,regularOrSudden,interval,method))
						loadedTxt = np.array([y for y in loadedTxt if y != 0.0])
						if len(loadedTxt) > 40:
							# should chop off values that are more than 40
							loadedTxt = np.resize(loadedTxt, (40))
						meanVal = np.mean(loadedTxt)
						stdVal = np.std(loadedTxt)
						# confidence intervals
						#lower = (meanVal - 1.96*(stdVal / math.sqrt(len(loadedTxt))))
						upper = (meanVal + 1.96*(stdVal / math.sqrt(len(loadedTxt))))
						diff = upper - meanVal
						sbar = float(interval)
						if 'disaster' not in experiments:
							rho = (rate * sbar) / numAgent
							Tval = (rate * area) / (.7*.7*numAgent*numAgent*(1-rho)*(1-rho))
						else:
							rho = (rate1 * sbar) / numAgent
							Tval1 = (rate1 * area1) / (.7*.7*numAgent*numAgent*(1-rho)*(1-rho))
							rho = (rate2 * sbar) / numAgent
							Tval2 = (rate2 * area2) / (.7*.7*numAgent*numAgent*(1-rho)*(1-rho))
							Tval = (2./5)*Tval1+(3./5)*Tval2
						# now set the values
						agentCode = filter(str.isdigit, method)
						means[agentToIndex[agentCode]][intervalToIndex[interval]] = meanVal
						err[agentToIndex[agentCode]][intervalToIndex[interval]] = diff
						T[agentToIndex[agentCode]][intervalToIndex[interval]] = Tval
						#print("tval = {}".format(Tval))	
						#print("mean = {} std = {} lowerend = {} upper = {} diff = {}".format(meanVal, stdVal,lower, upper, (upper - meanVal) ))
			#print(T)
			# so now i can make the plot
			count = 0
			for (x,y,e) in zip(T, means, err):
				if not np.all(x == np.zeros((len(x)))):
					plt.errorbar(x, y, yerr=e, fmt='o', label=indexToName[str(count)])
				count = count + 1
			plt.xlabel(r'$\frac{\lambda A}{m^2v^2(1-\rho)^2}$')
			plt.ylabel('Experimental T')
			plt.title('{} with {}'.format(experiments, split_upper(fuelOrNoFuel)))
			plt.legend()
			plt.show()


