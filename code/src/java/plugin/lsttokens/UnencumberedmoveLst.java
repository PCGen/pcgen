/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Load;

/**
 * @author djones4
 */
public class UnencumberedmoveLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "UNENCUMBEREDMOVE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean hasArmor = false;
		boolean hasMove = false;

		Load loadMove = Load.LIGHT;
		Load loadArmor = Load.LIGHT;

		while (tok.hasMoreTokens())
		{
			String loadString = tok.nextToken();
			if (loadString.equalsIgnoreCase("MediumLoad"))
			{
				if (!validateOnlyMove(hasMove))
				{
					return false;
				}
				loadMove = Load.MEDIUM;
				hasMove = true;
			}
			else if (loadString.equalsIgnoreCase("HeavyLoad"))
			{
				if (!validateOnlyMove(hasMove))
				{
					return false;
				}
				loadMove = Load.HEAVY;
				hasMove = true;
			}
			else if (loadString.equalsIgnoreCase("Overload"))
			{
				if (!validateOnlyMove(hasMove))
				{
					return false;
				}
				loadMove = Load.OVERLOAD;
				hasMove = true;
			}
			else if (loadString.equalsIgnoreCase("MediumArmor"))
			{
				if (!validateOnlyArmor(hasArmor))
				{
					return false;
				}
				loadArmor = Load.MEDIUM;
				hasArmor = true;
			}
			else if (loadString.equalsIgnoreCase("HeavyArmor"))
			{
				if (!validateOnlyArmor(hasArmor))
				{
					return false;
				}
				loadArmor = Load.OVERLOAD;
				hasArmor = true;
			}
			else if (loadString.equalsIgnoreCase("LightLoad"))
			{
				if (!validateOnlyMove(hasMove))
				{
					return false;
				}
				// do nothing, but accept values as valid
			}
			else if (loadString.equalsIgnoreCase("LightArmor"))
			{
				if (!validateOnlyMove(hasMove))
				{
					return false;
				}
				// do nothing, but accept values as valid
			}
			else
			{
				ShowMessageDelegate.showMessageDialog("Invalid value of \""
						+ loadString + "\" for UNENCUMBEREDMOVE in \""
						+ obj.getDisplayName() + "\".", "PCGen",
						MessageType.ERROR);
				return false;
			}
		}
		context.getObjectContext().put(obj, ObjectKey.UNENCUMBERED_LOAD,
				loadMove);
		context.getObjectContext().put(obj, ObjectKey.UNENCUMBERED_ARMOR,
				loadArmor);
		return true;
	}

	private boolean validateOnlyArmor(boolean hasArmor)
	{
		if (hasArmor)
		{
			Logging.errorPrint("Encountered Second Armor Load Type in "
					+ getTokenName() + " this is not valid.");
		}
		return !hasArmor;
	}

	private boolean validateOnlyMove(boolean hasMove)
	{
		if (hasMove)
		{
			Logging.errorPrint("Encountered Second Move Load Type in "
					+ getTokenName() + " this is not valid.");
		}
		return !hasMove;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Load load = context.getObjectContext().getObject(obj,
				ObjectKey.UNENCUMBERED_LOAD);
		Load at = context.getObjectContext().getObject(obj,
				ObjectKey.UNENCUMBERED_ARMOR);
		if (load == null && at == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needsLight = false;
		if (load != null)
		{
			if (Load.OVERLOAD.equals(load))
			{
				sb.append("Overload");
			}
			else if (Load.HEAVY.equals(load))
			{
				sb.append("HeavyLoad");
			}
			else if (Load.MEDIUM.equals(load))
			{
				sb.append("MediumLoad");
			}
			else if (Load.LIGHT.equals(load))
			{
				needsLight = true;
			}
			else
			{
				context.addWriteMessage(getTokenName()
						+ " encountered unknown Movement Load: " + load);
				return null;
			}
		}
		if (at == null || Load.LIGHT.equals(at))
		{
			if (needsLight)
			{
				sb.append("LightLoad");
			}
		}
		else
		{
			if (sb.length() != 0)
			{
				sb.append(Constants.PIPE);
			}
			if (Load.OVERLOAD.equals(at))
			{
				sb.append("HeavyArmor");
			}
			else if (Load.MEDIUM.equals(at))
			{
				sb.append("MediumArmor");
			}
			else
			{
				context.addWriteMessage(getTokenName()
						+ " encountered invalid Armor Load: " + load);
				return null;
			}
		}
		return new String[] { sb.toString() };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
