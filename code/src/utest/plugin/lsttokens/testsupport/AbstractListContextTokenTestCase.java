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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;

import org.junit.jupiter.api.Test;

public abstract class AbstractListContextTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
        extends AbstractListInputTokenTestCase<T, TC>
{

    protected abstract CDOMReference<? extends CDOMList<? extends PrereqObject>> getListReference();

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeAllFromList(getListReference());
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        TC wp1 = construct(primaryContext, "TestWP1");
        addToList(CDOMDirectSingleRef.getRef(wp1));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "TestWP1");
    }

    @Test
    public void testUnparseMultiple()
    {
        TC wp1 = construct(primaryContext, "TestWP1");
        addToList(CDOMDirectSingleRef.getRef(wp1));
        TC wp2 = construct(primaryContext, "TestWP2");
        addToList(CDOMDirectSingleRef.getRef(wp2));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "TestWP1" + getJoinCharacter() + "TestWP2");
    }

    @Test
    public void testUnparseDupe()
    {
        if (allowDups())
        {
            TC wp1 = construct(primaryContext, "TestWP1");
            addToList(CDOMDirectSingleRef.getRef(wp1));
            addToList(CDOMDirectSingleRef.getRef(wp1));
            String[] unparsed = getToken().unparse(primaryContext, primaryProf);
            expectSingle(unparsed, "TestWP1" + getJoinCharacter() + "TestWP1");
        } else
        {
            TC wp1 = construct(primaryContext, "TestWP1");
            addToList(CDOMDirectSingleRef.getRef(wp1));
            addToList(CDOMDirectSingleRef.getRef(wp1));
            String[] unparsed = getToken().unparse(primaryContext, primaryProf);
            expectSingle(unparsed, "TestWP1");
        }
    }

    @Test
    public void testUnparseNullInList()
    {
        addToList(null);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            //Yep!
        }
    }

    @Test
    public void testUnparseType()
    {
        if (isTypeLegal())
        {
            CDOMGroupRef<TC> tr = getTypeReference();
            addToList(tr);
            String[] unparsed = getToken().unparse(primaryContext, primaryProf);
            expectSingle(unparsed, tr.getLSTformat(false));
        }
    }

    protected CDOMGroupRef<TC> getTypeReference()
    {
        return primaryContext.getReferenceContext().getCDOMTypeReference(getTargetClass(), "Type1");
    }

    @Test
    public void testUnparseAll()
    {
        if (isTypeLegal())
        {
            CDOMGroupRef<TC> allReference = getAllReference();
            addToList(allReference);
            String[] unparsed = getToken().unparse(primaryContext, primaryProf);
            expectSingle(unparsed, getAllString());
        }
    }

    protected CDOMGroupRef<TC> getAllReference()
    {
        return primaryContext.getReferenceContext().getCDOMAllReference(getTargetClass());
    }

    /*
     * TODO Need to figure out who owns this responsibility
     */
    // @Test
    // public void testUnparseGenericsFail() throws PersistenceLayerException
    // {
    // ListKey listKey = getListKey();
    // primaryProf.addToListFor(listKey, new Object());
    // try
    // {
    // String[] unparsed = getToken().unparse(primaryContext, primaryProf);
    // fail();
    // }
    // catch (ClassCastException e)
    // {
    // // Yep!
    // }
    // }

    protected void addToList(CDOMReference<TC> val)
    {
        SimpleAssociatedObject sao = new SimpleAssociatedObject();
        sao.setAssociation(AssociationKey.TOKEN, getToken().getTokenName());
        primaryProf.putToList(getListReference(), val, sao);
        doCustomAssociations(sao);
    }

    protected void doCustomAssociations(AssociatedPrereqObject apo)
    {
        // Empty here, can be overridden
    }

}
