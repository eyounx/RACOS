% example for continuous optimization
clear;
dim_size = 50;
dim(:,1) = -1*ones(dim_size, 1);
dim(:,2) = ones(dim_size, 1);
iteration = 100;
samplesize = 20;
positivesize = 2;
uncertainbits = 1;
probability = 0.99;
fct = 'Sphere';

[x, fx] = RacosContinue(dim_size,dim,iteration,samplesize,positivesize,uncertainbits,probability,fct);

fprintf('continuous problem best solution value ; %f\n', fx);
