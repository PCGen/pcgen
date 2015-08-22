PRExxx Tags
===========

With these tags you can restrict access to a class, feat, deity, domain or any other object in a lst file. The same PRExxx tags are used in every lst file to make crafting restrictions as easy as possible.

### Usage

PRExxx tags are used in two different manners:

1.  A PRExxx tag can be used is as a stand-alone tag to qualify an entire line. Thus, if a character does not meet the prerequisites of a stand-alone PRExxx tag, he will not be able to select the lst object containing the PRExxx and therefore shall not gain the benefits granted by the lst object.
2.  BONUS tags, and certain other tags, can be qualified by appending a PRExxx tag to the end of it. If the character does not meet the prerequisites, he will not gain the BONUS but is not restricted from any other benefits he may receive from the line. PRExxx tags are usually added to BONUS tags with an additional pipe (|) followed by the PRExxx statement. There are, on the other hand, a few tags that use a different syntax, such as enclosing the PRExxx statement in square brackets (\[\]). Also note that all tags that will take PRExxx tags are clearly marked as such. Review the tag documentations to verify the syntax for adding prerequisites and to see which tags will take PRExxx tags.

There are a few additional rules to keep in mind when using PRExxx tags.

1.  When using PRExxx tags as part of a class, the level will be tested before any new level is fully applied. This means that `      PREPCLEVEL:MAX=1     ` will pass when adding a second level to a first level character as that character is not yet level 2.
2.  Any PRExxx tag may be prefixed with a "!" character (i.e., !PRExxx) in order to invert the requirement logic. The PRExxx tag will be evaluated like normal, then the result inverted (meaning you CAN NOT have the things listed in this tag) when determining whether the prerequisite is passed or not.
3.  There is a `      PRE:.CLEAR     ` tag. Be aware that it will clear all PRExxx tags from an object and you will need to re-enter those PRExxx tags that you want to keep.

General notes
-------------

### Boolean logic

#### NOT

Logical negation of any pre-requisite is obtained by pre-pending an exclamation mark, `     !    ` , to the `     PRE    ` tag.

-   `      PREALIGN:LG     ` , **without** the `      !     ` , requires that the character's alignment **is** lawful good.
-   `      !PREALIGN:LG     ` , **with** the `      !     ` , requires that the character **is not** lawful good.

#### OR

Use `     PREMULT:1,[sub_prereq1],[sub_prereq2]...    ` to require **at least one** of the sub-prerequisites `     [sub_prereq1]    ` , `     [sub_prereq2]    ` , ... to be true.

-   2-variable OR: `       PREMULT:1,[PREALIGN:LG],[PREGENDER:M]      `

    Must have lawful good alignment OR male gender.

-   3-variable OR: `       PREMULT:1,[PREALIGN:LG],[PREGENDER:M],[PREATT:10]      `

    Must have lawful good alignment OR male gender OR a base attack bonus of at least 10.

#### AND

Use `     PREMULT:N,[sub_prereq1],[sub_prereq2]...    ` , where `     N    ` is the number of sub-prerequisites, to require that **all** of the sub-prerequisites `     [sub_prereq1]    ` , `     [sub_prereq2]    ` , ... are true.

-   2-variable AND: `       PREMULT:2,[PREALIGN:LG],[PREGENDER:M]      `

    Must have lawful good alignment AND male gender.

-   3-variable AND: `       PREMULT:3,[PREALIGN:LG],[PREGENDER:M],[PREATT:10]      `

    Must have lawful good alignment AND male gender AND a base attack bonus of at least 10.

#### NOR and NAND

The `     NOR    ` ( `     not OR    ` ) and `     NAND    ` ( `     not AND    ` ) functions are produced by prefixing `     !    ` , giving the `     !PREMULT    ` tag.

-   2-variable NOR: `       !PREMULT:1,[PREALIGN:LG],[PREGENDER:M]      `

    Must NOT have (lawful good alignment OR male gender).

-   2-variable NAND: `       !PREMULT:2,[PREALIGN:LG],[PREGENDER:M]      `

    Must NOT have (lawful good alignment AND male gender).

#### Nested functions

It is possible to nest the `     PREMULT    ` tag within other `     PREMULT    ` tags, allowing logic functions like `     (A OR B) AND (C OR D)    ` to be realised.

### Comparison Operators

Tags which deal with numbers or ordered sequences (i.e. creature sizes - small, medium, large ...) allow the following comparison operators.

-   `      EQ     ` - equals
-   `      GT     ` - greater than
-   `      GTEQ     ` - greater than or equal to
-   `      LT     ` - less than
-   `      LTEQ     ` - less than or equal to
-   `      NEQ     ` - not equal to

The tags which use this syntax include:

-   [`PREBASESIZE`](#prebasesize)
-   [`PREHANDS`](#prehands)
-   [`PRELEGS`](#prelegs)
-   [`PREREACH`](#prereach)
-   [`PRESIZE`](#presize)
-   [`PRESR`](#presr)
-   [`PREVAR`](#prevar)

The comparison operator is written **as a part of the tag name** . i.e. `     PREVARNEQ    ` or `     PREREACHLTEQ    ` .

(Not as `     PREVAR:EQ    ` or `     PREREACH:LTEQ    ` .)

------------------------------------------------------------------------

### PREABILITY

*Require that the character has at least `      N     ` abilities from a given `      ability_category     ` . A type of ability can be specified, or individual abilities can be specified by name.*

#### Status

-   Updated 6.03.00.
    -   The syntax `        PREABILITY:N,CATEGORY=ability_category,ALL       ` was deleted. (Formerly used to require `        N       ` abilities from `        ability_category       ` . Removed because it was too broad.)
    -   Refer [JIRA issue NEWTAG-423](http://jira.pcgen.org/browse/NEWTAG-423) - Eliminate use of ANY for a Category in PREABILITY.

#### Syntax

1.  `       PREABILITY:N,CATEGORY=ability_category,TYPE.ability_type_1,TYPE.ability_type_2,...      `

    Requires the character to have at least `       N      ` abilities from `       ability_category      ` , of the specific type(s) `       ability_type_1      ` , `       ability_type_2      ` , and so on.

2.  `       PREABILITY:N,CATEGORY=ability_category,ability_name_1,ability_name_2,...      `

    Requires the character to have at least `       N      ` abilities from a specific list of abilities: `       ability_name_1      ` , `       ability_name_2      ` , and so on.

It is possible to combine the `     TYPE.ability_type1    ` and the `     ability_name_1    ` syntax:

    PREABILITY:N,CATEGORY=ability_category,TYPE.ability_type_1,specific_ability_2,...  

#### Notes

-   The `       CATEGORY      ` tag will only accept "parent" categories.

-   Square brackets around an ability name, `       [ability_name_1]      ` , means that the character must **not** have `       ability_name_1      ` .

-   Square brackets around an ability type, `       [TYPE.ability_type_1]      ` , means that the character must **not** have any ability of the type `       ability_type_1      ` .

#### Examples

-   Examples using specifically named abilities

    -   `         PREABILITY:1,CATEGORY=Feat,Dodge        `

        Requires the `         Dodge        ` feat. (Prerequisite for [d20 SRD Mobility](http://www.d20srd.org/srd/feats.htm#mobility) .)

    -   `         PREABILITY:2,CATEGORY=Feat,Dodge,Mobility        `

        Requires two of the `         Dodge        ` and `         Mobility        ` feats, i.e. both are required. (Prerequisite for [d20 SRD Spring Attack](http://www.d20srd.org/srd/feats.htm#springAttack) .)

    -   `         PREABILITY:2,CATEGORY=Feat,Improved Disarm,Improved Feint,Improved Trip        `

        Requires any two of the three feats specified.

-   Examples using ability types

    -   `         PREABILITY:1,Category=Feat,TYPE.SpellFocus        `

        Requires at least one `         SpellFocus        ` type feat.

    -   `         PREABILITY:3,Category=Feat,TYPE.Metamagic        `

        Requires at least three `         Metamagic        ` type feats.

-   Examples of excluding abilities

    -   `         PREABILITY:1,Category=SpecialQuality,[Night Vision]        `

        Requires that the creature does **not** have the `         Night Vision        ` special quality.

        Note that the `         !        ` syntax could also be used, i.e.

        `         !PREABILITY:1,Category=SpecialQuality,Night Vision        ` .

    -   `         PREABILITY:1,CATEGORY=Feat,Power Attack,[Weapon Finesse]        `

        Requires that the creature has the `         Power Attack        ` feat, and does **not** have the `         Weapon Finesse        ` feat.

------------------------------------------------------------------------

<span id="preageset"></span> \*\*\* Updated 5.13.12

**Tag Name:** PREAGESET:x,y,y

**Variables Used (x):** Number (Number of agesets required)

**Variables Used (y):** Text (Ageset abbreviation)

**What it does:**

-   Requires the PC to have the specified Ageset or older.
-   Agesets are defined in [biosettings.lst](../systemfilestagpages/systemfilesbiosettingslist.html) files by the [AGESET](../systemfilestagpages/systemfilesbiosettingslist.html#ageset) tag.
-   *Biosettings.lst* files can be placed in the gameMode or in the applicable datasets.
-   There are four Agesets defined in the 35e gameMode: Adulthood, Middle Age, Old, and Venerable.

**Example:**

`     PREAGESET:1,Old    `

Requires the PC to be "Old" or "Venerable".

`     PREAGESET:1,Venerable    `

Requires the PC to be "Venerable".

`     !PREAGESET:1,Venerable    `

Requires the PC to be younger than "Venerable".

`     PREMULT:2,[PREAGESET:1,Middle Age],[!PREAGESET:1,Old]    `

Requires the PC to be "Middle Age" but not older.

------------------------------------------------------------------------

### PREALIGN

Requires the character to have some particular alignment(s).

#### Status

Updated 5.7.6

#### Syntax

`PREALIGN:alignment_1,alignment_2,...`

The `alignment_1`, `alignment_2`, ... may be identified either by abbreviation or by numerical index.

* By abbreviation (preferred):
	* `LG` is lawful good, `TN` is true neutral, `CE` is chaotic evil, and so on: `LG, LN, LE, NG, TN, NE, CG, CN, CE`.
	* As a special case, the abbreviation `Deity` refers to the alignment of the character's deity.
* By numerical index (legacy; not recommended):
	* `0, 1, 2, 3, 4, 5, 6, 7, 8, 10` - based on the order in which alignments are listed in the `statsandchecks.lst` game mode file.
	* In the 3e and 3.5e game modes, 0=LG, 1=LN, 2=LE, 3=NG, 4=TN, 5=NE, 6=CG, 7=CN, 8=CE, 9=None, 10 Deity's Alignment.

The abbreviations, i.e. `TN`, are preferred, as this doesn't depend on the ordering of lines in the `statsandchecks.lst` file.

#### Notes
*   If no alignments are defined in `statsandchecks.lst`, then all `PREALIGN` tags will return TRUE.

#### Examples

* `PREALIGN:LG,NG,CG` - require any good alignment.

* `!PREALIGN:LE,NE,CE` - require any non-evil alignment.

* `PREALIGN:TN,Deity` - require either true neutral, or the character's deity's alignment.

------------------------------------------------------------------------

<span id="preapply"></span> \*\*\* deprecated 6.1.1 - Remove for 6.4 - Use [TEMPBONUS](globalfilesbonus.html#tempbonus)


**Tag Name:** PREAPPLY:x,x

**Variables Used (x):** ANYPC (Apply to any character).

**Variables Used (x):** PC (Apply to the character).

**Variables Used (x):** Text (A type name).

**What it does:**

-   Requires that the included qualifications be met by the target before the associated `      BONUS     ` will be applied to that target.
-   `      PREAPPLY     ` works **ONLY** at the end of a `      BONUS     ` tag.
-   Bonuses with `      PREAPPLY     ` tags will only appear in the *Temporary Bonuses Tab* .
-   Use a comma-delimiter (,) to indicate a logical **AND** , i.e. both conditions must be met.
-   Use a semi-colon delimiter (;) to indicate a logical **OR** , i.e. either condition must to be met.
-   All LST objects containing `      BONUS     ` tags with `      ANYPC     ` as the target of a `      PREAPPLY     ` tag will always show up on the *Temporary Bonus Tab* .
-   Use of the `      ANYPC     ` subtag allows for bonuses to be granted without the character needing to have added the containing object to his character, or to allow for conditional/situational bonuses on LST objects that have been added to the character.
-   LST objects that can include a `      PREAPPLY:ANYPC     ` tag include:
    -   Spells that can be cast on any character.
    -   Abilities that contain conditional or situational bonuses.
    -   Feats that contain conditional or situational bonuses.
    -   Skills that contain conditional or situational bonuses.
    -   Templates that contain conditional or situational bonuses.
-   Spells with `      PREAPPLY     ` tags with weapon types will be treated as containing `      ANYPC     ` subtag and will appear in the *Temporary Bonuses Tab* with such bonuses applied only to a weapon that meets the type requirements specified.
-   See the documentation for the [Temporary Bonus Tab](../../tabpages/players/inventory/inventorytempbonus.html) for more information on using this tag.

**Examples:**

`     PREAPPLY:ANYPC    `

This allows the bonus granted to be applied to any character.

`     PREAPPLY:PC    `

This allows the bonus granted to be applied to the PC to which the granting LST object is attached.

`     PREAPPLY:Ranged;Melee    `

This allows the bonus granted to be applied to either ranged or melee weapons.

`     PREAPPLY:Weapon,Blunt    `

This allows the bonus granted to be applied to blunt weapons only.

**Example Conversion to TEMPBONUS tag:**

`     BONUS:COMBAT|AC|2|PREAPPLY:PC    ` becomes `     TEMPBONUS:PC|COMBAT|AC|2    `

------------------------------------------------------------------------

<span id="prearmortype"></span> \*\*\* New 5.10

**Tag Name:** PREARMORTYPE:x,y,y

**Variables Used (x):** Number (The number of equipped armors needed).

**Variables Used (y):** Text (The name of an Armor).

**Variables Used (y):** Text (The name of a piece of Armor - "%" may be used as a wild-card).

**Variables Used (y):** TYPE.Text (The type of Armor).

**Variables Used (y):** LIST (Checks that the equipped Armor is one the character is proficient with).

**What it does:**

Checks for equipped Armor.

**Examples:**

`     PREARMORTYPE:1,Chainmail,Full Plate    `

Character must have either "Chainmail" or "Full Plate" armor equipped.

`     PREARMORTYPE:1,Leather Armor%    `

The "%" allows for items named Leather Armor (Masterwork), Leather Armor (+1) etc.

`     PREARMORTYPE:1,TYPE.Medium    `

Character must have a Medium type armor equipped.

`     PREARMORTYPE:1,LIST    `

Character must be proficient in the armor equipped.

------------------------------------------------------------------------

**<span id="preatt"></span> Tag Name:** PREATT:x

**Variables Used (x):** Number (Base Attack Bonus number).

**What it does:**

Number indicates the minimum base attack bonus.

Highest PREATT possible is 20.

**Example:**

`     PREATT:6    `

This would apply only if the PC had minimum base attack bonus of 6.

------------------------------------------------------------------------

### PREBASESIZE

Require the character's base size is a particular size category, or in a particular range of size categories.

#### Status

Changed: `     size    ` used to be specified as the size name in words, i.e. `     Small    ` . Now is is specified as the `     SIZENAME    ` , i.e. `     S    ` .

#### Syntax

`     PREBASESIZEoperator:size    `

`     operator    ` and `     size    ` are as per the [`      PRESIZE     ` tag](#presize) .

#### Examples

As per the [`      PRESIZE     ` tag](#presize)

------------------------------------------------------------------------

**<span id="prebirthplace"></span> Tag Name:** PREBIRTHPLACE:x

**Variables Used (x):** Text (A birthplace name).

**What it does:**

Character's birthplace must match the listed text.

**Example:**

`     PREBIRTHPLACE:Klamath    `

Character must have been born in "Klamath".

------------------------------------------------------------------------

<span id="precampaign"></span> \*\*\* New 5.15.2

**Tag Name:** PRECAMPAIGN:x,y,y

**Variables Used (x):** Number (Number of items required to pass)

**Variables Used (y):** Text (Data set name as set by the CAMPAIGN tag).

**Variables Used (y):** BOOKTYPE=Text (Matches data set BOOKTYPE tag).

**Variables Used (y):** INCLUDES=Text (Data set name as set by the CAMPAIGN tag that is included in any selected campaign or one of its included sources). \*\*\* New 6.1.2

**Variables Used (y):** INCLUDESBOOKTYPE=Text (Data set BOOKTYPE of any selected campaign or one of its included sources). \*\*\* New 6.1.2

**What it does:**

-   Sets prerequisites for LST objects based upon the data sets loaded into PCGen. If the set is loaded it passes, if not it doesn't.
-   See PCC [PRECAMPAIGN](../datafilestagpages/datafilespcc.html#precampaign) docs for full information on this tag.

------------------------------------------------------------------------

<span id="precharactertype"></span> \*\*\* New 5.17.12

**Tag Name:** PRECHARACTERTYPE:x,y,y

**Variables Used (x):** Number (Number of character types required)

**Variables Used (y):** Text (Character type.)

**What it does:**

Sets requisites for character type.

**Examples:**

PRECHARACTERTYPE:1,PC

Character must be designated as a **PC** .

PRECHARACTERTYPE:1,NPC,Monster

Character must be a designated either as an **NPC** or as a **Monster** .

------------------------------------------------------------------------

**<span id="precheck"></span> Tag Name:** PRECHECK:x,y=z

**Variables Used (x):** Number (The number of checks that must be equal to or greater than the numbers specified for the check to succeed).

**Variables Used (y):** Text (A defined check from statsandchecks.lst - e.g. Fortitude or Willpower).

**Variables Used (z):** Number (The number the associated check must be greater than or equal to).

**What it does:**

Sets the minimum CHECK requirements.

**Examples:**

`     PRECHECK:1,Fortitude=5,Reflex=3    `

Would succeed if Fortitude meets or exceeds 5 or Reflex meets or exceeds 3.

`     PRECHECK:2,Fortitude=5,Reflex=3,Willpower=4    `

Would succeed if any 2 of the 3 three listed conditions were met.

------------------------------------------------------------------------

**<span id="precheckbase"></span> Tag Name:** PRECHECKBASE:x,y=z

**Variables Used (x):** Number (The number of base checks that must be equal to or greater than the numbers specified for the check to succeed).

**Variables Used (y):** Text (A defined check from statsandchecks.lst - e.g. Fortitude or Willpower).

**Variables Used (z):** Number (The number the associated base check must be greater than or equal to).

**What it does:**

Sets the minimum Base Check requirements. Base Checks are usually only those from class advancement (no stat modifiers, magic, etc.), but follow anything defined with " `     BONUS:CHECKS|BASE.Name|    ` ".

**Examples:**

`     PRECHECKBASE:2,Fortitude=3,Reflex=3    `

Would succeed if both Fortitude meets or exceeds 3 and Reflex meets or exceeds 3.

`     PRECHECKBASE:1,Fortitude=5,Reflex=3    `

Would succeed if Fortitude meets or exceeds 5 or Reflex meets or exceeds 3.

------------------------------------------------------------------------

**<span id="precity"></span> Tag Name:** PRECITY:x

**Variables Used (x):** Text (The name of a city that you must live in).

**What it does:**

Sets the required city. A character's city is set in the [Description Tab](../../tabpages/tabdescription.html)

**Example:**

`     PRECITY:Klamath    `

Character must currently reside in "Klamath".

------------------------------------------------------------------------

**<span id="preclass"></span> Tag Name:** PRECLASS:x,y=z,y=z

**Variables Used (x):** Number (The number of classes that must be equal to or greater than the numbers specified for the check to succeed).

**Variables Used (y):** Text (Class Name)

**Variables Used (y):** TYPE.Text (Class Type)

**Variables Used (y):** SPELLCASTER.

**Variables Used (y):** SPELLCASTER.Type (Spellcaster Type)

**Variables Used (y):** ANY

**Variables Used (z):** Number (Class Level)

**What it does:**

Sets class requirements.

**Example:**

`     PRECLASS:2,Wizard=5,Sorcerer=6,Cleric=7    `

Multi-classed character must be at least Wiz5/Sor6, Wiz5/Clr7 or Sor6/Clr7.

`     PRECLASS:1,SPELLCASTER=2    `

Character must have 2 levels in any spellcasting class. This encompasses psionic manifesting classes as well, as PCGen does not treat manifesting differently from spellcasting. Spellcaster and manifester can be considered interchangeable.

`     PRECLASS:1,SPELLCASTER.Arcane=2    `

Character must have 2 levels in any arcane spellcasting class.

`     PRECLASS:1,SPELLCASTER.Divine=6,SPELLCASTER.Psionic=3    `

Character must have 6 levels in any divine spellcasting class or 3 levels in any psionic manifesting class.

`     PRECLASS:2,TYPE.Base=5,TYPE.Prestige=1    `

Character must have 5 levels in any Base class and 1 level in any Prestige class.

`     PRECLASS:2,ANY    `

Multi-classed character must be at least two Classes.

`     PRECLASS:1,ANY    `

Character must have 1 level in any class.

`     PREMULT:1,[PREFEAT:1,Blood of the Fey],[PRECLASS:1,Fey-Touched=1]    `

Character must have 1 level in Fey-Touched class or feat Blood of the Fey.

`     PRECLASS:1,Jack o' the Green=5 !PREFEAT:1,TYPE.LASlipperyEel    `

Character must have 5 levels in Jack o' the Green class and not have the feat LASlipperyEel.

`     !PRECLASS:1,SPELLCASTER.Arcane=1    `

Character must not have 1 level in SPELLCASTER.Arcane class.

`     BONUS:COMBAT|AC|1|PRECLASS:1,Shaper=10    `

Character adds 1 to armor class if 10 levels in Shaper Class.

`     BONUS:WEAPON|TOHIT|4|TYPE=Luck|PRECLASS:1,Wizard=1,Sorcerer=1    `

Character adds 4 to To-Hit from luck if 1 level in either Wizard/Sorcerer Class.

`     BONUS:CASTERLEVEL|Minstrel|CL-3|PRECLASS:1,Minstrel=4    `

Character adds CL-3 bonus to Casterlevel if 4 levels in Minstrel Class.

`     SA:Damage reduction 1/opposed alignment|PRECLASS:1,Shaper=10    `

SA: will fire with 10 levels of Shaper Class.

`     SA:Spy|PREMULT:2,[PRECLASS:1,Aradil's Eye=5],[PRECLASSLEVELMAX:1,Aradil's Eye=9]    `

SA: will fire with 5 levels of Aradil's Eye Class or not more than 9 levels of Aradil's Eye Class.

------------------------------------------------------------------------

**<span id="preclasslevelmax"></span> Tag Name:** PRECLASSLEVELMAX:x,y=z,y=z

**Variables Used (x):** Number (Number of listed classes required)

**Variables Used (y):** Text (Class name)

**Variables Used (y):** TYPE.Text (Class type)

**Variables Used (y):** SPELLCASTER

**Variables Used (y):** SPELLCASTER.Text (Spellcaster type)

**Variables Used (z):** Number (Maximum class level)

**What it does:**

-   Sets maximum class level limits.
-   Class types are defined in the loaded datasets. Examples from the RSRD are "Base", "PC", or "Prestige".
-   `      SPELLCASTER     ` will check against all spellcasting classes.
-   `      SPELLCASTER.Text     ` will check against spellcaster types, e.g. Arcane or Divine.

**Example:**

`     PRECLASSLEVELMAX:2,Fighter=2,SPELLCASTER=2    `

Character cannot have more than 2 levels of fighter and cannot have more than 2 levels in any spellcasting class.

------------------------------------------------------------------------

<span id="precskill"></span> \*\*\* New 5.9.0

**Tag Name:** PRECSKILL:x,y

**Variables Used (x):** Number (The number of skills that must be considered Class Skills for the check to succeed).

**Variables Used (y):** Text (A defined skill name)

**Variables Used (y):** TYPE.Text (A defined skill type)

**What it does:**

Sets skill requirements.

**NOTE:** In skills types with multiple versions like Craft (Pottery) or Knowledge (Local) there must be a space between the name and the parentheses because the text must match the entry exactly.

**Examples:**

`     PRECSKILL:1,Spot,Listen    `

Character must have either "Spot" or "Listen" as a Class Skill.

`     PRECSKILL:2,TYPE.Spy    `

Character must have two "Spy" skills as Class Skills.

------------------------------------------------------------------------

**<span id="predeity"></span> Tag Name:** PREDEITY:x,y

**Variables Used (x):** Number (The number of the deity's name or pantheons that must match).

**Variables Used (y):** Y (The character must have chosen a deity).

**Variables Used (y):** N (The character must not have chosen a deity).

**Variables Used (y):** Text (The name(s) of deities).

**Variables Used (y):** PANTHEON.Text (The name(s) of pantheons).

**What it does:**

Sets deity requirements by presence, name or pantheon name.

**Examples:**

`     PREDEITY:1,Y    `

Character must have a deity chosen.

`     PREDEITY:1,N    `

Character must NOT have a deity chosen.

`     PREDEITY:1,Zeus,Odin    `

Character must have chosen either "Zeus" or "Odin".

`     PREDEITY:1,Zeus,PANTHEON.Celtic,Odin    `

Character must have chosen either "Zeus", "Odin" or a deity in the "Celtic" pantheon.

`     CLASS:Druid.MOD <tab> PREDEITY:1.Saluwe,Yarris,Belisarda,Fire Dragon    `

Character must have chosen either "Saluwe" "Yarris" "Belisarda" or "Fire Dragon".

------------------------------------------------------------------------

**<span id="predeityalign"></span> Tag Name:** PREDEITYALIGN:x

**Variables Used (x):** LG,LN,LE,NG,TN,NE,CG,CN,CE (Alignment abbreviation).

**Variables Used (x):** 0,1,2,3,4,5,6,7,8 (Alignment array number).

**What it does:**

-   Requires the PC to have a deity with a particular alignment.
-   Alignment names are defined in the *statsandchecks.lst* gameMode file.
-   The Alignment abbreviation is the prefered method of identifying the required alignments.
-   The Alignment array number, based on the order the Alignments are presented in the *statsandchecks.lst* file with the first one being 0.
-   If NO alignments are defined in GameMode, all PREALIGN tags will return TRUE.
-   In the 3e and 35e gameModes these are the listed Alignments: 0=LG, 1=LN, 2=LE, 3=NG, 4=TN, 5=NE, 6=CG, 7=CN, 8=CE, 9=None, 10 Deity's Alignment.

**Example:**

`     PREDEITYALIGN:0    `

Character must have chosen a Lawful Good deity.

`     PREDEITYALIGN:LG,NG,CG    `

Character must have chosen a deity of any Good alignment.

------------------------------------------------------------------------

**<span id="predeitydomain"></span> Tag Name:** PREDEITYDOMAIN:x,y,y

**Variables Used (x):** Number (The number of the deity's domains that must match).

**Variables Used (y):** Text (Domain names).

**What it does:**

-   Sets requirements for the character's deity's domains.
-   This tag applies to the domains of the PC's deity, and not the domains that the PC has selected for itself.

**Example:**

`     PREDEITYDOMAIN:1,Good,Law    `

Character must have chosen of deity with either the "good" domain or the "law" domain.

------------------------------------------------------------------------

<span id="predomain"></span> \*\*\* Updated 5.11.7

**Tag Name:** PREDOMAIN:x,y,y

**Variables Used (x):** Number (The number of the deity's domains that must match).

**Variables Used (y):** Text (Domain names or ANY).

**What it does:**

-   Set's requirements for a character's domains.
-   This tag applies to the domains that the PC has selected for itself.

**Example:**

`     PREDOMAIN:1,Good,Law    `

Character must have 1 of the two listed domains.

`     PREDOMAIN:2,ANY    `

Character must have 2 domains of any type.

------------------------------------------------------------------------

**<span id="predr"></span> Tag Name:** PREDR:x,y=z,y=z

**Variables Used (x):** Number (The number of the DR conditions that must be met).

**Variables Used (y):** Text (The type of DR).

**Variables Used (z):** Number (Value the DR must be greater or equal to).

**What it does:**

Set's requirements for a character's Damage Resistance.

**Examples:**

`     PREDR:1,+1=10    `

Must have DR of 10/+1 or greater (but DR Type must be +1).

`     PREDR:1,-=10,+1=10,+2=10,+3=10,+4=10,+5=10,Silver=10    `

Must have DR of 10 or greater of any type listed.

------------------------------------------------------------------------

**<span id="preequip"></span> Tag Name:** PREEQUIP:x,y,y

**Variables Used (x):** Number (The number of items to be equipped)

**Variables Used (y):** Text (Equipment name)

**Variables Used (y):** TYPE=Text (Equipment type)

**Variables Used (y):** WIELDCATEGORY=Text (Wield category of a weapon)

**What it does:**

-   This is used to determine if a character has a particular item(s) equipped.
-   The percent symbol (%) may be used as a wild-card when including text in the list of parameters.
-   Multiple TYPEs may be stacked by including them in a period delimited (.) list. (e.g. TYPE=Heavy.Armor for "Heavy Armor")

**Examples:**

`     PREEQUIP:1,Leather Armor    `

Must have Leather Armor (only) equipped.

`     PREEQUIP:1,Leather Armor%    `

The "%" allows for items named Leather Armor (Masterwork), Leather Armor (+1) etc.

`     PREEQUIP:1,TYPE=Armor    `

Must have some type of armor equipped.

`     PREEQUIP:2,TYPE=Armor,Sword (Long)%    `

Must be equipped with any Sword (Long), as well as any type of armor.

`     PREEQUIP:2,TYPE=Armor,TYPE=Shield    `

Must be equipped with any Armor and any Shield.

`     PREEQUIP:1,WIELDCATEGORY=TwoHanded    `

Must be equipped with a two handed weapon.

`     PREEQUIP:1,TYPE=Armor.Heavy,TYPE=Armor.Light    `

Must have equipped Heavy Armor or Light Armor.

------------------------------------------------------------------------

**<span id="preequipprimary"></span> Tag Name:** PREEQUIPPRIMARY:x,y,y

**Variables Used (x):** Number (The number of items from the list that must be equipped in a primary hand).

**Variables Used (y):** Text (The name of a piece of equipment - "%" may be used as a wild-card).

**Variables Used (y):** TYPE=Text (The type of a piece of equipment).

**Variables Used (y):** WIELDCATEGORY=Text (The wield category of a weapon).

**What it does:**

This is used to determine if a character has a particular item (usually a weapon) equipped in a Primary hand for the character. Typically has a value of 1 for the Number, however can be more than one if can have more than one Primary hand.

**Examples:**

`     PREEQUIPPRIMARY:1,Dagger    `

Must have a dagger equipped in the primary hand.

`     PREEQUIPPRIMARY:1,Dagger%    `

The "%" allows for items named Dagger (Masterwork), Dagger (Punching), etc.

`     PREEQUIPPRIMARY:1,TYPE=Slashing    `

Must have some type of slashing weapon equipped in the primary hand.

`     PREEQUIPPRIMARY:1,WIELDCATEGORY=OneHanded    `

Must have a one handed weapon equipped in the primary hand.

------------------------------------------------------------------------

**<span id="preequipsecondary"></span> Tag Name:** PREEQUIPSECONDARY:x,y,y

**Variables Used (x):** Number (The number of items from the list that must be equipped in a secondary hand).

**Variables Used (y):** Text (The name of a piece of equipment - "%" may be used as a wildcard).

**Variables Used (y):** TYPE=Text (The type of a piece of equipment).

**Variables Used (y):** WIELDCATEGORY=Text (The wield category of a weapon).

**What it does:**

This is used to determine if a character has a particular item (usually a weapon) equipped in a Secondary hand for the character. Typically has a value of 1 for the Number, however can be more than one if can have more than one Secondary hand.

**Examples:**

`     PREEQUIPSECONDARY:1,Dagger    `

Must have a dagger equipped in the secondary hand.

`     PREEQUIPSECONDARY:1,Dagger%    `

The "%" allows for items named Dagger (Masterwork), Dagger (Punching), etc.

`     PREEQUIPSECONDARY:1,TYPE=Slashing    `

Must have some type of slashing weapon equipped in the secondary hand.

`     PREEQUIPSECONDARY:1,WIELDCATEGORY=Light    `

Must have a light weapon equipped in the secondary hand.

------------------------------------------------------------------------

**<span id="preequipboth"></span> Tag Name:** PREEQUIPBOTH:x,y

**Variables Used (x):** Number (Number of items to be equipped).

**Variables Used (y):** Text (Equipment name).

**Variables Used (y):** TYPE=Text (Equipment type).

**Variables Used (y):** WIELDCATEGORY=Text (Wield category name).

**What it does:**

This is used to determine if a character has a particular item (usually a weapon) equipped and used two-handed style.

**Examples:**

`     PREEQUIPBOTH:1,Quarterstaff    `

Must have a Quarterstaff equipped in both hands.

`     PREEQUIPBOTH:1,Sword (Great%    `

The "%" allows for items named Sword (Great/Masterwork) etc.

`     PREEQUIPBOTH:1,TYPE=Slashing    `

Must have a slashing type weapon equipped in both hands.

------------------------------------------------------------------------

**<span id="preequiptwoweapon"></span> Tag Name:** PREEQUIPTWOWEAPON:x,y,y

**Variables Used (x):** Number (The number of items that must be equipped in a two weapon fighting manner - 1 or 2).

**Variables Used (y):** Text (Weapon name - "%" may be used as a wildcard)

**Variables Used (y):** TYPE=Text (Weapon type)

**Variables Used (y):** WIELDCATEGORY=Text (Wield category).

**What it does:**

This is used to determine if a character has a particular item (usually a weapon) equipped and used two weapon style.

**Examples:**

`     PREEQUIPTWOWEAPON:1,Sword (Short)    `

Must have a Sword (Short) equipped as one of two weapons for two weapon fighting.

`     PREEQUIPTWOWEAPON:1,Sword (Short%    `

Must have a Sword (Short) equipped as one of two weapons and allows for items named Sword (Short/Masterwork) etc.

`     PREEQUIPTWOWEAPON:1,TYPE=Slashing    `

Must have a "Slashing" type weapon equipped as one of two weapons for two weapon fighting.

`     PREEQUIPTWOWEAPON:1,WIELDCATEGORY=Light    `

Must have a light weapon equipped for two weapon fighting.

------------------------------------------------------------------------

### PREFEAT

-   6.05.04 (July 2015): JIRA [NEWTAG-477](http://jira.pcgen.org/browse/NEWTAG-477) / Github [PR \#380](https://github.com/PCGen/pcgen/pull/380)
-   -   `        PREFEAT       ` is deprecated. Use [`         PREABILITY        `](#preability) instead.
    -   All `        FEAT       ` tags have been **deprecated** and replaced by the `        ABILITY       ` system, which is more general. The `        ABILITY       ` system can model feats, racial features, class features, traits, flaws, temporary bonuses, and so on, all using a common format.

[Historical documentation for PREFEAT](./globalfilesprexxx_deprecated.html#prefeat) is available.

------------------------------------------------------------------------

**<span id="pregender"></span> Tag Name:** PREGENDER:x

**Variables Used (x):** Text (Gender to require).

**What it does:**

Sets gender requirement.  The character's gender must start with the text specified. The test is case sensitive so "male" is not the same as "Male".  The PCGen GUI allows the following genders by default: Male, Female, Neuter, None, and Other.

**Example:**

`     PREGENDER:M    `

Character's gender must start with "M".

------------------------------------------------------------------------

### PREHANDS

Requires a creature to have a certain number of hands.

See also <a href="#prelegs">PRELEGS</a>.

#### Syntax

`     PREHANDSoperator:number_of_hands    `

`     operator    ` is a comparison operator from the list `     EQ    ` , `     LT    ` , `     LTEQ    ` , `     GT    ` , `     GTEQ    ` , or `     NEQ    ` . The comparison operator is written as part of the tag name, i.e. `     PREHANDSLTEQ    ` . See [comparison operators](#comparison-operators) for more details.

The `     operator    ` is written as part of the tag name, i.e. `     PREHANDSNEQ    ` or `     PREHANDSGTEQ    ` .

`number_of_hands` is the required number of hands.

#### Examples

`     PRELEGSGTEQ:4    `

Character must have at least 4 hands.

------------------------------------------------------------------------

<span id="prehd"></span> \*\*\* Updated 5.13.5

**Tag Name:** PREHD:x,y

**Variables Used (x):** MIN=Number or Formula (Minimum racial Hit Dice)

**Variables Used (y):** MAX=Number or Formula (Maximum racial Hit Dice)

**What it does:**

-   Provides the number, minimum or maximum, of hit dice the character must have to qualify.
-   This test looks at a character's racial HD and/or any levels of Monster Classes only.

**Examples:**

`     PREHD:MIN=1,MAX=3    `

The character must have one, two, or three racial hit die monster levels.

`     PREHD:MIN=4    `

The character must have four or more racial hit die or monster levels.

`     PREHD:MAX=4    `

The character must have four or fewer racial hit die monster levels.

`     !PREHD:MIN=5,MAX=7    `

Character must have between 0-4 OR 7 or greater racial hit die or monster levels.

------------------------------------------------------------------------

<span id="prehp"></span> \*\*\* New 5.10

**Tag Name:** PREHP:x

**Variables Used (x):** Number (minimum Hitpoints)

**What it does:**

Makes the amount of hitpoints a character has a prerequisite.

**Examples:**

`     PREHP:50    `

The character must have at least 50 hp.

------------------------------------------------------------------------

**<span id="preitem"></span> Tag Name:** PREITEM:x,y,y

**Variables Used (x):** Number (The number of items a character must possess).

**Variables Used (y):** Text (The name of an item a character must possess - "%" may be used as a wildcard).

**Variables Used (y):** TYPE=Text (The type of an item the character must possess).

**What it does:**

Sets requirements for items a character must possess.

TYPE.xxx for reference, TYPE=xxx for assignment.

**Examples:**

`     PREITEM:1,Sword (Long),Sword (Short)    `

Character must possess either a "long sword" or a "short sword".

`     PREITEM:2,TYPE=Armor,TYPE=Armor    `

Character must possess two sets of armor.

`     PREITEM:1,TYPE=Natural    `

Character must possess one natural item.

------------------------------------------------------------------------

<span id="prekit"></span> \*\*\* New 6.1.9

**Tag Name:** PREKIT:x,y,y

**Variables Used (x):** Number (Number of Kits Required)

**Variables Used (y):** Text (Kit Name)

**What it does:**

-   Checks to see if the character has the specified kit(s).
-   The Kit name is taken from the `      STARTPACK     ` tag for the target kit.
-   This will only work with permenant kits.
-   The percent sign (%) can be used as a trailing wildcard.

**Examples:**

`     PREKIT:1,Starting Gold    `

Makes sure the character has the 'Starting Gold' kit.

`     PREKIT:2,Flumph Abilities,Flumph Skills    `

Makes sure the character has both kits

`     PREKIT:1,Alchemist's Kit    `

Makes sure the character does not have the Alchemist's Kit

`     PREKIT:1,Dragon Age%    `

Will match both 'Dragon Age 01 ~ Wyrmling' and 'Dragon Age 12 ~ Great Wyrm''

------------------------------------------------------------------------

**<span id="prelang"></span> Tag Name:** PRELANG:x,y,y

**Variables Used (x):** Number (The number of languages a character must know).

**Variables Used (y):** Text (The name of a language the character must know).

**Variables Used (y):** ANY (Indicator the any language will be allowed to help meet the required number).

**What it does:**

Makes speaking certain languages a prerequisite.

**Examples:**

`     PRELANG:1,Dwarven,Elven    `

Character must be able to speak either "Dwarven" or "Elven".

`     PRELANG:2,Dwarven,Elven    `

Character must be able to speak both "Dwarven" and "Elven".

`     PRELANG:2,Dwarven,Elven,Halfling    `

Character must be able to speak any two of "Dwarven", "Elven" or "Halfling".

`     PRELANG:3,ANY    `

Character must be able to speak any three languages.

`     PRELANG:4,TYPE=Spoken    `

Character must be able to speak any four languages that can be spoken.

------------------------------------------------------------------------

### PRELEGS

Requires a creature to have a certain number of legs.

See also <a href="#prehands">PREHANDS</a>.

#### Syntax

`     PRELEGSoperator:number_of_legs    `

`     operator    ` is a comparison operator from the list `     EQ    ` , `     LT    ` , `     LTEQ    ` , `     GT    ` , `     GTEQ    ` , or `     NEQ    ` . The comparison operator is written as part of the tag name, i.e. `     PRELEGSLTEQ    ` . See [comparison operators](#comparison-operators) for more details.

The `     operator    ` is written as part of the tag name, i.e. `     PRELEGSNEQ    ` or `     PRELEGSGTEQ    ` .

`number_of_legs` is the required number of legs.

#### Examples

`     PRELEGSGTEQ:4    `

Character must have at least 4 legs.

------------------------------------------------------------------------

<span id="prelevel"></span> \*\*\* Updated 5.13.5

**Tag Name:** PRELEVEL:x,y

**Variables Used (x):** MIN=Number or formula (Minimum total level).

**Variables Used (y):** MAX=Number or formula (Maximum total level).

**What it does:**

-   Provides the minimum and/or maximum total levels, including all character, racial, or monster class levels, the character must have to qualify.
-   Either value may be used without the other.

**Example:**

`     PRELEVEL:MIN=5    `

Character must be at least 5th level.

`     PRELEVEL:MAX=5    `

Character must be no greater than 5th level.

`     PRELEVEL:MIN=5,MAX=10    `

Character must be at least 5th level but no greater than 10th level.

`     !PRELEVEL:MIN=3,MAX=6    `

Character must NOT be 3rd, 4th, 5th or 6th level.

------------------------------------------------------------------------

**<span id="prelevelmax"></span> Tag Name:** PRELEVELMAX:x

**Variables Used (x):** Number (The maximum level).

**What it does:**

Requires that a character have a maximum number of character (non-monster) levels.

-- Do NOT mix PRELEVELMAX and BONUS:SKILLPOINTS|NUMBER, since it results in double negative and some very bad exceptions in the code.

**Example:**

`     PRELEVELMAX:10    `

Character cannot be over level 10.

------------------------------------------------------------------------

<span id="premove"></span> \*\*\* Updated 5.9.4

**Tag Name:** PREMOVE:x,y=z,y=z

**Variables Used (x):** Number (The minimum number movement types which must pass).

**Variables Used (y):** Text (The name of a type of movement).

**Variables Used (z):** Number (The minimum movement rate for the associated movement type).

**What it does:**

Makes movement rate a prerequisite.

**Examples:**

`     PREMOVE:1,Walk=30,Fly=20    `

Character must be able to either walk at speed 30 OR fly at speed 20

`     PREMOVE:1,Swim=10    `

Character must be able to swim at speed 10

`     PREMOVE:2,Walk=30,Climb=15    `

Character must be able to walk at speed 30 AND climb at speed 15

------------------------------------------------------------------------

**<span id="premult"></span> Tag Name:** PREMULT:x,y,y

**Variables Used (x):** Number (Number of Prereqs)

**Variables Used (y):** \[Text\] (Embedded PRExxx tags)

**What it does:**

-   PREMULT is a special PRExxx tag in that one embeds other PRExxx tags in it, allowing for the application of a logical OR across PRExxx tags.
-   PREMULT can be embedded into itself.

**Examples:**

`     PREMULT:1,[PRERACE:Gnome],[PRECLASS:1,Cleric=1]    `

Character must be either a "Gnome" or a "Cleric".

`     PREMULT:1,[PRERACE:Gnome],[PREMULT:2,[PRESIZEGTEQ:M],[PREFEAT:1,Alertness]]    `

Character must be "Gnome" OR a medium sized or larger creature with the "Alertness" feat.

`     PREMULT:1,[!PRERACE:%],[PRERACE:Bunch of Rocks]    `

Character only be from a "Bunch of Rocks".

`     Stamina.MOD <tab> PRE:.CLEAR <tab> PREMULT:1,[PREFEAT:1,Robust],[PRECLASS:1,Super Soldier=1]    `

The Stamna feat is modified to remove all PRE tags and replace with the following.

------------------------------------------------------------------------

<span id="prepclevel"></span> \*\*\* New 5.13.6

**Tag Name:** PREPCLEVEL:x,y

**Variables Used (x):** MIN=Number or formula (Minimum total level).

**Variables Used (y):** MAX=Number or formula (Maximum total level).

**What it does:**

-   Provides the minimum and/or maximum total character class levels the character must have to qualify.
-   PREPCLEVEL does not count Monster (Racial) class levels.
-   Class types PC, NPC and Prestige all count as character classes.
-   Either value may be used without the other.

**Example:**

`     PREPCLEVEL:MAX=1    `

Character must be no greater than 1st level. This can be used to qualify feats which can only be taken as a 1st level character. A character with racial hitdice (monster levels) but only 1 or no character class levels will still qualify.

`     PREPCLEVEL:MIN=5    `

Character must be at least 5th level.

`     PREPCLEVEL:MAX=5    `

Character must be no greater than 5th level.

`     PREPCLEVEL:MIN=5,MAX=10    `

Character must be at least 5th level but no greater than 10th level.

`     !PREPCLEVEL:MIN=2,MAX=4    `

Character must NOT be 2nd, 3rd or 4th level.

------------------------------------------------------------------------

<span id="prepointbuymethod"></span> \*\*\* Updated 5.13.12

**Tag Name:** PREPOINTBUYMETHOD:x,y,y

**Variables Used (x):** Number (Number of required point-buy methods)

**Variables Used (y):** Text (Pointbuy method)

**What it does:**

-   Sets a prerequisite based upon the saved method used by the point-buy system of stat determination.
-   **Note:** Only one (1) point-buy method is used at any given time so using a value greater then one is unecessary.

**Examples:**

`     PREPOINTBUYMETHOD:1,Standard    `

Sets a prerequisite of "Standard" for the point-buy saved method.

`     PREPOINTBUYMETHOD:1,Standard,High-powered    `

Sets a prerequisite of "Standard" or "High-powered" for the point-buy saved method.

------------------------------------------------------------------------

<span id="preprofwitharmor"></span> \*\*\* New 5.15.8

**Tag Name:** PREPROFWITHARMOR:x,y,y

**Variables Used (x):** Number (The number of armor proficiencies needed).

**Variables Used (y):** Text (The name of a Armor Prof).

**Variables Used (y):** TYPE.Text (The type of Armor prof).

**What it does:**

Checks for Armor proficiency requirements.

**Examples:**

`     PREPROFWITHARMOR:1,Chainmail,Full Plate    `

Character must be proficient with either "Chainmail" or "Full Plate".

`     PREPROFWITHARMOR:1,TYPE.Medium    `

Character must be proficient with Medium armor.

------------------------------------------------------------------------

<span id="preprofwithshield"></span> \*\*\* NEW 5.15.8

**Tag Name:** PREPROFWITHSHIELD:x,y,y

**Variables Used (x):** Number (The number of shield proficiencies needed).

**Variables Used (y):** Text (The name of a shield proficiency).

**Variables Used (y):** TYPE.Text (The type of shield proficiency).

**What it does:**

Checks for shield proficiency requirements.

**Examples:**

`     PREPROFWITHSHIELD:1,Buckler,Large Shield    `

Character must be proficient with either "Buckler" or "Large Shield".

`     PREPROFWITHSHIELD:1,TYPE.Tower    `

Character must be proficient with Tower shields.

------------------------------------------------------------------------

<span id="prerace"></span> \*\*\* Updated 5.9.5

**Tag Name:** PRERACE:x,y,y

**Variables Used (x):** Number (The number of racial properties which must be met).

**Variables Used (y):** Text (The name of a race).

**Variables Used (y):** TYPE=Text (The name of a race type defined by the race TYPE tag).

**Variables Used (y):** RACETYPE=Text (The name of a race type defined by the race RACETYPE tag).

**Variables Used (y):** RACESUBTYPE=Text (The name of a race subtype defined by the race RACESUBTYPE tag).

**What it does:**

The character must be one of the listed races or possess one of the racial properties.

TYPE=&lt;Text&gt; can be used to check types set by the race TYPE tag.

RACETYPE=&lt;Race Type&gt; can be used to check racial types such as Humanoid, Giant and Outsider.

RACESUBTYPE=&lt;Race Subtype&gt; can be used to check racial subtypes such as Air, Evil and Extraplanar.

The wildcard character (%) can be used to include any text, for example Elf% will include any race name which begins with Elf. Without the wildcard character the race name must match exactly.

Enclosing a selection in square brackets (\[ \]) excludes a race. Brackets do not work for the TYPE, RACETYPE and RACESUBTYPE properties.

TYPE.xxx for reference, TYPE=xxx for assignment.

**Examples:**

`     PRERACE:1,Dwarf,Elf,Human    `

Character must be a "Dwarf", "Elf" or "Human".

`     PRERACE:1,Elf%,[Elf (aquatic)]    `

Character must be one of the "Elf" races, except "Elf (aquatic)".

`     PRERACE:1,TYPE=Dire    `

Character's race type must be "Dire".

`     PRERACE:1,RACETYPE=Giant    `

Character's race type must be "Giant".

`     !PRERACE:1,RACETYPE=Outsider    `

Character's race type must not be "Outsider".

`     PRERACE:1,RACESUBTYPE=Incorporeal    `

Character must have the "Incorporeal" subtype.

`     PRERACE:2,RACETYPE=Undead,RACESUBTYPE=Incorporeal    `

Character must have both a race type of "Undead" and a subtype of "Incorporeal".

`     PRERACE:1,%    `

This will pass if any race has been chosen but will fail if no race has been selected.

`     !PRERACE:1,%    `

This will pass if no race has been selected.

`     PRERACE:TYPE=Humanoid    `

This will pass if a humanoid race has been selected.

`     CLASS:Sorcerer.MOD <tab> !PRERACE:Human    `

This will pass if no Human race has been selected.

------------------------------------------------------------------------

### PREREACH

Makes the character's [reach](http://www.d20srd.org/srd/combat/movementPositionAndDistance.htm#bigandLittleCreaturesInCombat) a pre-requisite. (See also [d20 SRD - Reach Weapons](http://www.d20srd.org/srd/equipment/weapons.htm#reachWeapons).)

#### Status

New 5.11.6.

#### Syntax

`     PREREACHoperator:reach    `

`     operator    ` is a comparison operator from the list `     EQ    ` , `     LT    ` , `     LTEQ    ` , `     GT    ` , `     GTEQ    ` , or `     NEQ    ` . The comparison operator is written as part of the tag name, i.e. `     PREREACHLTEQ    ` . See [comparison operators](#comparison-operators)
 for more details.

The `     operator    ` is written as part of the tag name, i.e. `     PREREACHNEQ    ` or `     PREREACHGTEQ    ` .

`reach` is the creature's required reach, usually in feet.

#### Examples

`     PREREACHGTEQ:10    `

Character must have at least a reach of 10 feet.

`     PREREACHEQ:5    `

Character must have a reach of exactly 5 feet.

------------------------------------------------------------------------

**<span id="preregion"></span> Tag Name:** PREREGION:x

**Variables Used (y):** Text (The region name).

**What it does:**

Character's home region must match the listed text.

**Examples:**

`     PREREGION:Slithe    `

Character must hail from the "Slithe" region.

------------------------------------------------------------------------

**<span id="prerule"></span> Tag Name:** PRERULE:x,y

**Variables Used (x):** Number (Number of rules required)

**Variables Used (y):** Text (Rule name)

**What it does:**

-   Checks for the state of a rule.
-   The rule name must have been defined and match the variable ( `      VAR     ` ) entry in the *system/gameModes/rules.lst* file.
-   Rules can be turned on and off by the user in the **House Rules** section of the preferences.

**Example:**

`     PRERULE:1,SYS_WTPSK    `

The rule WeightPenaltyToSkill must be checked for this to apply.

------------------------------------------------------------------------

**<span id="presa"></span> Tag Name:** PRESA:x,y,y

**Variables Used (x):** Number (The number of Special Abilities a character must have).

**Variables Used (y):** Text (The name of an Special Abilities).

**What it does:**

-   Checks for the specified special abilities as a prerequisite.
-   Special abilities applied by the `      SAB     ` tag will satisfy this prerequisite.

**Example:**

`     PRESA:1,Turn undead,Rebuke undead,Smite Evil    `

Character must have any one of "Turn Undead", "Rebuke Undead" or "Smite Evil".

`     CLASS:Mystic Theurge.MOD <tab> !PRESA:1,Theurgy    `

Character must not have any Special Abilities of "Theurgy".

------------------------------------------------------------------------

### PRESIZE

Require the character is a particular size category, or in a particular range of size categories.

#### Status

#### Syntax

`     PRESIZEoperator:size    `

`     operator    ` is a comparison operator from the list `     EQ    ` , `     LT    ` , `     LTEQ    ` , `     GT    ` , `     GTEQ    ` , or `     NEQ    ` . The comparison operator is written as part of the tag name, i.e. `     PRESIZELTEQ    ` . See [comparison operators](#comparison-operators)
 for more details.

The `     operator    ` is written as part of the tag name, i.e. `     PRESIZENEQ    ` or `     PRESIZEGTEQ    ` .

`     size    ` is one of the `     SIZENAME    ` s defined in the "game mode", i.e. `     /system/gameModes/35e/sizeAdjustment.lst    ` . For the `     35e    ` game mode the `     SIZENAMES    ` are:

-   `      F     ` - fine
-   `      D     ` - diminutive
-   `      T     ` - tiny
-   `      S     ` - small
-   `      M     ` - medium
-   `      L     ` - large
-   `      H     ` - huge
-   `      G     ` - gargantuan
-   `      C     ` - colossal
-   `      P     ` - larger than colossal

#### Examples

1.  `       PRESIZEGTEQ:S      `

    Require the character is at least small-sized.

2.  `       PRESIZELT:S      `

    Require that the character is fine, diminutive, or tiny.

3.  `       PREMULT:2,[PRESIZEGTEQ:S],[PRESIZELTEQ:L]      `

    Require the character to be small, medium, or large.

    Alternately, `       PREMULT:1,[PRESIZEEQ:S],[PRESIZEEQ:M],[PRESIZEEQ:L]      ` would do the same thing.

------------------------------------------------------------------------

**<span id="preskill"></span> Tag Name:** PRESKILL:x,y=z,y=z

**Variables Used (x):** Number (Number of skills)

**Variables Used (y):** Text (Skill name)

**Variables Used (y):** TYPE.Text (Skill type)

**Variables Used (z):** Number (Skill ranks)

**What it does:**

-   Sets skill requirements.
-   If you require multiples of a particular type of skill, you need to include a `      TYPE.Text     ` sub-tag for each instance in the comma delimited list.
-   Skills types with multiple versions like "Craft (Pottery)" or "Knowledge (Local)" there must be a space between the name and the parentheses because the text must match the entry exactly.

**Examples:**

`     PRESKILL:1,Spot=10,Listen=10    `

Character must have at least 10 ranks in either "Spot" or "Listen".

`     PRESKILL:2,TYPE.Spy=2,TYPE.Spy=2    `

Character must have two "Spy" skills with at least two ranks in each of them.

`     PRESKILL:3,Appraise=3,Decipher Script=3,Knowledge (TYPE=Other)=3    `

Character must have three skills with at least three ranks in each of them.

------------------------------------------------------------------------

**<span id="preskillmult"></span> Tag Name:** PRESKILLMULT:x,y=z,y=z

**Variables Used (x):** Number (The number of skills that must be equal to or greater than the numbers specified for the check to succeed).

**Variables Used (y):** Text (A defined skill name).

**Variables Used (y):** TYPE=Text (A defined skill type).

**Variables Used (z):** Number (The number of ranks the associated skill must meet or exceed).

**What it does:**

-   Similar to PRESKILL.
-   This tag will set a flag on the character.
-   It only works as a prereq if the rank in the skill divided by the rank needed is equal to the flag.
-   The flag starts at 1, but goes up by one every-time the prereq is met. So the first feat would require 'rank' in the skill, the second feat 'rank\*2', and so on. That would support the way regional feats are described in FR.
-   If you require multiples of that type, you need to add TYPE.x for each instance in the comma delimited list.
-   This tag is merely a dummy tag that works like PRESKILL so we don't have to redesign the lst files when this tag gets working.

NOTE: This tag has some problems. It will work in some files but not all.

**Example:**

`     PRESKILLMULT:1,Spot=10,Listen=10    `

Character must have a Spot or Listen at 10 or above.

------------------------------------------------------------------------

<span id="preskillsit"></span> \*\*\* New 6.03.00

**Tag Name:** PRESKILLSIT:w,x,y=z,y=z

**Variables Used (w):** Number (Number of skills)

**Variables Used (x):** SKILL=Text (Skill name)

**Variables Used (y):** Text (Situation)

**Variables Used (z):** Number, Variable, or Formula (Skill ranks)

**What it does:**

Sets situational skill requirements.

**Examples:**

`     PRESKILLSIT:1,SKILL=Spot,Woodlands=10    `

Character must have at least 10 ranks in "Spot (Woodlands)".

------------------------------------------------------------------------

**<span id="preskilltot"></span> Tag Name:** PRESKILLTOT:x,x=y

**Variables Used (x):** Text (skill name).

**Variables Used (x):** TYPE=Text (skill type).

**Variables Used (y):** Number (total non-bonus skill ranks required).

**What it does:**

-   A comma-delimited list of skills whose total non-bonus ranks must equal y.
-   Skills may be identified by TYPE.

**Example:**

`     PRESKILLTOT:Spot,Listen,Search=30    `

Character must have a total of 30 non-bonus ranks between Spot, Search, and Listen skills.

------------------------------------------------------------------------

**<span id="prespell"></span> Tag Name:** PRESPELL:x,y,y

**Variables Used (x):** Number (The number of spells required).

**Variables Used (y):** Text (The name of a spell).

**What it does:**

Sets spell requirements.

**Example:**

`     PRESPELL:1,Magic Missile,Lightning Bolt    `

Character must have either Magic Missile OR Lightning Bolt in their spell list.

------------------------------------------------------------------------

**<span id="prespellbook"></span> Tag Name:** PRESPELLBOOK:x

**Variables Used (x)** : YES

**Variables Used (x)** : NO

**What it does:**

Sets spellbook requirements.

**Examples:**

`     PRESPELLBOOK:YES    `

At least one of the character's classes must use spell books.

`     PRESPELLBOOK:NO    `

None of the character's classes can use spell books.

------------------------------------------------------------------------

**<span id="prespellcast"></span> Tag Name:** PRESPELLCAST:x=y

**Variables Used (x):** MEMORIZE (Set requirements based on memorization).

**Variables Used (x):** TYPE (Set requirements based on spelltype).

**Variables Used (y):** Y (Yes - used with MEMORIZE).

**Variables Used (y):** N (No - used with MEMORIZE).

**Variables Used (y):** Text (A spelltype - used with TYPE).

**What it does:**

Basically each label=value pair is processed, and as the character's classes fail to meet that pair, the class is removed from the list. After all the label=value pairs have been processed, if the character has any classes remaining (meaning they meet all the requirements), then this prerequisite is met.

**Examples:**

`     PRESPELLCAST:MEMORIZE=Y    `

Character's class must have to memorize spells.

`     PRESPELLCAST:MEMORIZE=N    `

Character's class must NOT have to memorize spells.

`     PRESPELLCAST:TYPE=Arcane    `

Character must be able to cast arcane spells.

`     PRESPELLCAST:TYPE=Divine    `

Character must be able to cast divine spells.

`     PRESPELLCAST:TYPE=Arcane,TYPE=Divine    `

Character must be able to cast arcane/divine spells.

------------------------------------------------------------------------

**<span id="prespelldescriptor"></span> Tag Name:** PRESPELLDESCRIPTOR:x,y=z

**Variables Used (x):** Number (Number of spells known)

**Variables Used (y):** Text (Spell descriptor)

**Variables Used (z):** Number (Minimum spell level)

**What it does:**

Sets spell descriptor requirements. (i.e. Fire, Acid, Sonic, Evil, Good, Mind-Affecting, etc.)

**Example:**

`     PRESPELLDESCRIPTOR:4,Mind-Affecting=3    `

Character must have at least four 3rd level or higher Mind-Affecting spells to meet the requirement.

**Deprecated Syntax:**

`     PRESPELLDESCRIPTOR:y,x,z    `

------------------------------------------------------------------------

<span id="prespellschool"></span> \*\*\* NEW 5.12

**Tag Name:** PRESPELLSCHOOL:x,y=z,y=z

**Variables Used (x):** Number (Number of spells known)

**Variables Used (y):** Text (Name of a school of magic).

**Variables Used (z):** Number (Minimum spell level)

**What it does:**

-   Establishes a requirement for (x) number of spells from magic school (y) of at least level (z).
-   You may include as many "School=Level" pairs as you require as a comma-delimited (",") list.

**Example:**

`     PRESPELLSCHOOL:3,Necromancy=2    `

Character must have at least three 2nd level or higher Necromancy spells to meet the requirement.

------------------------------------------------------------------------

<span id="prespellschoolsub"></span> \*\*\* Updated 5.12

**Tag Name:** PRESPELLSCHOOLSUB:x,y=z,y=z

**Variables Used (x):** Number (Number of spells known)

**Variables Used (y):** Text (Name of sub-school of magic.)

**Variables Used (z):** Number (Minimum spell level)

**What it does:**

-   Establishes a requirement for (x) number of spells from magic sub-school (y) of at least level (z).
-   You may include as many "Sub-School=Level" pairs as you require as a comma-delimited (",") list.

**Example:**

`     PRESPELLSCHOOLSUB:3,Creation=2    `

Character must have at least three 2nd level "Creation" spells to meet the requirement.

------------------------------------------------------------------------

<span id="prespelltype"></span> \*\*\* Updated 5.10

**Tag Name:** PRESPELLTYPE:x,y=z

**Variables Used (x):** Number (Number of spells known).

**Variables Used (y):** Arcane (Arcane type spells).

**Variables Used (y):** Divine (Divine type spells).

**Variables Used (y):** Psionic (Psionic type spells).

**Variables Used (y):** ANY (Any type of spell).

**Variables Used (z):** Number (Spell level).

**What it does:**

-   Sets spell type requirements. (Number of spells known of a specific type and level.)
-   Use two, or more, PRESPELLTYPE tags if more than one spell type is required

**Example:**

`     PRESPELLTYPE:4,Arcane=5    `

Character must have at least four 5th level arcane spells to meet the requirement.

`     PRESPELLTYPE:1,Arcane=3,Divine=3,Psionic=3    `

Character must have at least one 3rd level spell from any of the "Arcane", "Divine", or "Psionic" types.

`     PRESPELLTYPE:1,Arcane=3 <tab> PRESPELLTYPE:1,Divine=3    `

Character must have at least one 3rd level "Arcane" type spell and one 3rd level "Divine" type spell.

------------------------------------------------------------------------

### PRESR

Makes a character's spell resistance (not including SR from equipment) a prerequisite.

#### Syntax

`     PRESRoperator:spell_resistance    `

`     operator    ` is a comparison operator from the list `     EQ    ` , `     LT    ` , `     LTEQ    ` , `     GT    ` , `     GTEQ    ` , or `     NEQ    ` . The comparison operator is written as part of the tag name, i.e. `     PREVARLTEQ    ` . See [comparison operators](#comparison-operators) for more details.

`     spell_resistance    ` is the required spell resistance, before equipment bonuses to SR.

**Example:**

`     PRESRGTEQ:10    `

Character must have spell resistance of 10 or greater.

`     PRESREQ:10    `

Character must have spell resistance of exactly 10.

------------------------------------------------------------------------

**<span id="prestat"></span> Tag Name:** PRESTAT:x,y=z,y=z

**Variables Used (x):** Number (The number of stats that must match).

**Variables Used (y):** Text (The stats abbreviation - as defined in statsandchecks.lst).

**Variables Used (z):** Number (The minimum value of the stat).

**What it does:**

Sets stat requirements.

**Examples:**

`     PRESTAT:1,STR=18    `

Character must have an 18 Strength to meet the requirement.

`     PRESTAT:1,STR=18,WIS=18    `

Either STR or WIS at 18 - one of the two listed must meet the requirements.

`     PRESTAT:2,STR=18,WIS=18    `

BOTH STR and WIS at 18 - two of the two listed must meet the requirements.

`     PRESTAT:1,STR=15,WIS=13    `

Either STR at 15 or WIS at 13 - one of the two listed must meet the requirements.

`     PRESTAT:2,STR=13,INT=10,CHA=13    `

Either STR at 13, INT at 10 or CHA at 13 - two of the three listed must meet the requirements.

------------------------------------------------------------------------

**<span id="presubclass"></span> Tag Name:** PRESUBCLASS:x,y,y

**Variables Used (x):** Number (number of needed occurrences).

**Variables Used (y):** Text (Subclass Name).

**What it does:**

Sets subclass requirements.

**Examples:**

`     PRESUBCLASS:1,Evoker,Abjurer,Enchanter,Illusionist    `

Character must be one of the listed subclasses.

------------------------------------------------------------------------

**<span id="pretemplate"></span> Tag Name:** PRETEMPLATE:x,y,y

**Variables Used (x):** Number (Number of templates required)

**Variables Used (y):** Text (Template name)

**What it does:**

-   Sets template requirements. If one of the template names in the list matches, the prerequisite is met.
-   The wildcard character (%) can be used within this tag.

**Example:**

`     PRETEMPLATE:1,Celestial,Fiendish    `

Character must be either "Celestial" or "Fiendish".

`     PRETEMPLATE:1,Feral%    `

Character must be any subrace of Feral.

------------------------------------------------------------------------

**<span id="pretext"></span> Tag Name:** PRETEXT:x

**Variables Used (x):** Text (Explanation of requirement)

**What it does:**

This is used when there are special requirements which are not covered by another PRExxx tag.

Mainly used for feats the text is displayed in the same field as the other pre-requisites.

This tag in itself does not disqualify anything but alerts the player to the additional pre-requisites.

**Example:**

`     PRETEXT:Character must make a sacrifice of bananas to the Monkey God.    `

Explains what additional requirements are needed for the character to qualify.

------------------------------------------------------------------------

<span id="pretotalab"></span> \*\*\* New 6.03.x

**Tag Name:** PRETOTALAB:x

**Variables Used (x):** Number (Integer)

**What it does:**

This sets the total attack bonus requirement and considers both the base attack bonu and the epic attack bonus.

**Example:**

`     PRETOTALAB:21    `

Required total attack bonus is 21.

------------------------------------------------------------------------

**<span id="pretype"></span> Tag Name:** PRETYPE:x,y,y

**Variables Used (x):** Number (Number of types required)

**Variables Used (y):** Text (Type)

**What it does:**

-   Sets specific "type" requirements appropriate to the object being applied to, i.e. Racial, Equipment, EqMod, Template, etc.
-   See the `      PRETYPE     ` entry in [Equipment](../datafilestagpages/datafilesequipment.html#pretype) or [Equipment Modifier](../datafilestagpages/datafilesequipmentmodifiers.html#pretype) files for usage.
-   If you need to prerequisite race type from equipment you can use [PRETEMPLATE](#pretemplate) to check for the racial type template.

**Examples:**

`     PRETYPE:1,Elemental,Fey,Outsider    `

Type must either "Elemental", "Fey" or "Outsider".

`     PRETYPE:2,Humanoid,Undead    `

Character must be an "Undead Humanoid".

`     PRETYPE:1,Heavy    `

Object must have the type "Heavy".

------------------------------------------------------------------------

**<span id="preuatt"></span> Tag Name:** PREUATT:x

**Variables Used:** Number (Unarmed Attack Bonus number).

**What it does:**

Number indicates the minimum unarmed base attack bonus.

**Example:**

`     PREUATT:4    `

This would apply only if the PC had minimum unarmed attack bonus of 4.

------------------------------------------------------------------------

### PREVAR

Requires that a `     VAR    ` variable has a certain value or range of values.

#### Status

#### Syntax

`     PREVARoperator:varname,value    `

`     operator    ` is a comparison operator from the list `     EQ    ` , `     LT    ` , `     LTEQ    ` , `     GT    ` , `     GTEQ    ` , or `     NEQ    ` . The comparison operator is written as part of the tag name, i.e. `     PREVARLTEQ    ` . See [comparison operators](#comparison-operators) for more details.

`     varname    ` is the name of a variable - as used in a `     DEFINE:    ` or `     BONUS:VAR    ` statement.

`     value    ` is the value the variable is to be compared to.

#### Examples

1.  `       PREVARGT:Rage,4      `

    Character must must have a variable 'Rage' with a value greater than four.

2.  `       PREVARGT:SneakAttack,5      `

    Character must have a variable 'Sneak Attack' with a value greater than five.

3.  `       PREVARGT:SneakAttack,5,Rage,4      `

    Character must have a variable 'SneakAttack' with a value greater than five and a variable 'Rage' with a value greater than four.

------------------------------------------------------------------------

**<span id="prevision"></span> Tag Name:** PREVISION:x,y=z,y=z

**Variables Used (x):** Number (Number of matching vision types)

**Variables Used (y):** Text (Vision type)

**Variables Used (z):** Number (Distance for related vision type)

**Variables Used (z):** ANY

**What it does:**

-   Sets the vision requirements by type and distance.
-   The variable (z) is required for each vision type (y) provided and determines the minimum distance required for the related vision type.

**Examples:**

`     PREVISION:2,Normal=ANY,Darkvision=ANY    `

Character must have both Normal and Darkvision.

`     PREVISION:1,Blindsight=30,Darkvision=30    `

Character must have either Blindsight or Darkvision at at least 30.

`     PREVISION:1,Low-Light=ANY    `

Character must have low-light vision to any distance.

------------------------------------------------------------------------

<span id="preweaponprof"></span> \*\*\* Updated 5.13.0

**Tag Name:** PREWEAPONPROF:x,y,y

**Variables Used (x):** Number (Number of matching proficiencies).

**Variables Used (y):** Text (Weapon proficiency name).

**Variables Used (y):** TYPE.Text (Weapon Proficiency type).

**Variables Used (y):** DEITYWEAPON (Deities favored weapon).

**What it does:**

-   Sets requirements based upon weapon proficiencies held.
-   Though types can be used within this tag, their use can be tricky and can sometiime produce unexpected or unwanted results.

**Examples:**

`     PREWEAPONPROF:2,Kama,Katana    `

Character must have proficiency with both the "Kama" and the "Katana".

`     PREWEAPONPROF:1,TYPE.Exotic    `

Character must have a weapon proficiency of type "Exotic".

`     PREWEAPONPROF:1,TYPE.Martial,Chain (Spiked)    `

Character must have either a weapon proficiency of type "Martial" or proficiency with "Chain (Spiked)".

`     PREWEAPONPROF:1,DEITYWEAPON    `

Character must have proficiency with one of the chosen deity's favored weapons.

------------------------------------------------------------------------

Misc PRExxx Stuff
-----------------

------------------------------------------------------------------------

**<span id="preclear"></span> Tag Name:** PRE:.CLEAR

**Variables Used:** .CLEAR

**What it does:**

Clear all prerequisites.

**Example:**

`     PRE:.CLEAR    `

Clears all prerequisites.

------------------------------------------------------------------------

**<span id="prexxxqdata"></span> Tag Name:** PRExxx:Q:data

**Variables Used:** Q

**What it does:**

This is a variation on any PRE tag listed above. Adding a Q: between the tag and it's data will cause it to take precedence over the QUALIFY tag. Thus allowing QUALIFY to null out some but not all prereqs.

**Example:**

`     QUALIFY:Whirlwind Attack    ` (In Template)

`     PRESTAT:Q:1,DEX=13    ` (In Whirlwind Attack Feat)

If you have QUALIFY:Whirlwind Attack in a template (which means the character ignores all the prereqs), but you still want the DEX prereq to be enforced, you would edit the PRE in the Whirlwind Attack feat to read as above.

------------------------------------------------------------------------


