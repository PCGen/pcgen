/*
	getopt.h
	Copyright (c) 1996,1997 by Christopher Heng. All rights reserved.

	$Id: getopt.h,v 1.1 2004/10/01 12:33:39 chris Exp $
*/

#if !defined(GETOPT_H_INCLUDED)
#define GETOPT_H_INCLUDED

#ifdef __cplusplus
extern "C" {
#endif

/* macros for getopt() */
#define	OPT_BADOPT		0			/* error return code for getopt() */
									/* Note that EOF better not be zero! */
#define	OPT_BADARG      ((int)':')	/* no argument given when there should */
									/* be one! Note that ':' cannot be an */
									/* option! */

/* global variables for getopt() */
extern char * optarg;	/* argument if option has one */
extern int opterr;		/* 0 = don't print err msg, 1 = print */
extern int optind;		/* next argument to process */
extern int optopt ;		/* current option */
extern char * optprogname;	/* name of program to print before error msg */
extern int optreset ;	/* set by user to 1 if you want to reset getopt() */

/* global variables for gesubopt() */
extern char * suboptarg ;

/* function declarations */
extern int getopt (int argc, char * const * argv, const char * optlist );
extern int getsubopt ( char ** optargp, char * const * optsp,
	char ** suboptvalp );

#ifdef __cplusplus
}
#endif

#endif /* GETOPT_H_INCLUDED */
