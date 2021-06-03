function hexlist(in)

fprintf(1,'\n\nList of experiments located in %s\n', strcat(matlabroot,'\udaq\hydro2pumps\myschemes'));
more on
more(20)
if nargin==1 & strcmp(in,'sk')
	type(strcat(matlabroot,'\udaq\hydro2pumps\myschemes\hexlist2.txt'));
else
	type(strcat(matlabroot,'\udaq\hydro2pumps\myschemes\hexlist3.txt'));
end