global alpha1 alpha2  U m1c m2c;

w=.1;
alpha1=-.2;
alpha2=-.3;
U=[0 1.5e-5];
m1c=0;
m2c=0;
A1=0.001;
c12 = .16273e-1;
c2 = .86799e-2;
Ts=0.2;




model='moddel.mdl'; %Ejs Model
h1=0;  %Ejs Variable
h=0;   %Ejs Variable
time=0;  %Ejs Variable
