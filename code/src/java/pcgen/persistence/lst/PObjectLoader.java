/*
 * PObjectLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.FeatParser;
import pcgen.persistence.lst.utils.PObjectHelper;
import pcgen.core.Ability;

/**
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class PObjectLoader {

	/**
	 * Creates a new instance of PObjectLoader Private since instances need never
	 * be created and API methods are public and static
	 */

	private static List<PObjectHelper> featList = new ArrayList<PObjectHelper>();

	private PObjectLoader() {
		// Empty Constructor
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor) It applies the value of the tag to the
	 * provided PObject.
	 *
	 * @param obj
	 *          PObject which the tag will be applied to
	 * @param aTag
	 *          String tag and value to parse
	 * @return boolean true if the tag is parsed; else false.
	 * @throws PersistenceLayerException
	 */
	public static boolean parseTag(PObject obj, String aTag)
		throws PersistenceLayerException
	{
		return parseTagLevel(obj, aTag, -9);
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor) It applies the value of the tag to the
	 * provided PObject If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag] A level of -9 or
	 * lower is treated as "at all levels."
	 *
	 * @param obj
	 *          PObject which the tag will be applied to
	 * @param aTag
	 *          String tag and value to parse
	 * @param anInt
	 *          int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 * @throws PersistenceLayerException
	 */
	public static boolean parseTagLevel(PObject obj, String aTag, int anInt)
		throws PersistenceLayerException
	{
		if ((obj == null) || (aTag.length() < 1))
		{
			return false;
		}

		obj.setNewItem(false);

		aTag.charAt(0);

		boolean result = false;
		int colonIdx = aTag.indexOf(':');
		if (colonIdx < 0)
		{
			return false;
		}
		String key = aTag.substring(0, colonIdx);
		String value = aTag.substring(colonIdx + 1);
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(GlobalLstToken.class);
		LstToken token = tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, obj, value);
			result = ((GlobalLstToken)token).parse(obj, value, anInt);
		}
		else
		{
			result = true;
			if (aTag.startsWith("CAMPAIGN:") && !(obj instanceof Campaign))
			{
				// blank intentionally
			}
			else if (aTag.startsWith("PRE") || aTag.startsWith("!PRE") || aTag.startsWith("RESTRICT:"))
			{
				if (aTag.toUpperCase().equals("PRE:.CLEAR"))
				{
					obj.clearPreReq();
				}
				else
				{
					aTag = CoreUtility.replaceAll(aTag, "<this>", obj.getKeyName());
					try
					{
						PreParserFactory factory = PreParserFactory.getInstance();
						obj.addPreReq(factory.parse(aTag), anInt);
					}
					catch (PersistenceLayerException ple)
					{
						throw new PersistenceLayerException("Unable to parse a prerequisite: " + ple.getMessage());
					}
				}
			}
			else if (aTag.startsWith("VFEAT:"))
			{
				featList.add(new PObjectHelper(obj, aTag.substring(6), anInt));
			}
			else
			{
				result = false;
			}
		}

		return result;
	}

	public static void finishFeatProcessing()
	{
		for ( PObjectHelper p : featList )
		{
			List<Ability> vFeatList = FeatParser.parseVirtualFeatList(p.getTag());
			p.getObject().addVirtualFeats(vFeatList);
		}
	}
}
