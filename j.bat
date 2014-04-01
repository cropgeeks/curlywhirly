@ECHO OFF
CALL libraries.bat

java -Xmx1024m -cp .;classes;%avcp% curlywhirly.gui.CurlyWhirly %1 %2 %3 %4 %5