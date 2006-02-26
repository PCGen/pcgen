Rem Batch file to run development build of PCGEN.
Rem Make sure you run "ant" first (or "ant clean", then "ant").
Rem $Id: pcgendev.bat,v 1.10 2005/10/23 13:17:43 binkley Exp $
copy bin\pcgen.jar .

Rem If you have arguments, the class to run must be the FIRST
Rem one!  Example:
Rem java -jar pcgen.jar pcgen.core.pcGenGUI arg1 another-arg
REM To load all sources, the JVM runs out of memory with default 64m
java -Xmx96m -jar pcgen.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
