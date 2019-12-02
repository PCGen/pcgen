/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

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
public final class TokenStore implements PluginLoader
{
	private static TokenStore inst;
	private HashMap<Class<? extends LstToken>, Map<String, LstToken>> tokenTypeMap;
	private final List<Class<? extends LstToken>> tokenTypeList;

	private TokenStore()
	{
		tokenTypeMap = new HashMap<>();
		tokenTypeList = new ArrayList<>();
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

	public static void reset()
	{
		if (inst != null)
		{
			inst.tokenTypeList.clear();
			inst.tokenTypeMap.clear();
		}
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

		//migrate.lst
		tokenTypeList.add(MigrationLstToken.class);
	}

	@Override
	public void loadPlugin(Class<?> clazz) throws Exception
	{
		addToTokenMap((LstToken) clazz.newInstance());
	}

	@Override
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
					Logging.errorPrint("More than one " + tokClass.getName() + " has the same token name: '"
						+ newToken.getTokenName() + "'. " + "Classes were " + test.getClass().getName() + " and "
						+ newToken.getClass().getName());
				}
			}
		}
	}

	/**
	 * Get the token map
	 * @param tokInterface
	 * @return the token map
	 */
	public Map<String, LstToken> getTokenMap(Class<? extends LstToken> tokInterface)
	{
        Map<String, LstToken> tokenMap = tokenTypeMap.computeIfAbsent(tokInterface, k -> new HashMap<>());
        return tokenMap;
	}
}
