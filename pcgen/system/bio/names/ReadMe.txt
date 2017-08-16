Look at the Mar_ files for the basic layout of the name files.  

[RULES] is a list of rules to create names

The rest of the tags you may name anything you want.

Every item must be on its own line.



Quick how to for RULES--

The number before the rule sets is the number or less needed to roll to use that rule set.   Max of 100.

Example

5	[FULL]  = 1-5 roll on FULL list.
37	[SYL1]	[SYL2]   = 6-37 roll on syl1 and syl2 and put them together.
42	[{Mar_BarbarianFemale}] = 38-42 get the name from the file Mar_BarbarianFemale.nam
100	[TITLE]	[FULL]   = 43-100 roll on title and full and put them together.

Each rule set must have its own line and be tabbed, no spaces:

5(tab)[FULL](tab)[TITLE]

Just like all other list files.  The order of the rules, [FULL] [SYL1] [TITLE], dosen't matter.  The generator will combine the them into one word.  If you want 'spaces' leave a space after (or before) the word. 

In creating a name file, you also have the ability to refer to other name files, and have it grab a certain percentage of names from said file.

To do this, create a rule like:
42	[{Mar_BarbarianFemale}] = 38-42 get the name from the file Mar_BarbarianFemale.nam
note, that you leave off the .nam extension.

Filters
-------
you could have a file like this:

[RULES]
10	[SYL1]	[SYL2]
70	[TITLE]	[SYL2]	[SYL3]
100	[SYL1]	[SYL3]

[SYL1]
[PREGENDER:M]
Boo
Ga
Wan
Tar
[PREGENDER:F}
La
Far
Wi
Ke
[/PRE]
etc.

If you don't pass a filter, like PREGENDER:M (you're not Male), then all lines are ignored until you get to another filter or [/PRE] is reached (which turns filtering off).  You can include more than one PRExxx tag if you tab delimit them within the brackets... e.g. [PRERACE:Dwarf	PREGENDER:M]

This way we don't need a million nam files broken along gender/region/racial/whatever lines.  You could do them all in one file using the PRExxx filters.  All the PRExxx tags are available.

Enjoy,

Mario
zebuleon@peoplepc.com

and

John
john@sleazyweasel.com

and

Devon
soulcatcher@evilsoft.org

and

Bryan
merton_monk@yahoo.com