/*
	utility.c	Utility functions.
	Copyright (c) 1996,1997 by Christopher Heng. All rights reserved.

	$Id: utility.c,v 1.3 2005/03/07 13:40:52 chris Exp $
*/

/* this should always be first */
#include "config.h"

/* standard headers */
#include <string.h>	/* strdup() */
#include <stdlib.h>	/* malloc() */

/* our headers */
#include "emsg.h"
#include "tofrodos.h"
#include "utility.h"

/*
	errnomem

	Display error message about being out of memory, and exits.
	It never returns.

	WARNING: It must never return. All code assumes it does not
	return. Also in Watcom, we define it as a function which does
	not return (see utility.h) so that the optimiser can jump to
	this function (instead of calling it).
*/
void errnomem ( int exitcode )
{
	emsg( EMSG_NOMEM );
	exit( exitcode );
}

/*
	xmalloc

	Does the same thing as malloc() except that it never returns
	a NULL pointer. It aborts with an error message on running
	out of memory.
*/
void * xmalloc ( size_t len )
{
	void * ptr ;

	if ((ptr = malloc( len )) == NULL)
		errnomem( EXIT_ERROR );
	return ptr ;
}

/*
	xstrdup

	Same as strdup(). Only, it never returns a NULL pointer.
	If memory could not be allocated, it exits with an error
	message.
*/
char * xstrdup ( const char * s )
{
	char * t ;

	if ((t = strdup( s )) == NULL)
		errnomem( EXIT_ERROR );
	return t ;
}

