set j=c:\Program Files\Java\jdk1.7.0_45\bin\
"%j%javac.exe" -d classes src/*.java 
if not errorlevel 1 "%j%java" -cp classes DrGruebel %*
