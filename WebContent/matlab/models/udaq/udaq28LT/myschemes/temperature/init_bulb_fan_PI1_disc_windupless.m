
Ts=1; 
Umin=-5;
Umax=5;

Ks=input('Put identified value of Ks = ');
Td=input('Put identified transport delay value Td = '); 
a=input('Put identified value of a = ');

y0=input('Put actual temperature value y0 = ');
disp(' ');
disp('for a~=0 filt0 = 0 ; for a=0 filt0 = w*1/static_gain_of_system')
filt0=input('Put filter output initial value filt0 = ');

t_sim=inf;

Tfil=4.337*(Td+Ts);
KR=1/(4.337*Ks*(Td+Ts));
P=KR;
alfa=-1/Tfil;


%dicr.system

if a~=0
    Ka=Ks*(1-exp(-a*Ts))/a 
else
    Ka=Ks*Ts
end

D=exp(-a*Ts)

%PI1 diskretny - parametre

lambda=exp(-Ts/Tfil)

open('bulb_fan_PI1_disc_windupless');
set_param(gcs,'SimulationCommand','start');


