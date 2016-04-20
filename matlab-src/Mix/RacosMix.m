function [Optimal, Optimalv] = RacosMix(dim_size,dim,dim_type,iteration,samplesize,positivesize,uncertainbits,probability,fct)
%--------------parameters-----------------%
%dim_size: dimension size
%dim: region matrix, is a d*2 matrix. d=dim_size, dim(i,1) is i-th
%   dimension's lower bound, dim(i,2) is upper bound
%dim_type: dimension type vector, if dim_type(i)=0, means i-th dimension is
%   discrete; else is continue
%iteration: iteration size
%samplesize: the number of sample in each iteration
%positivesize: the number of good samples that we consider in each
%   iteration
%uncertainbits: parameter in algorithm, can set 2 usually
%probability: usually set 0.99
%fct: objective function name
%------------------initialize------------------------
objectivefct = fct;
%generate the first population randomly
for i=1:samplesize
    for j=1:dim_size
        if dim_type(1,j)==1
            Pop(i,j) = dim(j,1)+(dim(j,2)-dim(j,1))*rand();
        else
            Pop(i,j) = (dim(j,1)-1)+unidrnd(dim(j,2)-dim(j,1)+1);
        end
    end
end
%query for each sample
for i=1:samplesize
    Popv(1,i) = feval(objectivefct,Pop(i,:));
end
%initialize postive population
for i=1:positivesize
    m = find(Popv == min(Popv));  %find min value in Pop
    m = m(1, 1);
    PosPop(i,:) = Pop(m,:);       %obtain positive instance
    PosPopv(1,i) = Popv(1,m);     %obtain the value of positive instance 
    Pop(m,:) = [];                %delete this instance in Pop
    Popv(m:m) = [];
end

%initialize optimal
Optimal = PosPop(1,:);
Optimalv = PosPopv(1,1);

%--------------------Iteration Loop-----------------------
for itera=2:iteration
    for i=1:samplesize
        while(1)
            %reset model
            region = dim;          
            Ilabel = ones(1,dim_size);
            Plabel = zeros(1,dim_size);
            ChosenPos = unidrnd(positivesize);%choose a positive sample randomly
            if rand()>probability             %global sample
            else
                %shrink model
                seq = 1:dim_size;
                while(DistinguishM(dim_type,Plabel,region,Pop,PosPop(ChosenPos,:))==0)
                    ChosenDim = unidrnd(size(seq,2));
                    if dim_type(1,seq(1,ChosenDim))==1    %if chosen dimension is continue
                        ChosenNeg = unidrnd(size(Pop,1)); %choose a negative instance randomly
                        if PosPop(ChosenPos, seq(1,ChosenDim))<Pop(ChosenNeg, seq(1,ChosenDim))
                            tempbound = PosPop(ChosenPos, seq(1,ChosenDim))+(Pop(ChosenNeg, seq(1,ChosenDim))-PosPop(ChosenPos, seq(1,ChosenDim)))*rand();
                            if tempbound<region(seq(1,ChosenDim),2)
                                region(seq(1,ChosenDim),2) = tempbound;
                            end
                        else
                            tempbound = Pop(ChosenNeg, seq(1,ChosenDim))+(PosPop(ChosenPos, seq(1,ChosenDim))-Pop(ChosenNeg, seq(1,ChosenDim)))*rand();
                            if tempbound>region(seq(1,ChosenDim),1)
                                region(seq(1,ChosenDim),1) = tempbound;
                            end
                        end
                    else                                  %if chosen dimension is discrete
                        Plabel(1,seq(1,ChosenDim)) = 1;   
                        Ilabel(1,seq(1,ChosenDim)) = 0;
                        seq(ChosenDim:ChosenDim) = [];    %the chosen discrete dimension will not be chosen next time
                    end
                end
                
                %set uncertain bits
                uncertain = find(Plabel==0);
                unseq = randperm(size(uncertain,2));
                for j=1:(size(unseq,2)-uncertainbits)
                    Plabel(1,uncertain(1,unseq(1,j))) = 1;   
                    Ilabel(1,uncertain(1,unseq(1,j))) = 0;
                end
            end
            %sample
            for j=1:dim_size
                if Plabel(1,j)==1                       %needn't sample randomly
                    NextPop(i,j) = PosPop(ChosenPos,j);
                else                                    %need sample randomly
                    if dim_type(1,j)==1                 %this dimension is continue
                        NextPop(i,j) = region(j,1)+(region(j,2)-region(j,1))*rand();
                    else                                %this dimension is discrete
                        NextPop(i,j) =region(j,1)-1+unidrnd(region(j,2)-region(j,1)+1);
                    end
                end
            end
            %if unique then query
            if notExist(NextPop(i,:),PosPop,Pop,NextPop,i-1)==1
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
        m = m(1,1);
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
    
    if Optimalv>PosPopv(1,1)    %update Optimal
        Optimal = PosPop(1,:);
        Optimalv = PosPopv(1,1);
    end
end
% fprintf('%d',Optimalv);        %print the best value
return;
%return the best point Optimal after last iteration
%------------------------------------------------------------

%-----------------function notExist--------------------------
%this function is used to judge whether the new sample is unique
%return 1 means new sample is unique
function re=notExist(ins, PosPop, Pop, NextPop, endl)
for i=1:size(PosPop,1)               %in set PosPop
    if isequal(ins,PosPop(i,:))==1
        re = 0;
        return;
    end
end
for i=1:size(Pop,1)                  %in set Pop
    if isequal(ins,Pop(i,:))==1
        re = 0;
        return;
    end
end
for i=1:endl                         %in set NextPop
    if isequal(ins,NextPop(i,:))==1
        re = 0;
        return;
    end
end
re = 1;
return;
%-------------------------------------------------------------

%-----------------function DistinguishM-----------------------
%this function is used to judge whether the model can distinguish the
%   positive sample from negative samples
%return 1 means the model is able to distinguish positive sample from
%   negative samples
function re=DistinguishM(dim_type,Plabel,region,Pop,Pos)
con_vector = dim_type;
con_matrix = ones(size(Pop,1),1)*con_vector;
Pop_con = Pop.*con_matrix;
lower = ones(size(Pop,1),1)*(region(:,1).'.*con_vector);
upper = ones(size(Pop,1),1)*(region(:,2).'.*con_vector);
lower = Pop_con-lower;
upper = Pop_con-upper;
middle1 = lower.*upper;
middle1(:,sum(abs(middle1),1)==0)=[];  %middle1 is judging model for continue dimensions. if there exist element>=0 for each line, means this model can distinguish
Posp = ones(size(Pop,1),1)*Pos;
Plabelp = ones(size(Pop,1),1)*Plabel;
middle2 = (Pop-Posp).*Plabelp;         %middle2 is judging model for discrete dimensions. if there exist element~=0 for each line, means this model can distinguish
for i=1:size(Pop,1)
    %if middle1 and middle2 are both not distinguished model
    if (isempty(find(middle1(i,:)>=0, 1))==1)&&(isempty(find(middle2(i,:)~=0, 1))==1)
        re = 0;
        return;
    end
end
re = 1;
return ;
%-------------------------------------------------------------