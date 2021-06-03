%  MFILES
%
%  Listuje *.m subory v aktualnom adresari,
%  po zadani cisla suboru ho vykona.

disp(' ');

files=what;

if ( length(files.m)==0)
   fprintf('\n\n  V aktualnom adresari sa nenachadzaju ziadne m-subory.');
   return;
end

for i=1:length(files.m)
 c_m=char(files.m(i));             
 fprintf(1,'%3d  %-25s',i,c_m);
 if(mod(i,3)==0)
     fprintf(1,'\n');
 end
end

fprintf(1,'\n\n');
fid=input('  Zadaj cislo m suboru pre vykonanie: ');

fname1=char(files.m(fid));
fname2=fname1(1:end-2);		%treba vyhodit .m priponu

fprintf(1,'\n  Spustam script %s  .................. \n\n', fname1);
run (fname2);              	%treba zatvorky aby nahradil obsahom ... 
