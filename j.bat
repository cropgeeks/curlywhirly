@ECHO OFF

SET PATH=system;%PATH%;

SET LIBS=lib\
SET LIBS=%LIBS%;lib\gluegen-rt.jar
SET LIBS=%LIBS%;lib\i4jruntime.jar
SET LIBS=%LIBS%;lib\j3dcore.jar
SET LIBS=%LIBS%;lib\j3dutils.jar
SET LIBS=%LIBS%;lib\jmf.jar
SET LIBS=%LIBS%;lib\jogl.jar
SET LIBS=%LIBS%;lib\scri-commons.jar
SET LIBS=%LIBS%;lib\osxadapter.jar
SET LIBS=%LIBS%;lib\vecmath.jar

java -Xmx1024m -cp .;classes;%LIBS% curlywhirly.gui.CurlyWhirly %1 %2 %3 %4 %5