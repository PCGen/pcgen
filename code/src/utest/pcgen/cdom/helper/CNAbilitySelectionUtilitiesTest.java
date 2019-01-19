package pcgen.cdom.helper;

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class CNAbilitySelectionUtilitiesTest
{

	AbilityCategory feat;
	AbilityCategory fighterfeat;
	AbilityCategory specialty;
	Ability nomult;
	Ability multyes;
	Ability stackyes;
	Ability othernomult;

	@BeforeEach
	public void setUp() throws Exception
	{
		CNAbilityFactory.reset();
		feat = new AbilityCategory();
		feat.setName("FEAT");
		fighterfeat = new AbilityCategory();
		fighterfeat.setName("Fighter Feat");
		fighterfeat.setAbilityCategory(CDOMDirectSingleRef.getRef(feat));
		specialty = new AbilityCategory();
		specialty.setName("Specialty");
		nomult = new Ability();
		nomult.setName("NoMult");
		nomult.setCDOMCategory(feat);
		multyes = new Ability();
		multyes.setName("MultYes");
		multyes.setCDOMCategory(feat);
		multyes.put(ObjectKey.MULTIPLE_ALLOWED, true);
		stackyes = new Ability();
		stackyes.setName("MultYes");
		stackyes.setCDOMCategory(feat);
		stackyes.put(ObjectKey.MULTIPLE_ALLOWED, true);
		stackyes.put(ObjectKey.STACKS, true);
		othernomult = new Ability();
		othernomult.setName("OtherNoMult");
		othernomult.setCDOMCategory(feat);
	}

	@Test
	public void testDifferentCategory()
	{
		CNAbility nomultCNA = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		Ability trickster = new Ability();
		trickster.setName("NoMult");
		trickster.setCDOMCategory(specialty);
		CNAbility othernomultCNA =
				CNAbilityFactory.getCNAbility(specialty, Nature.NORMAL, trickster);
		CNAbilitySelection cnas1 = new CNAbilitySelection(nomultCNA);
		CNAbilitySelection cnas2 = new CNAbilitySelection(othernomultCNA);
		assertTrue(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}

	@Test
	public void testDifferentAbility()
	{
		CNAbility nomultCNA = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		CNAbility othernomultCNA =
				CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, othernomult);
		CNAbilitySelection cnas1 = new CNAbilitySelection(nomultCNA);
		CNAbilitySelection cnas2 = new CNAbilitySelection(othernomultCNA);
		assertTrue(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}

	@Test
	public void testDifferentNature()
	{
		CNAbility normal = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		CNAbility virtual = CNAbilityFactory.getCNAbility(feat, Nature.VIRTUAL, nomult);
		CNAbilitySelection cnas1 = new CNAbilitySelection(normal);
		CNAbilitySelection cnas2 = new CNAbilitySelection(virtual);
		assertFalse(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}

	@Test
	public void testDetectSameParentCategory()
	{
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		CNAbility n2 = CNAbilityFactory.getCNAbility(fighterfeat, Nature.NORMAL, nomult);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1);
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2);
		assertFalse(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
		CNAbility virtual = CNAbilityFactory.getCNAbility(fighterfeat, Nature.VIRTUAL, nomult);
		CNAbilitySelection cnas3 = new CNAbilitySelection(virtual);
		assertFalse(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas3));
	}

	@Test
	public void testDetectSameSelection()
	{
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "English");
		assertFalse(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}

	@Test
	public void testDifferentSelection()
	{
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "German");
		assertTrue(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}

	@Test
	public void testDifferentSelectionAndNature()
	{
		CNAbility normal = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbility virtual = CNAbilityFactory.getCNAbility(feat, Nature.VIRTUAL, multyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(normal, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(virtual, "German");
		assertTrue(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}

	@Test
	public void testDifferentSelectionStack()
	{
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "German");
		assertTrue(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}

	@Test
	public void testStack()
	{
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "English");
		assertTrue(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
	}
}
