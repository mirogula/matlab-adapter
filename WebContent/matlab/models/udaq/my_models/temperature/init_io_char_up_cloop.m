
more off

Ts=0.5;
t_prep=0;
t_step=120;

Umin=0;
Umax=5;

disp('Measuring input-output chracteristic');

y_min=20;
y_max=45;
w=[0 .1 .2 .3 .4 .5 .6 .7 .8 .9 1 ]*20 + 20;
fprintf(1,'\n\nGetting actual temperature ...');
P=0;
I=0;
arw_on=1;
t_sim=7;
Ts=0.5;
sim('io_char_up_cloop');
act_temp=mean(yt(6:end,3));
fprintf(1,'\nActual temperature is %6.2f',act_temp);

y_min=ceil(act_temp)+2;
y_max=45;

y_range=y_max-y_min;
w=[0 .1 .2 .3 .4 .5 .6 .7 .8 .9 1 ]*y_range + y_min

t_sim=t_prep+length(w)*t_step
P=2;
I=0.1;

open('io_char_up_cloop');
%set_param('io_char_up_cloop','SimulationCommand','start');
sim('io_char_up_cloop');

times=[1 2 3 4 5 6 7 8 9 10 11 ]*t_step-Ts

indexes=times/Ts+1

y_10=[];
for i=1:length(indexes)
   y_10=[y_10 yt(indexes(i)-10:indexes(i),3)];
end

u_10=[];
for i=1:length(indexes)
   u_10=[u_10 u(indexes(i)-10:indexes(i),2)];
end

y_io=mean(y_10);
u_io=mean(u_10);
K_vec=diff(y_io)./diff(u_io);

save iochar_thermal;

figure(1);
plot(u_io,y_io);
grid;
title('Input-output characteristic');
xlabel('u');
ylabel('y');

figure(2);
plot(u_io(2:end),K_vec);
grid;
title('Gain of open loop');
xlabel('u');
ylabel('K');


