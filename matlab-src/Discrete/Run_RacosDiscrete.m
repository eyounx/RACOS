% example for discrete optimization
clear;
dim_size = 20;
dim(:,1) = zeros(dim_size, 1);
dim(:,2) = ones(dim_size, 1);
iteration = 100;
samplesize = 20;
positivesize = 2;
uncertainbits = 1;
probability = 0.99;
fct = 'SetCover';

[x, fx] = RacosDiscrete(dim_size,dim,iteration,samplesize,positivesize,uncertainbits,probability,fct);

fprintf('discrete problem best solution value ; %f\n', fx);