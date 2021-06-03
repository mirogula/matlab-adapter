%  MDL
%  Listuje MDL subory v aktualnom adresari, po zadani cisla suboru ho otvori

files=what;

if ( length(files.mdl)==0)
   fprintf('\n\n  V aktualnom adresari sa nenachadzaju ziadne mdl subory.');
   return;
end

for i=1:length(files.mdl)
 c_mdl=char(files.mdl(i));           
 fprintf(1,'%3d  %-25s',i,c_mdl);
 if(mod(i,3)==0)
     fprintf(1,'\n');
 end
end

fprintf(1,'\n\n');
fid=input('  Zadaj cislo mdl suboru pre otvorenie: ');

open(char(files.mdl(fid)));
