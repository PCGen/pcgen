package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;

public class ActiveEqModFacet extends
		AbstractSourcedListFacet<EquipmentModifier> implements
		DataFacetChangeListener<Equipment>
{

	/*
	 * In theory, this doesn't need to check for additions/removals from the
	 * EqMod list, because such changes can't happen to equipment that is
	 * currently equipped by the PC (new equipment is a clone, not the original
	 * item)
	 */
	public void dataAdded(DataFacetChangeEvent<Equipment> dfce)
	{
		CharID id = dfce.getCharID();
		Equipment eq = dfce.getCDOMObject();
		for (EquipmentModifier eqMod : eq.getEqModifierList(true))
		{
			add(id, eqMod, eq);
		}
		for (EquipmentModifier eqMod : eq.getEqModifierList(false))
		{
			add(id, eqMod, eq);
		}
	}

	public void dataRemoved(DataFacetChangeEvent<Equipment> dfce)
	{
		CharID id = dfce.getCharID();
		Equipment eq = dfce.getCDOMObject();
		for (EquipmentModifier eqMod : eq.getEqModifierList(true))
		{
			remove(id, eqMod, eq);
		}
		for (EquipmentModifier eqMod : eq.getEqModifierList(false))
		{
			remove(id, eqMod, eq);
		}
	}
}