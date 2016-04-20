%example for mixed optimization
clear;

dim_size = 20;
dim(:,1) = zeros(dim_size, 1);
dim(:,2) = 2*ones(dim_size, 1);
dim_type = [0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1];
iteration = 100;
samplesize = 20;
positivesize = 2;
uncertainbits = 1;
probability = 0.99;
fct = 'SimpleMixFunction';

[x, fx] = RacosMix(dim_size,dim,dim_type, iteration,samplesize,positivesize,uncertainbits,probability,fct);

fprintf('discrete problem best solution value ; %f\n', fx);