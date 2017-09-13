import numpy as np
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison
import matplotlib.pyplot as plt
import os



# colors = ['blue', '#9370DB', 'orange', 'yellow', 'green']
# titles = ['Static Environment', "Emergent Jobs", "Hard Jobs", "Dynamic Agents", "Sudden Tasks"]

#for i in ["regular", "death", "emergentjobs", "hardjobs", "suddentasks"]:
for experiments in os.listdir('/home/drew/tmp/forpaper/'):
	for fuelOrNoFuel in os.listdir('/home/drew/tmp/forpaper/{}/'.format(experiments)):
		means = []
		for regularOrSudden in os.listdir('/home/drew/tmp/forpaper/{}/{}/'.format(experiments, fuelOrNoFuel)):
			for interval in os.listdir('/home/drew/tmp/forpaper/{}/{}/{}/'.format(experiments, fuelOrNoFuel,regularOrSudden)):
				for method in os.listdir('/home/drew/tmp/forpaper/{}/{}/{}/{}'.format(experiments, fuelOrNoFuel,regularOrSudden,interval)):
					print("the mean is {}".format(np.mean(np.loadtxt('/home/drew/tmp/forpaper/{}/{}/{}/{}/{}'.format(experiments, fuelOrNoFuel,regularOrSudden,interval,method)))))