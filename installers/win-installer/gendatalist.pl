#!/usr/bin/perl

# gendatalist.pl
# ==============
#
# This script prepares the list of data directories and publishers
# for the NSIS script that builds the Windows installer.
# Author: James Dempsey October 2006
#
# $Id: release.pl 1551 2006-10-29 03:21:16Z jdempsey $


use strict;
use warnings;
use Readonly;
use English;

my $DATA_ROOT = "../../data/";
my $data_dir;
my $filename;
my @nondots;
my @dirlist;
my %pub;
my @basedirlist;
my %basedir;
my $dirname;
my $pubname;
my $currDir;
my @subdirlist;

# The directories under data that will be included.
@basedirlist = qw(d20ogl alpha);
$basedir{'d20ogl'} = 'd20OGL';
$basedir{'alpha'} = 'Alpha';

# The list of publishers - add an entry here to correct a reported missing publisher
$pub{'12tomidnight'} = '12 to Midnight';
$pub{'alderacentertainmentgroup'} = 'Alderac Entertainment Group';
$pub{'alderacentgroup'} = 'Alderac Entertainment Group';
$pub{'aleapublishinggroup'} = 'Alea Publishing Group';
$pub{'atlasgames'} = 'Atlas Games';
$pub{'aurand20'} = 'Auran d20';
$pub{'avalanchepress'} = 'Avalanche Press';
$pub{'badaxegames'} = 'Bad Axe Games';
$pub{'bardsandsages'} = 'Bards and Sages';
$pub{'bastionpress'} = 'Bastion Press';
$pub{'battlefieldpress'} = 'Battlefield Press';
$pub{'behemoth3'} = 'Behemoth3';
$pub{'bigfingergames'} = 'Big Finger Games';
$pub{'bloodstonepress'} = 'Bloodstone Press';
$pub{'bluedevilgames'} = 'Blue Devil Games';
$pub{'craftygames'} = 'Crafty Games';
$pub{'creativemountaingames'} = 'Creative Mountain Games';
$pub{'distanthorizonsgames'} = 'Distant Horizon Games';
$pub{'doghouserules'} = 'Dog House Rules';
$pub{'dragonwinggames'} = 'DragonWing Games';
$pub{'dreamscarredpress'} = 'Dreamscarred Press';
$pub{'en_publishing'} = 'EN Publishing';
$pub{'fantasycommunitycouncil'} = 'Fantasy Community Council';
$pub{'fantasyflightgames'} = 'Fantasy Flight Games';
$pub{'gallantryproductions'} = 'Gallantry Productions';
$pub{'goodmangames'} = 'Goodman Games';
$pub{'greenronin'} = 'Green Ronin';
$pub{'lionsdenpress'} = 'Lions Den Press';
$pub{'malhavocpress'} = 'Malhavoc Press';
$pub{'mongoose'} = 'Mongoose';
$pub{'mongoosepublishing'} = 'Mongoose Publishing';
$pub{'msrd'} = 'MSRD';
$pub{'mythicdreamsstudios'} = 'Mythic Dreams Studios';
$pub{'necromancergames'} = 'Necromancer Games';
$pub{'nitehawkinteractivegames'} = 'Nitehawk Interactive Games';
$pub{'pandahead'} = 'Pandahead';
$pub{'paradigmconcepts'} = 'Paradigm Concepts Inc';
$pub{'paizo'} = 'Paizo Publishing';
$pub{'parentsbasementgames'} = 'Parents Basement Games';
$pub{'pcgen'} = 'PCGen OGL';
$pub{'pinnacleentertainment'} = 'Pinnacle Entertainment';
$pub{'realitydeviant'} = 'Reality Deviant Publications';
$pub{'rite'} = 'Rite Publishing';
$pub{'rpgobjects'} = 'RPG Objects';
$pub{'seculargames'} = 'Secular Games';
$pub{'silvenpublishing'} = 'Silven Publishing';
$pub{'silverthornegames'} = 'Silverthorne Games';
$pub{'skirmisherpublishing'} = 'Skirmisher Publishing LLC';
$pub{'sovereignpress'} = 'Sovereign Press';
$pub{'srd'} = 'SRD';
$pub{'srd35'} = 'SRD35';
$pub{'stcooleypublishing'} = 'S T Cooley Publishing';
$pub{'supergeniusgames'} = 'Super Genius Games';
$pub{'swordandsorcerystudios'} = 'Sword and Sorcery Studios';
$pub{'swordsedgepublishing'} = 'Swords Edge Publishing';
$pub{'thegamemechanics'} = 'The Game Mechanics Inc';
$pub{'vigilancepress'} = 'Vigilance Press';
$pub{'wizardsofthecoast'} = 'Wizards of the Coast';

# Open the script output file
my $script_file = 'includes/data.nsh';

open SCRIPT, ">$script_file " or die "can't open $script_file  $!";
# Loop through each of the directories under data
foreach $dirname (@basedirlist)
{
	# Read the files under the directory that do not have have names starting with "."
	$data_dir = $DATA_ROOT . $dirname;
	opendir(DIR, $data_dir) || die "can't opendir $data_dir: $!";
	@dirlist =  readdir(DIR);
	closedir DIR;
	@nondots = grep ( !/^[\.]/, @dirlist );

	# generate the Data category section
	print SCRIPT "SubSection \"$basedir{$dirname}\"\n";
	
	# Loop through the publisher directories adding a section for each
NAME:	foreach $filename (@nondots)
	{
		$currDir = $data_dir . "/" . $filename;
		opendir(DIR, $currDir) || die "can't opendir $currDir: $!";
		@subdirlist =  readdir(DIR);
		closedir DIR;

		# Skip any pub directories that are empty (., .., .svn only contents)		
		if (scalar(grep( !/^\..*$/, @subdirlist)) == 0)
		{
			next NAME;
		}

		if (defined($pub{$filename}))
		{
			$pubname = $pub{$filename};
		}
		else
		{
			$pubname = $filename;
			print STDERR "Unknown publisher \"$filename\" - using directory name instead.\n";			
		}
		
		print SCRIPT "	Section \"$pubname\"\n";

		if ($dirname eq 'd20ogl')
		{
			print SCRIPT "	SectionIn 1 2";
			# The SRD files get installed under some extra configs, so add those in
			if ($filename eq 'msrd')
			{
				print SCRIPT " 3 6";
			}
			if ($filename eq 'pcgen')
			{
				print SCRIPT " 3 4 5";
			}
			if ($filename eq 'srd')
			{
				print SCRIPT " 3 4";
			}
			if ($filename eq 'srd35' || $filename eq 'necromancergames')
			{
				print SCRIPT " 3 5";
			}
			if ($filename eq 'paizo')
			{
				print SCRIPT " 3";
			}
			print SCRIPT "\n";
		}
		elsif ($dirname eq 'permissioned')
		{
			print SCRIPT "	SectionIn 1 2\n";
		}
		else
		{
			print SCRIPT "	SectionIn 1";
			if ($filename eq 'paizo')
			{
				print SCRIPT " 2 3";
			}
			print SCRIPT "\n";
		}

		print SCRIPT "	SetOutPath \"\$INSTDIR\\\${APPDIR}\\data\\$dirname\\" . $filename . "\"\n";
		print SCRIPT "	File /r \"\${SrcDir}\\PCGen_\${SIMPVER}c\\data\\$dirname\\" . $filename . "\\*.*\"\n";

		print SCRIPT "	SectionEnd\n\n";
	}
	print SCRIPT "SubSectionEnd\n\n";
}
close(SCRIPT);
