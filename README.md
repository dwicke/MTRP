# MTRP
Multiagent Traveling Repairman Problem


Say you have a leak in your bathroom and you don't know how to fix it, but you don't have the time to call or price repairmen to fix your leak.  Instead, you place a bounty reward on the task to fix your leak and let the plumbers come to you.  While the task has not been completed the bounty associated with the task rises in accordance with the urgency of the task.  Once the task has been completed the bounty is awarded to the plumber that has completed the task.  As with bounty hunting the tasks are not exclusive.  A bounty hunter might get to the task and say that they are going to work on it, but if they don't have all the equipment to complete the task they will have to abandon the task to obtain the resources.  During that time another agent may finish the task before they return.  Once the requisite supplies have been obtained the time to complete the task is drawn from a distribution.  As always only the bounty hunter who completes the task obtains the current bounty.

So, here we have a scenario where instead of making the customer find the right plumber it is up to the plumber to find the right customer.  As is the case with these problems, we can assume we have more tasks than we have bounty hunters (and the tasks appear dynamically drawn from task classes), the bounty hunters can carry only so many supplies, and they can travel only so far with the fuel they have.  In order to obtain supplies the bounty hunter must purchase them at some depo.  The quantity and price of the items at the depos are known with some probability (like when you get on walmart's website and it says they have stock for something and you get there and they are out of stock).  Also, there is a travel cost for fuel to go between the different customers and depos.  The problem then is how do the bounty hunters decide which tasks to go after and when so as to minimize the time that the customers are waiting and maximize their profit.

So, that is basically what this project is intending on simulating and trying to solve.  I will simulate this problem using MASON.

From this I hope to continue to look at more complex problems such as how do groups of repairmen cooperate in order to maximize there combined profit?  This is similar to the k-traveling repairman problem but where there are also other groups competing.  Also, how do we distribute this system accross different bondsmen when there are a large number of tasks and bounty hunters?  So, there are a number of additional questions that will possibly be address and can be studied within this problem setting.

Note, this is an offshoot of the bounties repo https://github.com/dwicke/bounties.  I do more general testing of bounty hunting not specifically focused on the MTRP there and here I will focus on the MTRP only.
