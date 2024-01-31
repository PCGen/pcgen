# Test creating a new branch and seeing how vercel does with it.

![PCGenShot](https://user-images.githubusercontent.com/470400/67638917-5f6e8a80-f8c0-11e9-972b-7adf4c9126e7.png)

PCGen is a program designed to create and manage player characters in pen & paper games like D&D.
It works on Windows, Mac & Linux, basically anywhere the Java JDK works.
It will let you create a character under a system of rules, track its levels and abilities as you progress, inventory and spells.
It supports numerous game systems, most notably:

- D&D 3.5, 4.0, 5.0
- Pathfinder 1e
- Starfinder

# Table of Contents
1. [Installing From Release 6.08](#installing-from-release-608)

1. [Installing From Release 6.09 (Alpha)](#installing-from-release-609-alpha)

1. [PCGen Needs You](#pcgen-needs-you)

1. [The Old Wiki](#the-old-wiki)

1. [PCGen LST Tutorial](#pcgen-lst-tutorial)

1. [Basic Workflow](#basic-workflow)

1. [Development Setup](#development-setup)

1. [Essential Gradle Tasks](#essential-gradle-tasks)

# Installing From Release 6.08
1. Install Java.
   - JDK 11 is recommended and has long term support, later versions should also work. 10 and below are not supported.
   - To check if you have Java installed, see [Install Java](#install-java)
   - If you don't have it already, you can get it from [AdoptOpenJDK](https://adoptopenjdk.net/installation.html?variant=openjdk11&jvmVariant=hotspot).
   
1. Download and extract the full zip file from https://github.com/PCGen/pcgen/releases/latest.

1. You should now be able to run PCGen. The exact invocation depends on your operating system, but you should be able to either double-click to launch the file for your platform.
    - Windows: `pcgen.exe` (`pcgen.bat` for command-line users)
    - Linux: `pcgen.sh` 
    - Mac: `pcgen.jar` (or `pcgen.dmg` if it exists)
    
# Installing From Release 6.09 (Alpha)
> Note: Java does not need to be preinstalled with PcGen >6.09.05

## Using Zip bundle
1. Download and extract the full zip file from https://github.com/PCGen/pcgen/releases/ labled 6.09.xx.

1. You should now be able to run PCGen. The exact invocation depends on your operating system, but you should be able to either double-click to launch the file for your platform.
    - Windows: `pcgen.exe` (`pcgen.bat` for command-line users)
    - Linux: `pcgen.sh`
    - Mac: `pcgen.sh` (or `pcgen.dmg` if it exists. Launching .jar may throw java errors so generally avoid)

## Using installer (windows and mac only)
1. Download and extract the full installer from https://github.com/PCGen/pcgen/releases/ labled 6.09.xx. 
    - Windows: `pcgen-6.09.xx_win_install.exe`
    - Mac: `pcgen-6.09.xx.dmg` or `pcgen-6.09.xx.pkg`

1. Run installer and follow instruction
   - Windows: Open `pcgen-6.09.xx_win_install.exe`
   - Mac:
   - - `dmg`: Open `dmg` and drag into Applications. Right click on `PcGen` and click open.
   - - `pkg`: Right click and `pkg` and click open and click `open` on security warning due to application being unsigned. 

1. You should be able to launch PcGen as normal application.
   -  Mac: You may need to on first launch right click on application and then click `open`.

# PCGen Needs You

PCGen is an open source program driven by contributors, without your help no new fixes or content can be added.
If you can program Java or want to contribute to expanding the book support please consider joining our team.
Many of the original members have become inactive and we need new contributors to keep the project going.

To join our group:
- Join our [Discord](https://discord.gg/M7GH5BS)
- Post in the volunteers channel to get access to the [Slack](https://slack.com). A senior member will add you by email.
- Make an account on the [JIRA] bug tracker. See [CODE] and [DATA] issues. Work is tracked here to easily generate release notes.
- Review the [Basic Workflow](#basic-workflow) & [Development Setup](#development-setup) below to get started.

# The Old Wiki
The [old wiki](http://159.203.101.162/w/index.php) archives historical meetings, many design documents and useful dev information.
Browse it when you have time, it can provide some insight into certain parts of the architecture.

# PCGen LST Tutorial
Andrew has made a series of videos [explaining the LST files](https://www.youtube.com/watch?v=LhGkqdXNtOw&list=PLLa5A1qjBOPekqEC_R9BAZW-8q5IT-klM).
These are mainly targetted at new DATA contributors adding new books/content and fixing bugs.
You can of course ask questions in the discord or slack if you are unsure.
Programmers may want to review these if they work on the LST parsing or related systems.

# Basic Workflow

1. Get a bug from [JIRA] primarily from the [CODE] or [DATA] sections. Alternatively if you want to propose a new feature/change, make a JIRA entry to track it.
1. Create a branch in your fork of [PCGen] during development. It is good to name branches after ticket numbers, like fix_code_3444 or fix_data_3322.
1. Work until the feature or bug is finished. Add tests if needed, especially if new code added.
1. Push all work up into your copy of [PCGen]. Try to ensure build passes BEFORE submitting a pull request.
1. Submit pull request from your fork to master and respond to review by members.
1. Go back to first step.

# Development Setup

These steps will guide you to a basic setup for development.
These steps should work for Linux, Mac or Windows. Where steps differ it will be highlighted.
If you have trouble, feel free to ask in the discord or slack once you have joined.

### Running Commands
Anything `written like this` should be executed in a terminal.
For Windows this means opening the start menu and typing cmd.exe or Powershell. The latter is more modern if available.
For Linux or Mac, whatever default terminal you have is fine.

### Install Java
Check the installed version with:

    java -version

For 6.08 development you will want Java with a minimum version of 11.
For 6.09 development you will want Java with a minimum version of 17.
You can install the latest version from [AdoptOpenJDK](https://adoptopenjdk.net) regardless of your OS, please see instructions there.

### Install Git
Check the installed version with:

    git --version

Any version should do.
You can install git on debian machines:

    sudo apt-get install git

On Windows, [Git For Windows](https://gitforwindows.org) is a good choice. Download and install.
Be sure to install both the GUI & command line version. The default options are fine.

If you do not know about git, reading the first 3 or 4 chapters of [Pro Git](https://git-scm.com/book/en/v2)
will go a long way. It is designed about command line but all principles apply to the GUI version.

### Fork and Clone PCGen
Log in to github and go to [PCGen] in your browser.
Fork the project to have your own copy.
Clone the fork locally, if you use ssh for instance it should be:

    git clone git@github.com:USERNAME/pcgen.git

Where USERNAME is your github username.
This can be done on the command line, or else open the git GUI and clone from there.

### Stay Up To Date
Open a terminal inside the cloned pcgen project.
Run the following command:

    git remote add upstream https://github.com/PCGen/pcgen

This sets up the project for upstream rebasing, to keep you level with changes.
You can rebase the master with latest changes with the following. It can be done from GUI as well.

    git checkout master && git fetch upstream && git rebase master

### Get an IDE
This step is optional. You are free to program in what you prefer, these are several popular IDEs for Java.
If you are new we would suggest IntelliJ. Follow download/setup instructions then continue.
These IDEs have git and gradle plugins either out of box or that can be installed.
- [IntelliJ Community](https://www.jetbrains.com/idea)
- [Eclipse](https://www.eclipse.org)
- [Netbeans](https://netbeans.org)

Once setup, open your IDE of choice. You should be able to import the cloned fork.
Import the [PCGen] fork as a gradle project. From this point, you can work on the project.
For IntelliJ Community do: File > New > Project from Existing Sources ...
All of these IDEs have git and gradle plugins that can be used instead of commands.

# Essential Gradle Tasks

This is a __quick__ rundown on gradle. You can get more information from [gradle docs](https://gradle.org/guides).
Gradle is like make, it allows you to run commands to build projects.
The tasks can depend on one another and will ensure all dependencies are met before running.
These commands will be the same if you use a GUI to execute them.

Note: `./gradlew` indicates you are executing the gradle wrapper command binary that comes with PCGen's source tree.
This will automatically download and use the latest version of gradle. If you have gralde installed, you just
substitute `./gradlew` for `gradle` on the command line.

### See All Available Commands
    ./gradlew tasks

### Just (Re)Compile Java
    ./gradlew compileJava

### Build All Required Files
    ./gradlew assemble

### Run PCGen
    ./gradlew run

### Run Test Suite
    ./gradlew test

### Run Full Test Suite
Do this primarily __before__ pull requests.
This is almost exactly the command Travis runs to verify, if it fails locally you will fail the build and your PR will not be merged.

    ./gradlew clean build copyToOutput test compileSlowtest datatest pfinttest allReports buildDist

### Clean All Build Files
    ./gradlew clean

### Generate IntelliJ IDEA Project
    ./gradlew idea

[PCGen]: https://github.com/PCGen/pcgen
[JIRA]: https://pcgenorg.atlassian.net
[CODE]: https://pcgenorg.atlassian.net/projects/CODE/issues
[DATA]: https://pcgenorg.atlassian.net/projects/DATA/issues
