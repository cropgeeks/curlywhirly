@ECHO OFF
CALL libraries.bat

java -Xmx1024m -cp .;classes;%cwcp% jhi.curlywhirly.gui.CurlyWhirly