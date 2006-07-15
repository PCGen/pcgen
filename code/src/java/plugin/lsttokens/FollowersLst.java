package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import java.util.StringTokenizer;

public class FollowersLst implements GlobalLstToken
{
	/**
	 *
	 * @return token name
	 */
	public String getTokenName()
	{
		return "FOLLOWERS";
	}

	/**
	 *
	 * @param obj PObject
	 * @param value String
	 * @param anInt int
	 * @return true if OK
	 * @throws PersistenceLayerException
	 * @todo Implement this pcgen.persistence.lst.GlobalLstToken method
	 */
	public boolean parse(PObject obj, String value, int anInt)
		throws PersistenceLayerException
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		final String followerType;
		if ( tok.hasMoreTokens() )
		{
			followerType = tok.nextToken().toUpperCase();
		}
		else
		{
			throw new PersistenceLayerException("Invalid FOLLOWERS token format");
		}
		final String followerNumber;
		if ( tok.hasMoreTokens() )
		{
			followerNumber = tok.nextToken();
		}
		else
		{
			throw new PersistenceLayerException("Invalid FOLLOWERS token format");
		}

		obj.setNumFollowers(followerType, followerNumber);
		return true;
	}
}
