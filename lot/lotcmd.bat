@setlocal
set PATH=%PATH%;%~d0\mozilla-build\svn-win32-1.6.3\bin
set PATH=%PATH%;%~d0\mozilla-build\hg
set PATH=%PATH%;%~d0\mozilla-build\python
set PATH=%PATH%;%~d0\mozilla-build\msys\bin
set PATH=%PATH%;%~d0\ant\bin
set HOME=%HOMEDRIVE%%HOMEPATH%
ssh-agent cmd.exe /k ssh-add "%HOME%\.ssh\id_dsa"
@endlocal
