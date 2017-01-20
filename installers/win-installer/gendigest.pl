#!/usr/bin/perl

# gendigest.pl
# ============
#
# This script generates SHA1 digests (checksums) for the release files.
# The checksums are placed in a SHA1-digests.txt file in the release folder
#
# args: DEST_FOLDER - Optionally override the default release folder name.
#


use strict;
use warnings;
use Readonly;
use English;

use Digest::SHA1;

# Define the release folder and allow it to be overriden
my $DEST_FOLDER   = 'E:/Projects/release';
if ($#ARGV >= 0) {
	$DEST_FOLDER = $ARGV[0];
}

my @files_found = <$DEST_FOLDER/*>;
my $file;
my $outputFileName = $DEST_FOLDER . "/SHA1-digests.txt";
print "Generating SHA1 digests to $outputFileName";
open (OUTPUT, ">$outputFileName") or die "Can't open '$outputFileName': $!";

# Loop through the files in the releas folder, only working on the release files.
foreach(@files_found) {
	next unless /\.(zip|exe|jar|dmg)$/;
	$file = $_;
	my $name = $file;
	$name =~ s/^.*\///;
	open(FILE, $file) or die "Can't open '$file': $!";
	binmode(FILE);
	print OUTPUT "SHA1 digest for $name:\n";
	print OUTPUT Digest::SHA1->new->addfile(*FILE)->hexdigest, " \n\n";
	close FILE;
}
close OUTPUT;
