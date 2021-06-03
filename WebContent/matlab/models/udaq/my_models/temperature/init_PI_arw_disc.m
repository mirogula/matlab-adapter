
Ts=0.5;

Umin=0;
Umax=5;

P=input('Put P gain of controller (2)   = ');
I=input('Put I gain of controller (0.5) = ');

t_sim=150;

open('PI_arw_disc.mdl');
set_param(gcs,'SimulationCommand','start');