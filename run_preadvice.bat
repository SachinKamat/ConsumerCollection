@echo off
REM
CD "C:\Program Files (x86)\WinSCP"
"C:\Program Files (x86)\WinSCP\WinSCP.exe" /log="C:\Workspace\Royal_Heartbeat\Winscp_output.log" /ini=nul /script="C:\Workspace\Royal_Heartbeat\winSCP_uploadScript.txt"
