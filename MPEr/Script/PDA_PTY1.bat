@echo off
TITLE=MQPUT - PDA_PTY 1
REM Check to see if argument passed in (running from a scenario) or run directly
set ResultsDirectory=%1\MQputs

if "%1"=="" set ResultsDirectory=C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Results

echo Results Directory is %ResultsDirectory%

echo %DATE%
echo %TIME%
set datetime=
for /f "skip=1 delims=" %%x in ('wmic os get localdatetime') do if not defined datetime set datetime=%%x

set outputLog=%ResultsDirectory%\PDA_PTY_1_%datetime:~0,8%_%datetime:~8,6%_mqputs.txt

set MQSERVER=MQSUPPORT.SVRCONN/TCP/10.106.85.21(1420)

"C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Input_files\RFHUtil\mqputsc.exe" -f "C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Input_files\Param_Files\PUTS\Paramfile_MQPUT_PDA_PTY1.txt" > %outputLog%
exit
