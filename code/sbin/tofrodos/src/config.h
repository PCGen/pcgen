/*
	config.h	Handles system dependencies.
	Copyright (c) 1996-2013 by Christopher Heng. All rights reserved.
*/

/*
	You need an ANSI C compiler. I assume this everywhere. If you
	have a pre-ANSI C compiler, it's likely that you have to make
	a lot of changes to the sources that you might as well just
	rewrite the program. It *is* afterall a trivial program.

	I have not specifically designed this program so that it is
	portable across systems. The comments below might help if you
	are using anything other than the compilers I used to develop
	the program. Note that the comments and macros in this file
	about system dependencies are not necessarily exhaustive.

	1. These macros are defined for the following systems:
	System					Macros defined
	------					--------------
	LINUX					LINUX, UNIX
	MSDOS					MSDOS
	WIN32					WIN32

	2. You will need a getopt() implementation. It must support the
	usual behaviour of the Unix getopt(), plus the variables
	optind, opterr, and optarg.

	If your system has the header <getopt.h>, define HAVE_GETOPT_H.
	I have defined this for the systems I compile for.

	I have supplied my own version of getopt.c and getopt.h in
	the lib subdirectory since most MSDOS and Win32 compilers do
	not have getopt() in their libraries.

	3. If your system has <unistd.h>, define HAVE_UNISTD_H. This is
	usually relevant only for Unix systems, although the DJGPP GNU C
	compiler has that as well. If you don't have unistd.h, you may
	have to declare some standard Unix functions that are usually
	found there, such as chown(), chmod(), etc.

	4. Note that on MSDOS systems, you will need _splitpath()
	and _makepath(). If you use DJGPP, you probably can get away
	with defining _splitpath() to call fnsplit(), etc. Otherwise,
	you will need to roll your own version. I think all the
	commercial MSDOS C compilers have these functions.

	5. You will also need stricmp() and strnicmp() on MSDOS or
	strcasecmp() or strncasecmp() on Unix. If you have stricmp() and/or
	strnicmp() on a Unix system, define HAVE_STRICMP and/or
	HAVE_STRNICMP respectively. I assume stricmp() for all non-Unix
	systems so if you are neither compiling for Unix or MSDOS, you
	better check out my macros below. If you have a Unix system,
	defining UNIX here will cause the compiler to use strcasecmp()
	and strncasecmp().

	6. You will need mkstemp(). On Unix systems, this is probably
	declared in <unistd.h>.

	I have supplied my own mktemp.c and mktemp.h for use with the
	Open Watcom C (Windows/DOS) and Visual C++ (Windows) compilers
	that includes my implementation of mkstemp(). If your system
	does not have mkstemp(), you might try to see if you can use
	this. (It's in the lib directory.)

	If your compiler has mkstemp() declared somewhere else (other
	than unistd.h on Unix), define MKTEMP_HEADER to be the name
	of the header, eg <whatever.h> (include the angle brackets or
	double quotes), and HAVE_MKTEMP_H to force inclusion of the
	header in the relevant files.

	7. tofrodos.c assumes utime.h exists in <sys/utime.h> for
	Microsoft's compiler and Watcom C/C++ (which tries to emulate
	Microsoft's compiler closely). It assumes that all other compilers
	keep utime.h in the standard include directories which are accessible
	simply by including <utime.h>. I must confess I have not bothered to
	keep this system dependent setting in this file, only noting it here
	for completeness. If you find that you have to tweak this for your
	system, please let me know.
*/

#if !defined(CONFIG_H_INCLUDED)
#define	CONFIG_H_INCLUDED

#if defined(__cplusplus)
extern "C" {
#endif

/* define the systems */
#if defined(__linux__)	/* (predefined) */
#if !defined(LINUX)
#define	LINUX
#endif
#if !defined(UNIX)
#define	UNIX		/* make sure this is defined */
#endif
#endif

#if defined(__FreeBSD__) || defined(__FreeBSD_kernel__) || \
	defined(__OpenBSD__) || defined(__NetBSD__) || defined(__GNU__)
	/* these systems seem to work like Linux. Note to self: __GNU__ == Hurd */
#if !defined(LINUX)
#define	LINUX
#endif
#if !defined(UNIX)
#define	UNIX		/* make sure this is defined */
#endif
#endif

#if defined(__MSDOS__)
#if !defined(MSDOS)
#define	MSDOS		/* make sure this is defined */
#endif
#endif

#if defined(__WIN32__) || defined(__NT__) || defined(_WIN32)
#if !defined(WIN32)
#define	WIN32
#endif
#endif

#if defined(__APPLE__)
#if !defined(UNIX)
#define	UNIX
#endif
#define	HAVE_UNISTD_H
#endif

/* define what headers we have (based on the systems) */
#if defined(LINUX)
#define HAVE_GETOPT_H
#define	HAVE_UNISTD_H
#endif

#if defined(WIN32) || defined(MSDOS)
#if !defined(HAVE_MKTEMP_H)
#define	HAVE_MKTEMP_H
#endif
#if !defined(MKTEMP_HEADER)
#define	MKTEMP_HEADER	<mktemp.h>
#endif
#if !defined(HAVE_GETOPT_H)
#define	HAVE_GETOPT_H
#endif
#endif

#if defined(__MINGW32__)
#define	HAVE_GETOPT_H
#endif

/* if we are in Unix define stricmp to be strcasecmp and strnicmp to */
/* be strncasecmp. I'm not sure if all Unices have these, but Linux */
/* does. */
#if defined(UNIX)
#if !defined(HAVE_STRICMP)
#define	stricmp 	strcasecmp
#endif
#if !defined(HAVE_STRNICMP)
#define	strnicmp	strncasecmp
#endif
#endif

/* Microsoft's compiler havs S_IREAD and S_IWRITE in its sys/stat.h */
/* but not S_IRUSR and S_IWUSR which I use. */
#if defined(_MSC_VER)
#define	S_IRUSR	S_IREAD
#define	S_IWUSR	S_IWRITE
#endif

#if defined(__cplusplus)
}
#endif

#endif	/* CONFIG_H_INCLUDED */
