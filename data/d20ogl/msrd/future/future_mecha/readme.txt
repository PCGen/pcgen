Issues with this:

1.)	Most of the Equipment is put into Equipmods and each one of these is holding a lot of its information in SPROPs. Given that a Colossal
	Mecha might have up to 30 Equipmods applied, this can add to quite some large output there. When abilities are done, some of that in-
	fomation should be moved into EquipmentAbilities. I have refrained from using hidden feats here right now, because Devon expressed his
	desire on the BOD-meeting to see "no more hacks". For now if the output that is in there right now is considered too much, I suggest
	whoever will go over this to bring it into final shape, to cut what he deems unnecessary.
	
2.)	There are some EQMODS that could be applied more than once. Where one sample mecha needed multiples, I have added duplicate EQMOD with
	differing KEYs. We should consider adding a MULT:YES tag for EQMODs to allow multiples.
	
3.)	There are also EQMODs that AUTO:EQUIP weapons and Ammo. AUTO:EQUIP won't allow multiple EQUIPs of the same item, it would be nice to
	see that fixed also.
	
4.)	COST doesn't work well with the Wealth System, so the EQMODs that would have their own Purchase DC come free for now. The DC is left
	as a comment in the line below the entry. This is mean for faster reformatting, when the Wealth System should be done at sometime.
	
5.)	Most EQMODs should be applied to Equipment-Slots. We don't have a solution for that yet. The only way to handle this at the time that
	I could see is to make the slots pieces of equipment themselves that get AUTO:EQUIPed by the mecha, to work as containers for the
	EQMODS that would have to be made pieces of equipment also. But that clutters up the equipment tab, there is no way to enforce that
	these "EQMOD"-items must be put into the container to be working and if the user unequips the mecha, the slots would still be there.

6.)	Mecha act more like templates than like equipment. They change hp, STR, DEX, and Size. I used BONUS:SIZEMOD|NUMBER|x to set the size,
	but that will only work right with medium-sized creatures.
	
7.)	I changed the Feat DESCs to show the full benefits, as we now do with SRD feats.

8.)	I put in basic "Custom Mecha" entries for the user to built his own designs. Without the slots usage of this is very limited. The user
	should built his mecha externally and only use this to get his creation into PCGen.

9.)	Prettylst.pl throws a lot of "The tag "AUTO:EQUIP" from "AUTO:EQUIP|xxx" is not in the EQUIPMOD tag list" errors with this, but it
	works there.
	
10-19-05	Frank Kliewe

I've reconfigured this set slightly. To fully Equip a Mecha requires two steps, the first step is to add the template "Mecha Pilot" and select
the proper size Mecha you will be piloting from the resulting pop-up window. The template will change the character size and activate all the 
variables which are used for the Mecha stats. If the template is not added none of the Mecha's statistics will be correct and you will not be 
able to wield the Mecha's over sized weapons. Once the template is added you can then equip the Mecha equipment item.

March 6, 2006	Eddy Anthony