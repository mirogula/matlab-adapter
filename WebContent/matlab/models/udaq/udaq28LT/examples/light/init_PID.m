
Ts=0.2;

Umin=0;
Umax=5;

P=input('Put P gain of controller (5)   = ');
I=input('Put I gain of controller (0.25) = ');
D=input('Put D gain of controller (0)   = ');

t_sim=120;

open('PID.mdl');
set_param(gcs,'SimulationCommand','start');