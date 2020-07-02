@echo off
TITLE=PDA_PTY Scenario

REM ******************************************
REM PDA_PTY injection Scenario
REM ******************************************

REM Define variables
REM ******************************************
set scriptDirectory="C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Script"
set num1=0
set num2=1 
set terminator=1
set datetime=
for /f "skip=1 delims=" %%x in ('wmic os get localdatetime') do if not defined datetime set datetime=%%x

REM append date and time to results folder
set runName=PDA_PTY_%datetime:~0,8%_%datetime:~8,4%
set resultsDirectory=C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Results\%runName%
REM ******************************************

REM Make results directories
REM ******************************************
mkdir %resultsDirectory%
REM mkdir %resultsDirectory%\Drain
mkdir %resultsDirectory%\MQPuts




	




REM ******************************************
REM start the Injection scripts
	:loop
	set /a num1= %num1% + %num2%
	if %num1%==%terminator% goto close
	goto open
	:close
		start %scriptDirectory% %scriptDirectory%\PDA_PTY1.bat %resultsDirectory%
		echo INNER LOOP %num1%
	exit
	:open
	echo ITERATION %num1%
		REM start %scriptDirectory% %scriptDirectory%\PDA_PTY1.bat %resultsDirectory%
		REM TIMEOUT 5
		

	goto loop
REM ******************************************

pause