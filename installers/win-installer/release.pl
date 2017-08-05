#!/usr/bin/perl

# release.pl
# ==========
#
# This script prepare two release packages for PCGEN: pcgen_full and pcgen_partial and
# put the files in the proper directories for the NSIS script that build the Windows
# installer.
#


use strict;
use warnings;
use Readonly;
use English;

use Carp;
use IO::Handle;
use File::Path qw( mkpath rmtree );
use File::Copy::Recursive qw( fcopy rcopy );
use File::Find qw( find );
use Archive::Zip  qw( :ERROR_CODES );

# Make sure that IO functions throw exception on failure
#use Fatal qw( mkpath rmtree fcopy unlink );
use Fatal qw( mkpath rmtree unlink );

use vars qw($SRC_BRANCH $DEST_BASE_FOLDER);
# ------------------------------------------
# Application control
# ------------------------------------------

Readonly my $CREATE_ZIP         => 1;
Readonly my $SEPERATE_ALPHA     => 0;

# ------------------------------------------
# Release version identification
# TODO: This stuff should be being read (or calced) from PCGenProp.properties
# ------------------------------------------

my $VER_NUMBER_FULL    = '5.11.0';
if ($#ARGV >= 0) {
	$VER_NUMBER_FULL = $ARGV[0];
}

my $VER_NUMBER         = $VER_NUMBER_FULL;
my $VER_NUMBER_SUFIX = '';
$VER_NUMBER =~ s/\.//g;
my $index = index($VER_NUMBER, "RC");
if ($index > 0) {
	$VER_NUMBER_SUFIX = substr($VER_NUMBER, $index);
	$VER_NUMBER = substr($VER_NUMBER, 0, $index);
}

Readonly my $RELEASE_NAME       => "pcgen$VER_NUMBER$VER_NUMBER_SUFIX";



# ==========================================
# Local Overide of file locations 
# ==========================================
# Main folder for the source files
$SRC_BRANCH         = 'D:/eclipse/pcgen';

# Destination folder for the release files
$DEST_BASE_FOLDER   = "$SRC_BRANCH/../release";

eval { require "includes/localpaths.pl"};
if ($@) {
  print "$@\n";
  print "Default file locations will be used.\n\n";
  print "includes/localpaths.pl should look something like\n";
  print "#!/usr/bin/perl\n";
  print "\$SRC_BRANCH = 'C:/Projects/pcgen-release/pcgen';\n";
  print "\$DEST_BASE_FOLDER   = '\$SRC_BRANCH/../../release';\n\n";
}
print "=================\n";
print "Paths in use are:\n";
print "\$SRC_BRANCH is $SRC_BRANCH\n";
print "\$DEST_BASE_FOLDER is $DEST_BASE_FOLDER\n";
print "=================\n\n";

# ------------------------------------------
# Source files definitions
# ------------------------------------------

# Release notes
Readonly my $SRC_RELEASE_NOTES  => "$SRC_BRANCH/installers/release-notes/pcgen-release-notes-$VER_NUMBER$VER_NUMBER_SUFIX.html";

# Sub folders needed (there are variables for these
# since they are all part of different repository)
Readonly my $SRC_PCGEN          => "$SRC_BRANCH";
Readonly my $SRC_DATA           => "$SRC_BRANCH/data";
Readonly my $SRC_OUTPUTSHEETS   => "$SRC_BRANCH/outputsheets";
Readonly my $SRC_DOCS           => "$SRC_BRANCH/docs";
Readonly my $SRC_NSIS           => "$SRC_BRANCH/installers/win-installer";

Readonly my $SRC_NSIS_LICENCE_FILE => "$SRC_NSIS/PCGenLicense.txt";

Readonly my $SRC_LOGGING_PROP_FILE => "$SRC_PCGEN/logging.properties";

# ------------------------------------------
# Destination folder and file information
# ------------------------------------------

Readonly my $DEST_FULL_FOLDER
    => "$DEST_BASE_FOLDER/$RELEASE_NAME" . '_full';

Readonly my $DEST_PARTIAL_FOLDER
    => "$DEST_BASE_FOLDER/$RELEASE_NAME" . '_partial';

Readonly my $DEST_ALPHA_FOLDER
    => "$DEST_BASE_FOLDER/$RELEASE_NAME" . '_alpha';

Readonly my $DEST_NSIS_BASE_FOLDER
    => "$DEST_BASE_FOLDER/nsis_dir/PCGen_$VER_NUMBER$VER_NUMBER_SUFIX". q{b};
Readonly my $DEST_NSIS_OPTION_FOLDER
    => "$DEST_BASE_FOLDER/nsis_dir/PCGen_$VER_NUMBER$VER_NUMBER_SUFIX". q{c};

Readonly my $DEST_ZIP           => $DEST_BASE_FOLDER;
Readonly my $DEST_FULL_ZIP_FILE
    => "$DEST_ZIP/$RELEASE_NAME" . '_full.zip';
Readonly my $DEST_PARTIAL_ZIP_FILE
    => "$DEST_ZIP/$RELEASE_NAME" . '_partial.zip';
Readonly my $DEST_ALPHA_ZIP_FILE
    => "$DEST_ZIP/$RELEASE_NAME" . '_alpha.zip';

# ------------------------------------------
# Files and directories that must always
# be skiped.
# ------------------------------------------

Readonly my @FILES_TO_SKIP => (
    # directories and sub-directories
    qr{ [/] .git [/]             }xmsi,  #

    # files
    qr{ [/] \.\#        [^/]* \z}xms,   # Files begining with .# (CVS conflict and deleted files)
);



# ==========================================
# Real work starts here
# ==========================================

# print immediately instead of waiting for a \n
*STDOUT->autoflush();

print "Removing old and creating new directories...\n";

# Remove existing directories if present
if ( -e $DEST_FULL_FOLDER ) {
    rmtree($DEST_FULL_FOLDER);
}

if ( -e $DEST_PARTIAL_FOLDER ) {
    rmtree($DEST_PARTIAL_FOLDER);
}

if ( -e $DEST_ALPHA_FOLDER ) {
    rmtree($DEST_ALPHA_FOLDER);
}

if ( -e $DEST_NSIS_BASE_FOLDER ) {
    rmtree($DEST_NSIS_BASE_FOLDER);
}

if ( -e $DEST_NSIS_OPTION_FOLDER ) {
    rmtree($DEST_NSIS_OPTION_FOLDER);
}

# Remove existing zip files if present
if ($CREATE_ZIP) {
    if ( -e $DEST_FULL_ZIP_FILE ) {
        unlink $DEST_FULL_ZIP_FILE;
    }

    if ( -e $DEST_PARTIAL_ZIP_FILE ) {
        unlink $DEST_PARTIAL_ZIP_FILE;
    }
}

# Creation of the destination folders (mkpath created the entire path is one shot)
for my $dir_name qw( characters data docs lib outputsheets plugins preview system ) {
    mkpath("$DEST_FULL_FOLDER/$dir_name");
    mkpath("$DEST_PARTIAL_FOLDER/$dir_name");
    mkpath("$DEST_NSIS_BASE_FOLDER/$dir_name");
}

if($SEPERATE_ALPHA) {
    mkpath("$DEST_ALPHA_FOLDER/data");
}

# Generate the publisher/data list for the Windows installer script 

print "Generating the Windows installer publisher list...\n";
do 'gendatalist.pl' or die "Failed to generate publisher list: $@";
 
# Create the Windows Installer constants 

print "Create Installer Constants file...\n";

my $NSIS_CONSTANTS = "includes/constants.nsh";
open NSISC, ">$NSIS_CONSTANTS " or die "can't open $NSIS_CONSTANTS  $!";
print NSISC "; Constants file generated by release.pl\n";
print NSISC "!define SIMPVER \"$VER_NUMBER$VER_NUMBER_SUFIX\"\n";
print NSISC "!define LONGVER \"$VER_NUMBER_FULL\"\n";
print NSISC "!define OutDir \"$DEST_BASE_FOLDER\"\n";
print NSISC "!define SrcDir \"$DEST_BASE_FOLDER/nsis_dir\"\n";
close(NSISC);

 
# Copy the Windows Installer local files over

print "Copy Windows local files...\n";

rcopy( "$SRC_NSIS/Local", "$DEST_BASE_FOLDER/nsis_dir/Local" );

# Copy the release notes over

print "Release notes...\n";

if ( ! -e $SRC_RELEASE_NOTES) {
    # We need release notes. We find them or die trying since they are far to
    # easy to forget.
    die "Cannot find $SRC_RELEASE_NOTES: $OS_ERROR";
}
else {
    fcopy( $SRC_RELEASE_NOTES, $DEST_FULL_FOLDER );
    fcopy( $SRC_RELEASE_NOTES, $DEST_PARTIAL_FOLDER );
    fcopy( $SRC_RELEASE_NOTES, $DEST_NSIS_BASE_FOLDER );
    if($SEPERATE_ALPHA) {
        fcopy( $SRC_RELEASE_NOTES, $DEST_ALPHA_FOLDER );
    }
}

# Copy the logging properties file
{
    print "pcgen directory... \n";

    fcopy( $SRC_LOGGING_PROP_FILE, $DEST_FULL_FOLDER );
    fcopy( $SRC_LOGGING_PROP_FILE, $DEST_PARTIAL_FOLDER );
    fcopy( $SRC_LOGGING_PROP_FILE, $DEST_NSIS_BASE_FOLDER );
    if($SEPERATE_ALPHA) {
        fcopy( $SRC_LOGGING_PROP_FILE, $DEST_ALPHA_FOLDER );
    }
}

# Copy the binaries (the .jar and scripts)
{
    print "pcgen/bin directory... ";

    my %copied_file_for = copy_distro_files({
        source                          => "$SRC_PCGEN/code/bin",

        full_destination                => $DEST_FULL_FOLDER,
        full_files_to_skip_ref          => [ qr{[/] pcgen-tests\.jar \z}xmsi ],

        partial_destination             => $DEST_PARTIAL_FOLDER,

        other_copies_ref => [
            {   destination             => $DEST_NSIS_BASE_FOLDER,
                files_to_skip_ref       => [ qr{[/] pcgen-tests\.jar \z}xmsi ],
            },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and $copied_file_for{other} files copied]\n";
}

# Copy the lib files
{
    print "pcgen/lib directory... ";

    my %copied_file_for = copy_distro_files({
        source                      => "$SRC_PCGEN/lib",

        full_destination            => "$DEST_FULL_FOLDER/lib",
        full_files_to_skip_ref      => [
            # directory
            qr{ [/] javancss [/] }xmsi,

            # files
            qr{ [/]                                 # Start by...
                (?: javacc | junit | xmlunit )      # ... javacc, junit or xmlunit
                [^/]* \z                            # it is a file name, not a directory
            }xmsi,
        ],

        partial_destination         => "$DEST_PARTIAL_FOLDER/lib",
        partial_files_to_keep_ref   => [
            # directory
            qr{ [/] cobra [/] }xmsi, 			# Needed for the character sheet

            # files
            qr{ [/]
                (?: jep |                           # Needed for the formulas
                    wraplf | 						# New dependency with 5.9.4
                    commons-lang ) 					# New dependency with 5.13.x
                [^/]* \z                            # It's a file since there is no /
            }xmsi,
        ],

        other_copies_ref => [
            # In the installation base folder, we have jep, spring, cobra etc
            {   destination         => "$DEST_NSIS_BASE_FOLDER/lib",
                files_to_skip_ref   => [
                    # files - skip jars installed using other options and testing only jars
                    qr{ [/]
                        (?:  skinlf | fop | jdom
                        	| javacc | junit | xmlunit | objenesis | clover | easymock | emma | hamcrest  )
                        [^/]* \z
                    }xmsi,
                ]
            },
            # For the pdf export, we need the fop, and jdom librairies
            {   destination         => "$DEST_NSIS_OPTION_FOLDER/plugin/pdf/lib",
                files_to_keep_ref   => [
                    # files
                    qr{ [/]
                        (?: fop | jdom )
                        [^/]* \z
                    }xmsi,
                ],
            },
            # In the skin folder option, we take all the other librairies
            {   destination         => "$DEST_NSIS_OPTION_FOLDER/plugin/skin/lib",
                files_to_keep_ref   => [
                    # files
                    qr{ [/]
                        (?: skinlf  )
                        [^/]* \z
                    }xmsi,
                ],
            },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and "
        . "$copied_file_for{other} files copied]\n";
}

# Copy the plug-in files
{
    print "pcgen/plugins... ";

    my %copied_file_for = copy_distro_files({
        source                      => "$SRC_PCGEN/plugins",

        full_destination            => "$DEST_FULL_FOLDER/plugins",

        partial_destination         => "$DEST_PARTIAL_FOLDER/plugins",
        partial_files_to_keep_ref   => [
            # directories
            qr{ [/] (?: lstplugins
                      | bonusplugins
                      | outputplugins
                      | jepplugins
                      | preplugins
                      | systemlstplugins
                    ) [/] }xmsi,

            # file
            qr{ [/] CharacterSheet\.jar                  \z }xmsi,
        ],

        other_copies_ref => [
            {   destination         => "$DEST_NSIS_BASE_FOLDER/plugins",
                files_to_keep_ref   => [
                    # directories
            qr{ [/] (?: lstplugins
                      | bonusplugins
                      | outputplugins
                      | jepplugins
                      | preplugins
                      | systemlstplugins
                    ) [/] }xmsi,

                    # file
                    qr{ [/] CharacterSheet\.jar                  \z }xmsi,
                ],
            },
            {   destination         => "$DEST_NSIS_OPTION_FOLDER/plugin/gmgen/plugins",
                files_to_skip_ref   => [
                    # directories
            qr{ [/] (?: lstplugins
                      | bonusplugins
                      | outputplugins
                      | jepplugins
                      | preplugins
                      | systemlstplugins
                    ) [/] }xmsi,

                    # file
                    qr{ [/] CharacterSheet\.jar                  \z }xmsi,
                ],
            },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and "
        . "$copied_file_for{other} files copied]\n";
}

# Copy the system files
{
    print "pcgen/system... ";

    my %copied_file_for = copy_distro_files({
        source                      => "$SRC_PCGEN/system",

        full_destination            => "$DEST_FULL_FOLDER/system",

        partial_destination         => "$DEST_PARTIAL_FOLDER/system",

        other_copies_ref => [
            {   destination         => "$DEST_NSIS_BASE_FOLDER/system", },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and "
        . "$copied_file_for{other} files copied]\n";
}

# Copy the documentation files
{
    print "pcgen/docs... ";

    my %copied_file_for = copy_distro_files({
        source                      => $SRC_DOCS,

        full_destination            => "$DEST_FULL_FOLDER/docs",

        partial_destination         => "$DEST_PARTIAL_FOLDER/docs",

        other_copies_ref => [
            {   destination         => "$DEST_NSIS_BASE_FOLDER/docs", },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and "
        . "$copied_file_for{other} files copied]\n";
}

# Copy the output sheet files
{
    print "pcgen/outputsheets... ";

    my %copied_file_for = copy_distro_files({
        source                      => $SRC_OUTPUTSHEETS,

        full_destination            => "$DEST_FULL_FOLDER/outputsheets",

        partial_destination         => "$DEST_PARTIAL_FOLDER/outputsheets",
        partial_files_to_skip_ref   => [ qr{ [/] pdf [/] }xmsi ],

        other_copies_ref => [
            {   destination         => "$DEST_NSIS_BASE_FOLDER/outputsheets",
                files_to_skip_ref   => [ qr{ [/] pdf [/] }xmsi ],
            },
            {   destination         => "$DEST_NSIS_OPTION_FOLDER/plugin/pdf/outputsheets",
                files_to_keep_ref   => [ qr{ [/] pdf [/] }xmsi ],
            },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and "
        . "$copied_file_for{other} files copied]\n";
}

# Copy the preview files
{
    print "pcgen/preview... ";

    my %copied_file_for = copy_distro_files({
        source                      => "$SRC_PCGEN/preview",

        full_destination            => "$DEST_FULL_FOLDER/preview",

        partial_destination         => "$DEST_PARTIAL_FOLDER/preview",

        other_copies_ref => [
            {   destination         => "$DEST_NSIS_BASE_FOLDER/preview", },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and "
        . "$copied_file_for{other} files copied]\n";
}

# Copy the data files
{
    print "pcgen/data... ";

    my %copied_file_for = copy_distro_files({
        source                      => $SRC_DATA,

        full_destination            => "$DEST_FULL_FOLDER/data",

        partial_destination         => "$DEST_PARTIAL_FOLDER/data",

        other_copies_ref => [
            {   destination         => "$DEST_NSIS_BASE_FOLDER/data",
                files_to_keep_ref   => [ qr{ [/] customsources [/]   }xmsi,
                                         qr{ [/] homebrew [/]      }xmsi,
                                         qr{ [/] publisher_logos [/] }xmsi, ],
            },
            {   destination         => "$DEST_NSIS_OPTION_FOLDER/data",
                files_to_skip_ref   => [ qr{ [/] customsources [/]   }xmsi,
                                         qr{ [/] homebrew [/]      }xmsi,
                                         qr{ [/] publisher_logos [/] }xmsi, ],
            },
        ],
    });

    print "[$copied_file_for{full}, $copied_file_for{partial} and "
        . "$copied_file_for{other} files copied]\n";
}

# Do we need a seperate alpha package?
if ($SEPERATE_ALPHA) {
    print "Moving pcgen/data/alpha... ";

    # Copy the alpha folder files from the full package to the alpha package
    my $num_of_files_and_dirs
        = rcopy( "$DEST_FULL_FOLDER/data/alpha", "$DEST_ALPHA_FOLDER/data/alpha" );

    # Remove the alphe (now empty) folder from the full package
    rmtree("$DEST_FULL_FOLDER/data/alpha");

    # Remove the alpha folder from the partial package
    rmtree("$DEST_PARTIAL_FOLDER/data/alpha");

    print "[$num_of_files_and_dirs dirs and files moved]\n";
}

# Creation of the full package zip file
if ($CREATE_ZIP) {
    my $zip_full = Archive::Zip->new()
        or die "Can't create zip object";

    print "Creating full zip file... ";

    if($zip_full->addTree( $DEST_FULL_FOLDER, $RELEASE_NAME ) != AZ_OK) {
        die "Can't add $DEST_FULL_FOLDER to $RELEASE_NAME";
    };

    if($zip_full->writeToFileNamed($DEST_FULL_ZIP_FILE) != AZ_OK) {
        die "Can't create $DEST_FULL_ZIP_FILE";
    }

    print "done\n";

    my $zip_partial = Archive::Zip->new()
        or die "Can't create zip object";

    print "Creating partial zip file... ";

    if($zip_partial->addTree( $DEST_PARTIAL_FOLDER, $RELEASE_NAME ) != AZ_OK) {
        die "Can't add $DEST_PARTIAL_FOLDER to $RELEASE_NAME";
    }


    if($zip_partial->writeToFileNamed($DEST_PARTIAL_ZIP_FILE) != AZ_OK) {
        die "Can't create $DEST_PARTIAL_ZIP_FILE";
    }

    print "done\n";

    if($SEPERATE_ALPHA) {
        my $zip_alpha = Archive::Zip->new()
            or die "Can't create zip object";

        print "Creating alpha zip file... ";

        if($zip_alpha->addTree( $DEST_ALPHA_FOLDER, $RELEASE_NAME ) != AZ_OK) {
            die "Can't add $DEST_ALPHA_FOLDER to $RELEASE_NAME";
        }


        if($zip_alpha->writeToFileNamed($DEST_ALPHA_ZIP_FILE) != AZ_OK) {
            die "Can't create $DEST_ALPHA_ZIP_FILE";
        }

        print "done\n";
    }

}

# Copy the PCGenLicense.txt for the NSIS installer
if ($DEST_NSIS_BASE_FOLDER) {
    print 'Copying the NIS licence file...';

    fcopy( $SRC_NSIS_LICENCE_FILE, "$DEST_NSIS_BASE_FOLDER/docs/acknowledgments" );

    print "done\n";
}


# ==========================================
# Utility functions
# ==========================================

# ------------------------------------------
# copy_distro_files
#
# This subroutines walk a source directory and copy files into
# a sery of destination directories according to the list of
# dir and file patterns.
#
# The list of patterns to skip are tested againts $File::Find::name so
# it is possible to use both dir and file names.
#
# Parameters:
#
#   source                      Path to the source directory (string)
#
#   full_destination            Path to the destination directory for
#                               the full distribution package (string)
#   full_files_to_skip_ref      Files to skip for the full package
#                               (reference to a list of patterns)
#   full_files_to_keep_ref      Files to keep (reference to a list of patterns)
#
#   partial_destination         Path to the destination directory for
#                               the full distribution package (string)
#   partial_files_to_skip_ref   Files to skip for the full package
#                               (reference to a list of patterns)
#   partial_files_to_keep_ref   Files to keep (reference to a list of patterns)
#
#   other_copies_ref            Array reference of other series of files to copy
#                               (Hash reference with the following entries)
#       destination             Path to the destination (string)
#       files_to_skip_ref       Files to skip (reference to a list of patterns)
#       files_to_keep_ref       Files to keep (reference to a list of patterns)
#
#   Note: When both full_destination and partial_destination are givien,
#         whatever is excluded by full is also de-facto excluded by partial.
#
#   Return a list with the following pairs:
#
#       full_count      => number of files copied for the full distribution
#       partial_count   => number of files copied for the partial distribution

{
    my $_arg_ref;

    my $_full_file_count;
    my $_partial_file_count;
    my $_other_file_count;

    my @_full_files_to_skip;
    my @_full_files_to_keep;
    my @_partial_files_to_skip;
    my @_partial_files_to_keep;

    my @_other_copies_ref;

    sub copy_distro_files {
        ($_arg_ref) = @_;

        $_full_file_count       = 0;
        $_partial_file_count    = 0;
        $_other_file_count      = 0;

        # Verify that we were called in list context
        croak q{copy_distro_files must be called in list context}
            if !wantarray;

        # No source, no use
        croak q{The source parameter is mandantory}
            if !exists $_arg_ref->{source};

        # Check the destination
        if ( !(   exists $_arg_ref->{full_destination}
               || exists $_arg_ref->{partial_destination}
               || exists $_arg_ref->{other}
              )
        ) {
            croak q{I need at least one destination to work};
        }


        # Set up the variables for the find function

        @_full_files_to_skip = ();
        if ( exists $_arg_ref->{full_files_to_skip_ref} ) {
            push @_full_files_to_skip, @{ $_arg_ref->{full_files_to_skip_ref} };
        }

        @_full_files_to_keep = ();
        if ( exists $_arg_ref->{full_files_to_keep_ref} ) {
            push @_full_files_to_skip, @{ $_arg_ref->{full_files_to_keep_ref} };
        }

        @_partial_files_to_skip = ();
        if ( exists $_arg_ref->{partial_files_to_skip_ref} ) {
            push @_partial_files_to_skip, @{ $_arg_ref->{partial_files_to_skip_ref} };
        }

        @_partial_files_to_keep = ();
        if ( exists $_arg_ref->{partial_files_to_keep_ref} ) {
            push @_partial_files_to_keep, @{ $_arg_ref->{partial_files_to_keep_ref} };
        }

        # Black f**king magic time
        if (exists $_arg_ref->{other_copies_ref} ) {
            @_other_copies_ref = ();
            for my $set_ref ( @{ $_arg_ref->{other_copies_ref} } ) {
                my %new_set;
                $new_set{destination}       = $set_ref->{destination};

                if ( exists $set_ref->{files_to_skip_ref} ) {
                    push @{ $new_set{files_to_skip_ref} }, @{ $set_ref->{files_to_skip_ref} };
                }

                if ( exists $set_ref->{files_to_keep_ref} ) {
                    push @{ $new_set{files_to_keep_ref} }, @{ $set_ref->{files_to_keep_ref} };
                }

                push @_other_copies_ref, \%new_set;
            }
        }

        find( \&cdf_wanted, $_arg_ref->{source} );

        return ( full           => $_full_file_count,
                 partial        => $_partial_file_count,
                 other          => $_other_file_count,
        );
    }

    sub cdf_wanted {
        # Skip directories, we only deal with files
        return if ! -f $File::Find::name;

        # We remove the source from the name (we check on anything under the source dir)
        my $name_for_check = $File::Find::name;
        $name_for_check =~ s{ $_arg_ref->{source} }{}xms
            or die "Something is wrong with $name_for_check";

        # We check for the global @FILES_TO_SKIP first
        FILES_TO_SKIP:
        for my $regex (@FILES_TO_SKIP) {
            return if $name_for_check =~ $regex;
        }

        my $full_copy = 1;

        # Full distro
        if ( exists $_arg_ref->{full_destination} ) {
            # Filter files
            FILES_TO_SKIP:
            for my $regex (@_full_files_to_skip) {
                if ( $name_for_check =~ $regex ) {
                    $full_copy = 0;
                    last FILES_TO_SKIP;
                }
            }

            if( $full_copy && scalar @_full_files_to_keep ) {
                # reverse thinking here, we only copy the files
                # that match one of the regex.
                my $found = 0;

                FIND_MATCH:
                for my $regex (@_full_files_to_keep) {
                    if ( $name_for_check =~ $regex ) {
                        $found = 1;
                        last FIND_MATCH;
                    }
                }

                $full_copy = $found;
            }

            # Copy the full files
            if($full_copy) {
                my $new_dest_full = $File::Find::name;
                $new_dest_full =~ s{ \A $_arg_ref->{source} }
                                   {$_arg_ref->{full_destination}}xms
                    or die "Can't create proper destination for $File::Find::name";

                fcopy( $File::Find::name, $new_dest_full );

                $_full_file_count++;
            }
        }

        # Partial distro (we assume that partial files are all
        # part of the full distro if full_destination is provided along
        # with partial_destination)
        if ( $full_copy && exists $_arg_ref->{partial_destination} ) {
            my $partial_copy = 1;

            # Filter files
            FILES_TO_SKIP:
            for my $regex (@_partial_files_to_skip) {
                if ($name_for_check =~ $regex) {
                    $partial_copy = 0;
                    last FILES_TO_SKIP;
                }
            }

            if($partial_copy && scalar @_partial_files_to_keep) {
                # reverse thinking here, we only copy the files
                # that match one of the regex.
                my $found = 0;

                FIND_MATCH:
                for my $regex (@_partial_files_to_keep) {
                    if ( $name_for_check =~ $regex ) {
                        $found = 1;
                        last FIND_MATCH;
                    }
                }

                $partial_copy = $found;
            }

            # Copy the partial files
            if ($partial_copy) {
                my $new_dest_full = $File::Find::name;
                $new_dest_full =~ s{ \A $_arg_ref->{source} }
                                   {$_arg_ref->{partial_destination}}xms
                    or die "Can't create proper destination for $File::Find::name";

                fcopy( $File::Find::name, $new_dest_full );

                $_partial_file_count++;
            }
        }

        # Other sets of files to copy
        for my $set_ref (@_other_copies_ref) {
            my $must_copy = 1;

            # Filter files to skip
            if ( exists $set_ref->{files_to_skip_ref} ) {
                FILES_TO_SKIP:
                for my $regex ( @{ $set_ref->{files_to_skip_ref} } ) {
                    if ($name_for_check =~ $regex) {
                        $must_copy = 0;
                        last FILES_TO_SKIP;
                    }
                }
            }

            # Filter files to keep
            if ( $must_copy && exists $set_ref->{files_to_keep_ref} ) {
                my $found = 0;

                FILE_TO_KEEP:
                for my $regex ( @{ $set_ref->{files_to_keep_ref} } ) {
                    if ( $name_for_check =~ $regex ) {
                        $found = 1;
                        last FILE_TO_KEEP;
                    }
                }

                $must_copy = $found;
            }

            # Should we copy after all?
            if ($must_copy) {
                my $new_dest_full = $File::Find::name;
                $new_dest_full =~ s{ \A $_arg_ref->{source}}
                                   {$set_ref->{destination}}xms
                    or die "Can't create proper destination for $File::Find::name";
#                warn " -->$File::Find::name\n";
#                warn " ++>$new_dest_full\n";
                fcopy( $File::Find::name, $new_dest_full );

                $_other_file_count++;
            }
        }

        return;
    }

}
