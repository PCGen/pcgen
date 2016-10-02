/*
	emsg.c		Error message printing functions.
	Copyright (c) 2005-2012 by Christopher Heng. All rights reserved.

*/

/* this should always be first */
#include "config.h"

/* standard headers */
#include <stdarg.h>	/* va_arg and family */
#include <stdio.h>	/* fprintf(), fopen(), fclose() */
#include <stdlib.h>	/* atexit() */

/* our own headers */
#include "emsg.h"
#include "tofrodos.h"

/* macros */
#if defined(MSDOS) || (WINDOWS)
#define	ERROR_LOG_MODE	"at"
#else
#define	ERROR_LOG_MODE	"a"
#endif

/* data local to this file */
static FILE * errorfp ;

/* local functions */
static void close_error_file ( void ) ;

	
void emsg ( char * message, ... )
{
	va_list	argp ;

	if (errorfp == NULL) {
		if (errorlogfilename == NULL) {
			errorfp = stderr ;
		}
		else {
			errorfp	= fopen ( errorlogfilename, ERROR_LOG_MODE );
			if (errorfp == NULL) {
				fprintf( stderr, EMSG_ERRORLOG, progname, errorlogfilename );
				errorfp = stderr ;
			}
			else {
				/* close error file on exit (not needed, but just being pedantically neat) */
				atexit( close_error_file );	/* ignore errors */
			}
		}	
	}
	fprintf( errorfp, "%s: ", progname );
	va_start( argp, message );
	vfprintf( errorfp, message, argp );
	va_end( argp );
	return ;
}

static void close_error_file ( void )
{
	/* there's no need to check for stderr, since we should not have set this function */
	/* on exit() if only stderr were used */
	if (errorfp != NULL) {
		fclose( errorfp );
	}
	return ;
}
