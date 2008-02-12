/*
 * AbilityLst.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.QualifiedObject;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Implements the ABILITY: global LST token.
 * 
 * <p>
 * <b>Tag Name</b>: <code>ABILITY</code>:x|y|z|z<br />
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this ability will be added to).<br />
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability: 
 * <tt>NORMAL</tt>, <tt>AUTOMATIC</tt>, or <tt>VIRTUAL</tt>)<br />
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have 
 * choices specified in &quot;()&quot;)<br />
 * <b>Prereqs Allowed</b>: Yes <br />
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Adds an Ability to a character.</li>
 * <li>The Ability is added to the Ability Category specied and that category's
 * pool will be charged if the Nature is <tt>NORMAL</tt></li>
 * <li>This tag will <b>not</b> cause a chooser to appear so all required 
 * choices must be specified in the tag</li>
 * <li>Choices can be specified by including them in parenthesis after the 
 * ability key name (whitespace is ignored).</li>
 * <li>A <tt>CATEGORY</tt> tag can be added to the ability key to specify that
 * the innate ability category specified be searched for a matching ability.</li>
 * <li>If no <tt>CATEGORY</tt> is specified the standard list for the ability
 * category will be used to find a matching ability.</li>
 * <li>This tag is a replacement for the following tags: <tt>FEAT</tt>, 
 * <tt>VFEAT</tt>, and <tt>FEATAUTO</tt>.
 * </ul>
 * <b>Where it is used:</b><br />
 * Global tag can be used anywhere.
 * <p />
 * <b>Examples:</b><br />
 * <code>ABILITY:FEAT|AUTOMATIC|TYPE=Metamagic</code><br />
 * Adds a Metamagic feat as an Auto feat.
 * <p />
 * 
 * <code>ABILITY:CLASSFEATURE|VIRTUAL|CATEGORY=FEAT:Stunning Fist</code><br />
 * Adds the Stunning Fist feat as a virtual class feature.
 * <p />
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 *
 */
public class AbilityLst implements GlobalLstToken
{

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject, java.lang.String, int)
	 */
	public boolean parse(PObject anObj, String aValue, int anInt)
		throws PersistenceLayerException
	{
		final StringTokenizer tok = new StringTokenizer(aValue, Constants.PIPE);

		final String cat = tok.nextToken();
		final AbilityCategory category =
				SettingsHandler.getGame().getAbilityCategory(cat);
		if (category == null)
		{
			throw new PersistenceLayerException(PropertyFactory.getFormattedString(
				"Errors.LstTokens.ValueNotFound", //$NON-NLS-1$
				getClass().getName(), "Ability Category", cat));
		}

		if (tok.hasMoreTokens())
		{
			final String natureKey = tok.nextToken();
			final Ability.Nature nature = Ability.Nature.valueOf(natureKey);
			if (nature == null)
			{
				throw new PersistenceLayerException(PropertyFactory.getFormattedString(
					"Errors.LstTokens.ValueNotFound", //$NON-NLS-1$
					getClass().getName(), "Ability Nature", cat));
			}

			ArrayList<Prerequisite> preReqs =
					new ArrayList<Prerequisite>();
			if (anInt > -9)
			{
				try
				{
					PreParserFactory factory =
							PreParserFactory.getInstance();
					String preLevelString = "PRELEVEL:MIN=" + anInt; //$NON-NLS-1$
					if (anObj instanceof PCClass)
					{
						// Classes handle this differently
						preLevelString =
								"PRECLASS:1," + anObj.getKeyName() + "=" + anInt; //$NON-NLS-1$ //$NON-NLS-2$
					}
					Prerequisite r = factory.parse(preLevelString);
					preReqs.add(r);
				}
				catch (PersistenceLayerException notUsed)
				{
					return false;
				}
			}
			final List<String> abilityList = new ArrayList<String>();
			boolean isPre = false;
			boolean isFirst = true;
			while (tok.hasMoreTokens())
			{
				final String key = tok.nextToken();
				if (PreParserFactory.isPreReqString(key))
				{
					isPre = true;
					final PreParserFactory factory =
							PreParserFactory.getInstance();
					final Prerequisite r = factory.parse(key);
					preReqs.add(r);
				}
				else
				{
					if (isPre)
					{
						Logging.errorPrint("Invalid " + getTokenName() + ": " + aValue);
						Logging.errorPrint("  PRExxx must be at the END of the Token");
						isPre = false;
					}
					if (".CLEAR".equals(key))
					{
						if (isFirst)
						{
							for (QualifiedObject<String> ab : new ArrayList<QualifiedObject<String>>(
									anObj.getRawAbilityObjects(category, nature)))
							{
								if (ab.getPrereqs().toString().equals(
										preReqs.toString()))
								{
									anObj.removeAbility(category, nature, ab);
								}
							}
						}
						else
						{
							Logging.errorPrint("Invalid " + getTokenName()
									+ ": .CLEAR non-sensical unless it appears first");
							return false;
						}
					}
					else if (key.startsWith(".CLEAR."))
					{
						String abil = key.substring(7);
						for (QualifiedObject<String> ab : new ArrayList<QualifiedObject<String>>(
								anObj.getRawAbilityObjects(category, nature)))
						{
							if (abil.equals(ab.getObject(null))
									&& ab.getPrereqs().toString().equals(
											preReqs.toString()))
							{
								anObj.removeAbility(category, nature, ab);
							}
						}
					}
					else
					{
						abilityList.add(key);
					}
				}
				isFirst = false;
			}
			for (final String ability : abilityList)
			{
				anObj.addAbility(category, nature,
					new QualifiedObject<String>(ability, preReqs));
			}
			return true;
		}

		throw new PersistenceLayerException(PropertyFactory.getFormattedString(
			"Errors.LstTokens.InvalidTokenFormat", //$NON-NLS-1$
			getClass().getName(), aValue));
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "ABILITY"; //$NON-NLS-1$
	}

}
