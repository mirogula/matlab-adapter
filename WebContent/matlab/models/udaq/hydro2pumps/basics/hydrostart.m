% program na automaticku kalibraciu senzorov vysky hladiny
% testovanie spravnosti kalibracie
% identifikaciu parametrov sustavy
% autor: Vladimir Zilka, vladimir.zilka@stuba.sk
% verzia: 0.7
% posledna zmena: 09.10.2009

clear all;
close all;
clc;
load('calibParams.mat');

choice=0;
T = 0.25;
A = 0.001;  % plocha vodnej hladiny v nadrzi
min_max_levels;

while choice ~= 13
    choice = MENU('Your choice:', 'Calibration test', 'Calibration', 'Valve 1','Valve 2','Valve 1-2', 'Valve 3', 'Valve 2-3', 'All valves', 'Pump 1', 'Pump 2', 'Both pumps', 'All params', 'End of identification');
    switch choice
        case 1,            
            caltest;
        case 2
            calib;
        case 3,            
            sub_ident_c1;
        case 4,             
            sub_ident_c2;
        case 5
            sub_ident_c12;
        case 6,            
            sub_ident_c3;            
        case 7
            sub_ident_c23;
        case 8
            sub_ident_c1;
            sub_ident_c2;
            sub_ident_c3;
            sub_ident_c12;
            sub_ident_c23;  
        case 9
            sub_ident_pump1;
        case 10
            sub_ident_pump2;
        case 11
            sub_ident_pump1;
            sub_ident_pump2;  
        case 12
            sub_ident_c1;
            sub_ident_c2;
            sub_ident_c3;
            sub_ident_c12;
            sub_ident_c23;            
                        
            sub_ident_pump1;
            sub_ident_pump2; 
        otherwise, 
            choice = 13;
    end 
end