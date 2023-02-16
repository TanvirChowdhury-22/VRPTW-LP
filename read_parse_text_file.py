#!/usr/bin/env python
import os
import time
import schedule
import psycopg2
from collections import Counter
import subprocess
import sys

folder_dir = "/home/rippercus/Development/git/test/examples/Problems"
def running_lp():
    try:
        # Connect to your postgres DB
        conn = psycopg2.connect("host=localhost dbname=deliverymapping user=dynamic password=dynamic")

        # Open a cursor to perform database operations
        cur = conn.cursor()
        cur1 = conn.cursor()
        cur2 = conn.cursor()

        # Execute a query
        cur.execute("SELECT problem_id FROM delivery_problem WHERE is_result_parsed = 0 AND is_route_processed = 2")

        # Retrieve query results
        records = cur.fetchall()
        print(records)
        if len(records)!=0:
            for row in records:
                problem_id = row[0]
                print(problem_id)
                text_files_with_X = []
                text_files_with_X_1_lines = [] 
                solutions = []
                nodes_for_vehicle1 = []
                nodes_for_vehicle2 = []
                vehicles_data = []

                openfile = folder_dir + "/resultProblem"+str(problem_id)+".txt"
                isExist = os.path.isfile(openfile)
                try:
                    if isExist==True:
                        with open (openfile, 'rt') as datfile:
                            for eachline in datfile: 
                                if eachline.startswith('X') :
                                    text_files_with_X.append(eachline)
                                    if eachline.endswith('1\n'):              
                                        text_files_with_X_1_lines.append(eachline)                        
                                        
                        if(len(text_files_with_X)==0):
                            sql_select_query3 = """UPDATE delivery_problem SET is_result_parsed = 3 WHERE problem_id = %s"""
                            cur1.execute(sql_select_query3, (problem_id,))
                            conn.commit()
                        else:             
                            if(len(text_files_with_X_1_lines)==0):
                                sql_select_query4 = """UPDATE delivery_problem SET is_result_parsed = 2 WHERE problem_id = %s"""
                                cur1.execute(sql_select_query4, (problem_id,))
                                conn.commit() 
                            else: 
                                for eachroute in range(len(text_files_with_X_1_lines)):
                                    eachroute_0 = text_files_with_X_1_lines[eachroute]
                                    eachroute_1 = eachroute_0.split('[')
                                    eachroute_2 = eachroute_1[1]
                                    route_name =  eachroute_2.split(']')
                                    route_name_1 = route_name[0]
                                    solutions.append(route_name_1)

                                for eachentity in range(len(solutions)):
                                    eachentity_0 = solutions[eachentity]
                                    eachentity_1 = eachentity_0.split(',')
                                    start = eachentity_1[0]
                                    end = eachentity_1[1]
                                    vehicle = eachentity_1[2]
                                    if(vehicle == "VEHICLE1"):
                                        nodes_for_vehicle1.append(start + ", " + end)
                                    elif(vehicle == "VEHICLE2"):
                                        nodes_for_vehicle2.append(start + ", " + end)   
                                    vehicles_data.append(vehicle)

                                sortedroute_for_vehicle1 = []
                                sortedroute_for_vehicle1.append(nodes_for_vehicle1[0]);

                                for nodes in range(len(nodes_for_vehicle1)):
                                    nodes_2 = sortedroute_for_vehicle1[nodes]
                                    nodes_1 = nodes_2.split(', ')
                                    startnode = nodes_1[0]
                                    endnode = nodes_1[1]  
                                    for nodes in range(len(nodes_for_vehicle1)):
                                        if(nodes_for_vehicle1[nodes].startswith(endnode)):
                                            sortedroute_for_vehicle1.append(nodes_for_vehicle1[nodes])

                                sortedroute_for_vehicle2 = []
                                sortedroute_for_vehicle2.append(nodes_for_vehicle2[0]);

                                for nodes in range(len(nodes_for_vehicle2)):
                                    nodes_2 = sortedroute_for_vehicle2[nodes]
                                    nodes_1 = nodes_2.split(', ')
                                    startnode = nodes_1[0]
                                    endnode = nodes_1[1]  
                                    for nodes in range(len(nodes_for_vehicle2)):
                                        if(nodes_for_vehicle2[nodes].startswith(endnode)):
                                            sortedroute_for_vehicle2.append(nodes_for_vehicle2[nodes])

                                new_sortedroute_for_vehicle1 = []            
                                for nodes in range(len(sortedroute_for_vehicle1)):
                                    nodes_2 = sortedroute_for_vehicle1[nodes]
                                    nodes_1 = nodes_2.split(', ')
                                    startnode = nodes_1[0]
                                    endnode = nodes_1[1]  
                                    new_sortedroute_for_vehicle1.append(startnode)

                                new_sortedroute_for_vehicle1.append(new_sortedroute_for_vehicle1[0])

                                new_sortedroute_for_vehicle2 = []            
                                for nodes in range(len(sortedroute_for_vehicle2)):
                                    nodes_2 = sortedroute_for_vehicle2[nodes]
                                    nodes_1 = nodes_2.split(', ')
                                    startnode = nodes_1[0]
                                    endnode = nodes_1[1]  
                                    new_sortedroute_for_vehicle2.append(startnode)

                                new_sortedroute_for_vehicle2.append(new_sortedroute_for_vehicle2[0])


                                cnt = Counter(vehicles_data)
                                vehicles = [k for k, v in cnt.items() if v >= 1]

                                i,j = 0,1
                                vehicles[i], vehicles[j] = vehicles[j], vehicles[i]
                                finalroute_list = []
                                finalroute_list.append(new_sortedroute_for_vehicle1)
                                finalroute_list.append(new_sortedroute_for_vehicle2)

                                d = {vehicles[k]:finalroute_list[k] for k in range(0,len(vehicles))} 
                                d_str = str(d)
                                print(d_str)
                                sql_insert_query = """ INSERT INTO problem_results (problem_id, parsed_result) VALUES (%s,%s)"""
                                record_to_insert = (problem_id, d_str)
                                cur2.execute(sql_insert_query, record_to_insert)
                                sql_select_query = """UPDATE delivery_problem SET is_result_parsed = 1 WHERE problem_id = %s"""
                                cur1.execute(sql_select_query, (problem_id,))
                                conn.commit()
                                try:
                                    result = subprocess.run([sys.executable, "-c", "import time; time.sleep(1)"], timeout=600)
                                except:        
                                    print("Time Out!")
                                    sql_select_query1 = """UPDATE delivery_problem SET is_result_parsed = 3 WHERE problem_id = %s"""
                                    cur1.execute(sql_select_query1, (problem_id,))
                                    conn.commit()
                
                    else:
                        print("text file doesn't exist for "+problem_id+"!")
                except:
                    print("Error Ocurred!")      
        else:
            print("No file to parse!")
        
        cur.close()
        cur1.close()
        cur2.close()
    except:
        print("Error Ocurred!")
        
schedule.every(5).seconds.do(running_lp)

while True:

    # Checks whether a scheduled task
    # is pending to run or not
    schedule.run_pending()
    time.sleep(1)                

