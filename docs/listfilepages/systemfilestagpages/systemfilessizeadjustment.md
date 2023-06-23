Game Mode: sizeAdjustment.lst
=============================

The `sizeAdjustment.lst` is a part of the "game mode".

`sizeAdjustment.lst` defines:

* what size categories exist in the game mode, such as 'Small', 'Medium', and 'Large'.
* what bonuses apply to each size category; i.e. small creatures having better Hide checks, or big creatures being able to carry more weight.
* what penalties apply to each size category; i.e. small creatures having a reduced carrying capacity, or big creatures having to pay more for weapons and armour. 

The `sizeAdjustment.lst` files can be found in the `system/gameModes/<game_mode_name>/` directory for each game mode.

## Structure of the `sizeAdjustment.lst` file

Each creature size is identified by a `SIZENAME`.

Tokens are added to the `SIZENAME` to indicate the in-game effects of the creature's size. The `SIZENUM` token is mandatory; all others are optional.

A typical size definition, from `system/gameModes/35e/sizeAdjustment.lst`, looks like:

    SIZENAME:S
      →  ABB:S
      →  DISPLAYNAME:Small
      →  BONUS:ITEMCOST|TYPE=Ammunition,TYPE=Armor,TYPE=Shield,TYPE=Weapon|1
    SIZENAME:S
      →  BONUS:ITEMWEIGHT|TYPE=Ammunition,TYPE=Armor,TYPE=Shield,TYPE=Weapon|0.5
      →  BONUS:ITEMWEIGHT|TYPE=Goods|0.25
    SIZENAME:S
      →  BONUS:COMBAT|AC|1|TYPE=Size
      →  BONUS:COMBAT|TOHIT|1|TYPE=SIZE
      →  BONUS:COMBAT|TOHIT.GRAPPLE|-5|TYPE=Size
    SIZENAME:S
      →  BONUS:ITEMCAPACITY|TYPE=Goods|0.25
    SIZENAME:S
      →  BONUS:SKILL|Hide|4|TYPE=SIZE
    SIZENAME:S
      →  BONUS:LOADMULT|TYPE=SIZE|0.25|PRELEGSGTEQ:4
    SIZENAME:S
      →  BONUS:STAT|STR|2|PREBASESIZELT:Tiny|PREVAREQ:BypassSizeMods,0
      →  BONUS:STAT|STR|4|PREBASESIZELT:Small|PREVAREQ:BypassSizeMods,0
    SIZENAME:S
      →  BONUS:STAT|DEX|-2|PREBASESIZEEQ:Fine|PREVAREQ:BypassSizeMods,0
      →  BONUS:STAT|DEX|-2|PREBASESIZELT:Tiny|PREVAREQ:BypassSizeMods,0
      →  BONUS:STAT|DEX|-2|PREBASESIZELT:Small|PREVAREQ:BypassSizeMods,0
    SIZENAME:S
      →  SIZENUM:040


----

## Tags

<h3 id="ABB">ABB</h3>

**Optional:** A one letter abbreviation of the size name.

#### Syntax

`ABB:X`, where `X` is a single letter.

#### Example

`SIZENAME:Fine  →  ABB:F` sets the abbreviation for the `Fine` size to the letter `F`.

#### Notes

The `ABB` tag is implemented in [code/src/java/plugin/lsttokens/sizeadjustment/AbbToken.java](https://github.com/PCGen/pcgen/blob/master/code/src/java/plugin/lsttokens/sizeadjustment/AbbToken.java).

------------------------------------------------------------------------

<h3 id="ISDEFAULTSIZE">ISDEFAULTSIZE</h3>

**Mandatory:** Marks which of the creature sizes is the default for the game mode.

#### Syntax

`SIZENAME:size_1 → ISDEFAULTSIZE:Y` indicates that `size_1` **is** the default size for the game mode.

`SIZENAME:size_2 → ISDEFAULTSIZE:N` indicates that `size_2` **is not** the default size.

#### Example 

`SIZENAME:Medium → ISDEFAULTSIZE:Y` makes `Medium` the default size for this game mode.


#### Notes

There must be one, and only one, size marked as the default size.

- It is an error to have no size marked as the default size.
- It is an error to have more than one default size.

If the `ISDEFAULTSIZE` tag is missing for a particular `SIZENAME`, then  `ISDEFAULTSIZE:N` is assumed.

------------------------------------------------------------------------

<h3 id="SIZENAME">SIZENAME</h3>

Identifies a creature size category.

Each line of the `sizeAdjustment.lst` file **MUST** begin with a `SIZENAME` tag. Features are added to the category by adding sub-tags after the `SIZENAME`, such as `BONUS` tags.

The same `SIZENAME` may appear on multiple lines. The tokens following the `SIZENAME` are appended to any tokens for the same `SIZENAME` on preceding lines.

#### Status

New in 5.10.1.

#### Syntax

`SIZENAME:size_name`, where `size_name` is a single letter, i.e. `S`, `M`, `L`. This is the current standard for all game modes included in PCGen.

Alternately, `size_name` may be a single word such as `Small`, `Medium`, `Large`, etc.

**Example:**

`SIZENAME:F` defines a creature size named `F`. (This form is preferred.)

`SIZENAME:Fine` defines a creature size `Fine`.

----

<h3 id="SIZENUM">SIZENUM</h3>

**Mandatory:** Defines the sorting order for the size categories.

#### Status

New in 6.05.04.

Before 6.05.04, the size order was inferred from the order of the `SIZENAME` tags in the `sizeAdjustment.lst` file. As of 6.05.04, the order is explicitly indicated using the `SIZENUM` tag.

* [JIRA NEWTAG-480](https://pcgenorg.atlassian.net/browse/NEWTAG-480) / [GitHub PR #397](https://github.com/PCGen/pcgen/pull/397) - token parser changes.
* [JIRA DATA-2507](https://pcgenorg.atlassian.net/browse/DATA-2507) / [GitHub PR #410](https://github.com/PCGen/pcgen/pull/410) - added `SIZENUM` tags to all the default game modes.

#### Syntax:

`SIZENAME:size_name  →  SIZENUM:size_number`

`size_number` is an integer. Bigger numbers correspond to larger size categories.

#### Example:

The following code, included in the `35e` version of `sizeAdjustment.lst`, defines the ordering of the `35e` size categories:

    SIZENAME:F	SIZENUM:010
    SIZENAME:D	SIZENUM:020
    SIZENAME:T	SIZENUM:030
    SIZENAME:S	SIZENUM:040
    SIZENAME:M	SIZENUM:050
    SIZENAME:L	SIZENUM:060
    SIZENAME:H	SIZENUM:070
    SIZENAME:G	SIZENUM:080
    SIZENAME:C	SIZENUM:090
    SIZENAME:P	SIZENUM:100

------------------------------------------------------------------------

## Global BONUS tags which are useful in `sizeAdjustment.lst`

All global `BONUS` tags may be used in `sizeAdjustment.lst`.

Some useful tags include:

* `BONUS:COMBAT|AC|1|TYPE=Size` - grant a +1 size AC bonus due to size.
* `BONUS:COMBAT|TOHIT|1|Type=Size` - grant a +1 attack bonus due to size.
* `BONUS:COMBAT|TOHIT.GRAPPLE|5|Type=Size` - grant a bonus on Grapple checks due to large size.
* `BONUS:SKILL|Hide|4|TYPE=Size` - grant a bonus on Hide checks due to small size.

------------------------------------------------------------------------

<h3 id="BONUSITEMCAPACITY">BONUS:ITEMCAPACITY</h3>

Changes the "item carrying capacity" based on the size of the character.

This is used to implement the DnD 3.5 rule for equipment made in different sizes for different creatures, such as backpacks and bed-rolls. These items  *"... weigh one-quarter this amount when made for Small characters. Containers for Small characters also **carry one-quarter the normal amount.**"* ([Refer to the d20 SRD.](http://www.d20srd.org/srd/equipment/goodsAndServices.htm#adventuringGear))



#### Status

New in 5.10.1

#### Syntax

`BONUS:ITEMCAPACITY|TYPE=item_type|capacity_multiplier`

* `item_type` is a type of equipment from the data set's equipment files, i.e. the `35e` files `rsrd_equip_arms_and_armor.lst`, `rsrd_equip_general.lst`, and `rsrd_equip_magic_items.lst`. Such types might include:
  * `Resizable` - for items which are [supposed to come in different sizes for different characters](http://www.d20srd.org/srd/equipment/goodsAndServices.htm#adventuringGear), like backpacks, blankets, bedrolls, and so on.
  * `Goods` - seems to include the majority of things in the `rsrd_equip_general.lst`, which covers general [goods and services](http://www.d20srd.org/srd/equipment/goodsAndServices.htm#adventuringGear).

* `capacity_multiplier` is a number.

#### Examples

* `BONUS:ITEMCAPACITY|TYPE=Resizable|0.25`

  Reduces the carrying capacity of items of `TYPE=Resizable` to one quarter of the normal value.

* `BONUS:ITEMCAPACITY|TYPE=Goods|2`

  Increases the carrying capacity of `TYPE=Goods` to double the normal value.

------------------------------------------------------------------------

<h3 id="BONUSITEMCOST">BONUS:ITEMCOST</h3>

Modifies the cost of items based on the item's size category, i.e. the size of the creature the item was made for.

For example, in 3.5e, a large-sized weapon [costs twice as much](http://www.d20srd.org/srd/equipment/weapons.htm#cost) as a medium-sized weapon, and large-sized armour [costs twice as much](http://www.d20srd.org/srd/equipment/armor.htm#armorForUnusualCreatures) as medium-sized armour.

#### Status

New in 5.10.1

#### Syntax

`BONUS:ITEMCOST|TYPE=item_type_1,TYPE=item_type_2,...|cost_multiplier`

* `item_type` is a type of equipment from the data set's equipment files, i.e. the `35e` files `rsrd_equip_arms_and_armor.lst`, `rsrd_equip_general.lst`, and `rsrd_equip_magic_items.lst`. Such types might include:
	* `Armor`
	* `Weapon`
	* `Potion`
	* Very specific types such as `Quarterstaff`.
* `cost_multiplier` is a number.

#### Examples

* `BONUS:ITEMCOST|TYPE=Weapon|0.5`
 
  Halves the cost of weapons.
 
* `BONUS:ITEMCOST|TYPE=Weapon,TYPE=Armor|2`
 
  Doubles the cost of weapons and armour.
 
* `BONUS:ITEMCOST|TYPE=Scrolls,TYPE=Potions|1`
 
  Leaves the cost of scrolls and potions un-modified.
 
* `BONUS:ITEMCOST|TYPE=Alchemical,TYPE=Liquid,TYPE=Clothing,TYPE=Food|2`
 
  Doubles the costs of all items of type Alchemical, Liquid, Clothing, or Food.

------------------------------------------------------------------------

<h3 id="BONUSITEMWEIGHT">BONUS:ITEMWEIGHT</h3>

Modifies the weight of items based on the item's size category, i.e. the size of the creature the item was made for.

For example, in 3.5e, armour for small-size creatures [weighs half as much](http://www.d20srd.org/srd/equipment/armor.htm#armorForUnusualCreatures) as armour for medium-sized creatures.

#### Status

New in 5.10.1.

#### Syntax

`BONUS:ITEMWEIGHT|TYPE=item_type_1,TYPE=item_type_2,...|weight_multiplier`

* `item_type` is a type of equipment from the data set's equipment files, as per `BONUS:ITEMCOST` above.
* `weight_multiplier` is a number.

#### Example

* `ITEMWEIGHT|TYPE=Armor|2`

  Doubles the weight of armour made for a creature of this size.

* `ITEMWEIGHT|TYPE=Resizable|0.25`

  Reduces the weight of `Resiable` items, such as backpacks and bedrolls, to one-quarter the normal value.

------------------------------------------------------------------------

<h3 id="BONUSLOADMULT">BONUS:LOADMULT</h3>

Adds to the `SIZEMULT` value as defined in the `load.lst` game mode file.

Mostly, the rules for "Large creatures can carry twice as much weight" [and so on](http://www.d20srd.org/srd/carryingCapacity.htm#biggerandSmallerCreatures) are implemented in `load.lst`. The `BONUS:LOADMULT` tag is used to implement rules for creatures with four legs having greater load-carrying capacity.

#### Status

New in 5.10.1

#### Syntax

`BONUS:LOADMULT|TYPE=SIZE|sizemult_modifier`

* `sizemult_modifier` is a number. This number is added to the `SIZEMULT` in the `load.lst` file to determine the "effective" carrying capacity multiplier, relative to a normal sized creature.

##### Examples

* `SIZENAME:H  →  BONUS:LOADMULT|TYPE=SIZE|2`

  Increases the creature's carrying capacity by one multiple of the normal carrying capacity. This adds with the multiplier already defined for the creature's size category, in `load.lst`.

  For example, in DnD 3.5e, a huge-size creature can already carry four times the load of a medium-size creature. This is defined by the code `SIZEMULT:H|4` in `load.lst`.

  The above `BONUS:LOADMULT|...|2` increases this by a further two multiples of the medium-size creature's carrying capacity, to a total of six times.

* `SIZENAME:H  →  BONUS:LOADMULT|TYPE=SIZE|2|PRELEGSGTEQ:4`

  Like the above, but will only apply if the creature has at least four legs.  
