/*
 *
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;


import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Race;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractChooseTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.primitive.spell.AllToken;
import plugin.primitive.spell.ClassListToken;
import plugin.primitive.spell.DescriptorToken;
import plugin.primitive.spell.DomainListToken;
import plugin.primitive.spell.ProhibitedToken;
import plugin.primitive.spell.SchoolToken;
import plugin.primitive.spell.SpellBookToken;
import plugin.primitive.spell.SpellTypeToken;
import plugin.primitive.spell.SubSchoolToken;

import org.junit.jupiter.api.BeforeEach;

/**
 * The Class {@code SpellsTokenTest} verifies the parsing and
 * unparsing of the CHOOSE:SPELLS subtoken.
 */
public class SpellsTokenTest extends
        AbstractChooseTokenTestCase<CDOMObject, Spell>
{

    static ChooseLst token = new ChooseLst();
    static SpellsToken subtoken = new SpellsToken();
    static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(new SubSchoolToken());
        TokenRegistration.register(new AllToken());
        primaryContext.getReferenceContext().constructNowIfNecessary(Spell.class, "Placeholder");
        secondaryContext.getReferenceContext().constructNowIfNecessary(Spell.class, "Placeholder");
    }

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return token;
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<Spell> getTargetClass()
    {
        return Spell.class;
    }

    @Override
    protected boolean allowsQualifier()
    {
        return true;
    }

    @Override
    protected String getChoiceTitle()
    {
        return subtoken.getDefaultTitle();
    }

    @Override
    protected QualifierToken<Spell> getPCQualifier()
    {
        return null;
    }

    @Override
    protected boolean isTypeLegal()
    {
        return true;
    }

    @Override
    protected boolean isAllLegal()
    {
        return true;
    }

    /**
     * Check that a School qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidSchool() throws PersistenceLayerException
    {
        TokenRegistration.register(new SchoolToken());
        SpellSchool schoolA =
                primaryContext.getReferenceContext().constructNowIfNecessary(SpellSchool.class,
                        "Abjuration");
        SpellSchool schoolB =
                secondaryContext.getReferenceContext().constructNowIfNecessary(SpellSchool.class,
                        "Abjuration");
        CDOMObject a =
                (CDOMObject) construct(primaryContext, "Endure Elements");
        a.addToListFor(ListKey.SPELL_SCHOOL, schoolA);
        CDOMObject c = (CDOMObject) construct(secondaryContext, "Remove Curse");
        c.addToListFor(ListKey.SPELL_SCHOOL, schoolB);
        runRoundRobin("SPELLS|SCHOOL=Abjuration");
    }

    /**
     * Check that a SubSchool qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidSubSchool() throws PersistenceLayerException
    {
        CDOMObject a =
                (CDOMObject) construct(primaryContext, "Endure Elements");
        a.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        CDOMObject c = (CDOMObject) construct(secondaryContext, "Remove Curse");
        c.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        runRoundRobin("SPELLS|SUBSCHOOL=Summoning");
    }

    /**
     * Check that a Descriptor qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidDescriptor() throws PersistenceLayerException
    {
        TokenRegistration.register(new DescriptorToken());
        runRoundRobin("SPELLS|DESCRIPTOR=mind-affecting");
    }

    /**
     * Check that a Prohibited qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidProhibited() throws PersistenceLayerException
    {
        TokenRegistration.register(new ProhibitedToken());
        runRoundRobin("SPELLS|PROHIBITED=YES");
    }

    /**
     * Check that a SpellBook qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidSpellBook() throws PersistenceLayerException
    {
        TokenRegistration.register(new SpellBookToken());
        runRoundRobin("SPELLS|SPELLBOOK=City spells");
    }

    /**
     * Check that a ClassList qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidClassList() throws PersistenceLayerException
    {
        TokenRegistration.register(new ClassListToken());
        primaryContext.getReferenceContext().constructNowIfNecessary(ClassSpellList.class,
                "Wizard");
        secondaryContext.getReferenceContext().constructNowIfNecessary(ClassSpellList.class,
                "Wizard");
        runRoundRobin("SPELLS|CLASSLIST=Wizard");
    }

    /**
     * Check that a DomainList qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidDomainList() throws PersistenceLayerException
    {
        TokenRegistration.register(new DomainListToken());
        construct(primaryContext, "Endure Elements");
        primaryContext.getReferenceContext().constructNowIfNecessary(DomainSpellList.class,
                "Good");
        secondaryContext.getReferenceContext().constructNowIfNecessary(DomainSpellList.class,
                "Good");
        runRoundRobin("SPELLS|DOMAINLIST=Good");
    }

    /**
     * Check that a SpellType qualifier is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidSpellType() throws PersistenceLayerException
    {
        TokenRegistration.register(new SpellTypeToken());
        runRoundRobin("SPELLS|SPELLTYPE=Arcane");
    }

    /**
     * Check that an All qualifier with a Known restriction is parsed
     * correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidAllKnown() throws PersistenceLayerException
    {
        CDOMObject a =
                (CDOMObject) construct(primaryContext, "Endure Elements");
        a.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        CDOMObject c = (CDOMObject) construct(secondaryContext, "Remove Curse");
        c.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        runRoundRobin("SPELLS|ALL[KNOWN=YES],SUBSCHOOL=Summoning");
    }

    /**
     * Check that a All qualifier with a LevelMin restriction is parsed
     * correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidAllLevelMin() throws PersistenceLayerException
    {
        runRoundRobin("SPELLS|ALL[LEVELMIN=MAXCASTABLE]");
    }

    /**
     * Check that a All qualifier with a LevelMax restriction is parsed
     * correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidAllLevelMax() throws PersistenceLayerException
    {
        runRoundRobin("SPELLS|ALL[LEVELMAX=7]");
    }

    /**
     * Check that a All qualifier with multiple restrictions is parsed
     * correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidAllMultiple() throws PersistenceLayerException
    {
        runRoundRobin("SPELLS|ALL[LEVELMIN=3,LEVELMAX=MAXCASTABLE,KNOWN]");
    }

    /**
     * Check that a ANY qualifier with multiple restrictions is parsed
     * correctly and migrated to ALL.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testMigrationAny() throws PersistenceLayerException
    {
        runMigrationRoundRobin(
                "SPELLS|ANY[LEVELMIN=3,LEVELMAX=MAXCASTABLE,KNOWN]",
                "SPELLS|ALL[LEVELMIN=3,LEVELMAX=MAXCASTABLE,KNOWN]");
    }

    /**
     * Check that an ANY qualifier with a Known restriction is parsed
     * correctly and migrated to ALL.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidAnyKnown() throws PersistenceLayerException
    {
        CDOMObject a =
                (CDOMObject) construct(primaryContext, "Endure Elements");
        a.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        CDOMObject c = (CDOMObject) construct(secondaryContext, "Remove Curse");
        c.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        runMigrationRoundRobin("SPELLS|ANY[KNOWN=YES],SUBSCHOOL=Summoning",
                "SPELLS|ALL[KNOWN=YES],SUBSCHOOL=Summoning");
    }

    /**
     * Check that an ANY qualifier with a Known restriction is parsed
     * correctly and migrated to ALL.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidAllKnownRev() throws PersistenceLayerException
    {
        CDOMObject a =
                (CDOMObject) construct(primaryContext, "Endure Elements");
        a.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        CDOMObject c = (CDOMObject) construct(secondaryContext, "Remove Curse");
        c.addToListFor(ListKey.SPELL_SUBSCHOOL, "Summoning");
        runMigrationRoundRobin("SPELLS|SUBSCHOOL=Summoning,ALL[KNOWN=YES]",
                "SPELLS|ALL[KNOWN=YES],SUBSCHOOL=Summoning");
    }

}
