@ECHO OFF

SET PATH=system\windows_x86;%PATH%;

SET LIBS=lib\
SET LIBS=%LIBS%;lib\gluegen-rt.jar
SET LIBS=%LIBS%;lib\i4jruntime.jar
SET LIBS=%LIBS%;lib\jogl-all.jar
SET LIBS=%LIBS%;lib\scri-commons.jar
SET LIBS=%LIBS%;lib\osxadapter.jar
SET LIBS=%LIBS%;lib\vecmath-1.3.1.jar

java -Xmx1024m -cp .;classes;%LIBS% curlywhirly.gui.CurlyWhirly %1 %2 %3 %4 %5