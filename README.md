[![Operations Research Journal Logo](https://orjournal.github.io/OperationsReseachHeader.jpg)](https://pubsonline.informs.org/journal/opre)


# ddBanded



The software and data in this repository are a snapshot of the software and data
that were used in the research reported on in the paper 
[Real-time solution of quadratic optimization problems with banded matrices and indicator variables](https://arxiv.org/abs/2405.03051) by Andres Gomez, Shaoning Han and Leonardo Lozano. 


<!--**Important: This code is being developed on an on-going basis at 
https://github.com/agomez8/ddBanded. Please go there if you would like to
get a more recent version or would like support**-->

## Cite

Below is the BibTex for citing this snapshot of the repository.

```
@misc{ddBanded,
  author =        {Andres Gomez and Shaoning Han and Leonardo Lozano},
  title =         {{Real-time solution of quadratic optimization problems with banded matrices and indicator variables}},
  year =          {2025},          
  url =           {https://github.com/agomez8/ddBanded},
  note =          {Available for download at https://github.com/agomez8/ddBanded},
}  

```


## Description

The goal of this software is to demonstrate the use of decision diagrams to solve mixed-integer quadratic optimization problems with banded matrices and indicator variables.

The methods are implemented in Java and rely on the commercial solver Mosek. Executing the code requires a license for this solver.

## Executing the code

As a java code, the source code is precompiled and can be executed directly via file ./dist/MINLP.jar. Ensure to install [Mosek](https://www.mosek.com/downloads) and add file mosek.jar obtained from installing the software to ./dist/lib. 

The code can be executed from the console. Files "runDD.bat" and "runDDOnline.bat" contain examples of how to execute the code in offline and online settings. 

An example command to execute the code to tackle an offline problem is
```
java  -cp ./dist/MINLPDD.jar minlpdd_ConsecutiveOnes.MINLPDD ./data/daily_data_1990.csv 25 0.001 0.25 2 0 101 0
```
where: "java  -cp ./dist/MINLP.jar" points to the direction of the executable jar file, and "minlpdd_ConsecutiveOnes.MINLPDD" is the class used to run offline instances. The rest of parameters are as follows:
* First parameter (./data/daily_data_1990.csv) is a path to the dataset to use
* Second parameter (25) is the number of time periods to use
* Third parameter (0.001) is the weight of the L0 parameter (objective cost for the discrete variables)
* Fourth parameter (0.25) is the weight of the smooth regularization term
* Fifth parameter (2) is the bandwidth of the smooth regularization term
* Sixth parameter (0) is the minimum number of consecutive ones (by default, 0 means no constraint)
* Seventh parameter (101) is the seed
* Eighth parameter (0) is the method to be used. Method: 0: Mosek based on perspective reformulation. 1: Decision diagram.

An example command to execute the code to simulate solution in an online setting is
```
java  -cp ./dist/MINLPDD.jar minlpdd_ConsecutiveOnes.MINLPDDOnline ./data/daily_data_1990.csv 7022 0.001 0.25 2 0 200 101 1
```
where: "java  -cp ../dist/MINLPDD.jar minlpdd_ConsecutiveOnes.MINLPDDOnline" points to the direction of the executable jar file, and "minlpdd_ConsecutiveOnes.MINLPDDOnline" is the class used to run online instances. The rest of parameters are as follows:
* First parameter (./data/daily_data_1990.csv) is a path to the dataset to use
* Second parameter (7022) is the number of total time periods
* Third parameter (0.001) is the weight of the L0 parameter (objective cost for the discrete variables)
* Fourth parameter (0.25) is the weight of the smooth regularization term
* Fifth parameter (2) is the bandwidth of the smooth regularization term
* Sixth parameter (0) is the minimum number of consecutive ones (by default, 0 means no constraint)
* Seventh parameter (200) is the horizon (i.e., problems are solved at each step using this number of previous datapoints)
* Seventh parameter (101) is the seed
* Eighth parameter (1) is the method to be used. By default it is one, referring to decision diagrams.

## Output

After solving an instance, the results are recorded in "./results/resultsOffline.csv" (for offline instances) and "./results/resultsOnline.csv" (for online instances). Each instance solved is added as a new row to these files. 

For offline problems and method =0 (Mosek), each row is organized as follows:
* Columns 1-8: Are the parameters used to generate the instance
* Column 9: 0
* Column 10: 0
* Column 11: 0
* Column 12: 0
* Column 13: Time spent in branch-and-bound
* Column 14: 0
* Column 15: 0
* Column 16: Objective value as reported by Mosek
* Column 17: Objective value computed by retrieving the optimal discrete variables from Mosek, and computing the objective in closed form
* Column 18: Number of branch and bound nodes
* Column 19: Relative gap reported by Mosek after branch-and-bound
* Column 20: 0
* Column 21: 0
* Column 22: 0
* Column 23: 0

For offline problems and method =1 (Decision diagram), each row is organized as follows:
* Columns 1-8: Are the parameters used to generate the instance
* Column 9: Number of nodes in the decision diagram
* Column 10: Number of arcs in the decision diagram
* Column 11: Time to construct the decision diagram
* Column 12: Time to solve a shortest path in the decision diagram (not including construction)
* Column 13: Time to solve the SOCP hull relaxation in the decision diagram
* Column 14: Objective value obtained by solving a shortest path in the decision diagram
* Column 15: Objective value computed by retrieving the optimal discrete variables from the shortest path method, and computing the objective in closed form
* Column 16: Objective value obtained from the SOCP hull relaxartion as reported by Mosek
* Column 17: Objective value computed by retrieving the optimal discrete variables from the SOCP hull relaxation, and computing the objective in closed form
* Column 18: 0
* Column 19: 0  
* Column 20: Maximum number of nodes per layer in the decision diagram
* Column 21: Number of previous decisions m that a truncated decision diagram would have to remember
* Column 22: Maximum number of nodes per layer 2^m of a truncated decision diagram
* Column 23: Condition number of the matrix Q

For online problems, each row is organized as follows:
* Columns 1-8: Are the parameters used to generate the instance
* Column 9: Number of nodes in the decision diagram
* Column 10: Number of arcs in the decision diagram
* Column 11: Time to construct the decision diagram
* Column 12: Total time spent solving shortest paths in the decision diagram
* Column 13: 0



## Replicating

To replicate the results in offline setting, reported in Table 1, Table 2, and Figure 5, Figure 6 and Figure 7 of the paper, use file runDD.bat (on a Windows machine). This file creates the raw output file resultsOffline.csv, stored in the results folder. To replicate the results reported in Table 3, use file runDDTable3.bat, which will store the raw output in the same file resultsOffline.csv. To process this result to generate Tables 1 and 2-3 from the paper, use file generateTables.bat. This would create files Table1.csv and Table2.csv in the results folder, with the information from the respective tables (file Table 2 contains information from Table 3 as well if runDDTable3.csv was used). To generate Figures 5-7, run file generateFigures5-7.bat, which will store the figures in the results folder.

To replicate the results in online setting, reported in Table 4 and Figure 1 of the paper, use file runDDOnline.bat (on a Windows machine). To process this result to generate Table 4 from the paper, use file generateTableOnline.bat. This would create file Table4.csv in the results folder, with the information of that table. Figure 1 can be generated by executing file generateFigure1.bat.

Note that Mosek 10.0 was used in the paper. The experiments were run in a laptop with a 12th Gen Intel Core i7-1280 CPU and 32 GB RAM. All experiments were run using a single thread.

## Source code
The source code can be found in the src folder.


