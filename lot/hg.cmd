@echo off
for /f %%i in ("hg") DO set HGSCRIPT="%%~$PATH:i"
if %HGSCRIPT% == "" (
        echo Cannot find hg on PATH
        exit /b 1
)
python %HGSCRIPT% %*

