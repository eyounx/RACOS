function re=Ackley(data)
bias = ones(1,size(data,2))/5;
re=-20*exp(-0.2*sqrt((1/size(data,2))*norm(data)^2))-exp((1/size(data,2))*sum(cos(2*pi*data)))+exp(1)+20;
return;
