
Umin=0;
Umax=5;

Ks=input('Put identified integrator gain value Ks (.5) = ');
Td=input('Put identified transport delay value Td (.3) = '); 

Ts=Td*2
lambda=0.76  % for Ts=Td lambda=0.83;

P=(1-lambda)/(Ks*Ts)

%for static plant
%T1=25;
%P=(1-lambda)/(Ks*Ts)*exp(-Td/T1)

t_sim=80;

open('PI1_disc_windupless');
set_param(gcs,'SimulationCommand','start');




