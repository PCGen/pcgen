#!/bin/sh
cd `dirname $0`

pcgendir="$HOME/.pcgen"

if [ ! -d "$pcgendir" ]
then
    mkdir "$pcgendir" || exit 2
fi

if [ -f bin/pcgen.jar ]
then
    if [ ! -f pcgen.jar -o bin/pcgen.jar -nt pcgen.jar ]
    then
        cp bin/pcgen.jar .
    fi
fi

if [ ! -f pcgen.jar ]
then
    echo "$0: File not found: pcgen.jar: try 'ant build'" >&2
    exit 2
fi

if [ "x$BROWSER" = x ]
then
    echo "$0: warning: please help fix the Unix desktop guess" >&2

    case "$WINDOWMANAGER" in
        *kde ) BROWSER=kde-open
            echo "$0: warning: guessing KDE environment" >&2 ;;
        *gdm ) BROWSER=gnome-open
            echo "$0: warning: guessing GNOME environment" >&2 ;;
        * ) BROWSER=netscape
            echo "$0: warning: fallback to netscape" >&2
    esac
fi

# To load all sources, the JVM runs out of memory with default 64m
javaargs="-Xmx96m"
pcgenargs=""
whosearg=java

while [ "x$1" != x ]
do
    case "$1" in
    -h ) cat <<EOM
usage: $0 [java-options] [-- pcgen-options]
    For java options, try 'java -h' and 'java -X -h'.
    Useful java property defines:
        -DBROWSER=/path/to/browser
        -Dpcgen.filter=/path/to/filter.ini
        -Dpcgen.options=/path/to/options.ini
    This script recognizes the BROWSER environment variable.
EOM
		exit 0
        ;;
	-- ) whosearg=pcgen
        ;;
	* ) if [ "$whosearg" = java ]
        then
            javaargs="$javaargs $1"
        else
            pcgenargs="$pcgenargs $1"
        fi
        ;;
    esac
    shift
done

# If you have arguments, the class to run must be the FIRST one!  Example:
#     $ java -jar pcgen.jar pcgen.core.pcGenGUI arg1 another-arg

# PCGen related properties:
#
# pcgen.filter  - the full path to the file name containing the filter settings
# pcgen.options - the full path to the file name containing the options
#
# Both of these properties are optional.  Default behaviour is to get the
# files from the "user.dir" directory.
#
# Additional properties:
#     -DBROWSER="$BROWSER"
#     -Dpcgen.filter=/path/to/filter.ini
#     -Dpcgen.options=/path/to/options.ini

exec java -DBROWSER="$BROWSER" $javaargs -jar pcgen.jar $pcgenargs
