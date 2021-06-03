
Ts=0.5;
t_sim=100;

Umin=0;
Umax=5;

K=input('Put identified integrator gain value K (8) = ');
T1=input('Put identified time constant value T1 (20) = ');
%Ta=input('Put identified accumulative delay value Ta = ');

Tfil=4*(T1+Ts);
open('PI0_windupless');
set_param(gcs,'SimulationCommand','start');

