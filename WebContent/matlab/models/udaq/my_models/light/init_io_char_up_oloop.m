
Ts=0.1;
Umin=0;
Umax=5;

disp('Measuring input-output chracteristic - open loop');

u_range=Umax-Umin;
w=[0 .1 .2 .3 .4 .5 .6 .7 .8 .9 1 ]*u_range + Umin

t_prep=0;
t_step=3;
t_sim=t_prep+length(w)*t_step;

open('io_char_up_oloop');
sim('io_char_up_oloop');

times=[1 2 3 4 5 6 7 8 9 10 11 ]*t_step-Ts;

indexes=ceil(times/Ts)+1;

y_io=yl([indexes],2);
K_vec=diff(y_io)./(w(2)-w(1));

figure(1);
plot(w,y_io);
grid;
title('Input-output characteristic');
xlabel('u');
ylabel('y');

figure(2);
plot(y_io(2:end),K_vec);
grid;
title('Gain of open loop');
xlabel('y');
ylabel('K');
