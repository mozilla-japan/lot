@setlocal
@echo off
set LANG=C
cd src\trunk
hg diff > ..\trunk.patch
@endlocal
