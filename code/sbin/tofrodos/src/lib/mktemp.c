/*
	mktemp.c	Create a temporary file name.
	Copyright 1996-2012 by Christopher Heng. All rights reserved.

	This code is released under the terms of the GNU General Public
	License Version 2. You should have received a copy of the GNU
	General Public License along with this program; if not, write to the
	Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139,
	USA.

	Originally written for use in tofrodos, when compiled with
	Watcom 10.0, which did not have either mktemp() or mkstemp().
	Tofrodos can be found at
	http://www.thefreecountry.com/tofrodos/index.shtml
*/

#include <errno.h>	/* errno, ENOENT, EINVAL, EEXIST */
#include <io.h>		/* access() */
#include <string.h>	/* strlen(), strcmp() */
#include <sys/types.h>
#include <sys/stat.h>	/* O_IRUSR, O_IWUSR */
#include <fcntl.h>	/* open(), O_RDWR, O_CREAT, O_EXCL */
#include "mktemp.h"	/* our own header */

#define	MAXVAL	(65535u)	/* unsigned is at least this (ANSI) */

/*
	mkstemp

	Creates a temporary file using "templ" and returns a
	file descriptor opened using open(). The file is
	open for read and write binary (not text) access
	for the current user. The new filename is placed
	in "templ", overwriting its existing contents.

	The file path in "templ" must have six trailing "X"s,
	ie, it must end with "XXXXXX".

	On success, mkstemp() returns the file descriptor.
	On failure, it returns -1, and errno is set to
	EINVAL if "templ" does not end with "XXXXXX" on
	entry to the function, or EEXIST if no file could
	be created.

	Function compatibility:
		O_BINARY is used in file creation. This flag
		only exists on Windows and MSDOS compilers.

	Example:
		char tempfilename[] = "\\tmp\\myXXXXXX" ;
		int fd ;
		fd = mkstemp( tempfilename );
*/
int mkstemp ( char * templ )
{
	static unsigned val ;
	static char fch = 'A' ;

	char *s ;
	char *startp ;
	size_t len ;
	unsigned tval ;
	int	fd ;
	int orig_errno ;

	orig_errno = errno ;

	/* do some sanity checks */
	/* make sure that templ is at least 6 characters long */
	/* and comprises the "XXXXXX" string at the end */
	if ((len = strlen(templ)) < 6 ||
		strcmp( (s = startp = templ + len - 6), MKTEMP_TEMPLATE )) {
		errno = EINVAL ;
		return -1 ;
	}
	for ( ; fch <= 'Z'; val = 0, fch++ ) {
		/* plug the first character */
		*startp = fch ;
		/* convert val to ascii */
		/* note that we skip the situation where val == MAXVAL */
		/* because if unsigned has a maximum value of MAXVAL */
		/* in an implementation, and we do a compare of */
		/* val <= MAXVAL, the test will always return true! */
		/* Our way, we have at least a cut-off point: MAXVAL. */
		for ( ; val < MAXVAL; ) {
			tval = val++ ;
			for (s = startp + 5; s > startp ; s--) {
				*s = (char) ((tval % 10) + '0') ;
				tval /= 10 ;
			}
			if ((fd = open( templ, O_CREAT | O_EXCL | O_BINARY | O_RDWR, S_IRUSR | S_IWUSR )) != -1) {
				errno = orig_errno ;
				return fd ;
			}
		}
	}
	errno = EEXIST ;
	return -1 ;
}
