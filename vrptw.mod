set ROOT_NODE;
set FINAL_NODE;
set DELIVERY_NODES;
set VEHICLE;

set LINKS := {i in DELIVERY_NODES, j in DELIVERY_NODES: i <> j};

param EARLIEST_TIME {DELIVERY_NODES};
param LAST_TIME {DELIVERY_NODES};
param SERVICE_TIME {DELIVERY_NODES};
param demand {DELIVERY_NODES};
param CAPACITY {VEHICLE};

param DISTANCE {DELIVERY_NODES, DELIVERY_NODES} > 0;

var X {i in DELIVERY_NODES, j in DELIVERY_NODES, k in VEHICLE} binary;

var W {i in DELIVERY_NODES, k in VEHICLE};

minimize total_DISTANCE:  sum {i in DELIVERY_NODES, j in DELIVERY_NODES, k in VEHICLE: i != j} DISTANCE[i,j]*X[i,j,k];

subject to c1 {i in DELIVERY_NODES: i not in ROOT_NODE and i not in FINAL_NODE}: sum {k in VEHICLE, j in DELIVERY_NODES: (i,j) in LINKS and j not in ROOT_NODE} X[i,j,k] = 1;

subject to c2 {k in VEHICLE, i in ROOT_NODE}: sum {j in {DELIVERY_NODES diff ROOT_NODE}: i != j} X[i,j,k] = 1;

subject to c3 {k in VEHICLE, j in DELIVERY_NODES: j not in ROOT_NODE and j not in FINAL_NODE}: sum {i in DELIVERY_NODES: (i,j) in LINKS and i not in FINAL_NODE} X[i,j,k] - sum{i in DELIVERY_NODES: (i,j) in LINKS and i not in ROOT_NODE} X[j,i,k] = 0;

subject to c4 {k in VEHICLE, j in FINAL_NODE}: sum {i in {DELIVERY_NODES diff FINAL_NODE}: i != j} X[i, j, k] = 1;

subject to c5 {i in DELIVERY_NODES, k in VEHICLE, j in DELIVERY_NODES: i != j and j not in ROOT_NODE and i not in ROOT_NODE and i not in FINAL_NODE}: W[i,k] - W[j,k] + (LAST_TIME[i] - EARLIEST_TIME[j] + SERVICE_TIME[i] + SERVICE_TIME[j]) * X[i,j,k] <= LAST_TIME[i] - EARLIEST_TIME[j];

subject to c6 {k in VEHICLE, i in DELIVERY_NODES: i not in ROOT_NODE and i not in FINAL_NODE}: W[i,k] - LAST_TIME[i] * sum {j in DELIVERY_NODES: (i,j) in LINKS and j not in ROOT_NODE} X[i,j,k] <= 0;

subject to c7 {k in VEHICLE, i in DELIVERY_NODES: i not in ROOT_NODE and i not in FINAL_NODE}: EARLIEST_TIME[i] * sum {j in DELIVERY_NODES: (i,j) in LINKS and j not in ROOT_NODE} X[i,j,k] - W[i,k] <= 0;

subject to c8 {i in ROOT_NODE, k in VEHICLE}: W[i,k] >= EARLIEST_TIME[i];

subject to c9 {i in ROOT_NODE, k in VEHICLE}: W[i,k] <= LAST_TIME[i];

subject to c10 {i in FINAL_NODE, k in VEHICLE}: W[i,k] >= EARLIEST_TIME[i];

subject to c11 {i in FINAL_NODE, k in VEHICLE}: W[i,k] <= LAST_TIME[i];

subject to c12 {k in VEHICLE}: sum {i in {DELIVERY_NODES diff {ROOT_NODE union FINAL_NODE}}} demand[i] * sum {j in DELIVERY_NODES: (i,j) in LINKS and j not in ROOT_NODE} X[i,j,k] <= CAPACITY[k];
end;