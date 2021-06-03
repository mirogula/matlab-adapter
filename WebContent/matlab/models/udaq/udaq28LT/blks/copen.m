%  COPEN
%
%  Listuje c/cpp subory v aktualnom adresari,
%  po zadani cisla suboru ho otvori.

disp(' ');

cfiles={};
allfiles=dir;

patt1='\.c$'; 
patt2='\.cpp$'; 

j=1;
for i=1:length(allfiles)
 if ~isempty(regexp(char(allfiles(i).name),patt1)) | ~isempty(regexp(char(allfiles(i).name),patt2)) 
   cfiles(j)= cellstr(allfiles(i).name); 
   j=j+1; 
 end
end

if ( length(cfiles)==0)
   fprintf('\n\n  V aktualnom adresari sa nenachadzaju ziadne c/cpp-subory.');
   return;
end

for i=1:length(cfiles)
 fprintf(1,'%3d  %-25s',i,char(cfiles(i)));
 if(mod(i,3)==0)
     fprintf(1,'\n');
 end
end

fprintf(1,'\n\n');
fid=input('  Zadaj cislo c/cpp suboru pre otvorenie: ');

cfname1=cfiles(fid);

fprintf(1,'\n  Otvaram c/cpp-file %s  .................. \n\n', char(cfname1));
open (char(cfname1));              	%treba zatvorky aby nahradil obsahom  
