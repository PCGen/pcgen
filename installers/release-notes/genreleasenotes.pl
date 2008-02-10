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
use LWP::Simple qw(get);


# print immediately instead of waiting for a \n
*STDOUT->autoflush();

# search through the roadmap file only keeping heading and closed trackers
my $firstTime = (1 == 1);
my $discardingSectionState = 0; # States are 0=not discarding, 1=discard line regardless, 2=stop discard at next blank line
my $trackerNum = "";
my @trackerLines;
open ROADMAP, "work/roadmap.txt";
open CHANGELOG, ">work/changelog.txt";
while (<ROADMAP>) {
        if ( /^[0-9\.]+%$/){
        	# do nothing with percentage match lines
        }
        elsif ( /^ ?[0-9] *\t *[0-9]+[ \t]*$/ ) {
        	# Tracker number
        	s/^ ?[0-9][ \t]*//;
        	s/\s*$//g;
        	$trackerNum = $_;
        	#print "Found tracker " . $trackerNum . "\n"; 
        } 
		elsif ( /^[ \t]*$/) {
			# Ignore blank lines
		}
		
		elsif ( /^[ \t]/ ) {
			# Tracker info line
        	my $cat = $_;
        	$cat =~ s/^[ \t]([A-Za-z ]*).*$/$1/;
        	$cat =~ s/\s*$//g;
        	s/^[ \t][A-Za-z ]*/<li>[ <a href\=\"http:\/\/sourceforge.net\/support\/tracker.php\?aid=$trackerNum\"\>$trackerNum<\/a> \]/;
        	s/\s+[0-9\-]+$//;
        	s/\s+[A-Za-z_0-9 \-\.]+\s+[A-Za-z_0-9 \-\.]+\s+[A-Za-z_0-9 \-\.]+$//;
        	s/\s*$//g;
			push(@trackerLines, $cat . "@@@" . $_ . "<\/li>");
		}

        elsif (/^[0-9].*Closed *$/oi) {
        	s/^([0-9]+)/<li>[ <a href\=\"http:\/\/sourceforge.net\/support\/tracker.php\?aid=$1\"\>$1<\/a> \]/;
        	s/\s*Closed *$//i;
        	print CHANGELOG $_."<\/li>";
        }
}

# Now output the info
@trackerLines = sort { $a cmp $b } @trackerLines;
my $lastTracker = "";
foreach (@trackerLines) {
	#Detect change in tracker type
	my $currTracker = $_;
	$currTracker =~ s/@@@[\s\S]*//;
	if ($currTracker ne $lastTracker) {
		$lastTracker = $currTracker;
    	if (!$firstTime) {
    		print CHANGELOG "</ul>\n\n";
    	}
    	
    	$firstTime = (0 == 1);
    	print CHANGELOG "<h3>".$currTracker."</h3>\n\n<ul>\n";
	}
	#output tracker line
	s/^[A-Za-z0-9 ]+@@@//;
  	print CHANGELOG $_."\n";
}

print CHANGELOG "\n</ul>\n";
close CHANGELOG;
close ROADMAP;

# Search through the changes report file trimming down text and adjusting URLs
my $page = get "http://pcgen.sourceforge.net/autobuilds/changes.rss";
if (!$page) {
    print "Autobuild site is not accessible\n";
    exit;
}

my $changeHtml = $page;
$changeHtml =~ s/.*\<description\>.*?\<\/tr\>//s;
$changeHtml =~ s/\s*<\/table>\s*<\/description>\s*<\/item>\s*<\/channel>\s*<\/rss>//s;

open WHATSNEW, ">work/whatsnew.txt";
my @data = split(/\n/, $changeHtml);
for (@data) {
	s/^\s*//;
	s/\s*$//;
	s/^<tr>$//;
	s/^<td>add<\/td>$/<li><img alt="add" title="add" src="http:\/\/pcgen.sourceforge.net\/autobuilds\/images\/add.gif\">/;
	s/^<td>fix<\/td>$/<li><img alt="fix" title="fix" src="http:\/\/pcgen.sourceforge.net\/autobuilds\/images\/fix.gif\">/;
	s/^<td>update<\/td>$/<li><img alt="update" title="update" src="http:\/\/pcgen.sourceforge.net\/autobuilds\/images\/update.gif\">/;
	s/src="images/src="http:\/\/pcgen.sourceforge.net\/autobuilds\/images/;
	s/href="team/href="http:\/\/pcgen.sourceforge.net\/autobuilds\/team/;
	s/<tr class=".">/<li>/;
	s/<\/*td>//g;
	s/<\/tr>/<\/li>/g;
	if (length $_ > 0 && !/<table/i && !/<\/table>/) {
	   	print WHATSNEW $_ . "\n";
	}
}
close WHATSNEW;
