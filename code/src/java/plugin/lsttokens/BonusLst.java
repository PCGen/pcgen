/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 */
public class BonusLst implements CDOMPrimaryToken<CDOMObject>
{
	/**
	 * Returns token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "BONUS";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		final String v = value.replaceAll(Pattern.quote("<this>"), obj
				.getKeyName());
		BonusObj bon = Bonus.newBonus(obj.bonusStringPrefix() + v);
		if (bon == null)
		{
			Logging.log(Logging.LST_ERROR, getTokenName() + " was given invalid bonus: "
					+ value);
			return false;
		}
		bon.setCreatorObject(obj);
		bon.setTokenSource(getTokenName());
		context.obj.addToList(obj, ListKey.BONUS, bon);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<BonusObj> changes = context.obj.getListChanges(obj,
				ListKey.BONUS);
		if (changes == null || changes.isEmpty())
		{
			// Empty indicates no token present
			return null;
		}
		// CONSIDER need to deal with removed...
		Collection<BonusObj> added = changes.getAdded();
		String tokenName = getTokenName();
		Set<String> bonusSet = new TreeSet<String>();
		for (BonusObj bonus : added)
		{
			if (tokenName.equals(bonus.getTokenSource()))
			{
				bonusSet.add(bonus.toString());
			}
		}
		if (bonusSet.isEmpty())
		{
			// This is okay - just no BONUSes from this token
			return null;
		}
		return bonusSet.toArray(new String[bonusSet.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

}
