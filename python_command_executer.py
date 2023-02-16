#!/usr/bin/env python
import os
import time
import schedule
import psycopg2

mod_file_path = '/home/rippercus/Development/git/test/examples/vrptw_gmpl/vrptw.mod'
folder_path_of_dat_file = '/home/rippercus/Development/git/test/examples/Problems/'
path_of_parse_result = '/home/rippercus/Development/git/test/examples/Problems/parse_result'
def running_lp():
    print("running pending lp...");
    
    #Connect to your postgres DB
    conn = psycopg2.connect("host=localhost dbname=deliverymapping user=dynamic password=dynamic")
    try:
        #Open a cursor to perform database operations
        cur = conn.cursor()
        cur.execute("SELECT problem_id FROM delivery_problem WHERE is_route_processed = 2 AND is_result_parsed = 0")
        records = cur.fetchall()
        if len(records)!=0:
            for row in records:
                problem_id = row[0]
                list_of_files = os.listdir(folder_path_of_dat_file)
                problem_id_new = str(problem_id) + '.dat'
                for file_name in list_of_files:
                    if problem_id_new in file_name:
                        file_name_without_dat_array = file_name.split(".")
                        file_name_without_dat = file_name_without_dat_array[0]
                        command = path_of_parse_result+ ' '+mod_file_path+' '+folder_path_of_dat_file+file_name+' > ' +folder_path_of_dat_file+'result'+file_name_without_dat+'.txt'
                        print(command)
                        status = os.system(command)
                        if status!=0:
                            print("Error Occurred!")
                        else:
                            sql_select_query = """UPDATE delivery_problem SET is_result_parsed = 0 WHERE problem_id = %s"""
                            cur.execute(sql_select_query, (problem_id,))
                            conn.commit()
        cur.close()                    
                            
    except:
        print("no request!")
               
    

schedule.every(5).seconds.do(running_lp)

while True:
 
    # Checks whether a scheduled task
    # is pending to run or not
    schedule.run_pending()
    time.sleep(1)                

