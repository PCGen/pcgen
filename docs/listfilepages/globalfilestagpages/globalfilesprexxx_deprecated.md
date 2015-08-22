### PREFEAT

#### Status

**`        PREFEAT       ` is deprecated. Use [`         PREABILITY        `](./globalfilesprexxx.html#preability) instead.**

#### Historical documentation

**Tag Name:** PREFEAT:x,y,z

**Variables Used (x):** Number (Number of required feats)

**Variables Used (y):** CHECKMULT (Used to make the program count each instance of a feat separately - optional)

**Variables Used (z):** Text (Feat name)

**Variables Used (z):** \[Text\] (Name of a feat a character must NOT have)

**Variables Used (z):** TYPE=Text (Feat type)

**Variables Used (z):** \[TYPE=Text\] (Type of feat the character must NOT have)

**What it does:**

-   Sets character feat requirements.
-   Feats with multiple instances where the instance is expressed within parentheses, e.g. Skill Focus or Spell Focus, there is no space between the name of the feat and the parentheses.
-   Use of the `       (TYPE=<text>)      ` syntax with feats with multiple instances, e.g. `       Weapon Focus(TYPE=Bow)      ` , though allowed, is only supported for feats with internal choosers for `       DOMAIN      ` , `       SKILL      ` , `       SPELL      ` , and `       WEAPONPROF      ` types.

**Examples:**

`      PREFEAT:1,Dodge,Combat Reflexes     `

Character must have either "Dodge" or "Combat Reflexes".

`      PREFEAT:2,CHECKMULT,Spell Focus     `

Character must have "Spell Focus" for two schools.

`      PREFEAT:2,CHECKMULT,Spell Focus,[Spell Focus(Enchantment)]     `

Character must have "Spell Focus" for two schools, but not the "Enchantment" school.

`      PREFEAT:2,Weapon Focus(TYPE=Bow),Weapon Focus(Longsword)     `

Character must have both "Weapon Focus(Longsword)" and any one "Bow" type "Weapon Focus".

`      PREFEAT:2,CHECKMULT,Weapon Focus(TYPE=Sword)     `

Character must have two "Sword" type "Weapon Focus" feats.

`      PREFEAT:2,Skill Focus(Spot),Skill Focus(Listen),Skill Focus(Search)     `

Character must have any two of "Skill Focus(Spot)", "Skill Focus(Listen)", or "Skill Focus(Search)".

`      PREFEAT:2,TYPE=ItemCreation     `

Character must have any two "ItemCreation" type feats.

`      PREFEAT:1,Skill Focus(Knowledge%)     `

Character must have one "Skill Focus" feat for a "Knowledge" sub-type skill.

`      Magecraft (Charismatic).MOD <tab> !PREFEAT:1,Untapped Potential     `

Character must not (!) have one feat called "Untapped Potential".