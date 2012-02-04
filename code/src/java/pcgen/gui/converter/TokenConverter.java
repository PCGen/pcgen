/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.enumeration.SubRace;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.gui.converter.event.TokenProcessEvent;
import pcgen.gui.converter.event.TokenProcessorPlugin;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;

public class TokenConverter
{

	private static final DoubleKeyMap<Class<?>, String, TokenProcessorPlugin> map = new DoubleKeyMap<Class<?>, String, TokenProcessorPlugin>();

	private static final DoubleKeyMap<Class<?>, String, Boolean> cached = new DoubleKeyMap<Class<?>, String, Boolean>();

	private static final DoubleKeyMapToList<Class<?>, String, TokenProcessorPlugin> tokenCache = new DoubleKeyMapToList<Class<?>, String, TokenProcessorPlugin>();

	private static final DefaultTokenProcessor defaultProc = new DefaultTokenProcessor();

	public static void addToTokenMap(TokenProcessorPlugin tpp)
	{
		TokenProcessorPlugin old = map.put(tpp.getProcessedClass(), tpp
				.getProcessedToken(), tpp);
		if (old != null)
		{
			Logging.errorPrint("More than one Conversion token for "
					+ tpp.getProcessedClass().getSimpleName() + " "
					+ tpp.getProcessedToken() + " found");
		}
	}

	public static PluginLoader getPluginLoader()
	{
		return new PluginLoader()
		{

			public void loadPlugin(Class<?> clazz) throws Exception
			{
				addToTokenMap((TokenProcessorPlugin) clazz.newInstance());
			}

			public Class[] getPluginClasses()
			{
				return new Class[]
						{
							TokenProcessorPlugin.class
						};
			}

		};
	}

	public static String process(TokenProcessEvent tpe)
	{
		Class<?> cl = tpe.getPrimary().getClass();
		String key = tpe.getKey();
		List<TokenProcessorPlugin> tokens = getTokens(cl, key);
		String error = "";
		try
		{
			if (tokens != null)
			{
				for (TokenProcessorPlugin converter : tokens)
				{
					error += converter.process(tpe);
					if (tpe.isConsumed())
					{
						break;
					}
				}
			}
			if (!tpe.isConsumed())
			{
				error += defaultProc.process(tpe);
			}
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Parse of " + tpe.getKey() + ":"
					+ tpe.getValue() + " failed");
			ex.printStackTrace();
		}
		return tpe.isConsumed() ? null : error;
	}

	static class ConverterIterator implements Iterator<TokenProcessorPlugin>
	{

		private Class<?> rootClass;
		private final String tokenKey;
		private TokenProcessorPlugin nextToken = null;
		private boolean needNewToken = true;

		public ConverterIterator(Class<?> cl, String key)
		{
			rootClass = cl;
			tokenKey = key;
		}

		public boolean hasNext()
		{
			setNextToken();
			return !needNewToken;
		}

		protected void setNextToken()
		{
			if (needNewToken)
			{
				nextToken = null;
				while (nextToken == null && rootClass != null)
				{
					nextToken = grabToken(rootClass, tokenKey);
					rootClass = rootClass.getSuperclass();
				}
				needNewToken = nextToken == null;
			}
		}

		protected TokenProcessorPlugin grabToken(Class<?> cl, String key)
		{
			return map.get(cl, key);
		}

		public TokenProcessorPlugin next()
		{
			setNextToken();
			if (needNewToken)
			{
				throw new NoSuchElementException();
			}
			needNewToken = true;
			return nextToken;
		}

		public void remove()
		{
			throw new UnsupportedOperationException(
					"Iterator does not support remove");
		}
	}

	public static List<TokenProcessorPlugin> getTokens(Class<?> cl, String name)
	{
		List<TokenProcessorPlugin> list = tokenCache.getListFor(cl, name);
		if (!cached.containsKey(cl, name))
		{
			for (Iterator<TokenProcessorPlugin> it = new ConverterIterator(cl,
					name); it.hasNext();)
			{
				TokenProcessorPlugin token = it.next();
				tokenCache.addToListFor(cl, name, token);
			}
			list = tokenCache.getListFor(cl, name);
			cached.put(cl, name, Boolean.TRUE);
		}
		return list;
	}

	public static void clearConstants()
	{
		AspectName.clearConstants();
		Pantheon.clearConstants();
		RaceSubType.clearConstants();
		RaceType.clearConstants();
		Region.clearConstants();
		SubClassCategory.clearConstants();
		SubRace.clearConstants();
		SubRegion.clearConstants();
		Type.clearConstants();
		VariableKey.clearConstants();
	}

}
