% Measuring input-output chracteristic
% Evaluating gain of local linearization

% check through Start, Control Panel and System Device manager port number
% used for Arduino and put this number (format COMd, or COMdd) manualy 
% to the command in line 9

clear all;
com = 'COM8';  % com port ('COM19' - windows or '/dev/ttyACMX' - linux)
delete(instrfind({'Port'},{com}));
baud = 250000;  % baud rate 

%***************************** Filters ************************************
fTt = 0.2;     % filter time constant for temperature (0.05s - 10s)
fTl = 0.2;     % filter time constant for light intensity (0.05s - 10s)
fTf = 0.2;     % filter time constant for for angular velocity (0.1s - 10s)
%***************************** Filters ************************************
Ts=0.1; %sampling period do 0,02
Umin=0; %low input constraint
Umax=100; %high input constraint

t_sim=inf;  %cas simulacie

bulb= 100;   %vstupna konstanta na ziarovke (0 - 100%)
fan = 0;     %vstupna konstanta na ventilatore (0 - 100%)
LED = 0;     %vstupna konstanta na LED (0 - 100%)