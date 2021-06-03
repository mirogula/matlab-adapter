load 'meranie_pump1.mat';
q_max1=max(Q1_arr)

figure;
plot(U1_arr, Q1_arr, 'b-');
grid on;
xlabel('pump voltage [V]');
ylabel('pump output [m^3/s]');
title(['The input - output characteristic of the pump ' char(10) ' and approximation with 4rd degree polynomial']);
hold on;
[PQ1, S] = polyfit(U1_arr, Q1_arr, 4);
Q1_arr_teor = polyval(PQ1, U1_arr);
plot(U1_arr, Q1_arr_teor, 'r-');
legend('Real i-o characteristic', 'Approximation');

figure;
hold on;
grid on;
plot(Q1_arr, U1_arr, 'b-');
ylabel('pump 1 voltage [V]');
xlabel('pump 1 output [m^3/s]');
title(['The inverse i-o characteristic of the pump 1' char(10) ' and approximation with 4rd degree polynomial']);
[PU1, S] = polyfit(Q1_arr, U1_arr, 4);
U1_arr_teor = polyval(PU1, Q1_arr);
plot(Q1_arr, U1_arr_teor, 'r-');
legend('Real i-o characteristic', 'Approximation');

str_datum = num2str(floor(clock));
str_datum = (strrep(str_datum, '    ', '-'));
file_name = sprintf('pump1-%s', str_datum);
cd meranie/pump1;
eval (['save ', file_name]);
cd ../.. ;

save pump1 Q1_arr U1_arr Q1_arr_teor U1_arr_teor PQ1 PU1 q_max1; 
str_datum = num2str(floor(clock));  
str_datum = (strrep(str_datum, '    ', '-'));            
file_name = sprintf('pump1-%s', str_datum);
eval (['save ', file_name, ' Q1_arr U1_arr Q1_arr_teor U1_arr_teor PQ1 PU1 q_max1']);