%  MOPEN
%
%  Listuje *.m subory v aktualnom adresari,
%  po zadani cisla suboru ho otvori.

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
fid=input('  Zadaj cislo m suboru pre otvorenie: ');

fname1=char(files.m(fid));

fprintf(1,'\n  Otvaram m-file %s  .................. \n\n', fname1);
open (fname1);              	%treba zatvorky aby nahradil obsahom ... 
