/*
	mktemp.h	Declares my mktemp() function.
	Copyright 1996-2012 by Christopher Heng. All rights reserved.

	Originally written for use in tofrodos, when compiled with
	Watcom 10.0, which did not have either mktemp() or mkstemp().
	Tofrodos can be found at
	http://www.thefreecountry.com/tofrodos/index.shtml
*/

#if !defined(MKTEMP_H_INCLUDED)
#define	MKTEMP_H_INCLUDED

#ifdef __cplusplus
extern "C" {
#endif

/* macros */
#define	MKTEMP_TEMPLATE	"XXXXXX"

/* functions declarations */
extern int mkstemp ( char * templ );

#ifdef __cplusplus
}
#endif

#endif
