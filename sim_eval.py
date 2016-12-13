#!/usr/bin/env python
"""
Author: Tommaso Soru <tsoru@informatik.uni-leipzig.de>
"""
import sys

reload(sys)
sys.setdefaultencoding("utf-8")

def eval_with(theta, filename):
    posneg = [[0, 0], [0, 0], [0, 0]]
    with open(filename) as f:
        for line in f:
            line = line[:-1].split('\t')
            if line[2] == "MFKC":
                continue # skip headers
            sims = [float(line[2]), float(line[3]), float(line[4])]
            for i in range(len(sims)):
                if sims[i] >= theta:
                    posneg[i][0] += 1.0 # add positive
                else:
                    posneg[i][1] += 1.0 # add negative            
    return posneg

def evaluation(theta):
    tpfn = eval_with(theta, 'p.tsv')
    # print tpfn
    fptn = eval_with(theta, 'n.tsv')
    # print fptn
    ev = list()
    for i in range(len(tpfn)):
        tp, fn = tpfn[i]
        fp, tn = fptn[i]
        if tp + fp > 0:
            pre = tp / (tp + fp)
        else:
            pre = 0
        if tp + fn > 0:
            rec = tp / (tp + fn)
        else:
            rec = 0
        if pre + rec > 0:
            f1 = 2 * pre * rec / (pre + rec)
        else:
            f1 = 0.0
        ev.append("{}\t{}\t{}\t{}\t{}".format(theta, i, pre, rec, f1))
    return ev
    
def do_cycle(th):
    theta = float(th) / 100.0
    ev = evaluation(theta)
    print('\n'.join(map(str, ev)))

# wide-range evaluation
for th in xrange(50, 105, 5):
    do_cycle(th)
print "-----"
# to find the peaks    
for th in xrange(88, 101, 1):
    do_cycle(th)
