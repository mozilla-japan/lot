@setlocal
@echo off
set PATH=%PATH%;%~d0\mozilla-build\svn-win32-1.6.3\bin
set PATH=%PATH%;%~d0\mozilla-build\hg
set PATH=%PATH%;%~d0\mozilla-build\python
set PATH=%PATH%;%~d0\mozilla-build\msys\bin
set PATH=%PATH%;%~d0\git\bin
set PATH=%PATH%;%~d0\ant\bin
if "%SSH_AGENT_PID%" == "" (
set HOME=%HOMEDRIVE%%HOMEPATH%
for /f "eol=; tokens=1,2 delims==;" %%1 in ('ssh-agent.exe') do (
 if "%%1" == "SSH_AUTH_SOCK" set SSH_AUTH_SOCK=%%2
 if "%%1" == "SSH_AGENT_PID" set SSH_AGENT_PID=%%2
)
)
ssh-add
start /b /wait
if not "%SSH_AGENT_PID%" == "" (
ssh-agent -k
)
@endlocal
