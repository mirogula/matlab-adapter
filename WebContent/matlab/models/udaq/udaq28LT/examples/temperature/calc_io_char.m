
y_io=[];

for i=0:1:10
   load(strcat('prev45_',num2str(i)));
   y_io=[y_io mean(y12(end-10:end,2))];  
end

K_vec=diff(y_io)./0.5;  %(w(2)-w(1))
u_vec=[0:0.5:5];

figure(1);
plot(u_vec,y_io);
grid;
title('Input-output characteristic');
xlabel('u');
ylabel('y');

figure(2);
%plot(u_vec(2:end),K_vec);
stairs(u_vec(2:end),K_vec);
grid;
title('Gain of open loop');
xlabel('u');
ylabel('K');

