# Xhare-Workspace
## Multimodal Trip Planning with Dynamic Ridesharing

This repository represents the work I carried out during my Winter Internship at Xerox Research Centre, Bangalore from December 2015 to January 2016.

We focussed on the problem of integrating Dynamic Ridesharing in Multimodal Trip Planning. While the previous approaches in dynamic ridesharing, partition the road network using grids, we propose to partition it into clusters of landmarks. Through our approach, not only can we incorporate their dynamicity and reachability, but also satisfy user-requirements such as safety. I implemented a benchmark that utilizes a spatio-temporal index for searching candidate taxis and lazy shortest path calculation strategy for scheduling. I also developed heuristics to calculate the road-network distances between all landmark pairs, that resulted in a 12x improvement. Further, I devised an effective way to cluster the city landmarks using K-Medoids and modified Hill Climbing algorithm. 

![Dynamic Ridesharing Toy Example](https://github.com/sidak/Xhare-Workspace/blob/master/tshare_toy_example.jpg)
