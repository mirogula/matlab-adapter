
Umin=0;
Umax=5;

u_step=4;
t_prep=80;

P=2;
I=0.5;
w=20;
arw_on=1;
Ts=0.2;
t_sim=220;

open('step_resp1');
sim('step_resp1');

t_prep_in=find(u(:,1)==t_prep);
t_prep_in=t_prep_in-1;
u0=mean(u(t_prep_in-10:t_prep_in,2))
y0=mean(y34(t_prep_in-10:t_prep_in,3))   


time=y34(t_prep_in+1:end,1)-t_prep;
y_time=y34(t_prep_in+1:end,3)-y0;       
y_time=y_time-y_time(1);          % (if mean was not equal 0) 
plot(time,y_time)
grid

save step_resp1