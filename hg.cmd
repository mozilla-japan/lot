@echo off
hg.exe > nul 2>&1
if "%ERRORLEVEL%" == "0" (
hg.exe %*
) else (
for /f %%i in ("hg") DO set HGSCRIPT="%%~$PATH:i"
if "%HGSCRIPT%" == "" (
        echo Cannot find hg on PATH
        exit /b 1
)
python %HGSCRIPT% %*
)
