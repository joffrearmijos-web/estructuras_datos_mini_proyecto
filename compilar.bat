@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>&1
if errorlevel 1 goto faltaJDK
where javac >nul 2>&1
if errorlevel 1 goto faltaJDK
where jar >nul 2>&1
if errorlevel 1 goto faltaJDK
if not exist build\classes\datos mkdir build\classes\datos
if not exist dist mkdir dist
if exist dist\BaseColecciones.jar del /q dist\BaseColecciones.jar
javac --release 11 -encoding UTF-8 -d build\classes src\*.java
if errorlevel 1 exit /b 1
copy /y datos\congreso_datos.csv build\classes\datos\congreso_datos.csv >nul
if errorlevel 1 exit /b 1
jar cfe dist\BaseColecciones.jar App -C build\classes .
if errorlevel 1 exit /b 1
echo Ejecutable generado: dist\BaseColecciones.jar
echo Abran desde el terminal: java -jar dist\BaseColecciones.jar
exit /b 0
:faltaJDK
echo Instalen un JDK 11 o superior y configuren PATH.
exit /b 1
