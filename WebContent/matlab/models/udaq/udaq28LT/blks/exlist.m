function exlist(in)

fprintf(1,'\n\nList of experiments located in %s\n', strcat(matlabroot,'\udaq\udaq28LT\myschemes'));
more on
more(20)
if nargin==1 & strcmp(in,'sk')
	type(strcat(matlabroot,'\udaq\udaq28LT\myschemes\exlist2.txt'));
else
	type(strcat(matlabroot,'\udaq\udaq28LT\myschemes\exlist3.txt'));
end