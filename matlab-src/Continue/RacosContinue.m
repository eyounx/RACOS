function [Optimal, Optimalv] = RacosContinue(dim_size,dim,iteration,samplesize,positivesize,uncertainbits,probability,fct)
%--------------parameters-----------------%
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
%--------------initialize-----------------%
objectivefct = fct;
%generate the first population randomly
for i=1:dim_size
    Pop(:,i) = dim(i,1)+(dim(i,2)-dim(i,1))*rand(samplesize,1);
end
%query for each sample
for i=1:samplesize
    Popv(1,i) = feval(objectivefct,Pop(i,:));
end
%initialize postive population
for i=1:positivesize
    m = find(Popv == min(Popv));  %find min value in Pop
    m = m(1,1);
    PosPop(i,:) = Pop(m,:);       %obtain positive instance
    PosPopv(1,i) = Popv(1,m);     %obtain the value of positive instance 
    Pop(m,:) = [];                %delete this instance in Pop
    Popv(m:m) = [];
end

%initialize optimal
Optimal = PosPop(1,:);
Optimalv = PosPopv(1,1);

%-----------------------Iteration--------------------------%
for itera=2:iteration
    for i=1:samplesize
        while(1)
            region = dim;                      %reset model
            ChosenPos = unidrnd(positivesize); %choose a positive instance randomly
            if rand()>probability
                Ilabel = ones(1,dim_size);
                Plabel = zeros(1,dim_size);
            else
                %reset model               
                Ilabel = zeros(1,dim_size);
                Plabel = ones(1,dim_size);

                

                %obtain region by shrinking
                while(Distinguish(Pop, region)==0)
                    ChosenDim = unidrnd(dim_size);%choose a dimension randomly
                    ChosenNeg = unidrnd(size(Pop,1));%choose a negative instance randomly
                    %obtain region by shrinking
                    if PosPop(ChosenPos, ChosenDim)<Pop(ChosenNeg, ChosenDim)
                        tempbound = PosPop(ChosenPos, ChosenDim)+(Pop(ChosenNeg, ChosenDim)-PosPop(ChosenPos, ChosenDim))*rand();
                        if tempbound<region(ChosenDim,2)
                            region(ChosenDim,2) = tempbound;
                        end
                    else
                        tempbound = Pop(ChosenNeg, ChosenDim)+(PosPop(ChosenPos, ChosenDim)-Pop(ChosenNeg, ChosenDim))*rand();
                        if tempbound>region(ChosenDim,1)
                            region(ChosenDim,1) = tempbound;
                        end
                    end
                end
                %set uncertainbits
                seq = randperm(dim_size);
                for j=1:uncertainbits
                    Ilabel(1,seq(j)) = 1;
                    Plabel(1,seq(j)) = 0;
                end
            end

            %sample a instance in new region
            for j=1:dim_size
                NextPop(i,j) = region(j,1)+(region(j,2)-region(j,1))*rand();
            end
            
            %compose new sample using random sample and positive sample
            NextPop(i,:) = NextPop(i,:).*Ilabel+PosPop(ChosenPos,:).*Plabel;

            if notExist(NextPop(i,:),PosPop,Pop,NextPop,i-1)==1        %avoid repetition
                NextPopv(1,i) = feval(objectivefct,NextPop(i,:));      %query
                break;
            end;
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
    
    %update Optimal
    if Optimalv>PosPopv(1,1)
        Optimal = PosPop(1,:);
        Optimalv = PosPopv(1,1);
    end
end
% fprintf('%d',Optimalv);     %print the best value
return ;
%return the best point Optimal after last iteration

%-----------------------function notExist--------------------------
%this function is used to judge whether the new sample is unique
%return 1 means new sample is unique
function re=notExist(ins, PosPop, Pop, NextPop, endl)
for i=1:size(PosPop,1)              %in set PosPop
    if isequal(ins,PosPop(i,:))==1
        re = 0;
        return;
    end
end
for i=1:size(Pop,1)                %in set Pop
    if isequal(ins,Pop(i,:))==1
        re = 0;
        return;
    end
end
for i=1:endl                      %in set NextPop
    if isequal(ins,NextPop(i,:))==1
        re = 0;
        return;
    end
end
re = 1;
return;

%-----------------function Distinguish-------------------------
%this function is used to judge whether the model can distinguish the
%   positive sample from negative samples
%return 1 means the model is able to distinguish positive sample from
%   negative samples
function dis=Distinguish(Pop, region)
lower = ones(size(Pop,1),1)*region(:,1).';
upper = ones(size(Pop,1),1)*region(:,2).';
lower = Pop-lower;
upper = Pop-upper;
re = lower.*upper;  %re is judging model for continue dimensions. if there exist element>=0 for each line, means this model can distinguish
for i=1:size(re,1)
    %if re is not distinguished model
    if(isempty(find(re(i,:)>=0, 1))==1)
        dis = 0;
        return;
    end
end
dis = 1;
return;

