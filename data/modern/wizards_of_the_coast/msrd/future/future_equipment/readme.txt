Issues with this:

1.)	The usual Wealth system problems. Most EQMODS do not add to the cost, but have their own Purchase DC. For the moment their cost tags
	are just in a comment line under the entry, so the EQMODS will be added for free. I put them in as comments to have them at hand at
	the time when the Wealth system should be incorporated into PCGen.
	
2.)	I had to use a different vehicle EQMOD than in the core set. For some reason they did change the data again. Cargo Capacity is now
	not given as a weight, but as a size category.
	
3.)	I couldn't make a weapon eqmod for fire-linked weapons. These require the DAMAGE to be raised by one half the base damage for every
	two fire-linked weapons, so basically DAMAGE:8d8 would become DAMAGE:12d8, but we do not have a BONUS for that.

4.)	I did Starship eqmods for including those fire-linked weapons and weapon batteries in a starship, that are used in the actual default
	starships. If I were going to do all possible combinations, there would be 7 eqmods for every weapon. I don't think that this is really
	necessary, at least  hope so. (Why isn't there a built-in LST-Editor for EQMODs?). It is possible to built batteries of weapons using
	the customizer and just buy them, so they will appear on the sheet, it will just be missing in the starship text. Linked-fire is a
	different issue, as described above.
	
5.)	Alternate Weapon Gadget: AUTO:EQUIP won't take (%)LIST or (%)CHOICE for substitution, therefore the user will have to add the second
	weapon by hand. As the 2nd weapon needs to be fully paid and will have the same weight when linked with the first one, this is not a
	big problem. A problem is, that the user will not be able to wield both weapons at the same time. This should be mentioned in the
	Source Help Docs

6.)	Autoloader Module Gadget has box magazin or power pack for ammo feed as prerequisite. We could go over every weapon in modern and add
	a TYPE for this, but is it worth that?

7.)	I have only included TYPE=Goods in the Integrated Equipment Gadget and Multiple Use Item Gadget choosers. It is my interpretation of
	what makes sense there. Following the exact wording of the rules though, any non-weapon choice would be eligible. Maybe add that?

8.)	The Miniaturized and Compact Gadgets should reduce the size of the item. BONUS:SIZEMOD would only affect creature size? Went SPROP for
	that matter.

9.)	Rangefinding Laser Scope Gadget: BONUS:EQMWEAPON|RANGEMULT doesn't take fractions. Had to go SPROP there as well.

10.)	As with Mecha there are some EQMODS here that can be taken multiple times. Makes having a MULT:YES for EQMODS even more interesting.

11.)	Satellite Datalink Gadget is restricted to "gear containing computerized communications equipment". I'm using PRETYPE:1,Electronic,Surveillance
	but that includes a few items that wouldn't be eligible.

12.)	The Storage Compartment Gadgets would need the ability for an EQMOD to change the item, which it is getting applied to, into being a
	container.

13.)	Sound Suppressor Gadget for items has the text for weapons in it. That is an error in the MSRD.

14.)	I hurried a bit through this in the end, therefore I didn't really test this as much as I would usually do. For that matter there
	might be more errors than usual. The logic should be okay, but especially the Starship variables need to be checked (which I will do
	in the next time).
	
	
11-17-05	Frank Kliewe
