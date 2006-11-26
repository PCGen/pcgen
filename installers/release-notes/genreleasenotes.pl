#!/usr/bin/perl

# genreleasenotes.pl
# ==================
#
# This script prepares release notes for PCGen release. It converts roadmap 
# extracts into changelogs and the changes report to a What's New section.
#
# $Id: release.pl 1639 2006-11-12 11:45:20Z jdempsey $


use strict;
use warnings;
use Readonly;
use English;

use IO::Handle;


# print immediately instead of waiting for a \n
*STDOUT->autoflush();

# search through the roadmap file only keeping heading and closed trackers
my $firstTime = (1 == 1);
open ROADMAP, "work/roadmap.txt";
open CHANGELOG, ">work/changelog.txt";
while (<ROADMAP>) {
        if (/^[0-9].*Closed *$/oi) {
        	s/^([0-9]+)/<LI>[ <a href\=\"http:\/\/sourceforge.net\/support\/tracker.php\?aid=$1\"\>$1<\/a> \]/;
        	s/\s*Closed *$//i;
        	print CHANGELOG $_;
        }
        elsif ( /^[a-z]/oi ) {
        	if (!$firstTime) {
        		print CHANGELOG "</ul>\n\n";
        	}
        	
        	$firstTime = (0 == 1);
        	print CHANGELOG "<h3>".$_."</h3><ul>\n";
        }
}
print CHANGELOG "\n</ul>\n";
close CHANGELOG;
close ROADMAP;

# Search through the changes report file trimming down text and adjusting URLs
open CHANGES, "work/changes.txt";
open WHATSNEW, ">work/whatsnew.txt";
while (<CHANGES>) {
	s/^\s*//;
	s/\s*$//;
	s/src="images/src="http:\/\/pcgen.sourceforge.net\/autobuilds\/images/;
	s/href="team/href="http:\/\/pcgen.sourceforge.net\/autobuilds\/team/;
	if (length $_ > 0 && !/<table/i && !/<\/table>/) {
	   	print WHATSNEW $_ . "\n";
	}
}
close WHATSNEW;
close CHANGES;
