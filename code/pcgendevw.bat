Rem Batch file to run development build of PCGEN.
Rem Make sure you run "ant" first (or "ant clean", then "ant").
Rem $Id: pcgendevw.bat,v 1.7 2005/10/23 13:17:43 binkley Exp $

REM To load all sources, the JVM runs out of memory with default 64m

start javaw -Xmx96m -jar bin\pcgen.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
