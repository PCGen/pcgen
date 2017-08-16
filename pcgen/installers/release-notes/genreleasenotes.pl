#!/usr/bin/perl

# genreleasenotes.pl
# ==================
#
# This script prepares release notes for PCGen release. It converts roadmap 
# extracts into changelogs and the changes report to a What's New section.
#


use strict;
use warnings;
use English;

use IO::Handle;
use LWP::Simple qw(get);


# print immediately instead of waiting for a \n
*STDOUT->autoflush();

if ($#ARGV < 0) {
	print "Error: no fromdate supplied. Usage is  perl genreleasenotes.pl fromdate\n";
	print "  e.g. perl genreleasenotes.pl 20080126\n";
	exit;
}
my $fromDate = $ARGV[0];
my $toDate = sprintf("%04d%02d%02d",
	sub {($_[5]+1900, $_[4]+1, $_[3])}->(localtime));
print("Generating changes and tracker listing for trackers closed between " . 
	$fromDate . " and " . $toDate . ".\n");

# Create a hash of the tracker categories against sort order
my %trackerOrder = (
	"New Data Source Development" => "A",
	"Feature Requests" => "B Code",
	"Data Feature Requests" => "C",
	"New Release Steps" => "D",
	"Documentation Feature Requests" => "E",
	"Output Sheet Feature Requests" => "F",
	"Plugin Feature Requests" => "G",
	"Bugs" => "H Code",
	"Data Bugs" => "I",
	"Documentation Bugs" => "J",
	"Output Sheet Bugs" => "K",
	"Plugin Bugs" => "L"
);
 
# Run the tracker search on SourceForge to retrieve the trackers closed in the period.
my $url = "http://sourceforge.net/search/index.php?group_id=25576&" .
   "type_of_search=artifact&pmode=0&words=group_artifact_id%3A%28384719+" .
   "384721+384722+439552+441567+453331+627102+679269+689516+748234+" . 
   "748235+748296+748297+750091+750092+772045+1036937%29+AND+" . 
   "status_id%3A%282%29+AND+last_update_date%3A%5B" . $fromDate . 
   "+TO+" . $toDate. "%5D&Search=Search&limit=100";

my @trackerLines;
my $rowCountForPage = 0;
my $startIndex = 0;
my $firstTime = (1 == 1);
my $trackerCat = "";
my $trackerNum = "";
my $trackerName = "";
my $page;
my @data;
my $offset = 0;

do {
	$offset += $rowCountForPage;
	#print $url . "&offset=" . $offset . "\n";
	if ($offset > 0) {
		print "Read " . $offset . "...\n";
	}    
	$page = get $url . "&offset=" . $offset;
	   
	if (!$page) {
	    print "Tracker search site is not accessible\n";
	    exit;
	}
	
	# Trim out headers and footers 
	$page =~ s/.*\<table id="searchtable"//s;
	$page =~ s/.*\<tbody>//s;
	$page =~ s/\s*\<\/tbody>\<\/table>.*//s;
	
	$firstTime = (1 == 1);
	$rowCountForPage = 0;
	
	# Scan the search results, building up the list of trackers
	@data = split(/\n/, $page);
	for (@data) {
		s/^\s*//;
		s/\s*$//;
	    if ( /^^<a href=\"\/tracker\/index.php\?/ ) {
	    	# Extract tracker number and name from link. 
	    	$trackerNum = $_;
	    	$trackerNum =~ s/.*aid=([0-9]*).*/$1/;
	    	$trackerName = $_;
	    	$trackerName =~ s/.*?>(.*?)<\/a>.*$/$1/;
	    } 
		
		elsif ( /^<a href=\"\/tracker\/\?group_id=25576&atid\=/ ) {
			# Tracker info line
	    	$trackerCat = $_;
	    	$trackerCat =~ s/<.*?>//g;
		}
	
	    elsif ( /<\/tr>/ && $trackerNum gt "") {
	    	# End of tracker
	    	push(@trackerLines, $trackerOrder{$trackerCat} . " " . $trackerCat . "@@@<li>[ <a " .
	    		"href=\"http://sourceforge.net/support/tracker.php?aid=" . 
	    		$trackerNum . "\">" . $trackerNum . "</a> ]	" . 
	    		$trackerName . "<\/li>");
	    	$rowCountForPage += 1;
	    } 
	}
}
while($rowCountForPage > 0);

# Now output the info
open CHANGELOG, ">work/changelog.txt";
@trackerLines = sort { $a cmp $b } @trackerLines;
my $lastTracker = "";
my $trackerTitle;
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
    	$trackerTitle = $currTracker;
    	$trackerTitle =~ s/\w* //;
    	print CHANGELOG "<h3>".$trackerTitle."</h3>\n\n<ul>\n";
	}
	#output tracker line
	s/^[A-Za-z0-9 ]+@@@//;
  	print CHANGELOG $_."\n";
}

print CHANGELOG "\n</ul>\n";
close CHANGELOG;
print " - Found " . @trackerLines . " trackers.\n";

# Search through the changes file trimming down text and adjusting URLs
open CHANGES, "../../xdocs/changes.xml";
while (<CHANGES>) {
	$page .= $_ . "\n";
}
close CHANGES;

$page =~ s/.*\<body\>//s;
$page =~ s/\s*<\/release>.*//s;
$page =~ s/.*\<release[ \"\.A-Za-z0-9\=]*\>\S*\n//s;

open WHATSNEW, ">work/whatsnew.txt";
@data = split(/\n/, $page);
my $changeCount = 0;
for (@data) {
	s/^\s*//;
	s/\s*$//;
	if (!/^<action/) {
		next;
	}
   	$changeCount++;

	my $dev = $_;
	$dev =~ s/.*dev="([A-Za-z0-9 \-]*)".*/$1/;
	my $type = $_;
	$type =~ s/.*type="([A-Za-z0-9 \-]*)".*/$1/;
	my $issue = $_;
	$issue =~ s/.*issue="([A-Za-z0-9 \-]*)".*/$1/;
	my $desc = $_;
	$desc =~ s/.*>(.*)<\/action>/$1/;

	my $result = "<li><img alt=\"${type}\" title=\"${type}\" src=\"http://pcgen.sourceforge.net/autobuilds/images/${type}.gif\"> " .
		$desc . ". Fixes <a href=\"http://sourceforge.net/support/tracker.php?aid=${issue}\">${issue}</a> (" .
		"<a href=\"http://pcgen.sourceforge.net/autobuilds/team-list.html#${dev}\">${dev}</a>)</li>";
   	print WHATSNEW $result . "\n";
}
close WHATSNEW;
print " - Found " . $changeCount . " changes.\n";
