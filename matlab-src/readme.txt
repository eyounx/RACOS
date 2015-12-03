------------------------------------------------------------------------------------------
	        Readme for the RACOS Package
	 		       version 3 Dec., 2015
------------------------------------------------------------------------------------------

The package includes the Matlab code of the RACOS (Randomized coordinate shrinking classifier for optimization) algorithm. Details can be found in paper [1]. 

[1] Yang Yu, Hong Qian, and Yi-Qi Hu. Derivative-free optimization via classification. In: Proceedings of the 30th AAAI Conference on Artificial Intelligence (AAAI'16), Phoenix, AZ, 2016.
 

There are three versions for RACOS: RacosContinue for continouous optimization, RacosDiscrete for discrete optimization, and RacosMix for conitnuous-discrete mixed optimization.
An example of useage is:
	RacosContinue(10,upper-and-lower-bounds,100,20,2,2,0.99,'Sphere')
where, upper-and-lower-bounds is a 10*2 matrix storing the upper and lower bounds of the search region, and the 'Sphere' is the objective function name.


ATTN: 
- This package is free for academic usage. You can run it at your own risk. For other
  purposes, please contact Dr. Yang Yu (yuy@nju.edu.cn) or Prof. Zhi-Hua Zhou (zhouzh@nju.edu.cn).

- This package was developed by Mr. Yi-Qi Hu (huyq@lamda.nju.edu.cn). For any
  problem concerning the code, please feel free to contact Mr. Hu.

------------------------------------------------------------------------------------------