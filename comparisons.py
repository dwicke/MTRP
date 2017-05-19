import numpy as np
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison


data = np.loadtxt("/home/dfreelan/tmp/regular/0/allBountyResults.txt", dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S20')})

# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
mc = MultiComparison(data['mean'], data['group'])
result = mc.tukeyhsd()
 
print(result)
print(mc.groupsunique)

a = result.plot_simultaneous()
a.show()

raw_input("Press Enter to continue...")


data = np.loadtxt("/home/dfreelan/tmp/regular/5/allBountyResults.txt", dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S20')})

# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
mc = MultiComparison(data['mean'], data['group'])
result = mc.tukeyhsd()
 
print(result)
print(mc.groupsunique)

a = result.plot_simultaneous()
a.show()

raw_input("Press Enter to continue...")


data = np.loadtxt("/home/dfreelan/tmp/regular/10/allBountyResults.txt", dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S20')})

# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
mc = MultiComparison(data['mean'], data['group'])
result = mc.tukeyhsd()
 
print(result)
print(mc.groupsunique)

a = result.plot_simultaneous()
a.show()

raw_input("Press Enter to continue...")


data = np.loadtxt("/home/dfreelan/tmp/regular/15/allBountyResults.txt", dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S20')})

# http://www.statsmodels.org/stable/_modules/statsmodels/sandbox/stats/multicomp.html#MultiComparison
mc = MultiComparison(data['mean'], data['group'])
result = mc.tukeyhsd()
 
print(result)
print(mc.groupsunique)

a = result.plot_simultaneous()
a.show()

raw_input("Press Enter to continue...")

