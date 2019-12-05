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

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AddLst;

import org.junit.jupiter.api.Test;

public abstract class AbstractAddTokenTestCase<TC extends CDOMObject> extends
        AbstractSelectionTokenTestCase<CDOMObject, TC>
{
    static AddLst token = new AddLst();
    static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public Class<? extends CDOMObject> getCDOMClass()
    {
        return PCTemplate.class;
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
    public boolean isTypeLegal()
    {
        return true;
    }

    @Override
    public boolean allowsParenAsSub()
    {
        return false;
    }

    @Override
    public boolean allowsFormula()
    {
        return false;
    }

    protected abstract ChoiceActor<TC> getActor();

    protected void buildTC(Formula count, List<CDOMReference<TC>> refs)
    {
        ReferenceChoiceSet<TC> rcs = new ReferenceChoiceSet<>(refs);
        ChoiceSet<TC> cs = new ChoiceSet<>(getSubToken().getTokenName(), rcs);
        PersistentTransitionChoice<TC> tc = new ConcretePersistentTransitionChoice<>(
                cs, count);
        primaryProf.addToListFor(ListKey.ADD, tc);
        tc.setChoiceActor(getActor());
    }

    @Test
    public void testUnparseSingle()
    {
        String name = "TestWP1";
        List<CDOMReference<TC>> refs = new ArrayList<>();
        addSingleRef(refs, name);
        buildTC(FormulaFactory.ONE, refs);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + name);
    }

    @Test
    public void testUnparseSingleThree()
    {
        List<CDOMReference<TC>> refs = new ArrayList<>();
        addSingleRef(refs, "TestWP1");
        buildTC(FormulaFactory.getFormulaFor(3), refs);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "3|TestWP1");
    }

    @Test
    public void testUnparseSingleNegative()
    {
        List<CDOMReference<TC>> refs = new ArrayList<>();
        addSingleRef(refs, "TestWP1");
        buildTC(FormulaFactory.getFormulaFor(-3), refs);
        assertBadUnparse();
    }

    @Test
    public void testUnparseSingleZero()
    {
        List<CDOMReference<TC>> refs = new ArrayList<>();
        addSingleRef(refs, "TestWP1");
        buildTC(FormulaFactory.getFormulaFor(0), refs);
        assertBadUnparse();
    }

    @Test
    public void testUnparseSingleVariable()
    {
        List<CDOMReference<TC>> refs = new ArrayList<>();
        addSingleRef(refs, "TestWP1");
        buildTC(FormulaFactory.getFormulaFor("Formula"), refs);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "Formula|TestWP1");
    }

    @Test
    public void testUnparseType()
    {
        List<CDOMReference<TC>> refs = new ArrayList<>();
        addTypeRef(refs, "Bar", "Foo");
        buildTC(FormulaFactory.ONE, refs);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + getTypePrefix()
                + "TYPE=Bar.Foo");
    }

    @Test
    public void testUnparseSingleAll()
    {
        if (isAllLegal())
        {
            List<CDOMReference<TC>> refs = new ArrayList<>();
            addSingleRef(refs, "TestWP1");
            addAllRef(refs);
            buildTC(FormulaFactory.ONE, refs);
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseAll()
    {
        if (isAllLegal())
        {
            List<CDOMReference<TC>> refs = new ArrayList<>();
            addAllRef(refs);
            buildTC(FormulaFactory.ONE, refs);
            String[] unparsed = getToken().unparse(primaryContext, primaryProf);
            expectSingle(unparsed, getSubTokenName() + '|' + "ALL");
        }
    }

    @Test
    public void testUnparseTypeAll()
    {
        if (isAllLegal())
        {
            List<CDOMReference<TC>> refs = new ArrayList<>();
            addTypeRef(refs, "Bar", "Foo");
            addAllRef(refs);
            buildTC(FormulaFactory.ONE, refs);
            assertBadUnparse();
        }
    }

    protected void addSingleRef(List<CDOMReference<TC>> refs, String string)
    {
        TC obj = primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(),
                string);
        refs.add(CDOMDirectSingleRef.getRef(obj));
    }

    protected void addTypeRef(List<CDOMReference<TC>> refs, String... types)
    {
        CDOMGroupRef<TC> ref = primaryContext.getReferenceContext().getCDOMTypeReference(
                getTargetClass(), types);
        refs.add(ref);
    }

    protected void addAllRef(List<CDOMReference<TC>> refs)
    {
        CDOMGroupRef<TC> ref = primaryContext.getReferenceContext()
                .getCDOMAllReference(getTargetClass());
        refs.add(ref);
    }
}
