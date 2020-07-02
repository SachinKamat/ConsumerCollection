@echo off
REM Next line NOT needed if WinSCP folder was added to PATH 
CD "C:\Program Files (x86)\WinSCP"
"C:\Program Files (x86)\WinSCP\WinSCP.exe" /log="C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\SDG\Logs\WinSCP.log" /ini=nul /script="C:\Users\sachin.kamat\.jenkins\workspace\MPEr\BatchScript\winSCP_uploadScriptSDG.txt"