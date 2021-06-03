%  MAT
%  Listuje mat subory v aktualnom adresari, po zadani cisla suboru
%  nacita z neho premenne do workspaceu. 

files=what;

if ( length(files.mat)==0)
   fprintf('\n\n  V aktualnom adresari sa nenachadzaju ziadne mat subory.');
   return;
end

for i=1:length(files.mat)
 c_mat=char(files.mat(i));            
 fprintf(1,'%3d  %-25s',i,c_mat);
 if(mod(i,3)==0)
     fprintf(1,'\n');
 end
end

fprintf(1,'\n\n');
fid=input('  Zadaj cislo mat suboru pre nacitanie hodnot: ');

load(char(files.mat(fid)));
