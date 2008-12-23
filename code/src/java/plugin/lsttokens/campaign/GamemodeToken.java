package plugin.lsttokens.campaign;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with GAMEMODE Token
 */
public class GamemodeToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "GAMEMODE";
	}

	public boolean parse(LoadContext context, Campaign obj, String gameMode)
			throws PersistenceLayerException
	{
		if (isEmpty(gameMode))
		{
			return false;
		}
		context.obj.removeList(obj, ListKey.GAME_MODE);

		StringTokenizer aTok = new StringTokenizer(gameMode, Constants.PIPE);
		while (aTok.hasMoreTokens())
		{
			context.obj.addToList(obj, ListKey.GAME_MODE, aTok.nextToken());
		}
		return true;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<String> changes = context.getObjectContext().getListChanges(
				obj, ListKey.GAME_MODE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Collection<?> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			return new String[] { StringUtil.join(added, Constants.PIPE) };
		}
		return null;
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}
