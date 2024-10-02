#!/bin/sh
set -e
cd "$(dirname "$0")"

# Most GUIs will change directory to the location of the script
# when the script is double-clicked.  But nooooo, not macOS.
# So if we don't find the script in the current directory, we
# need to find it.
if [ ! -e ./pcgen.sh ]; then
    # We're not in the directory where the script lives.
    # Change to it so relative paths will work.
    if ! cd "${0%/*}"; then
       # Can't change to the directory containing this script??
       # Could be because invoker doesn't put full path in $0,
       # but then how are scripts supposed to figure out where
       # they are executed from?  I suppose we could check the
       # PATH iteratively?  Maybe in the next version.
       echo >&2 "pcgen.sh: Not in proper directory (must be in same directory as 'pcgen.sh')"
       exit 1
    fi
fi

available_memory="unknown"
default_min_memory=256
default_max_memory=512

# Linux /proc/meminfo
if [ -e "/proc/meminfo" ]; then
	available_memory=$(grep MemAvailable: /proc/meminfo | awk '{ print $2; }')
	echo "Available memory: $available_memory kB"

# BSD (thus MacOSX) memory command line should be in /usr/bin/vm_stat
elif [ -x /usr/bin/vm_stat ]; then
	# Mach Virtual Memory Statistics: (page size of 4096 bytes)
	# Pages free:                         713087.
	BLOCK_SIZE=$(vm_stat | grep 'page size of' | cut -d ' ' -f 8);
	FREE_BLOCKS=$(vm_stat | grep 'Pages free' | awk '{ print $3; }' | sed -e 's/\.//');
	FREE_SPACE=$(($FREE_BLOCKS * $BLOCK_SIZE))
	available_memory=$(($FREE_SPACE / 1024))

	echo "Available memory: $available_memory kB"
else
	echo "Could not detect available memory. Will stick to defaults"
fi

# Test if the value is numeric before performing arithmetic on it
if [ $available_memory -eq $available_memory ]; then

	# We go with the defaults if memory is too low
	if [ $available_memory -gt 1048576 ]; then
		echo "There is more than 1 GB of free memory available. Will raise memory limits."
		echo "Will take a quarter as low limit and half as upper limit:"
		default_min_memory=$(($available_memory/1024/4))
		default_max_memory=$(($available_memory/1024/2))
	else
		echo "There is less than 1 GB of free memory available. Will keep default memory limits"
	fi

	echo "min: $default_min_memory MB, max: $default_max_memory MB"
fi

# To load all sources takes more than the default 64MB.
javaargs="-Xms${default_min_memory}m -Xmx${default_max_memory}m -Dsun.java2d.dpiaware=false"

while [ "x$1" != x ]
do
    case "$1" in
    -h ) cat <<EOM
usage: $0 [java-options] [-- pcgen-options]
    For java options, try 'java -h' and 'java -X -h'.
    Useful java property defines:
        -Dpcgen.filter=/path/to/filter.ini
        -Dpcgen.options=/path/to/options.ini
EOM
        exit 0
        ;;
    -- ) shift
	 break
        ;;
    * ) javaargs="$javaargs $1"
	shift
        ;;
    esac
done

# PCGen related properties:
#
# pcgen.filter  - the full path to the file name containing the filter settings
# pcgen.options - the full path to the file name containing the options
#
# Both of these properties are optional.  Default behaviour is to get the
# files from the "user.dir" directory.
#
# Additional properties:
#     -Dpcgen.filter=/path/to/filter.ini
#     -Dpcgen.options=/path/to/options.ini

# shellcheck disable=SC2086
exec java $javaargs -jar ./pcgen.jar -- "$@"
