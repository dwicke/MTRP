import numpy as np
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison


data = np.loadtxt("/home/drew/tmp/death/allTimeResults.txt", dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S10')})

# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
mc = MultiComparison(data['mean'], data['group'])
result = mc.tukeyhsd()
 
print(result)
print(mc.groupsunique)

a = result.plot_simultaneous()
a.show()

raw_input("Press Enter to continue...")


data = np.loadtxt("/home/drew/tmp/death/allBountyResults.txt", dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S10')})

# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
mc = MultiComparison(data['mean'], data['group'])
result = mc.tukeyhsd()
 
print(result)
print(mc.groupsunique)

a = result.plot_simultaneous()
a.show()
raw_input("Press Enter to continue...")
