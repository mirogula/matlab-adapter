q_max;
q1_percenta = 0;
q2_percenta = 0;
q3_percenta = 0;
lev1_init = h1(1,2);
lev2_init = h2(1,2);
lev3_init = 0;
posun1 = 0;
posun2 = 0;
posun3 = 0;

figure;
plot(h1(:,1),(h1(:,2)),'b');
hold on;
plot(h2(:,1),(h2(:,2)),'r');
xlabel('t [s]');
ylabel('level [m]');
title(['Outflow characteristic for real system and model - average of c12 and c21']);
grid on;
axis([0 40 0 0.25]);

sim('model_3_nadoby.mdl');

plot(h1s(:,1),h1s(:,2),'c');
plot(h2s(:,1),h2s(:,2),'k');
legend('Real level 1', 'Real level 2', 'Simulation level 1', 'Simulation level 2');