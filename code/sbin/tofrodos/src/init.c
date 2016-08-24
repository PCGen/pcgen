/*
	init.c		Initialisation functions.
	Copyright 1996-2013 Christopher Heng. All rights reserved.
*/

/* this should always be first */
#include "config.h"

/* standard headers */
#if defined(HAVE_GETOPT_H)
#include <getopt.h>	/* getopt() (what else?) */
#endif

#include <signal.h>	/* signal() (surprise!) */
#include <stdlib.h>	/* _splitpath(), _MAX_FNAME, exit, EXIT_SUCCESS */
#include <stdio.h>	/* fprintf() */
#include <string.h>	/* stricmp() */

#if defined(HAVE_UNISTD_H)
#include <unistd.h>
#endif

/* our own headers */
#include "emsg.h"
#include "tofrodos.h"
#include "utility.h"
#include "version.h"

/* macros */
#define	HELPFMT		"Usage: %s [options] [file...]\n"\
			"-a\t(Deprecated option, see manual for info.)\n"\
			"-b\tMake backup of original file (.bak).\n"\
			"-d\tConvert DOS to Unix.\n"\
			"-e\tAbort processing files on error in any file.\n"\
			"-f\tForce: convert even if file is not writeable.\n"\
			"-h\tDisplay help on usage and quit.\n"\
			"-l file\tLog most errors and verbose messages to <file>\n"\
			"-o\tOverwrite original file (no backup).\n"\
			"-p\tPreserve file owner and time.\n"\
			"-u\tConvert Unix to DOS.\n"\
			"-v\tVerbose.\n"\
			"-V\tShow version and quit.\n"
#define	OPTLIST		"abdefhl:opuvV"
#define	VERFMT		"%s Ver %d.%d.%d "\
			"Converts text files between DOS and Unix formats.\n"\
			"Copyright 1996-2013 Christopher Heng. "\
                        "All rights reserved.\n"\
			"http://www.thefreecountry.com/tofrodos/index.shtml\n"

#if defined(MSDOS) || defined(WIN32)
#if !defined(_MAX_NAME) || (_MAX_NAME < 260)
#define MAXFILESIZE	260
#else
#define	MAXFILESIZE	_MAX_NAME
#endif
#endif

#if !defined(MSDOS)
#define	DIRSLASH	'/'
#endif


/* local functions */
static void showhelp ( void );
static void showversion ( void );

/*
	init

	Checks for correct operating system version (DOS only).
	Sets the default direction of conversion.
	Sets the signal traps.

	Returns 0 on success, -1 on error.
*/
int init ( char * firstarg )
{
#if defined(MSDOS) || defined(WIN32)
	char filename[MAXFILESIZE];
#else
	char * s ;
#endif

#if defined(MSDOS)
	/* Check that we have the minimum version of DOS needed. */
	/* We only run on DOS 3.1 and above. */
	if (_osmajor < 3 ||
		(_osmajor == 3 && _osminor < 10)) {
		emsg( EMSG_WRONGDOSVER );
		return -1 ;
	}
#endif

	/* set the name of the binary to set default direction of */
	/* conversion */
#if defined(MSDOS) || defined(WIN32)
	/* got to extract the name from the full path and extension */
	_splitpath( firstarg, NULL, NULL, filename, NULL );
	progname = xstrdup( filename );
#else	/* neither MSDOS nor WIN32 - assume Unix */
	/* got to wipe out the path prefix if any */
	if ((s = strrchr( firstarg, DIRSLASH )) == NULL)
		progname = firstarg ;
	else { /* we got the last slash - let's get rid of it */
		progname = ++s ;
	}
#endif

	/* set the default direction: Unless we are explicitly named */
	/* to convert in a particular direction, the default direction */
	/* depends on the system. If we are on a DOS system, it is to */
	/* convert from Unix to DOS. If we are on a Unix system, it */
	/* is to convert from DOS to Unix. */
	/* The default direction is set in tofrodos.c using a macro defined in tofrodos.h */
	if (!stricmp( progname, FROMDOSNAME ) ||
		!stricmp( progname, FROMDOSNAME2 ))
		direction = DOSTOUNIX ;
	else if (!stricmp( progname, TODOSNAME ) ||
		!stricmp( progname, TODOSNAME2 ))
		direction = UNIXTODOS ;

	/* set the signal traps - we use the old Unix version 7 signal */
	/* mechanism since that is most portable to DOS. In any case, */
	/* we don't do anything sophisticated when we receive a signal */
	/* except cleaning up and quitting! */
	if (signal( SIGINT, sighandler ) == SIG_IGN)
		signal( SIGINT, SIG_IGN );
	if (signal( SIGTERM, sighandler ) == SIG_IGN)
		signal( SIGTERM, SIG_IGN );

	return 0 ;
}

/*
	parseargs

	Parses the options.

        Returns 0 on success, -1 on error.
*/
int parseargs ( int argc, char ** argv )
{
	int c ;

	while ((c = getopt( argc, argv, OPTLIST )) != -1) {
		switch( c ) {
			case 'a': /* force conversion of all \r\n to \n */
				alwaysconvert = 1 ;
				break ;
			case 'b': /* make backup of original file */
				overwrite = 0 ;
				break ;
			case 'd': /* DOS to Unix */
				direction = DOSTOUNIX ;
				break ;
			case 'e': /* abort processing list of files if */
				  /* we encounter errors in any file in */
				  /* a list of file names */
				abortonerr = 1 ;
				break ;
			case 'f': /* convert even if file is not writeable*/
				forcewrite = 1 ;
				break ;
			case 'h': /* display short usage screen and quit */
               	showhelp() ;
				exit( EXIT_SUCCESS );
				break ;
            case 'l': /* log errors to filename */
            	errorlogfilename = optarg ;
            	break ;
			case 'o': /* overwrite original file (default) */
            	overwrite = 1 ;
            	break ;
			case 'p': /* preserve file owner and date */
				preserve = 1 ;
				break ;
			case 'u': /* Unix to DOS */
				direction = UNIXTODOS ;
				break ;
			case 'v': /* verbose */
				verbose = 1 ;
				break ;
			case 'V': /* show version and quit */
				showversion() ;
				exit( EXIT_SUCCESS );
				break ;
			default:  /* error */
				return -1 ;
		}
	}
	return 0 ;
}

static void showversion ( void )
{
	static int vershown ;

	if (!vershown) {
		fprintf( stderr, VERFMT, VERSN_PROGNAME, VERSN_MAJOR, VERSN_MINOR, VERSN_PATCH );
		vershown = 1 ;
	}
	return ;
}

/*
	showhelp

	Display the short usage help screen.
*/
static void showhelp ( void )
{
	showversion();
	fprintf( stderr, HELPFMT, progname );
	return ;
}
