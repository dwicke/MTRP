import numpy as np
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison
import matplotlib.pyplot as plt






for i in ["regular", "death", "emergentjobs", "hardjobs", "suddentasks"]:
	for joblength in [0, 5, 10, 15]:
		print("starting to show results for {} with job length {}".format(i, joblength))
		data = np.loadtxt("/home/drew/tmp/jumpNN/{}/{}/allTimeResults.txt".format(i, joblength), dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S20')})

		# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
		mc = MultiComparison(data['mean'], data['group'])
		result = mc.tukeyhsd()
		 
		print(result)
		print(mc.groupsunique)

		a = result.plot_simultaneous()
		#a.title("Mean Wait Time Tukey Test Results for {} with Job Length {}".format(i, joblength))
		a.show()


		raw_input("Press Enter to continue...")


	

# for i in ["regular", "emergentjobs", "hardjobs", "death", "suddentasks"]:
# 	for joblength in [0, 5, 10, 15]:
# 		print("starting to show results for {} with job length {}".format(i, joblength))
# 		data = np.loadtxt("/home/drew/tmp/jumpNN/{}/{}/allBountyResults.txt".format(i, joblength), dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S20')})

# 		# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
# 		mc = MultiComparison(data['mean'], data['group'])
# 		result = mc.tukeyhsd()
		 
# 		print(result)
# 		print(mc.groupsunique)

# 		a = result.plot_simultaneous()
# 		#a.title("Bounty Tukey Test Results for {} with Job Length {}".format(i, joblength))
# 		a.show()


# 		raw_input("Press Enter to continue...")





colors = ['blue', '#9370DB', 'orange', 'yellow', 'green']
titles = ['Static Environment', "Emergent Jobs", "Hard Jobs", "Dynamic Agents", "Sudden Tasks"]
exp = ["regular", "emergentjobs", "hardjobs", "death", "suddentasks"]
for i in range(5):
	for joblength in [10]:
		print("starting to show results for {} with job length {}".format(exp[i], joblength))
		
		data = np.loadtxt("/home/drew/tmp/jumpNN/{}/{}/graphTimeResults.txt".format(exp[i], joblength), dtype={'names': ('time', 'mean', 'group'), 'formats': ('f4', 'f4', 'S20')})

		
		plt.plot(data['time'][0:200000], data['mean'][0:200000], label="Simple", color=colors[3])
		plt.plot(data['time'][200001:400000], data['mean'][200001:400000], label="SimpleJump", color=colors[2])
		plt.plot(data['time'][400001:600000], data['mean'][400001:600000],label="Auction", color=colors[1])
		if exp[i] != "death":
			plt.plot(data['time'][600001:800000], data['mean'][600001:800000], label="NN", color=colors[0])
			plt.plot(data['time'][800001:1000001], data['mean'][800001:1000001], label="NNJ", color=colors[4])
		#plt.legend(bbox_to_anchor=(0., 1.02, 1., .102), loc=3, ncol=2, mode="expand", borderaxespad=0.)
		plt.ticklabel_format(style='sci', axis='x', scilimits=(0,3))
		plt.legend()

		plt.xlabel('Time Step')
		plt.ylabel('Average Waiting Time')
		plt.title("{} with Job Length {}".format(titles[i], joblength))
		#print(" first {} second {} third {} forth {}".format(data['group'][0], data['group'][200001], data['group'][400001], data['group'][600001]))
		plt.show()

		raw_input("Press Enter to continue...")