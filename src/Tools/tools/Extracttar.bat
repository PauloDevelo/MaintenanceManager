%5
cd %1
mkdir %2
copy %3\%4 %2			
cd %2
%1\7z.exe x %4
del %4
exit