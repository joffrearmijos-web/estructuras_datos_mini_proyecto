@echo off
setlocal
cd /d "%~dp0"
rem Un solo comando: compilar, generar el JAR con los datos y abrir la aplicacion.
call compilar.bat
if errorlevel 1 exit /b 1
java -jar dist\BaseColecciones.jar %*
exit /b %errorlevel%
