@setlocal
@echo off
set PATH=%PATH%;%~d0\mozilla-build\svn-win32-1.6.3\bin
set PATH=%PATH%;%~d0\mozilla-build\hg
set PATH=%PATH%;%~d0\mozilla-build\python
set PATH=%PATH%;%~d0\mozilla-build\msys\local\bin
set PATH=%PATH%;%~d0\mozilla-build\msys\bin
set PATH=%PATH%;%~d0\git\bin
set PATH=%PATH%;%~d0\ant\bin

set JDKROOTKEY=HKLM\SOFTWARE\JavaSoft\Java Development Kit
set JDKROOTQRY=reg query "%JDKROOTKEY%" /v "CurrentVersion"
%JDKROOTQRY% >nul 2>nul
if %ERRORLEVEL% equ 0 (
	for /f "tokens=2* delims=	 " %%A in ('%JDKROOTQRY%') do (
		set JDKCURVER=%%B
	)
)
set JDKCURVERKEY=%JDKROOTKEY%\%JDKCURVER%
set JDKCURVERQRY=reg query "%JDKCURVERKEY%" /v "JavaHome"
%JDKCURVERQRY% >nul 2>nul
if %ERRORLEVEL% equ 0 (
	for /f "tokens=2* delims=	 " %%A in ('%JDKCURVERQRY%') do (
		set JAVA_HOME=%%B
	)
)

set HOME=%HOMEDRIVE%%HOMEPATH%
bash %~d0\mozilla-build\msys\etc\profile.d\profile-sshagent.sh
for /f "eol=; tokens=1,2 delims==;" %%1 in ('type "%HOME%\.ssh\environment"') do (
	if "%%1" == "SSH_AUTH_SOCK" set SSH_AUTH_SOCK=%%2
	if "%%1" == "SSH_AGENT_PID" set SSH_AGENT_PID=%%2
)
start /b /wait
@endlocal
