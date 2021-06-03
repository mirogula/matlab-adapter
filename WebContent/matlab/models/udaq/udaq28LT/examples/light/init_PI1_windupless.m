
Ts=0.2;

Umin=0;
Umax=5;

Ks=input('Put identified integrator gain value Ks (0.5) = ');
Td=input('Put identified transport delay value Td (0.3) = ');

alfa=-0.231/Td

P=-alfa/Ks
Tfil=4.337*Td

t_sim=100;

open('PI1_windupless');
set_param(gcs,'SimulationCommand','start');