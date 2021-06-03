
disp('This experiment needs the result data from experiment number 15');
disp('(If you have performed experiment 15, data are stored in file step_resp2.mat)');
load step_resp2

Ks=1;
Ta=1;
Td=1;
T1=1;
Tfd=1;
a=0;


fprintf(1,'\n\nStep response approximation of\n\n');
disp('1) Temperature signal measured directly');
disp('2) Temperature signal measured after filtration');
disp(' ');

sig=input('Put your choice: ','s');
while( ~(strcmpi(sig,'1') | strcmpi(sig,'2') ) )
    sig=input('Put your choice: ','s');
end

open('ident2_aprox_sim');

fprintf(1,'\n\nStep response approximation as\n\n');

disp('1) First order + dead time');
disp('2) First order + inverse filter + dead time');
disp('3) First order + dead time + time constant + inverse filter');
disp('4) First order + two time constant + inverse filter');
disp(' ');

ui=input('Put your choice: ','s');
while( ~(strcmpi(ui,'1') | strcmpi(ui,'2') | strcmpi(ui,'3') | strcmpi(ui,'4') ) )
    ui=input('Put your choice: ','s');
end

switch ui
    case '1',
       Ks=input('Put value of Ks = ');
       a=input('Put value of a (for astatic approx. a=0) =  ');
       Td=input('Put value of Td = ');
       inp=1;
    case '2',
       Ks=input('Put value of Ks = ');
       a=input('Put value of a (for astatic approx. a=0) =  ');
       Td=input('Put value of Td = ');
       Tfd=input('Put value of Tfd = ');
       inp=2;    
     case '3',
       Ks=input('Put value of Ks = ');
       a=input('Put value of a (for astatic approx. a=0) =  ');
       Td=input('Put value of Td = ');
       Tfd=input('Put value of Tfd = ');
       T1=input('Put value of T1 = ');
       inp=3;    
     case '4',
       Ks=input('Put value of Ks = ');
       a=input('Put value of a (for astatic approx. a=0) =  ');
       Ta=input('Put value of Ta = ');
       Tfd=input('Put value of Tfd = ');
       T1=input('Put value of T1 = ');
       inp=4;
end

if (strcmpi(sig,'2'))
    y_time=y_time2; 
end
       

fprintf(1,'\nTransient process last %6.2f seconds',time(end))
fprintf(1,'\nPut simulation time divider factor for approximation time')
fprintf(1,'\n(divider factor = 2 means half of transient process time, etc ...)\n\n');
mp=input('Put your choice: ');

sim('ident2_aprox_sim');

close all;
figure(1);
plot(time,y_time);
hold on;
plot(y_sim(:,1),y_sim(:,2),'r');
grid

[throw,t_index]=min(abs(time(11:end)-time(end)/mp));
axis([0 time(end)/mp 0 max(max(y_time(1:t_index)),y_sim(t_index,2)) ])
