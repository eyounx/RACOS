function result=Sphere(data)
bias = ones(1,size(data,2))/5;%bias = 0.2
result = norm(data-bias)^2;
return;