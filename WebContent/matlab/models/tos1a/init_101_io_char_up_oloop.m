% Measuring input-output chracteristic
% Evaluating gain of local linearization

% check through Start, Control Panel and System Device manager port number
% used for Arduino and put this number (format COMd, or COMdd) manualy 
% to the command in line 9

clear all;
com = 'COM33';  % com port ('COM19' - windows or '/dev/ttyACMX' - linux)
delete(instrfind({'Port'},{com}));
baud = 250000;  % baud rate 

%***************************** Filters ************************************
fTt = 0.2;     % filter time constant for temperature (0.05s - 10s)
fTl = 0.2;     % filter time constant for light intensity (0.05s - 10s)
fTf = 0.2;     % filter time constant for for angular velocity (0.1s - 10s)
%***************************** Filters ************************************
Ts=0.1; %sampling period
Umin=0; %low input constraint
Umax=100; %high input constraint
    
disp('Measuring input-output chracteristic - open loop');

u_range=Umax-Umin; %range of input signal
ui=[0:0.1:1]*u_range + Umin %preparing input values, uniformly distributed on the u_range



ti=[1:length(ui)] %the number of steps is equal to number of defined input values
t_prep=0;%starting time
t_step=4;%duration of one step
t_sim=t_prep+length(ui)*t_step;%simulation time


TB=reshape([ti-1;ti],1,size(ti,2)+size(ti,2))*t_step %time values in input repeating sequence
VB=reshape([ui;ui],1,size(ui,2)+size(ui,2))%output values of the repeating sequence of the input
open('io_char_up_oloop21_tapi'); %open simulink model
sim('io_char_up_oloop21_tapi'); %run experiment


indexes=ceil(ti*t_step/Ts)-5;%indexes of steady state values of the output

y_io=yl([indexes],2);%steady state values of the output
K_vec=diff(y_io)./(ui(2)-ui(1));%process gain  


%polynomal approximations
figure(1);
plot(ui,y_io,'ko','linewidth',3);hold on;
P = POLYFIT(ui,y_io',4);plot(ui,polyval(P,ui),'r','linewidth',3);
grid;title('Input-output characteristic y=f(u)');
xlabel('---> u');ylabel('---> y');
legend('Measured characteristic','Polynomial approximation')

figure(2);
plot(y_io(2:end),K_vec,'k','linewidth',3);grid;
title('Open loop gain K=\partialy/\partialu=f(y)');
xlabel('---> y');ylabel('---> K');


figure(3);
plot(ui(2:end),K_vec,'k','linewidth',3);grid;
title('Open loop gain K=\partialy/\partialu=f(u)');
xlabel('---> u');ylabel('---> K');

PI = POLYFIT(y_io',ui,5)
figure(4);plot(y_io,ui,'k','linewidth',3);hold on;
plot(y_io,polyval(PI,y_io),'r','linewidth',3);
grid;title('Inverse input-output characteristic u=f^{-1}(y)');
xlabel('---> y');ylabel('---> u');
legend('Measured characteristic','Polynomial approximation')



save ws_101_Input_output_charact


%load ws_101_Input_output_charact
%piecewise approximation START
%IOchar
%IOchar
[tmp4,tmp5,tmp6]=unique(ui);%data have to be distinct
x1=ui(tmp5)';%input values
y1=y_io(tmp5);%output values
f1 = fit(x1,  y1, 'linearinterp');

%inverse IOchar
[tmp4,tmp5,tmp6]=unique(y_io);%data have to be distinct
x2=y_io(tmp5);%output values
y2=ui(tmp5)';%input values
f2 = fit(x2,  y2, 'linearinterp');

%plot results
figure
plot(ui,y_io,'ko','LineWidth',2)
hold on
plot(x1(1):0.5:x1(end),f1(x1(1):0.5:x1(end)),'b','LineWidth',1)
plot(x2(1):0.5:x2(end),f2(x2(1):0.5:x2(end)),'g','LineWidth',1)
title('Input-output characteristic y=f(u)');
xlabel('---> u');ylabel('---> y');
legend('Measured points of IO characteristics','Linear interpolation of IO characteristics','Linear interpolation of  inverse IO characteristics')
save ws_101_Input_output_charact



