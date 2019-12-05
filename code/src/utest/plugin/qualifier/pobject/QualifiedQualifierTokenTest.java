/*
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
package plugin.qualifier.pobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.RaceToken;
import plugin.lsttokens.testsupport.AbstractQualifierTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QualifiedQualifierTokenTest extends
        AbstractQualifierTokenTestCase<CDOMObject, Race>
{

    private static final CDOMPrimaryToken TOKEN = new ChooseLst();
    private static final CDOMSecondaryToken SUBTOKEN = new RaceToken();
    private static final CDOMLoader<CDOMObject> LOADER = new CDOMTokenLoader<>();
    private Race s1;
    private Race s2;

    private static final LstToken QUALIFIED_TOKEN = new QualifiedToken();

    QualifiedQualifierTokenTest()
    {
        super("QUALIFIED", null);
    }

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(QUALIFIED_TOKEN);
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return SUBTOKEN;
    }

    @Override
    public Class<Race> getTargetClass()
    {
        return Race.class;
    }

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return LOADER;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return TOKEN;
    }

    @Override
    protected boolean allowsNotQualifier()
    {
        return true;
    }

    @Override
    protected Class<? extends QualifierToken> getQualifierClass()
    {
        return QualifiedToken.class;
    }

    @Test
    public void testGetSet()
    {
        setUpPC();
        initializeObjects();
        assertTrue(parse(getSubTokenName() + "|QUALIFIED[ALL]"));
        finishLoad();
        TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

        ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
        Collection<?> set = info.getSet(pc);
        assertTrue(set.isEmpty());
        pc.qualifiedSet.add(s1);
        pc.qualifiedSet.add(s2);
        set = info.getSet(pc);
        assertEquals(2, set.size());
        assertTrue(set.contains(s1));
        assertTrue(set.contains(s2));
    }

    @Test
    public void testGetSetFiltered()
    {
        setUpPC();
        initializeObjects();
        assertTrue(parse(getSubTokenName() + "|QUALIFIED[TYPE=Masterful]"));
        finishLoad();
        TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

        ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
        Collection<?> set = info.getSet(pc);
        assertTrue(set.isEmpty());
        pc.qualifiedSet.add(s1);
        pc.qualifiedSet.add(s2);
        set = info.getSet(pc);
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertTrue(set.contains(s2));
    }

    private void initializeObjects()
    {
        s1 = new Race();
        s1.setName("s1");
        primaryContext.getReferenceContext().importObject(s1);

        s2 = new Race();
        s2.setName("s2");
        primaryContext.getReferenceContext().importObject(s2);
        primaryContext.unconditionallyProcess(s2, "TYPE", "Masterful");

        Race s3 = new Race();
        s3.setName("s3");
        primaryContext.getReferenceContext().importObject(s3);
        primaryContext.unconditionallyProcess(s3, "TYPE", "Masterful");
    }
}
