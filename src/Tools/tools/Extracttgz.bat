copy %3\%4 %2
%2			
cd %2
%1\7z.exe e %2\%4
del %2\%4
%1\7z.exe x %2\%6
del %2\%6
move %7 %5
exit