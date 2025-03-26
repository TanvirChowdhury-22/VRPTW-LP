# VRPTW-LP
# Project on Delivery Mapping based on VRPTW


This repository focuses on solving the Vehicle Routing Problem with Time Windows (VRPTW) using Linear Programming (LP). The VRPTW is an extension of the classic Vehicle Routing Problem (VRP), incorporating specific time frames during which deliveries or pickups must occur.


Repository Structure
	•	deliverymapping/: Directory containing scripts and tools related to mapping deliveries based on VRPTW solutions.
	•	vrptw-datgenerator/: Folder housing utilities for generating data files compatible with VRPTW models.
	•	lpglpk.service: Service file, possibly for automating tasks related to GLPK (GNU Linear Programming Kit) in solving LP formulations of VRPTW.
	•	parse_result/: Directory containing scripts to parse and analyze results obtained from VRPTW solutions.
	•	parse_result.cpp: C++ source file for parsing the output of VRPTW solutions, likely converting solver outputs into a more interpretable format.
	•	python_command_executer.py: Python script designed to execute specific commands, potentially automating parts of the workflow.
	•	read_parse_text_file.py: Python script for reading and parsing text files, possibly related to input data or solver outputs.
	•	vrptw.mod: Model file defining the mathematical formulation of the VRPTW, intended for use with linear programming solvers like GLPK.

Getting Started

To utilize the resources in this repository:
	1.	Prerequisites:
	•	Install GLPK for solving linear programming models.
	•	Ensure Python is installed for executing auxiliary scripts.
	2.	Data Generation:
	•	Use the tools in vrptw-datgenerator to create data files that adhere to the VRPTW model specifications.
	3.	Model Execution:
	•	Employ vrptw.mod with GLPK to solve the VRPTW based on the generated data.
	4.	Result Parsing:
	•	Utilize parse_result.cpp or scripts within parse_result/ to interpret and analyze the solver’s output.
	5.	Automation:
	•	Consider using lpglpk.service and python_command_executer.py to automate the workflow for efficiency.
