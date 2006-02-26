/*
 * PreParserFactory.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.22 $
 *
 * Last Editor: $Author: byngl $
 *
 * Last Edited: $Date: 2005/11/15 14:21:35 $
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wardc
 *
 */
public class PreParserFactory
{
	private static PreParserFactory instance = null;
	private static Map parserLookup = new HashMap();

	private PreParserFactory() throws PersistenceLayerException
	{
		register(new PreAlignParser());
		register(new PreApplyParser());
		register(new PreArmorProfParser());
		register(new PreArmorTypeParser());
		register(new PreAttackParser());
		register(new PreBaseSizeParser());
		register(new PreBirthplaceParser());
		register(new PreCityParser());
		register(new PreCheckParser());
		register(new PreClassParser());
		register(new PreClassLevelMaxParser());
		register(new PreCSkillParser());
		register(new PreDamageReductionParser());
		register(new PreDefaultMonsterParser());
		register(new PreDeityParser());
		register(new PreDeityAlignParser());
		register(new PreDeityDomainParser());
		register(new PreDomainParser());
		register(new PreEquipParser());
		register(new PreEquippedBothParser());
		register(new PreEquippedPrimaryParser());
		register(new PreEquippedSecondaryParser());
		register(new PreEquippedTwoWeaponParser());
		register(new PreFeatParser());
		register(new PreGenderParser());
		register(new PreHandsParser());
		register(new PreHitDiceParser());
		register(new PreHitPointsParser());
		register(new PreItemParser());
		register(new PreLanguageParser());
		register(new PreLegsParser());
		register(new PreLevelParser());
		register(new PreLevelMaxParser());
		register(new PreMoveParser());
		register(new PreMultParser());
		register(new PrePointBuyMethodParser());
		register(new PreRaceParser());
		register(new PreRegionParser());
		register(new PreRuleParser());
		register(new PreShieldProficiencyParser());
		register(new PreSizeParser());
		register(new PreSkillParser());
		register(new PreSkillTotalParser());
		register(new PreSpellParser());
		register(new PreSpecialAbilityParser());
		register(new PreSpellBookParser());
		register(new PreSpellCastParser());
		register(new PreSpellDescriptorParser());
		register(new PreSpellResistanceParser());
		register(new PreSpellSchoolParser());
		register(new PreSpellSchoolSubParser());
		register(new PreSpellTypeParser());
		register(new PreStatParser());
		register(new PreSubClassParser());
		register(new PreTemplateParser());
		register(new PreTextParser());
		register(new PreTypeParser());
		register(new PreUnarmedAttackParser());
		register(new PreVariableParser());
		register(new PreVisionParser());
		register(new PreWieldParser());
		register(new PreWeaponProficiencyParser());
	}

	/**
	 * @return Returns the instance.
	 * @throws PersistenceLayerException
	 */
	public static PreParserFactory getInstance() throws PersistenceLayerException
	{
		if (instance == null)
		{
			instance = new PreParserFactory();
		}

		return instance;
	}

	public PrerequisiteParserInterface getParser(String kind)
	{
		PrerequisiteParserInterface test = (PrerequisiteParserInterface) parserLookup.get(kind.toLowerCase());

		return test;
	}

	private void register(PrerequisiteParserInterface testClass) throws PersistenceLayerException
	{
		String[] kindsHandled = testClass.kindsHandled();

		for (int i = 0; i < kindsHandled.length; i++)
		{
			Object test = parserLookup.get(kindsHandled[i].toLowerCase());

			if (test != null)
			{
				throw new PersistenceLayerException(
					"Error registering '"
						+ testClass.getClass().getName()
						+ "' as test '"
						+ kindsHandled[i]
						+ "'. The test is already registered to '"
						+ test.getClass().getName()
						+ "'");
			}

			parserLookup.put(kindsHandled[i].toLowerCase(), testClass);
		}
	}

	public Prerequisite parse(String prereqStr) throws PersistenceLayerException
	{

		if ((prereqStr == null) || (prereqStr.length() <= 0))
		{
			throw new PersistenceLayerException("Null or empty PRE string");
		}

		int index = prereqStr.indexOf(':');
		if (index < 0)
		{
			throw new PersistenceLayerException("'" + prereqStr+ "'" + " is a badly formatted prereq.");
		}

		String kind = prereqStr.substring(0, index);
		String formula = prereqStr.substring(index + 1);

		// Catch PRE:.CLEAR here and return a basic clear prereq.
		if ("pre".equals(kind.toLowerCase())
			&& ".clear".equals(formula.toLowerCase()))
		{
			Prerequisite prereq = new Prerequisite();
			prereq.setKind(Prerequisite.CLEAR_KIND);
			return prereq;
		}

		if (formula.startsWith("Q:"))
		{
			formula = formula.substring(2);
		}

		boolean invertResult = false;
		if (kind.startsWith("!"))
		{
			invertResult = true;
			kind = kind.substring(1);
		}
		kind = kind.substring(3);
		PrerequisiteParserInterface parser = getParser(kind);
		if (parser == null)
		{
			throw new PersistenceLayerException("Can not determine which parser to use for " + "'" + prereqStr + "'");
		}
		try
		{
			Prerequisite prereq = parser.parse(kind, formula, invertResult, false);
			//sanity check to make sure we have not got a top level element that
			// is a PREMULT with only 1 element.
			while (prereq.getKind() == null
				&& prereq.getPrerequisites().size() == 1
				&& prereq.getOperator().equals(PrerequisiteOperator.GTEQ)
				&& prereq.getOperand().equals("1"))
			{
				prereq = (Prerequisite) prereq.getPrerequisites().get(0);
			}
			return prereq;
		}
		catch (Throwable t)
		{
			throw new PersistenceLayerException("Can not parse '" + prereqStr + "': " + t.getMessage());
		}
	}

}
