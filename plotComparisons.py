import numpy as np
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison
import matplotlib.pyplot as plt
import os
import math
import re
from sklearn import linear_model
from scipy import stats


# some helpful links for the plotting and numpy
# https://s3.amazonaws.com/assets.datacamp.com/blog_assets/Python_Matplotlib_Cheat_Sheet.pdf
# https://s3.amazonaws.com/assets.datacamp.com/blog_assets/Numpy_Python_Cheat_Sheet.pdf

def split_upper(s):
    return " ".join(filter(None, re.split("([A-Z][^A-Z]*)", s)))


# colors = ['blue', '#9370DB', 'orange', 'yellow', 'green']
# titles = ['Static Environment', "Emergent Jobs", "Hard Jobs", "Dynamic Agents", "Sudden Tasks"]
rates = {'fouragentoneNeighborhood':.25,'fouragentfourNeighborhood':.25, 'fouragentfourneighborhoodSpreadout':.0625, 'oneagentoneneighborhood':.0625, 'OverlapFourAgentFourNeighborhood':.25, 'disaster1':.25, 'disaster2':.1875, 'sixtyfouragents':.8 }
areas = {'fouragentoneNeighborhood':1600,'fouragentfourNeighborhood': 6400, 'fouragentfourneighborhoodSpreadout':1600, 'oneagentoneneighborhood':1600, 'OverlapFourAgentFourNeighborhood':3600, 'disaster1':3600, 'disaster2':3200, 'sixtyfouragents':102400 }
numAgents = {'fouragentoneNeighborhood':4,'fouragentfourNeighborhood':4,'fouragentoneNeighborhood':4, 'fouragentfourneighborhoodSpreadout':1, 'oneagentoneneighborhood':1, 'OverlapFourAgentFourNeighborhood':4, 'disaster1':4, 'disaster2':4, 'sixtyfouragents':64 }
# 8 is used as the auction method for a single agent experiment
# 13 is equitable partitions with NN
# 6 is just NN when i split the space
# 4 is jumpship with comms with no range
# 14 is range limited bounty hunting
agentToIndex = {'0.0':0, '1.0E-4':1, '0.001':2, '0.01':3, '0.1':4, '1.0':5} #{'0':0,'8':0, '4':1, '6':2, '13':3, '14':4, '5':5}
indexToPointChar = {'0':'o', '1':'x', '2':'s', '3':'D', '4':'^', '5':'x'}
indexToName = {'0':'Auction', '1':'Bounty Hunting', '2':'NN', '3':'Equitable Paritions', '4':'Bounty Hunting With Comm', '5':'NN with Task Abandonment'}
myTitle = {'fouragentfourNeighborhood':'Four Agents 80x80','fouragentoneNeighborhood':'Four Agents $\lambda = .25$ A = 40x40', 'fouragentfourneighborhoodSpreadout':'Four Agents Serperate Regions', 'oneagentoneneighborhood':'One Agent $\lambda = .0625$ A = 40x40', 'OverlapFourAgentFourNeighborhood':'Four Agents Piecewise PPP', 'disaster':'Four Agents with Time Varying PPP',  'sixtyfouragents':'Sixty Four Agents' }


intervalToIndex = {'8':0, '9':1, '10':2, '11':3, '12':4, '13':5, '14':6, 
					'40':0,'45':1, '50':2, '55':3, '60':4, '65':5, '70':6}


startDir = '/home/drew/tmp/forpaper3/'

#for i in ["regular", "death", "emergentjobs", "hardjobs", "suddentasks"]:
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
			means = np.zeros((7, 6))
			err = np.zeros((7,6))
			T = np.zeros((7,6))
			for interval in os.listdir('{}{}/{}/{}/'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden)):
				for method in os.listdir('{}{}/{}/{}/{}'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden,interval)):
					
					# The thing is that for rho > .8 the equation does not seem to do so well
					# or the slope changes again and we have a degree two polynomial

					#print("the service time is {} the file is {} and the mean is {}".format(interval, method, np.mean(np.loadtxt('/home/drew/tmp/forpaper/{}/{}/{}/{}/{}'.format(experiments, fuelOrNoFuel,regularOrSudden,interval,method)))))
					if "Time" in method and "0_0.0" not in method and "_100.0_" not in method and "15" not in interval and "14" not in interval and "70" not in interval:
					#if "Time" in method and "15" not in interval and "14" not in interval and "70" not in interval:

						#loadedTxt = np.loadtxt('/home/drew/tmp/forpaper/{}/{}/{}/{}/{}'.format(experiments, fuelOrNoFuel,regularOrSudden,interval,method))
						loadedTxt = np.loadtxt('{}{}/{}/{}/{}/{}'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden,interval,method))
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
						if (2*diff) / meanVal > .10:
							# auction in the seperate neighborhoods does not converge in \bar{s} of 13 and 14
							print("bad {} {} {} interval {} ratio of width of 95\% confidence interval over mean is greater than 10\% for over 40 experiments an overestimate of 300,000 timesteps to the lower bound on 200*numtasks per serperate neighborhood".format(method, myTitle[experiments], split_upper(fuelOrNoFuel), interval))
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
						#agentCode = filter(str.isdigit, method.split('_')[0])
						agentCode = method.split('_')[1]
						means[agentToIndex[agentCode]][intervalToIndex[interval]] = meanVal
						err[agentToIndex[agentCode]][intervalToIndex[interval]] = diff
						T[agentToIndex[agentCode]][intervalToIndex[interval]] = Tval
						#print("tval = {}".format(Tval))	
						#print("mean = {} std = {} lowerend = {} upper = {} diff = {}".format(meanVal, stdVal,lower, upper, (upper - meanVal) ))
			#print(T)
				#stat, pval = stats.ttest_ind(np.loadtxt('{}{}/{}/{}/{}/{}'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden,interval,"4_0.0_allTimeResults.txt")), np.loadtxt('{}{}/{}/{}/{}/{}'.format(startDir, experiments, fuelOrNoFuel,regularOrSudden,interval,"5_0.0_allTimeResults.txt")))
				#print("interval = {} pval = {}".format(interval, pval))

			# so now i can make the plot
			count = 0
			for (x,y,e) in zip(T, means, err):
				if not np.all(x == np.zeros((len(x)))):
					# well this has an intercept
					intercept = np.zeros((len(x)))
					slope, intercept = np.polyfit(x, y, 1)
					#c1,c2, intercept = np.polyfit(x, y, 2)
					# this has a zero intercept (which is what I did on google sheets.)
					#slope=x.dot(y)/x.dot(x)

					gammaf = float(math.sqrt(slope))
					gamma = '{0:.4f}'.format(gammaf)
					slopef = '{0:.4f}'.format(slope)
					a,b,c = plt.errorbar(x, y, yerr=e, fmt='{}'.format(indexToPointChar[str(count)]),  label="{} $\gamma={}$ slope = {}".format(indexToName[str(count)], gamma, slopef))
					#a,b,c = plt.errorbar(x, y, yerr=e, fmt='o', label="{} $y={}x^2+{}x+{}$".format(indexToName[str(count)], c1,c2,intercept))
					p = plt.plot(x, x*slope+intercept)
					#p = plt.plot(x, x*x*c1+x*c2+intercept)
					p[-1].set_color(a.get_color())
				count = count + 1
			#plt.xscale('log')
			#plt.yscale('log')
			plt.xlabel(r'$\frac{\lambda A}{m^2v^2(1-\rho)^2}$')
			plt.ylabel('Experimental T')
			plt.title('{} with {}'.format(myTitle[experiments], split_upper(fuelOrNoFuel)))
			plt.legend(fontsize="small")

			plt.savefig('/home/drew/tmp/figssingle/{}{}.pdf'.format(experiments,fuelOrNoFuel), transparent=True)
			plt.show()


