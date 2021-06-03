
Ts=1;

Umin=0;
Umax=5;


fprintf(1,'Adjust PI1 controller gains\n1) based on I1Td approximation\n2) experimentally\n');
ui=input('Put your choice: ','s');
while( ~(strcmpi(ui,'1') | strcmpi(ui,'2') ) )
    ui=input('Put your choice: ','s');
end

switch ui
    case '1',
	Ks=input('Put identified integrator gain value Ks = ');
	Td=input('Put identified transport delay value Td = ');
	alfa=-0.231/Td
	P=-alfa/Ks
	Tfil=4.337*Td
    case '2',	
	Ks=input('Put integrator gain value Ks = ');
	P=input('Put gain of P component = ');
	Tfil=input('Put time constant Tfil of inner loop = ');
    end

t_sim=180;

open('PI1_windupless');
set_param(gcs,'SimulationCommand','start');