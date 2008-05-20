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

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.TokenLibrary;

public class TokenRegistration
{

	public static Set<String> ppiSet = new HashSet<String>();

	public static void register(PrerequisiteParserInterface ppi)
		throws PersistenceLayerException
	{
		String s = Arrays.asList(ppi.kindsHandled()).toString();
		if (!ppiSet.contains(s))
		{
			PreParserFactory.register(ppi);
			ppiSet.add(s);
		}
	}

	public static Set<LstToken> tokenSet = new HashSet<LstToken>();

	public static void register(LstToken token)
		throws PersistenceLayerException
	{
		if (!tokenSet.contains(token))
		{
			TokenLibrary.addToTokenMap(token);
			tokenSet.add(token);
//			if (token instanceof QualifierToken)
//			{
//				TokenLibrary.addToQualifierMap((QualifierToken<?>) token);
//			}
//			if (token instanceof PrimitiveToken)
//			{
//				TokenLibrary.addToPrimitiveMap((PrimitiveToken<?>) token);
//			}
		}
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
}
