% This program is free software; you can redistribute it and/or
% modify it under the terms of the GNU General Public License
% as published by the Free Software Foundation; either version 2
% of the License, or (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with this program; if not, write to the Free Software
% Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
%
% Copyright (C) 2015 Nanjing University, Nanjing, China
% 
% Author Yi-Qi Hu
% Date 2015.12.2
% Version 1.0
% 

function Optimal=RacosDiscrete(dim_size,dim,iteration,samplesize,positivesize,uncertainbits,probability,fct)

%---------------------parameters---------------------------%
%dim_size: dimension size
%dim: region matrix, is a d*2 matrix. d=dim_size, dim(i,1) is i-th
%   dimension's lower bound, dim(i,2) is upper bound
%iteration: iteration size
%samplesize: the number of sample in each iteration
%positivesize: the number of good samples that we consider in each
%   iteration
%uncertainbits: parameter in algorithm, can set 2 usually
%probability: usually set 0.99
%fct: objective function name
%--------------------initialization-----------------------%
objectivefct = fct;   %ojective function name
%generate the first population randomly
for i=1:dim_size
    Pop(:,i) = (dim(i,1)-1)*ones(samplesize,1)+unidrnd(dim(i,2)-dim(i,1)+1,samplesize,1);
end
%query for each sample
for i=1:samplesize
    Popv(1,i) = feval(objectivefct,Pop(i,:));
end
%initialize postive population
for i=1:positivesize
    m = find(Popv == min(Popv));  %find min value in Pop
    PosPop(i,:) = Pop(m,:);       %obtain positive instance
    PosPopv(1,i) = Popv(1,m);     %obtain the value of positive instance 
    Pop(m,:) = [];                %delete this instance in Pop
    Popv(m:m) = [];
end
%initialize optimal
Optimal = PosPop(1,:);
Optimalv = PosPopv(1,1);

%--------------------iteration loop----------------------%
for itera=2:iteration
    %generate model for each sample
    for i=1:samplesize
        while(1)
            ChosenPos = unidrnd(positivesize);
            Ilabel = ones(1,dim_size);
            Plabel = zeros(1,dim_size);
            if rand()>probability     %global sample           
            else
                %shrink model
                seq = randperm(dim_size);
                j=1;
                while(DistinguishD(Plabel,Pop,PosPop(ChosenPos,:))==0)
                    Plabel(1,seq(1,j)) = 1;
                    Ilabel(1,seq(1,j)) = 0;
                    j = j+1;
                end
                %set uncertainbits
                while(j<=dim_size-uncertainbits)
                    Plabel(1,seq(1,j)) = 1;
                    Ilabel(1,seq(1,j)) = 0;
                    j = j+1;
                end
            end
            
            %global sample
            for j=1:dim_size
              NextPop(i,j) = dim(j,1)-1+unidrnd(dim(j,2)-dim(j,1)+1);
            end
            
            %create new sample
            NextPop(i,:) = NextPop(i,:).*Ilabel+PosPop(ChosenPos,:).*Plabel;
            
            if notExist(NextPop(i,:),PosPop,Pop,NextPop,i-1)==1   %if new sample is unique, then query
                NextPopv(1,i) = feval(objectivefct,NextPop(i,:));
                break;
            end
        end
    end
    
    %new population
    Pop = NextPop;
    Popv = NextPopv;
    
    %update PosPop
    for j=1:positivesize
        m = find(Popv == min(Popv));
        k=1;
        while(k<=positivesize)
            if PosPopv(1,k)>Popv(1,m)
                break;
            end
            k = k+1;
        end
        if k<=positivesize
            temp = Pop(m,:);
            tempv = Popv(1,m);
            Pop(m,:) = PosPop(positivesize,:);
            Popv(1,m) = PosPopv(1,positivesize);
            l = positivesize;
            while(l>k)
                PosPop(l,:) = PosPop(l-1,:);
                PosPopv(1,l) = PosPopv(1,l-1);
                l = l-1;
            end
            PosPop(k,:) = temp;
            PosPopv(1,k) = tempv;
        end
    end
    
    if Optimalv>PosPopv(1,1)      %update Optimal
        Optimal = PosPop(1,:);
        Optimalv = PosPopv(1,1);
    end
end
fprintf('%d',Optimalv);           %print the best value
return;
%return the best point Optimal after last iteration
%-------------------------------------------------------------

%------------------function notExist--------------------------
%this function is used to judge whether the new sample is unique
%return 1 means new sample is unique
function re=notExist(ins, PosPop, Pop, NextPop, endl)
for i=1:size(PosPop,1)              %in set PosPop
    if isequal(ins,PosPop(i,:))==1
        re = 0;
        return;
    end
end
for i=1:size(Pop,1)                 %in set Pop
    if isequal(ins,Pop(i,:))==1
        re = 0;
        return;
    end
end
for i=1:endl                       %in set NextPop
    if isequal(ins,NextPop(i,:))==1
        re = 0;
        return;
    end
end
re = 1;
return;
%---------------------------------------------------------------

%-----------------function DistinguishD-------------------------
%this function is used to judge whether the model can distinguish the
%   positive sample from negative samples
%return 1 means the model is able to distinguish positive sample from
%   negative samples
function re=DistinguishD(Plabel,Pop,Pos)
Posp = ones(size(Pop,1),1)*Pos;
Plabelp = ones(size(Pop,1),1)*Plabel;
middle = (Pop-Posp).*Plabelp;         %middle is judging model for discrete dimensions. if there exist element~=0 for each line, means this model can distinguish
for i=1:size(middle,1)
    %if middle is not distinguished model
    if (isempty(find(middle(i,:)~=0, 1))==1)
        re = 0;
        return;
    end
end
re = 1;
return ;
%---------------------------------------------------------------