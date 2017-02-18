
Project: pcgen
Module:  win-installer
Created: 2004.10.02

This module will hold the different scripts needed to build the PCGen Windows installer.

List of files:
==============

@readme.txt			The file you are reading
PCGenLicense.txt		Licences text that is displayed during the installations
PCGen_alpha_install.nsi		NIS install script for the Alpha datasets that are included
				with the stable releases
PCGen_install.nsi		NIS install script used to build PCGen_win_install.exe
PCGScry.nsi			Old NIS install script for PCGScry
release.pl			Perl script to automate the packaging of the ZIP files
				(PCGen_full.zip and PCGen_partial.zip) and that prepare
				the files needed for the NIS install scripts to run
