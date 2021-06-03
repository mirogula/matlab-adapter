
Umin=0;
Umax=5;

fprintf(1,'\n\nGetting actual temperature ...');
w=0;
u_step=4;
t_prep=140;
P=0;
I=0;
arw_on=1;
t_sim=7;
Ts=0.5;
sim('step_resp2');
act_temp=mean(yt(6:end,2));
fprintf(1,'\nActual temperature is %6.2f',act_temp);

w=ceil(act_temp)+2;

P=2;
I=0.1;
arw_on=1;
Ts=0.2;
t_sim=300;

open('step_resp2');
%set_param('step_resp2','SimulationCommand','start');
sim('step_resp2');

t_prep_in=find(u(:,1)==t_prep);
t_prep_in=t_prep_in-1;
u0=mean(u(t_prep_in-10:t_prep_in,2))
y0=mean(yt(t_prep_in-10:t_prep_in,3))
y02=mean(yt(t_prep_in-10:t_prep_in,4))    % filtered output

time=yt(t_prep_in+1:end,1)-t_prep;
y_time=yt(t_prep_in+1:end,3)-y0;
y_time=y_time-y_time(1);          % (if mean was not equal 0) 
plot(time,y_time)
grid

y_time2=yt(t_prep_in+1:end,4)-y02;   % filtered output
y_time2=y_time2-y_time2(1);          % (if mean was not equal 0) 

save step_resp2