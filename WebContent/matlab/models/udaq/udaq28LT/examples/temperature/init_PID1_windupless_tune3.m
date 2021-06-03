P=1;Tfil=1;Td=1;T1=1;T2=1;Tfd=1;
i_action_on=1;
d_action_on=1;

Ts=0.5;

Umin=0;
Umax=5;

fprintf(1,'Adjust P(I)(D)1 controller\n1) based on I1T2 approximation\n2) experimentally\n');
ui=input('Put your choice: ','s');
while( ~(strcmpi(ui,'1') | strcmpi(ui,'2') ) )
    ui=input('Put your choice: ','s');
end

switch ui
    case '1',
	Ks=input('Put identified integrator gain value Ks = ');
	Td=input('Put identified transport delay Td = ');
        T1=input('Put identified time constant T1 = ');
	disp('Put time constant Tfd for derivative component');	
        Tfd=input('feasibility and filtration (T1/5) = ');

	alfa=-0.231/Td       % pre P,PI,PID    
	if i_action_on==0 && d_action_on==1
	   alfa=-exp(-1)/Td  % pre PD
	end

	P=-alfa/Ks
	Tfil=exp(1)*Td
	
    case '2',	
	Ks=input('Put integrator gain value Ks = ');
	P=input('Put gain of P component = ');
	T1=input('Put time constant T1 value = ');
	Tfil=input('Put time constant Tfil of inner loop = ');
	disp('Put time constant Tfd for derivative component');	
        Tfd=input('feasibility and filtration (T1/5) = ');
end

t_sim=inf;

open('PID1_windupless_tune3');
set_param(gcs,'SimulationCommand','start');
