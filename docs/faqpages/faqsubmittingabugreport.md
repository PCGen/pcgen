# The JIRA issue tracker

The PCGen project uses a system named `JIRA`, located at [pcgenorg.atlassian.net](http://pcgenorg.atlassian.net), to track bug reports and feature requests (*issues*).

This page explains how to submit bug reports or feature requests for the PCGen project.

Note: JIRA has replaced the previous issue tracker at SourceForge, which is no longer used.

## Submitting an issue

 * Search JIRA to see if your issue has already been discussed.

    ![Searching in JIRA.](../images/jira/jira_04.png)

    You can enter search terms (i.e. *bardic music*) in the search box at the top right-hand side of the screen. A list of issues matching your search terms will be shown. Check this list to see if your issue has already been discussed.

    If your issue has already been discussed, you should consider adding a comment to the existing issue, instead of filing a duplicate issue.
 
 * Sign up for JIRA and log in. (See: [Account creation and log-in](#account-creation-and-log-in).)

 * Click the *Create Issue* button at the top of the screen.

    ![The Create  Issue button.](../images/jira/jira_06.png)

 * Fill in the *Create Issue* form.

    ![The first half of the Create Issue form.](../images/jira/jira_05.png)

    1. In the *Project* field, select the part of PCGen that needs to be fixed or enhanced. (See: [Sub-projects](#sub-projects).)
    2. Select the *Issue Type* - either *Bug* or *Feature Request*.
	    * A **bug** is a thing which PCGen *should already do*, but PCGen contains the wrong data, does the wrong thing, or malfunctions.
	    * A **feature request** is a thing PCGen *doesn't do yet*, which you would like it to do; or, a piece of data which is missing, which should be added.
    3. The *Summary* is the title of the issue. It should be a short sentence describing what your issue is about.
    4. Select a *Priority*:
	    *  **Blocker** (most severe) - The entire program is un-usable.
	    *  **Critical** - some major feature, which most people use, is broken and does not work.
	    *  **Major** - some major feature, which most people use, needs improvement.
	    *  **Minor** - some minor feature, which some people use, needs improvement.
	    *  **Trivial** (least severe) - Some minor feature needs to be tweaked.
    5. Your *Environment* is the version of PCGen you are using, the Java version you are using, and your operating system. Example: *PCGen 6.05.03, Java 1.8, Windows 7.* (See: [Determining your PCGen and Java versions](#determining-your-pcgen-and-java-versions).)
    6. The *Description* should include as much detail as required to fully explain your issue.

      	If you are experiencing a bug, then describe:
		* What you were trying to do
		* What you *expected* to happen
		* What went wrong, i.e. how the program failed to do what you expected. Include details of any error messages that appear.
		* Instructions for re-producing the error. Ex: *Open PCGen, click New Character, go to Skills tab, add a rank in the Climb skill...*
		* Any troubleshooting steps you have already tried: i.e. *I uninstalled PCGen, then re-installed the latest version of PCGen...*
    	An example description of a bug is shown in the screenshot.

		If you are requesting a feature, then include enough detail to specify what it is you want added.

    ![The second half of the Create Issue form.](../images/jira/jira_08.png)

    7. You can attach files, such as PCGen character files or screenshots, using the *Attachment - Choose Files* button.
    8. Finally, click the *Create* button.
 
----

Once the issue is created, the project team will be notified there is a new issue for their attention. 

You will be notified via email if the project team needs any more information, and when the issue is resolved.

An example of a submitted issue is shown below.

![](../images/jira/jira_09.png)


----

## Account creation and log-in

An account is required to participate in the JIRA issue tracker. The sign-up link is on the [JIRA front page](http://jira.pcgen.org/): 

![The sign-up link on the front page.](../images/jira/jira_01.png)

Once you have completed the sign-up process, you can log in:

![The log-in screen.](../images/jira/jira_02.png)

----

## Sub-projects

The JIRA system contains several "sub-projects", representing parts of the overall PCGen project.

![The sub-projects displayed on the JIRA front page.](../images/jira/jira_03.png)

The sub-projects are as listed below.

Filing your issue under the right sub-project helps members of the development team to keep track of their part of the project. If in doubt, pick the sub-project you think is the closest match for your issue; someone will fix it up afterwards, if required.

* `CODE`: PCGen program including the user interface, parsing of data, rules engine, output tags etc.
	* Program crashes
* `DATA`: LST/Data issues and requests.
	* Errors and omissions in the data sets that come with PCGen (such as d20 3.5ed RSRD, Pathfinder, etc.)
* `DOCS`: The user manual, which you are reading now.
	* Typographical errors
	* Broken links
	* Confusing explanations, which should be simplified
* `NEWSOURCE`: This is where new books are entered that have permission to be included in PCGen.
* `NEWTAG`: *(Ed: What is this?)*
* `OS`: Output sheets.
	* Error in output sheet
	* Enhancements to output sheets
* `PLAYTEST`: Non-versioned sets. *(Ed: What is this for?)*
* `PRET`: The `prettylst.pl` Perl program, used to pretty-print `LST` files and update them to the latest tag formats.
* `HELP`: For general user support requests, such as *"How do I?..."* Note this is one of many places you can get help. Other places you can get help include:
	* By posting to the [PCGen web forums](http://groups.pcgen.org). (New.)
	* The Yahoo! `pcgen` mailing list. (Old; being phased out.)

----

## Determining your PCGen and Java versions

When reporting issues, you should include:

* The PCGen version number
* The Java version number
* Your operating system - examples: *Windows 7*, *OSX 10.6.8*, *Ubuntu Linux 15.04*. See: the [What's my OS?](http://whatsmyos.com/) website.
* How much memory (RAM) your computer has. See: [Computer Hope - Determining how much RAM is installed and available](http://www.computerhope.com/issues/ch000149.htm).

Your PCGen version and Java version can be found by opening the *Help > About* dialog in PCGen.

![The Help > About... dialog.](../images/jira/jira_07.png)
