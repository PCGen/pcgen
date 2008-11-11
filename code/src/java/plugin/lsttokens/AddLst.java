/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class AddLst extends AbstractToken implements GlobalLstToken,
		CDOMPrimaryToken<CDOMObject>
{
	/*
	 * Template's LevelToken adjustment done in addAddsFromAllObjForLevel() in
	 * PlayerCharacter
	 */

	@Override
	public String getTokenName()
	{
		return "ADD";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		int barLoc = value.indexOf(Constants.PIPE);
		if (barLoc == -1)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " syntax: "
				+ value + " ... must have a PIPE");
			return false;
		}
		else if (barLoc == 0)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " syntax: "
				+ value + " ... cannot start with a PIPE");
			return false;
		}
		String key = value.substring(0, barLoc);
		String contents = value.substring(barLoc + 1);
		if (contents == null || contents.length() == 0)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " syntax: "
				+ value + " ... cannot end with a PIPE");
			return false;
		}
		// Guaranteed to be the new syntax here...
		return AddLoader.parseLine(obj, key, contents, anInt);
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " requires a SubToken");
			return false;
		}
		String key = value.substring(0, pipeLoc);
		if (".CLEAR".equals(key))
		{
			context.getObjectContext().removeList(obj, ListKey.ADD);
		}
		return context.processSubToken(obj, getTokenName(), key, value
			.substring(pipeLoc + 1));
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		//TODO Need to unparse .CLEAR
		return context.unparse(obj, getTokenName());
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
