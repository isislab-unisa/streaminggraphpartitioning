#!/usr/bin/env python

import csv
import os
import re
import sys
from datetime import datetime

out_dir_name = sys.argv[1] if sys.argv[1:] else 'out-gr'

graphsDirectory = os.path.join(os.curdir, "graphs")

try:
    os.mkdir(out_dir_name)
except FileExistsError:
    print('La directory esiste già')
out_file_name = os.path.join(out_dir_name, 'metis.out')
csvoutfile = open(out_file_name, 'w')
writer = csv.writer(csvoutfile, delimiter=' ', quotechar='"')
writer.writerow(['GraphName', 'k', 'CuttedEdges','CuttedEdgesRatio', 'TotalTime'])

graphs = []

for file_name in os.listdir(graphsDirectory):
    if file_name.endswith('.graph'):
        graphs.append(os.path.join(graphsDirectory,file_name))

for gr in graphs:
    for i in range(1, 8):
        k = 2 ** i
        totalEdges = int(re.findall('\d+',open(gr).read())[1])
        cmd = "gpmetis " + gr + " " + str(k) + "| grep -e Edgecut"
        startTime = datetime.now().microsecond
        result = os.popen(cmd).read()
        endTime = datetime.now().microsecond
        try:
            edgeCut = int(re.findall('\d+', result)[0])
            writer.writerow([gr.split(os.sep)[2], k, edgeCut, round(edgeCut/totalEdges,4), (endTime - startTime) / 1000])
        except:
            pass

#TODO
os.system("rm graphs/*.part*")
