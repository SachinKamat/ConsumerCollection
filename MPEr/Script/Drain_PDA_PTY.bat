@echo off
TITLE=DRAIN PDA PTY

REM Check to see if argument passed in (running from a scenario) or run dBOSectly
set ResultsDirectory=%1\Drain

if "%1"=="" set ResultsDirectory=C:\Parcels\MPEr\Results

echo Results Directory is %ResultsDirectory%

echo %DATE%
echo %TIME%
set datetime=
for /f "skip=1 delims=" %%x in ('wmic os get localdatetime') do if not defined datetime set datetime=%%x

set outputLog=%ResultsDirectory%\Drain_PDA_PTY_%datetime:~0,8%_%datetime:~8,6%.txt

REM set MQSERVER=MQADMIN.SVRCONN/TCP/10.106.85.11(1416)
set MQSERVER=MQREAD.SVRCONN/TCP/10.106.85.11(1416)

"C:\Parcels\MPEr\Input_files\RFHUtil\mqtimes2.exe" -f "C:\Parcels\MPEr\Input_files\Param_Files\TIMES\Paramfile_MQTIMES_PDA_PTY.txt"  > %outputLog%

exit
