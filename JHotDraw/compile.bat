@echo off

set JHD_DIR=X:\JHotDraw5.1\JHotDraw5.1
set JDK=C:\Programme\JDK1.2

set OLD_CP=%CLASSPATH%
set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;%JHD_DIR%

%JDK%\bin\javac -d %JHD_DIR% %JHD_DIR%\src\CH\ifa\draw\applet\*.java %JHD_DIR%\src\CH\ifa\draw\application\*.java %JHD_DIR%\src\CH\ifa\draw\contrib\*.java %JHD_DIR%\src\CH\ifa\draw\figures\*.java %JHD_DIR%\src\CH\ifa\draw\framework\*.java %JHD_DIR%\src\CH\ifa\draw\standard\*.java %JHD_DIR%\src\CH\ifa\draw\util\*.java

set SAMPLES=%JHD_DIR%\src\CH\ifa\draw\samples
%JDK%\bin\javac -d %JHD_DIR% %SAMPLES%\javadraw\*.java %SAMPLES%\net\*.java %SAMPLES%\nothing\*.java %SAMPLES%\pert\*.java

set CLASSPATH=%OLD_CP%
