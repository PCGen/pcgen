/*
 * PreReqListParser.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author wardc
 *
 */
public abstract class AbstractPrerequisiteListParser
	extends AbstractPrerequisiteParser
	implements PrerequisiteParserInterface
{

	/**
	 *
	 */
	public AbstractPrerequisiteListParser()
	{
		super();
	}

	protected void convertKeysToSubKeys(Prerequisite prereq, String kind)
	{
		if (prereq == null)
		{
			return;
		}
		if (prereq.getKind() != null && prereq.getKind().equalsIgnoreCase(kind))
		{
			String key = prereq.getKey();

			int index = key.indexOf('(');
			int endIndex = key.lastIndexOf(')');

			if ((index >= 0) && (endIndex >= 0))
			{
				String subKey = key.substring(index + 1, endIndex).trim();
				key = key.substring(0, index).trim();

				prereq.setKey(key);
				prereq.setSubKey(subKey);
			}
		}

		for (Prerequisite element : prereq.getPrerequisites())
		{
			convertKeysToSubKeys(element, kind);
		}
	}

    /**
     * Parse the pre req list
     * 
     * @param kind 
     * @param formula 
     * @param invertResult 
     * @param overrideQualify 
     * @return PreReq 
     * @throws PersistenceLayerException 
     */
	@Override
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{

		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		parsePrereqListType(prereq, kind, formula);

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}

	protected void parsePrereqListType(Prerequisite prereq, String kind, String formula)
	{
		// CLASS:Spellcaster=3
		// <prereq kind="class" key="Spellcaster" min="3" />

		// SKILL:1,Heal=5
		// <prereq kind="skill" key="Heal" min="5" />

		// FEAT:1,TYPE=Necromantic
		// <prereq kind="feat" key="TYPE=Necromantic" />

		// SKILL:2,Knowledge (Anthropology),Knowledge (Biology),Knowledge
		// (Chemistry)=5
		// <prereq min="2">
		//   <prereq kind="skill" key="Knowledge (Anthropology)" min="5" />
		//   <prereq kind="skill" key="Knowledge (Biology)" min="5" />
		//   <prereq kind="skill" key="Knowledge (Chemistry)" min="5" />
		// </prereq>

		// FEAT:2,CHECKMULT,Spell Focus
		// <prereq kind="feat" count-multiples="true" key="feat.spell_focus" />

		// FEAT:2,CHECKMULT,Spell Focus,[Spell Focus(Enchantment)]
		// <prereq min="2">
		//   <prereq kind="feat" key="feat.spell_focus" count-multiples="true" min="2"/>
		//   <prereq kind="feat" key="feat.spell_focus_enchantment" logical="not" />
		// </prereq>

		// STAT:1,DEX=9,STR=13
		// <prereq operator="gteq" op1="1">
		//   <prereq kind="stat" key="dex" operator="gteq" op1="9" />
		//   <prereq kind="stat" key="str" operator="gteq" op1="13" />
		// </prereq>

		//		int min = 0;

		String[] elements = formula.split(",|\\|");
		boolean parseOldStyle = false;
		int numRequired = 0;
		try
		{
			numRequired = Integer.parseInt(elements[0]);
		}
		catch (NumberFormatException nfe)
		{
			parseOldStyle = true;
		}

		if (parseOldStyle)
		{
			parsePrereqOldStyleList(prereq, kind, formula);
		}
		else
		{
			// Examine the last element to see if it is of the form "foo=n"
			//			String[] lastElements = elements[elements.length - 1].split("=");
			//			if (lastElements.length == 2) {
			//				try {
			//					// Parse the number off of the end of the element
			//					min = Integer.parseInt(lastElements[1]);
			//
			//					// replace the element in the list with the name (less the "=n");
			//					elements[elements.length - 1] = lastElements[0];
			//				} catch (NumberFormatException nfe) {
			//				}
			//			}

			int elementsLength = elements.length;
			for (int i = elementsLength - 1; i >= 0; --i)
			{
				if ("CHECKMULT".equalsIgnoreCase(elements[i]))
				{
					prereq.setCountMultiples(true);
					--elementsLength;
				}
			}


			// tokens now contains all of the possible matches,
			// min contains the target number (if there is one)
			// number contains the number of 'tokens' that be be at lease 'min'
			if (elementsLength > 2)
			{
				// we have more than one option, so use a group
				prereq.setOperator(PrerequisiteOperator.GTEQ);
				prereq.setOperand(Integer.toString(numRequired));
				prereq.setKind(null);

				int min = -99;
				for (int i = 1; i < elements.length; i++)
				{
					if ("CHECKMULT".equals(elements[i]))
					{
						continue;
					}

					Prerequisite subreq = new Prerequisite();
					subreq.setKind(kind.toLowerCase());
					subreq.setCountMultiples(true);

					if (elements[i].indexOf('=') >= 0)
					{
						// The element is either of the form "TYPE=foo" or "DEX=9"
						// if it is the later, we need to extract the '9'
						subreq.setOperator(PrerequisiteOperator.GTEQ);
						String[] tokens = elements[i].split("=");
						try
						{
							min = Integer.parseInt(tokens[1]);
							subreq.setOperand(Integer.toString(min));
							subreq.setKey(tokens[0]);

							// now back fill all of the previous prereqs with this minium
							for (Iterator<Prerequisite> iter = prereq.getPrerequisites().iterator(); iter.hasNext();)
							{
								Prerequisite element = iter.next();
								if (element.getOperand().equals("-99"))
								{
									element.setOperand(Integer.toString(min));
									// If this requirement has already been added, we don't want to repeat it.
									if (element.getKey().equals(tokens[0]))
									{
										iter.remove();
									}
								}
							}
						}
						catch (NumberFormatException nfe)
						{
							subreq.setKey(elements[i]);
						}
					}
					else
					{
						subreq.setKey(elements[i]);
						subreq.setOperator(PrerequisiteOperator.GTEQ);
						subreq.setOperand(Integer.toString(min));
					}
					subreq.setOperand(Integer.toString(min));
					prereq.addPrerequisite(subreq);
				}
				for (Prerequisite element : prereq.getPrerequisites())
				{
					if (element.getOperand().equals("-99"))
					{
						element.setOperand("1");
					}
				}

			}
			else
			{
				// We only have a number of prereqs to pass, and a single prereq so we do not want a
				// wrapper prereq around a list of 1 element.
				// i.e. 1,Alertness, or 2,TYPE=ItemCreation, or 1,Reflex=7 or 3,Knowledge%=2 or 4,TYPE.Craft=5
				Prerequisite subreq = prereq;

				if (elementsLength > 1)
				{
					for(int i = 1; i < elements.length; ++i)
					{
						if ("CHECKMULT".equalsIgnoreCase(elements[i]))
						{
							continue;
						}

						if (elements[i].indexOf('=') >= 0)
						{
							// i.e. TYPE=ItemCreation or Reflex=7
							String[] tokens = elements[i].split("=");
							try
							{
								// i.e. Reflex=7 or TYPE.Craft=5
								int iOper = Integer.parseInt(tokens[1]);
								if (numRequired != 1)
								{
									//
									// If we would lose the required number of matches, then make this a PREMULT
									//
									prereq.setOperator(PrerequisiteOperator.GTEQ);
									prereq.setOperand(Integer.toString(numRequired));
									prereq.setKind(null);
									subreq = new Prerequisite();
									prereq.addPrerequisite(subreq);
									subreq.setCountMultiples(true);
								}
								subreq.setOperand(Integer.toString(iOper));
								subreq.setKey(tokens[0]);
							}
							catch (NumberFormatException nfe)
							{
								// i.e. TYPE=ItemCreation
								subreq.setOperand(elements[0]);
								subreq.setKey(elements[i]);
							}
						}
						else
						{
							subreq.setOperand(elements[0]);
							subreq.setKey(elements[i]);
						}
						break;
					}
				}
				else
				{
					subreq.setOperand(elements[0]);
				}
				subreq.setKind(kind.toLowerCase());
				subreq.setOperator(PrerequisiteOperator.GTEQ);
			}
		}
	}

	protected void parsePrereqOldStyleList(Prerequisite prereq, String kind, String formula)
	{
		// TEMPLATE:Cyrohydra|Lernaean Cryohydra
		// <prereq min="1">
		//   <prereq kind="template" key="Cyrohydra" />
		//   <prereq kind="template" key="Lernaean Cryohydra" />
		// </prereq>

		String[] templates = formula.split(",|\\|");
		int templatesLength = templates.length;
		for (int i = templatesLength - 1; i >= 0; --i)
		{
			if ("CHECKMULT".equalsIgnoreCase(templates[i]))
			{
				prereq.setCountMultiples(true);
				--templatesLength;
			}
		}

		//
		// Only 1 non-CHECKMULT entry?
		//
		if (templatesLength == 1)
		{
			int i;
			int min = 1;
			for (i = 0; i < templates.length; ++i)
			{
				if ("CHECKMULT".equalsIgnoreCase(templates[i]))
				{
					continue;
				}

				String[] lastElements = templates[i].split("=");
				if (lastElements.length == 2)
				{
					try
					{
						// Parse the number off of the end of the element
						min = Integer.parseInt(lastElements[1]);

						// replace the element in the list with the name (less the "=n");
						templates[i] = lastElements[0];
					}
					catch (NumberFormatException nfe)
					{
						// TODO Handle this?
					}
				}
				break;
			}
			prereq.setKind(kind.toLowerCase());
			prereq.setKey(templates[i]);
			prereq.setOperator(PrerequisiteOperator.GTEQ);
			prereq.setOperand(Integer.toString(min));
		}
		else
		{
			// Need to handle
			// PRECLASSLEVELMAX:Fighter,SPELLCASTER=2"
			// PRECLASSLEVELMAX:Rogue=5,Fighter,SPELLCASTER=2,Monk=3
			//
			prereq.setKind(null);
			prereq.setOperator(PrerequisiteOperator.GTEQ);
			//
			// TODO: there's got to be a better way to do this, than using this HACK.
			// There are probably more cases than PRECLASSLEVELMAX that require this treatment...
			// - Byngl Sept 27, 2005
			//
			if ("classlevelmax".equalsIgnoreCase(kind))
			{
				prereq.setOperand(Integer.toString(templates.length));
			}
			else
			{
				prereq.setOperand("1");
			}

			int lastEqIdx = -1;

			ArrayList<Prerequisite> prereqs = new ArrayList<Prerequisite>(templatesLength);
			for (int i = 0; i < templates.length; ++i)
			{
				if ("CHECKMULT".equalsIgnoreCase(templates[i]))
				{
					continue;
				}

				final Prerequisite subPrereq = new Prerequisite();
				subPrereq.setKind(kind.toLowerCase());
				final int eqIdx = templates[i].indexOf('=');
				if (eqIdx >= 0)
				{
					final String oper = templates[i].substring(eqIdx + 1);
					templates[i] = templates[i].substring(0, eqIdx);
					subPrereq.setOperand(oper);
					//
					// Need to go back through the prereq list and update the operand for all those without one
					//
					for(int updateIdx = lastEqIdx + 1; updateIdx < i; ++updateIdx)
					{
						prereqs.get(updateIdx).setOperand(oper);
					}
					lastEqIdx = i;
				}
				subPrereq.setKey(templates[i]);
				prereqs.add(subPrereq);
			}
			prereq.setPrerequisites(prereqs);

		}
	}

}
