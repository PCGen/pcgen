/*
	emsg.h		Error messages.
	Copyright 1996-2013 by Christopher Heng. All rights reserved.
*/

#if !defined(EMSG_H_INCLUDED)
#define	EMSG_H_INCLUDED

#if defined(__cplusplus)
extern "C" {
#endif

/* macros */
#define	EMSG_BAKFILENAME	"File cannot have a .bak extension "\
							"when used with -b flag.\n"
#define	EMSG_CONVERT		"File read/write error while "\
							"converting \"%s\".\n"
#define	EMSG_INTERNAL		"Internal error: %s.\n"
#define	EMSG_NOFILENAME		"Need to specify filename or redirect "\
							"stdin.\n"
#define	EMSG_NOMEM			"Insufficient memory to run program.\n"
#define	EMSG_NOTEMPNAME		"Unable to generate temporary filename for converting \"%s\".\n"
#define	EMSG_OPENFILE		"Unable to open file \"%s\".\n"
#define	EMSG_SIGNAL			"Terminated by user.\n"
#define	EMSG_WRONGDOSVER	"Requires DOS 3.1 and above.\n"
#define	EMSG_ACCESSFILE		"Unable to access file \"%s\".\n"
#define	EMSG_NOTREADABLE	"No read permission for \"%s\".\n"
#define	EMSG_NOTWRITEABLE	"No write permission for \"%s\". Use -f to force conversion.\n"
#define	EMSG_CHOWN			"Unable to change ownership of \"%s\".\n"
#define	EMSG_SYMLINK		"Unable to dereference symbolic link \"%s\".\n"
#define	EMSG_CREATETEMP		"Unable to create temporary file \"%s\" for converting \"%s\".\n"
#define	EMSG_ERRORLOG		"%s: Unable to create error log file \"%s\". Defaulting to stderr.\n" /* special case with progname */
#define	EMSG_RENAMEBAK		"Unable to save original file %s as %s.\n"\
							"Reason: %s\n"
#define	EMSG_RENAMETMP		"Unable to rename temporary file %s back to %s after converting it.\n"\
							"Reason: %s\n"\
							"Please recover your converted file by manually renaming it back.\n"

/* internal error macros */
#define	EINTNL_DIRECTION	"unknown direction"

/* verbose messages */
#define	VERBOSE_CONVERTING	"Converting \"%s\"\n"
#define	VERBOSE_SYMLINKSRC	"\"%s\" resolves to \"%s\"\n"

/* function declarations */
extern void emsg ( char * message, ... );

#if defined(__cplusplus)
}
#endif

#endif
