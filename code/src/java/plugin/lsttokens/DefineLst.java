/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class DefineLst implements CDOMPrimaryToken<CDOMObject>
{

	public static Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public String getTokenName()
	{
		return "DEFINE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		int barLoc = value.indexOf('|');
		if (barLoc != value.lastIndexOf('|'))
		{
			Logging
					.errorPrint(getTokenName()
							+ " must be of Format: varName|varFormula or LOCK.<stat>|value or UNLOCK.<stat>");
			return false;
		}
		if (barLoc == -1)
		{
			if (value.startsWith("UNLOCK."))
			{
				PCStat stat = context.ref.getAbbreviatedObject(PCSTAT_CLASS,
						value.substring(7));
				context.obj.addToList(obj, ListKey.UNLOCKED_STATS, stat);
				return true;
			}
			else
			{
				Logging.errorPrint(getTokenName() + " varName|varFormula"
						+ "or LOCK.<stat>|value syntax requires an argument");
				return false;
			}
		}
		if (value.startsWith("UNLOCK."))
		{
			Logging.errorPrint(getTokenName()
					+ " UNLOCK.<stat> does not allow an argument");
			return false;
		}
		String var = value.substring(0, barLoc);
		if (var.length() == 0)
		{
			Logging.errorPrint("Empty Variable Name found in " + getTokenName()
					+ ": " + value);
			return false;
		}
		try
		{
			Formula f = FormulaFactory.getFormulaFor(value
					.substring(barLoc + 1));
			if (value.startsWith("LOCK."))
			{
				PCStat stat = context.ref.getAbbreviatedObject(PCSTAT_CLASS,
						value.substring(5, barLoc));
				context.getObjectContext().addToList(obj, ListKey.STAT_LOCKS,
						new StatLock(stat, f));
			}
			else
			{
				context.getObjectContext().put(obj,
						VariableKey.getConstant(var), f);
			}
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Illegal Formula found in " + getTokenName()
					+ ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<StatLock> changes = context.getObjectContext().getListChanges(
				obj, ListKey.STAT_LOCKS);
		Changes<PCStat> ulchanges = context.getObjectContext().getListChanges(
				obj, ListKey.UNLOCKED_STATS);
		Set<VariableKey> keys = context.getObjectContext().getVariableKeys(obj);
		TreeSet<String> set = new TreeSet<String>();
		if (keys != null && !keys.isEmpty())
		{
			for (VariableKey key : keys)
			{
				set.add(key.toString() + Constants.PIPE
						+ context.getObjectContext().getVariable(obj, key));
			}
		}
		if (changes != null && !changes.isEmpty())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("DEFINE:LOCK does not support .CLEAR");
				return null;
			}
			if (changes.hasAddedItems())
			{
				for (StatLock sl : changes.getAdded())
				{
					set.add("LOCK." + sl.getLockedStat().getLSTformat() + "|"
							+ sl.getLockValue());
				}
			}
		}
		if (ulchanges != null && !ulchanges.isEmpty())
		{
			if (ulchanges.includesGlobalClear())
			{
				context.addWriteMessage("DEFINE:UNLOCK "
						+ "does not support .CLEAR");
				return null;
			}
			if (ulchanges.hasAddedItems())
			{
				for (PCStat st : ulchanges.getAdded())
				{
					set.add("UNLOCK." + st.getLSTformat());
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
