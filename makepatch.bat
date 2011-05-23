@setlocal
@echo off
set LANG=C
cd src\trunk
svn diff > ..\trunk.patch
@endlocal
