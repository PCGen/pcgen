#!/usr/bin/perl

# gendatalist.pl
# ==============
#
# This script prepares the list of data directories and publishers
# for the NSIS script that builds the Windows installer.
# Author: James Dempsey October 2006
#


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
opendir(DIR, $DATA_ROOT) || die "can't opendir $DATA_ROOT: $!";
@basedirlist = grep !/^\.\.?$/, readdir(DIR);
closedir DIR;
# @basedirlist = qw(d20ogl alpha);
$basedir{'d20ogl'} = 'd20OGL';
$basedir{'alpha'} = 'Alpha';

# The list of publishers - add an entry here to correct a reported missing publisher
$pub{'12_to_midnight'} = '12 to Midnight';
$pub{'alderac_entertainment_group'} = 'Alderac Entertainment Group';
$pub{'alderac_ent_group'} = 'Alderac Entertainment Group';
$pub{'alea_publishing_group'} = 'Alea Publishing Group';
$pub{'alluria_publishing'} = 'Alluria Publishing';
$pub{'atlas_games'} = 'Atlas Games';
$pub{'auran_d20'} = 'Auran d20';
$pub{'avalanche_press'} = 'Avalanche Press';
$pub{'badaxe_games'} = 'Bad Axe Games';
$pub{'bards_and_sages'} = 'Bards and Sages';
$pub{'bastion_press'} = 'Bastion Press';
$pub{'battlefield_press'} = 'Battlefield Press';
$pub{'behemoth3'} = 'Behemoth3';
$pub{'big_finger_games'} = 'Big Finger Games';
$pub{'bloodstone_press'} = 'Bloodstone Press';
$pub{'blue_devil_games'} = 'Blue Devil Games';
$pub{'broken_ruler_games'} = 'Broken Ruler Games';
$pub{'crafty_games'} = 'Crafty Games';
$pub{'creativemountaingames'} = 'Creative Mountain Games';
$pub{'distant_horizons_games'} = 'Distant Horizons Games';
$pub{'doghouse_rules'} = 'Dog House Rules';
$pub{'dragonwing_games'} = 'DragonWing Games';
$pub{'dreamscarred_press'} = 'Dreamscarred Press';
$pub{'en_publishing'} = 'EN Publishing';
$pub{'everyman_gaming'} = 'Everyman Gaming';
$pub{'fantasy_community_council'} = 'Fantasy Community Council';
$pub{'fantasy_flight_games'} = 'Fantasy Flight Games';
$pub{'gallantry_productions'} = 'Gallantry Productions';
$pub{'goodman_games'} = 'Goodman Games';
$pub{'green_ronin'} = 'Green Ronin';
$pub{'lions_den_press'} = 'Lions Den Press';
$pub{'malhavoc_press'} = 'Malhavoc Press';
$pub{'minotaur_games'} = 'Minotaur Games';
$pub{'mongoose'} = 'Mongoose';
$pub{'mongoose_publishing'} = 'Mongoose Publishing';
$pub{'msrd'} = 'MSRD';
$pub{'mythic_dream_studios'} = 'Mythic Dreams Studios';
$pub{'necromancer_games'} = 'Necromancer Games';
$pub{'nitehawk_interactive'} = 'Nitehawk Interactive Games';
$pub{'pandahead'} = 'Pandahead';
$pub{'paradigm_concepts'} = 'Paradigm Concepts Inc';
$pub{'paizo'} = 'Paizo Publishing';
$pub{'parents_basement_games'} = 'Parents Basement Games';
$pub{'pcgen'} = 'PCGen OGL';
$pub{'pinnacle_entertainment'} = 'Pinnacle Entertainment';
$pub{'reality_deviant'} = 'Reality Deviant Publications';
$pub{'rite'} = 'Rite Publishing';
$pub{'rite_publishing'} = 'Rite Publishing';
$pub{'rpg_objects'} = 'RPG Objects';
$pub{'sagaborn'} = 'Sagaborn';
$pub{'secular_games'} = 'Secular Games';
$pub{'silven_publishing'} = 'Silven Publishing';
$pub{'silverthorne_games'} = 'Silverthorne Games';
$pub{'skirmisher_publishing'} = 'Skirmisher Publishing LLC';
$pub{'sovereign_press'} = 'Sovereign Press';
$pub{'srd'} = 'SRD';
$pub{'srd35'} = 'SRD35';
$pub{'st_cooley_publishing'} = 'S T Cooley Publishing';
$pub{'storm_bunny_studios'} = 'Storm Bunny Studios';
$pub{'super_genius_games'} = 'Super Genius Games';
$pub{'sword_and_sorcery_studios'} = 'Sword and Sorcery Studios';
$pub{'swords_edge_publishing'} = 'Swords Edge Publishing';
$pub{'the_game_mechanics'} = 'The Game Mechanics Inc';
$pub{'vigilance_press'} = 'Vigilance Press';
$pub{'wizards_of_the_coast'} = 'Wizards of the Coast';

# Non-publishers
$pub{'homebrew'} = 'Homebrew';
$pub{'conversion_support'} = 'Conversion Support';
$pub{'my_pathfinder_campaign'} = 'Pathfinder Homebrew';

# Open the script output file
my $script_file = 'includes/data.nsh';

print "Populating $script_file ...\n";

open SCRIPT, ">$script_file " or die "can't open $script_file  $!";
# Loop through each of the directories under data
foreach $dirname (@basedirlist)
{
	# Skip some folders we don't wish to offer
	if ($dirname eq 'homebrew' || $dirname eq 'readme.md' || $dirname eq 'zen_test' || $dirname eq 'customsources')
	{
		next;
	}

	# Read the files under the directory that do not have have names starting with "."
	$data_dir = $DATA_ROOT . $dirname;
	opendir(DIR, $data_dir) || die "can't opendir $data_dir: $!";
	@dirlist =  readdir(DIR);
	closedir DIR;
	@nondots = grep ( !/^[\.]/, @dirlist );

	# generate the Data category section
	print SCRIPT "SubSection \"$dirname\"\n";
	
	# Loop through the publisher directories adding a section for each
NAME:	foreach $filename (@nondots)
	{
		# Skip some folders we don't wish to distribute
		if ($filename eq 'homebrew' || $filename eq 'readme.md' || $filename eq 'pcgen_test_advanced' || $filename =~ /zen_test.*/)
		{
			next NAME;
		}

		$currDir = $data_dir . "/" . $filename;
		if (-f $currDir) 
		{
			# print "Skipping file $currDir ...\n";
			next NAME;
		}

		opendir(DIR, $currDir) || die "can't opendir $currDir: $!";
		@subdirlist =  readdir(DIR);
		closedir DIR;

		# Skip any pub directories that are empty (., ..)
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
			print STDERR "Unknown publisher \"$filename\" at $currDir - using directory name instead.\n";			
		}
		
		print SCRIPT "	Section \"$pubname\"\n";

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

		print SCRIPT "	SetOutPath \"\$INSTDIR\\\${APPDIR}\\data\\$dirname\\" . $filename . "\"\n";
		print SCRIPT "	File /r \"\${SrcDir}\\PCGen_\${SIMPVER}_opt\\data\\$dirname\\" . $filename . "\\*.*\"\n";

		print SCRIPT "	SectionEnd\n\n";
	}
	print SCRIPT "SubSectionEnd\n\n";
}
close(SCRIPT);
