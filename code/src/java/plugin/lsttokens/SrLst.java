/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 * 
 */
public class SrLst implements CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "SR";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (".CLEAR".equals(value))
		{
			context.getObjectContext().remove(obj, ObjectKey.SR);
		}
		else
		{
			context.getObjectContext().put(obj, ObjectKey.SR,
					getSpellResistance(value));
		}
		return true;
	}

	private SpellResistance getSpellResistance(String value)
	{
		return new SpellResistance(FormulaFactory.getFormulaFor(value));
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		SpellResistance sr = context.getObjectContext().getObject(obj,
				ObjectKey.SR);
		/*
		 * TODO This can't unparse .CLEAR
		 */
		if (sr == null)
		{
			// Zero indicates no Token (so nothing to do)
			return null;
		}
		return new String[] { sr.getLSTformat() };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
