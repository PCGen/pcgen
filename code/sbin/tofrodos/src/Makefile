# makefile.gcc	Makefile for building with the GNU C compiler on Linux.
# Copyright 1996-2012 by Christopher Heng. All rights reserved.

# This makefile requires the GNU make to work (we use the GNU extensions).

# Define the following if you need
#
#	DEBUG	to add debugging information, eg "make DEBUG=1 all"


ifdef DEBUG
CDEBUG = -g
LDEBUG = -g
else
CDEBUG = -O2
LDEBUG = -s
endif

# programs
CC = gcc
CD = cd
CP = cp
GZIP = gzip
INSTALL = install
LN = ln
LD = gcc
MAKE = make
MKDIR = mkdir
MV = mv
RM = rm -f

# flags
CFLAGS = $(DEFINES) $(TFLAG) $(CDEBUG) -c -Wall
GZIPFLAGS = -9
INSTALLBINFLAGS = -m 755
INSTALLDATAFLAGS = -m 644
LDFLAGS = $(LDEBUG)
LNFLAGS = -sf
RMRECURFLAGS = -rf

# directories
BINDIR = /usr/bin
LIBDIR = lib
MANDIR = /usr/man/man1

# filenames
FROMDOS = fromdos
FROMDOSMAN = fromdos.1
FROMDOSTXT = fromdos.txt
TODOS = todos
ZIPSRCNAME = tfd.zip

# lists of filenames
OBJS =	emsg.o \
	init.o \
	tofrodos.o \
	utility.o

# implicit rules
.c.o:
	$(CC) $(CFLAGS) $<

# user visible rules
all: $(FROMDOS) $(TODOS)

clean:
	$(RM) $(OBJS)

clobber: clean
	$(RM) $(FROMDOS) $(TODOS)

install: installman
	$(INSTALL) $(INSTALLBINFLAGS) $(FROMDOS) $(BINDIR)
	($(CD) $(BINDIR) ; $(LN) $(LNFLAGS) fromdos todos)

installman: $(FROMDOSMAN)
	$(INSTALL) $(INSTALLDATAFLAGS) $(FROMDOSMAN) $(MANDIR)
	($(CD) $(MANDIR) ; $(LN) $(LNFLAGS) fromdos.1 todos.1)


# real rules
$(FROMDOS): $(OBJS)
	$(CC) $(LDFLAGS) -o $(FROMDOS) $(OBJS)

$(FROMDOSTXT): $(FROMDOSMAN)
	gtbl $(FROMDOSMAN) | groff -Tascii -mandoc | \
		troff -Tascii -mandoc | grotty > $@

$(TODOS): $(FROMDOS)
	$(LN) $(LNFLAGS) $(FROMDOS) $(TODOS)

# objects
init.o: init.c config.h emsg.h tofrodos.h utility.h version.h

tofrodos.o: tofrodos.c config.h emsg.h tofrodos.h utility.h version.h

utility.o: utility.c config.h emsg.h tofrodos.h utility.h
