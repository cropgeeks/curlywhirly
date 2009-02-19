@echo off

set path=system;%PATH%;

java -splash:splashScreen.jpg -Xmx256m -cp .;classes -Djava.ext.dirs=lib graphviewer3d.gui.GraphViewerFrame