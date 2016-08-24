Tofrodos Ver 1.7.13
Copyright 1996-2013 Christopher Heng. All rights reserved.
----------------------------------------------------------


Contents
--------

1. What Is Tofrodos?
2. How To Install Tofrodos
3. Compiling Tofrodos In Linux, Mac OS X and Unix-type Systems
4. Compiling Tofrodos In Msdos And Windows
5. Porting To Other Systems
6. History Of Changes
7. Copyright And Contacting The Author


1. What Is Tofrodos?
--------------------

DOS text files traditionally have CR/LF (carriage return/line feed) pairs
as their new line delimiters while Unix text files traditionally have
LFs (line feeds) to terminate each line.

Tofrodos comprises two programs, "fromdos" and "todos", which convert
ASCII and Unicode UTF-8 text files to and from these formats. Use "fromdos"
to convert DOS text files to the Unix format, and "todos" to convert Unix
text files to the DOS format.


2. How To Install Tofrodos
--------------------------

a. Windows 9x/ME/NT/2k/XP/Vista/7

The Tofrodos package comes with sources as well as precompiled
executables (binaries) for the Windows console mode. If you
want to install everything, just unpack the archive into
a directory of your choice.

Alternatively, if you only want to use the executables and
don't want to bother with the sources, just do the following
from a Windows console (command prompt):

	copy todos.exe {destination directory}\todos.exe
	copy fromdos.exe {destination directory}\fromdos.exe
	copy tofrodos.html {destination directory}\tofrodos.html

To read the manual, open the tofrodos.html file with a web browser.

If you work on the command line often, you might want to
consider putting the directory where you placed the tofrodos
executables in your PATH.

Note that if you prefer the names "dos2unix.exe" and
"unix2dos.exe", just make a copy of "todos.exe" (or "fromdos.exe")
under those names. Tofrodos automatically detects the name under
which it is run, and will change its behaviour accordingly.
(That is, "dos2unix" will convert files from the DOS format to
the Unix format, and "unix2dos" will convert files from the
Unix format to the DOS format.)

b. MSDOS/FreeDOS and Clones

You will need to compile the sources. See the section
"Compiling Tofrodos in MSDOS and Windows", particularly
the section on using OpenWatcom C/C++.

c. Linux, Mac OS X, Unix-type systems

See the section "Compiling Tofrodos in Linux, Mac OS X
and Unix-type Systems"

d. Other systems

See the section "Porting to Other Systems".


3. Compiling Tofrodos In Linux, Mac OS X and Unix-type Systems
--------------------------------------------------------------

I've stopped distributing precompiled versions of Tofrodos for
Linux for two main reasons. There are just too many distributions
to compile for and it's a simple matter to do it yourself. The
process is painless and fast, since the source code compiles
out-of-the-box.

To compile everything, simply do the following:

	cd src
	make all

You can then install by typing

	make install

Since you're installing the binary and manual page into your
system directories with this command, you'll need to be
logged in as root (either directly, or via "su" or "sudo").
(I know this is stating the obvious, but it's just in case
you're new to installing programs.)

To install them somewhere other than /usr/bin and /usr/man/man1,
redefine BINDIR and MANDIR for the binary and manual page
destinations respectively. For example, to install the binaries
in /usr/local/bin and the manual page in /usr/local/man/man1,
do the following:

	make BINDIR=/usr/local/bin MANDIR=/usr/local/man/man1 install

Note that Mac OS X does not have a specific directory for
man pages. As such, simply typing "make install" will fail on
that system. You will need to set MANDIR to some directory that
you know exists.

To read the manual page after installation, simply type

	man fromdos

If you've installed MANDIR to a non-standard location, so
that the above does not work (such as you may have done
on Mac OS X), just specify the path, for example:

	man ./fromdos.1

There should be no errors or warnings in the compilation.

Note that if you prefer the names "dos2unix" and
"unix2dos", just rename the binaries accordingly and you're done.
Tofrodos automatically detects the name under which it is run,
and will change its behaviour accordingly. That is, "dos2unix"
will convert files from the DOS format to the Unix format,
and "unix2dos" will convert files from the Unix format to the
DOS format. No code changes are necessary.


4. Compiling Tofrodos In MSDOS And Windows
------------------------------------------

Tofrodos comes with binaries (executables) for the Windows console
mode. However, if you prefer to recompile tofrodos yourself,
there are a few makefiles you may use, depending on the compiler
you have on your system.

To compile Tofrodos under MSDOS or Windows 9x/XP/Vista/7, simply use
the appropriate makefile for your compiler or roll your own.

If you want a trouble-free compilation, you should probably use
the Open Watcom C compiler (which is free), since that is the
compiler I currently use for creating the precompiled binaries
for Windows.

In case it's not clear from the above paragraph, please note
that although I provide makefiles for a few other compilers, most
of them were only tested with earlier versions of Tofrodos
and not the current version.

Makefiles for use with the following compilers and their MAKE
utilities are as follows:

	makefile.wcc	OpenWatcom C/C++ 1.9 and WMAKE.EXE (DOS 16/32 bit
			and Win32 targets)
	makefile.vs7	Visual Studio .NET 2003 and NMAKE.EXE (Win32 target)
	makefile.min	MinGW 3.1.0 and mingw32-make.exe (Win32 target)
	Makefile		Linux makefile. Do not use for Windows or DOS.

Note that the file Makefile is for use with the GNU C compiler and
GNU make on a Linux system. Do not use it under Windows or MSDOS.
To avoid using it by default, make sure you specify the makefile
name on your compiler system's "make" command line.

These makefiles can probably be adapted for other compilers or other
versions of the above compilers.

You can get free versions of most (if not all) of the supported
compilers from
http://www.thefreecountry.com/compilers/cpp.shtml

i. Recompiling with OpenWatcom C/C++

To recompile with OpenWatcom C/C++, copy the files
	src\startup\wildargv.c
	src\startup\initarg.h
from your WATCOM directory to the src\lib subdirectory. For example, if
you installed Watcom to c:\watcom, and you expanded the tofrodos
package into d:\tofrodos, do the following:
	copy c:\watcom\src\startup\wildargv.c d:\tofrodos\src\lib
	copy c:\watcom\src\startup\initarg.h d:\tofrodos\src\lib

If you cannot find wildargv.c on your hard disk, you probably did not
install the startup source code or have somehow deleted it. Just reinstall
OpenWatcom again, this time with the necessary options.

To create a Windows console executable, the following command line can be
used:
	cd src
	wmake -f makefile.wcc TARGET=Win32 all

To create the MSDOS executables, do the following:
	cd src
	wmake -f makefile.wcc TARGET=16bit all

Make sure you do a
	wmake -f makefile.wcc clean
before compiling again for a different target or the response files
and object files will be wrong for the new target.

Note that the included precompiled Windows executable was compiled
with OpenWatcom 1.9.

ii. Recompiling with Visual C++

Visual C++ (from Visual Studio) can only create Win32 console
executables. Use the following procedure to create todos.exe
and fromdos.exe.

	cd src
	nmake -f makefile.vc all

There should be no warnings or errors. Note that the latest
version of tofrodos was not tested with Visual C++.

iii. Recompiling with MinGW

To compile with MinGW, use the GNU Make utility (mingw32-make.exe)
with makefile.min as follows:

	cd src
	mingw32-make -f makefile.min all

Note that the latest version of tofrodos was not tested with
MinGW. However, since MinGW uses GCC, and I test all versions of
tofrodos using GCC on Linux, it should theoretically work fine.


5. Porting To Other Systems
---------------------------

If you want to compile Tofrodos for a system other than Linux, MSDOS
or Windows you may or may not have some work in store for you. The
program is fairly trivial, so the work you need to do is probably
(hopefully) minimal.

The first place to look into is probably the config.h file, where I
tried to place as many system and compiler macros as I could bother.

If you are compiling on other Unix systems, tweaking the config.h file
macros may well be all that you need to do. I have reports of success
with people using it on HP-UX and others.


6. History Of Changes
---------------------

Dates given are the dates where the code base was finalised and do not
necessarily refer to the date of public release.

Version 1.7.13	25 October 2013
- [Hurd, NetBSD, FreeBSD kernel] Added support for Hurd,
NetBSD and FreeBSD kernel (a system that uses the FreeBSD
kernel, but is not necessarily the full FreeBSD system).
As a side benefit, tofrodos is slightly more portable since
it no longer depends on certain system-specific macros
(namely MAXPATHLEN from sys/param.h).
- [All] Tofrodos now displays information on what to do if
it is not able to rename the temporary file back to the
original filename after a successful conversion.
- [All] The -a option is now documented as "deprecated",
since you shouldn't use it unless you have an unusual
text file that you're trying to fix.
- [All] Minor improvements to the documentation.

Version 1.7.12	1 October 2012
- [All] Under certain error conditions, Tofrodos may fail to
remove the temporary files that it creates. This is now fixed.
- [All] Fixed another bug where an exit code of 0, instead of 1,
is returned under certain failure conditions.
- [MSDOS] Although MSDOS is not a multitasking system, and
thus should not need it, the DOS port of Tofrodos now also uses
my implementation of mkstemp(). This simplifies maintenance
since I have fewer code paths to test.
- [Windows] Support for compiling the source code using
DJGPP 2, Borland C/C++, LCC-Win32 and Digital Mars C has been
removed. Please use one of the other supported compilers (eg,
Open Watcom C, etc).
- [All] The documentation now has information about the exit
codes returned by Tofrodos.

Version 1.7.11	27 September 2012
- [All] tofrodos now consistently returns an exit code of 1 when
there's a failure. Previously, under certain error conditions,
it could return an exit code of -1 (which is not a valid
exit code for some operating systems).
- [Windows] This version now includes an implementation of
mkstemp() for Windows (when compiled with Open Watcom C),
bringing the Windows (Open Watcom) port of tofrodos up to
par with versions for systems like Linux and Mac OS X which
provide mkstemp() in their C libraries. Hopefully, this will
help those of you who run multiple instances of tofrodos at
the same time in the same directory.
- [Windows] Fixed a bug introduced in 1.7.9 where the Windows port
of tofrodos always creates the temporary file in the current
directory instead of the directory where the target file is.
- [Mac OS X] The Mac OS X port of tofrodos now uses mkstemp() to
create the temporary file the way it does on Linux.
- [Mac OS X] Added more information in readme.txt for Mac users.
- [All] Improved documentation about the -p and -b options.

Version 1.7.10	25 September 2012
- Limited (beta) distribution only, not released for general use.
If you have this version, please upgrade to 1.7.11.

Version 1.7.9	21 February 2011
- [Windows, Linux, Mac OS X, Unix] The behaviour of the -b option
(create backup file) has been changed to be more useful on systems
with support for long filenames and an arbitrary number of file
extensions (ie, all systems except MSDOS). It now backs up
"filename.ext" as "filename.ext.bak" instead of "filename.bak".
Note that with this version, the DOS version no longer behaves
the same way (when the -b option is used) as the Windows, Linux,
Mac OS X, *BSD and other Unix versions, since DOS systems have
filename limitations. At least this way, we're not held back by
the least common denominator (DOS), especially since few people
use DOS nowadays.
- [All] The documentation has been updated to explain the behaviour of
the -b option in more detail.

Version 1.7.8	8 April 2008
- [Mac OS X] Fixed corrupted portion of config.h that only shows up when
compiling under Mac OS X (sorry - I don't know what happened)

Version 1.7.7	1 April 2008
- [Mac OS X] Added support for compiling tofrodos under Mac OS X.
- [FreeBSD] Added support for compiling tofrodos under FreeBSD.
- [OpenBSD] Added support for compiling tofrodos under OpenBSD (not tested).
- [All] Fixed typos in documentation (Unix man page and HTML version).

Version 1.7.6	15 March 2005
- [All systems] New option: -l allows you to send the error messages to
a log file. It's particularly convenient for systems with less powerful
command line shells that do not allow you to easily redirect stderr.
- [All systems] Fixed: all error and verbose messages are now sent to
stderr or the error log file (if -l is given).
- [Windows] Older versions of compilers like Borland 4.X and 5.0,
Watcom 10.X and Visual C++ 6.0 no longer have makefiles. Use the latest
versions; all these compilers are now available free of charge from
their vendors so using the latest version to compile tofrodos should
not be a great hardship.
- [All systems] Other minor fixes.

Version 1.7	26 November 2003
- [Linux/Unix] Bug fix: now handles symbolic link arguments correctly.
This bug only affects Unix-type sytems (like Linux, FreeBSD, OpenBSD,
etc).
- [Linux] tofrodos now uses mkstemp() to create the temporary file to
avoid a race between file name creation and opening which may occur
when using mktemp(). If you use a non-Linux system, and have
mkstemp(), you can take advantage of this by defining HAVE_MKSTEMP
in config.h (defined by default for Linux only).
- [Linux/Unix] "make install" now installs to /usr/bin by default (you
can still change this easily) since Linux distributions using tofrodos
appear to use this value. (Makes it easier for maintainers of those
distributions.)
- [All systems] Made some error messages a bit more informative.
- [All systems] Verbose mode is slightly more informative.
- [Windows] Added support for compiling with the MinGW (GNU) C Compiler
on Win32 systems.
- [All systems] Added an HTML version of the manual page. Useful for
systems that don't have a built-in facility to read a man page (like
MSDOS and Windows).

Version 1.6	1 July 2002
Added support for LCC-Win32 and BCC 5.5

Version 1.5	19 June 2002
Minor fixes to documentation.
Added support for the Digital Mars C/C++ compiler (Win32 console mode)

Version 1.4	16 March 1999
Fixed bug when using -b on a file not in the current directory.
Added RPM support.

Version 1.3	8 October 1997
Added new option (-p) to preserve file ownership and time.
Added support for Win32 compilation and some compilers under DOS and
Windows.

Version 1.2	5 April 1997
(Internal version - not publicly released.)

Version 1.1	16 December 1996
Fixed bug in creation of temporary filename on Linux.
Fixed bug in creation of backup filename on Linux.

Version 1.0	22 June 1996		Initial version.


7. Copyright And Contacting The Author
--------------------------------------

The program and its accompanying files and documentation are
Copyright 1996-2012 Christopher Heng. All rights reserved.
They are distributed under the terms of the GNU General Public License
Version 2, a copy of which is enclosed in this package in the file COPYING.

You can retrieve the latest version of tofrodos from the following
website:
	http://www.thefreecountry.com/tofrodos/index.shtml

If you need to contact me, you can use the feedback form at the
following URL:
	http://www.thefreecountry.com/feedback.php

While I generally like to hear from you if you are using this program,
especially if you find any bugs in it, I make no promises about fixing
anything or even replying. If you do contact me, please also let me have
the following information:

	1. The version of tofrodos that you are using;
	2. The operating system (and its version) on which you are
	running the program (eg, Debian Linux [Woody], or MSDOS 6.22,
	Windows 95/98/ME/NT/2k/XP/Vista/7, Mac OS/X with version number,
	or all of the above).
	If the bug only surfaces in one of the operating systems and not
	the other, please also note this.
	3. Any other information which you think might be relevant.

This will help me give you a more relevant reply (and also to trace
whatever bugs that may be present).
