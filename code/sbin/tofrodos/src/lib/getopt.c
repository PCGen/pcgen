/*
	getopt.c
	Copyright 1997-2013 by Christopher Heng. All rights reserved.

	This code is released under the terms of the GNU General Public
	License Version 2. You should have received a copy of the GNU
	General Public License along with this program; if not, write to the
	Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139,
	USA.

	The version of getopt() given here is based on the AT&T public
	domain source for getopt() given out at the 1985 UNIFORUM conference
	in Dallas. It has been modified as follows:

	1. I support the newer optreset variable used in BSD 4.4.
	2. I support a non-standard optprogname which the user can use
	to set the program name to print. The AT&T version uses argv[0].
	Mine simply does not print a program name if optprogname is NULL.
	3. I support the use of '/' as the option switch character.
	4. I use a character pointer to track which character within an
	argument we are at for efficiency sake instead of the array
	integer index used in AT&T's implementation.
	5. Added Microsoft Windows GUI support. To enable this, just
	define GUI_APPLICATION to have the message displayed in a message
	box.
	6. It returns -1 instead of EOF.

	This function is primarily designed for MSDOS and Windows, since those
	systems lack getopt(). Linux, BSD, and other Unix-type systems already
	have a fully-functioning and thoroughly-debugged version, so you should
	use the native versions on those systems instead. The error messages
	in this file also presuppose an MSDOS/Windows environment since they
	use '/' instead of '-' to describe the offending options.

	See also list of differences in general in the documentation
	preceding the function itself.
*/

#include <stdio.h>	/* fprintf() */
#include <string.h>	/* strchr() */
#include "getopt.h"	/* our very own header */

/* system specific includes */
#if defined(GUI_APPLICATION)
#define	STRICT
#define	WIN32_LEAN_AND_MEAN
#if defined(_MSC_VER)
#pragma warning ( disable: 4514 4201 4214 )
#endif
#include <windows.h>
#if defined(_MSC_VER)
#pragma warning ( default : 4201 4214 )
#endif
#endif

/* macros */
#define	OPT_SW1		'-'	/* for Unix afficiondos */
#define	OPT_SW2		'/'	/* MSDOS traditional switch character */

/* macros specific to systems */
#if defined(GUI_APPLICATION)	/* Windows version */
#define	MESSAGEMAX		128
#define	ERR_TITLE		"Error"
#define	ERR_UNKNOWNOPT	"Unknown option: /%c."
#define	ERR_OPTNEEDSARG	"Option /%c requires an argument."
#if !defined(MB_ICONERROR)
		/* define macro not defined in Win16's windows.h */
#define	MB_ICONERROR	MB_ICONHAND
#endif
#else	/* command line version */
#define	ERR_PREFIX		"%s: "
#define	ERR_UNKNOWNOPT	"Unknown option: /%c.\n"
#define	ERR_OPTNEEDSARG	"Option /%c requires an argument.\n"
#endif	/* command line version macros */

/* global variables */
char * optarg ;		/* argument to option */
int opterr = 1 ;	/* 0 = don't print error msg, 1 = print */
int optind = 1 ;	/* next argument to parse */
int optopt ;		/* the current option */
char * optprogname ;/* store program name here if you want the error */
					/* message spouter to issue the program name */
int optreset ;		/* 0 = continue processing, 1 = reset to start */

/* local functions */
static void error_message ( char * s );

/*
	getopt

	getopt() parses the command line given in the vector list,
	and returns the option character (if any) and the option
	argument in optarg.

	argc, argv corresponds to the versions passed to main().
	optlist is the list of valid options. Options which must
	have arguments have a ':' suffixed to the option character
	in optlist. Needless to say, ':' cannot be a valid option
	character.

	This function uses various globals: optarg, opterr, optind,
	optopt, optreset and optprogname to govern its actions.

	optarg		contain the argument to the option if the option is
				specified to have an argument.
	opterr		If this is set to 1, an error message will
				be printed on stderr for bad options or missing arguments
				to options. The default is to print the error message.
	optind		contains the next argv item which to be parsed. Defaults
				to 1 to skip over the program name in the standard
				argv[] passed to main().
	optopt		always contains the option character which is returned on
				a valid option or which caused an error.
	optprogname	(non-standard) Contains the program name to be printed
				prior to printing the error message.
	optreset	Set this to 1 if you need to call getopt() after using
				it to parse a different argv list.

		
	The options "//", "/-", "--", and "-/" will terminate the
	list of options (unless one of those characters are themselves
	specified in the option list, optlist). The character ':'
	can never be an option. A solitary '-' or '/' will also cause
	getopt() to return -1.

	Returns:

		-1				No more options to parse.
		OPT_BADOPT		An option character was encountered which
						was not in the list of valid options.
		OPT_BADARG		An option was supposed to have an argument
						but was found without one.
		Otherwise, the option character is returned.

	Differences from the Unix version:
	1. '?' can be a valid option, since we do not return '?' when
	there is an error. Unix getopt()s returns '?' when there is
	an error. We return OPT_BADOPT (which is equated to 0).
	2. the options are preceded by either '-' or '/' and the end
	of option list demarcator can be "--", "-/", "//" or "/-".
	Because our options begin with '/', filenames cannot begin
	with the '/' character else it would be interpreted as
	an option, unless you precede the file list with "--" to
	mark the end of options.
	3. We use optprogname to hold the program name to print
	when there is an error. If this is missing, no program name
	is printed. Some Unix versions print argv[0].
	4. Some Unix versions do not have optreset. This is present
	only in the later BSD versions. I have implemented it just in
	case I need it.
	5. Some Unix versions only return '?' when there is an error,
	not differentiating between an invalid option and a missing
	argument to an option. The version supplied with BSD 4.4
	returns ':' for the latter error. We follow the protocol of
	the BSD 4.4 version in this respect.
	7. A solitary '-' or '/' will cause -1 to be returned.
	According to the getopt manual page in BSD, this appears
	to be the behaviour in System V. This is the behaviour in
	Borland C/C++'s example getopt.c also.
*/
int getopt ( int argc, char * const * argv, const char * optlist )
{
	static char		nullstring[] = "" ;
	static char *	curptr = nullstring ;
	char *			s ;

	if (optreset || *curptr == '\0') {
		/* either end of current arg or first time or user wants us */
		/* to treat this as first time */

		/* got to restore this to zero for next iteration. Got to do */
		/* it here before we exit */
		optreset = 0 ;

		/* get next (or first arg) */
		if(optind >= argc ||
			(*(curptr = argv[optind]) != OPT_SW1 && *curptr != OPT_SW2)) {
			curptr = nullstring ;	/* reset */
			return -1;
		}
		/* got to set curptr since we could have got here by */
		/* optind < argc prior to curptr being set */
		curptr = argv[optind] ;
		/* check if user specified end of list of options */
		if (*++curptr == '\0' ||	/* solitary '-' */
			(*curptr == OPT_SW1 || *curptr == OPT_SW2)) {	/* "--" */
			optind++;	/* point to next argument */
			curptr = nullstring ;	/* reset */
			return -1;
		}
	}
	/* by the time we get here, we have skipped over the option */
	/* switch character */
	optopt = *curptr ;

	if(optopt == ':' ||	/* need to trap this or problems will arise */
		(s = strchr( optlist, optopt )) == NULL) { /* no such option */
		if (opterr)
			error_message ( ERR_UNKNOWNOPT );
		/* skip erroneous option character */
		if (*++curptr == '\0')			/* end of argument */
			optind++ ;					/* go to next */
			/* curptr = nullstring ; */	/* but already pointing to a null */
										/* string */
		return OPT_BADOPT ;
	}
	if(*++s == ':') {	/* argument expected */
		/* point to next argument - always done so might as well */
		/* do it before we test */
		optind++ ;
		/* is the argument at the end of current argument? */
		if(*++curptr != '\0') {	/* yep */
			optarg = curptr ;
			curptr = nullstring ;
			/* optind already pointing to next argument for next round */
		}
		else if (optind < argc)	/* optarg is in next argument */
			optarg = argv[optind++] ;
			/* since we use the next arg for our optarg, we needed to */
			/* point optind to the argument after that for next round */
			/* curptr already pointing to null byte */
		else {	/* optarg not at end of current arg nor are there any */
				/* more args */
			if (opterr)
				error_message ( ERR_OPTNEEDSARG );
			return OPT_BADARG ;
			/* optind already pointing to next (nonexistant) argument */
			/* curptr already pointing to null byte */
		}
	}
	else {	/* optarg is not expected */
		if (*++curptr == '\0')	/* end of current argument */
			optind++ ;			/* skip to next for next round */
		optarg = NULL;			/* just in case */
	}
	return optopt ;
}

/*
	error_message

	System specific error message spouter. It prints the program
	name first if optprogname is not NULL.

	References the global optopt.

	Returns: nothing.
*/
static void error_message ( char * msgfmt )
{
#if defined(GUI_APPLICATION)
	char buf[MESSAGEMAX];
	char * title ;

	sprintf( buf, msgfmt, optopt );
	title = (optprogname == NULL) ? ERR_TITLE : optprogname ;
	MessageBox( 0, buf, title, MB_ICONERROR | MB_OK );
#else
	if (optprogname != NULL)
		fprintf( stderr, ERR_PREFIX, optprogname );
	fprintf( stderr, msgfmt, optopt );
#endif
	return ;
}
