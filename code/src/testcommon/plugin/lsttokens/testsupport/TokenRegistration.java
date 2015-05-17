/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.testsupport;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pcgen.core.bonus.BonusObj;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.rules.types.FormatManager;
import pcgen.rules.types.FormatManagerLibrary;

public class TokenRegistration
{

	public static Set<String> ppiSet = new HashSet<String>();

	public static void register(PrerequisiteParserInterface ppi)
		throws PersistenceLayerException
	{
		String s = Arrays.asList(ppi.kindsHandled()).toString();
		if (!ppiSet.contains(s))
		{
//			try {
				PreParserFactory.register(ppi);
				ppiSet.add(s);
				TokenLibrary.addToTokenMap(ppi);
//			} catch (PersistenceLayerException e) {
//				Logging.log(Logging.WARNING,
//						"Ignoring error while registering parser for test", e);
//			}
		}
	}

	public static Set<LstToken> tokenSet = new HashSet<LstToken>();

	public static void register(LstToken token)
		throws PersistenceLayerException
	{
		if (!tokenSet.contains(token))
		{
			TokenLibrary.addToTokenMap(token);
			TokenStore.inst().addToTokenMap(token);
			tokenSet.add(token);
			if (token instanceof QualifierToken)
			{
				TokenLibrary.addToQualifierMap((QualifierToken<?>) token);
			}
			if (token instanceof PrimitiveToken)
			{
				TokenLibrary.addToPrimitiveMap((PrimitiveToken<?>) token);
			}
		}
	}

	public static Set<Token> exportSet = new HashSet<Token>();

	public static void register(Token token)
		throws PersistenceLayerException
	{
		if (!exportSet.contains(token))
		{
			exportSet.add(token);
			ExportHandler.addToTokenMap(token);
		}
	}

	public static void clearTokens()
	{
		TokenLibrary.reset();
		TokenStore.reset();
		tokenSet.clear();
		ppiSet.clear();
		PreParserFactory.clear();
		FormatManagerLibrary.reset();
	}

	public static Set<String> pwSet = new HashSet<String>();

	public static void register(PrerequisiteWriterInterface writer)
		throws PersistenceLayerException
	{
		String s = writer.kindHandled();
		if (!pwSet.contains(s))
		{
			PrerequisiteWriterFactory.register(writer);
			pwSet.add(s);
		}
	}

	public static void register(FormatManager<?> manager)
	{
		FormatManagerLibrary.addFormatManager(manager);
	}

	public static void register(Class<? extends BonusObj> cl)
	{
		try
		{
			TokenLibrary.addBonusClass(cl);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
