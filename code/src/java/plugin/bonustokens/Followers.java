package plugin.bonustokens;

import pcgen.core.bonus.BonusObj;

public class Followers extends BonusObj
{
	private static final String[] bonusHandled = { "FOLLOWERS" };

	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}

	protected boolean parseToken( final String argToken )
	{
//		Collection<String> followerTypes = Globals.getFollowerTypes();
//		if ( followerTypes.contains( argToken ) )
//		{
			addBonusInfo( argToken );
			return true;
//		}
//		return false;
	}

	protected String unparseToken(final Object obj)
	{
		return (String) obj;
	}
}
