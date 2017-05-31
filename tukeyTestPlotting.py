import numpy as np
import matplotlib.pyplot as plt
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.multicomp import MultiComparison


title = 'Sudden Tasks'
expType = 'jumpNN/regular'
files = ['/home/drew/tmp/{}/0/allTimeResults.txt'.format(expType),
    '/home/drew/tmp/{}/5/allTimeResults.txt'.format(expType),
    '//home/drew/tmp/{}/10/allTimeResults.txt'.format(expType), 
    '/home/drew/tmp/{}/15/allTimeResults.txt'.format(expType)]

colors = ['blue', '#9370DB', 'orange', 'yellow', 'green']

def get_data(files):
    data = []
    agent_means = [[] for i in range(5)]
    agent_errors = [[] for i in range(5)]
    for file in files:
        data = np.loadtxt(file, dtype={'names': ('mean', 'group'), 'formats': ('f4', 'S20')})
        mc = MultiComparison(data['mean'], data['group'])
        result = mc.tukeyhsd()
        result._simultaneous_ci()
        halfwidths = result.halfwidths
        means = mc.groupstats.groupmean
        for i in range(4):
            agent_means[i].append(means[i])
            agent_errors[i].append(halfwidths[i])

    return agent_means, agent_errors


def plot_bar_graphs(ax, means, errors, min_value=5, max_value=25, nb_samples=5):
    """Plot two bar graphs side by side, with letters as x-tick labels.
    """
    x = np.arange(nb_samples)*2
    label_shift = 0.375
    width = 0.25
    for i in range(5):
        ax.bar(x+i*width, means[i], width, yerr=errors[i], color=colors[i], error_kw=dict(ecolor='gray', lw=2, capsize=5, capthick=1))

    ax.set_xticks(x + label_shift)
    ax.set_xticklabels(['0', '5', '10', '15'])
    ax.legend(['NNJ', 'NN', 'Auction' ,'SimpleJump','Simple'])
    ax.set_title(title)
    ax.set_xlabel('Job Length')
    ax.set_ylabel('Average Waiting Time')
    return ax


if __name__ == "__main__":
    means, errors = get_data(files)
    fig, ax = plt.subplots()
    plot_bar_graphs(ax, means, errors)
    plt.show()