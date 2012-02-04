package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.system.PluginLoader;
import pcgen.util.Logging;

/**
 * A Store of LST tokens, has a map and list representation
 */
public class TokenStore implements PluginLoader
{
	private static TokenStore inst;
	private HashMap<Class<? extends LstToken>, Map<String, LstToken>> tokenTypeMap;
	private final List<Class<? extends LstToken>> tokenTypeList;

	private TokenStore()
	{
		tokenTypeMap =
				new HashMap<Class<? extends LstToken>, Map<String, LstToken>>();
		tokenTypeList = new ArrayList<Class<? extends LstToken>>();
		populateTokenTypeList();
	}

	/**
	 * Create an instance of TokenStore and return it.
	 * @return an instance of TokenStore and return it.
	 */
	public static TokenStore inst()
	{
		if (inst == null)
		{
			inst = new TokenStore();
		}
		return inst;
	}

	private void populateTokenTypeList()
	{
		//miscinfo.lst
		tokenTypeList.add(GameModeLstToken.class);

		//level.lst
		tokenTypeList.add(LevelLstToken.class);

		//equipIcon.lst
		tokenTypeList.add(EquipIconLstToken.class);

		//equipmentslots.lst
		tokenTypeList.add(EquipSlotLstToken.class);

		//install.lst
		tokenTypeList.add(InstallLstToken.class);
	}

	public void loadPlugin(Class<?> clazz) throws Exception
	{
		addToTokenMap((LstToken) clazz.newInstance());
	}

	public Class[] getPluginClasses()
	{
		return new Class[]{LstToken.class};
	}

	/**
	 * Add the new token to the token map
	 * @param newToken
	 */
	public void addToTokenMap(LstToken newToken)
	{
		for (Class<? extends LstToken> tokClass : tokenTypeList)
		{
			if (tokClass.isAssignableFrom(newToken.getClass()))
			{
				Map<String, LstToken> tokenMap = getTokenMap(tokClass);
				LstToken test = tokenMap.put(newToken.getTokenName(), newToken);

				if (test != null)
				{
					Logging.errorPrint("More than one " + tokClass.getName()
						+ " has the same token name: '"
						+ newToken.getTokenName() + "'");
				}
			}
		}
	}

	/**
	 * Get the token map
	 * @param tokInterface
	 * @return the token map
	 */
	public Map<String, LstToken> getTokenMap(
		Class<? extends LstToken> tokInterface)
	{
		Map<String, LstToken> tokenMap = tokenTypeMap.get(tokInterface);
		if (tokenMap == null)
		{
			tokenMap = new HashMap<String, LstToken>();
			tokenTypeMap.put(tokInterface, tokenMap);
		}
		return tokenMap;
	}
}
