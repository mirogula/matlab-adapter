disp('This experiment needs the result data from experiment number 5');
disp('(If you have performed experiment 5, data are stored in file step_resp1.mat)');
load step_resp1

Ks=1;
Td=1;
Ta=1;
a=0;

fprintf(1,'\n\nStep response approximation as\n\n');
disp('1) Simple integrator + dead time');
disp('2) First order static system + dead time');
disp('3) Simple integrator + accumulative delay');
disp(' ');

ui=input('Put your choice: ','s');
while( ~(strcmpi(ui,'1') | strcmpi(ui,'2') | strcmpi(ui,'3') ) )
    ui=input('Put your choice: ','s');
end

switch ui
    case '1',
       Ks=input('Put value of Ks = (0.5) ');
       Td=input('Put value of Td = (0.3) ');
       a=0;
       inp=2;
    case '2',
       Ks=input('Put value of Ks = (0.5) ');
       Td=input('Put value of Td = (0.3) ');
       a=input('Put value of  a = (0.042) ');
       inp=2;    
     case '3',
       Ks=input('Put value of Ks = ');
       Ta=input('Put value of Ta = ');
       inp=1;
end

fprintf(1,'\nTransient process last %6.2f seconds',time(end))
fprintf(1,'\nPut simulation time divider factor for approximation time')
fprintf(1,'\n(divider factor = 2 means half of transient process time, etc ...)\n\n');
mp=input('Put your choice: ');

sim('ident1_aprox_sim');

close all;
figure(1);
plot(time,y_time);
hold on;
plot(y_sim(:,1),y_sim(:,2),'r');
grid

[throw,t_index]=min(abs(time-time(end)/mp));
axis([0 time(end)/mp 0 max(  max(  y_time(1:t_index),y_sim(t_index,2)  )   )]);
