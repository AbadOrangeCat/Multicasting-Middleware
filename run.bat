@echo off
csc /out:Network\Network.exe Network\Network.cs 
start Network\Network.exe

cd Middleware1
start call 1.bat

cd ..
cd Middleware2
start call 2.bat

cd ..
cd Middleware3
start call 3.bat

cd ..
cd Middleware4
start call 4.bat

cd ..
cd Middleware5
start call 5.bat
