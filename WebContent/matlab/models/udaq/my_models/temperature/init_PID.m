
Ts=.5;

Umin=-5;
Umax=5;

P=input('Put P gain of controller (4)   = ');
I=input('Put I gain of controller (1) = ');
D=input('Put D gain of controller (0)   = ');

t_sim=inf;

open('PID.mdl');
set_param(gcs,'SimulationCommand','start');