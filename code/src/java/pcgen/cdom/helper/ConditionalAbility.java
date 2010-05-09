/**
 * 
 */
package pcgen.cdom.helper;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;

public class ConditionalAbility
{

	private final Ability ability;
	private final AssociatedPrereqObject assoc;
	private final CDOMObject parent;

	public ConditionalAbility(Ability ab, AssociatedPrereqObject apo,
			CDOMObject cdo)
	{
		ability = ab;
		assoc = apo;
		parent = cdo;
	}

	public CDOMObject getParent()
	{
		return parent;
	}

	public AssociatedPrereqObject getAPO()
	{
		return assoc;
	}

	public Ability getAbility()
	{
		return ability;
	}

}