import matplotlib.pyplot as plt
import numpy as np

f = open('sortedhist', 'r')
x=[]
y=[]
for line in f:
    terms = line.split("\t")
#    print terms.pop()
    y.append(int(terms.pop().strip("\n")))
    x.append(int(terms.pop()))
    
plt.title('CSCI 5673-Spring 2011-PA01-arsu2514')
plt.xlabel('Word Frequency (logarithmic scale)')
plt.ylabel('No. of words (logarithmic scale)')
plt.loglog(x,y,marker=",",linestyle="None")
plt.show()
