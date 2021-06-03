Ts=0.2;

Umin=0;
Umax=5;

K=input('Put open loop gain K = (15) '); %(22.6-18)/0.5 = 9.2
Ta=input('Put process time constant Ta value = (0.3) '); %.0.3
%Td=input('Put transport delay Td = (0.3) '); %.0.3

%Tfil=Td*exp(1);   % transport delay 

Tfil=4*Ta;        % accumulative delay

t_sim=35;

open('I0_cont');
set_param(gcs,'SimulationCommand','start');