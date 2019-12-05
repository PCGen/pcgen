/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.facet.analysis;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.ChangeProf;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.WeaponProf;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;

import org.junit.jupiter.api.BeforeEach;

public class ChangeProfFacetTest extends
        AbstractExtractingFacetTest<CDOMObject, ChangeProf>
{

    public static int n = 0;

    private ChangeProfFacet facet = new ChangeProfFacet();
    private ChangeProf[] target;
    private CDOMObject[] source;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        context =
                new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
                        new ConsolidatedListCommitStrategy());
        CDOMObject cdo1 = new PCTemplate();
        cdo1.setName("Template1");
        CDOMObject cdo2 = new Race();
        cdo2.setName("Race1");
        ChangeProf st1 = getObject();
        ChangeProf st2 = getObject();
        cdo1.addToListFor(ListKey.CHANGEPROF, st1);
        cdo2.addToListFor(ListKey.CHANGEPROF, st2);
        source = new CDOMObject[]{cdo1, cdo2};
        target = new ChangeProf[]{st1, st2};
    }

    @Override
    protected AbstractSourcedListFacet<CharID, ChangeProf> getFacet()
    {
        return facet;
    }

    private LoadContext context;

    @Override
    protected ChangeProf getObject()
    {
        return new ChangeProf(context.getReferenceContext().getCDOMReference(WeaponProf.class,
                "StartProf" + n++), context.getReferenceContext().getCDOMTypeReference(
                WeaponProf.class, "Gorpy"));
    }

    @Override
    protected CDOMObject getContainingObject(int i)
    {
        return source[i];
    }

    @Override
    protected DataFacetChangeListener<CharID, CDOMObject> getListener()
    {
        return facet;
    }

    @Override
    protected ChangeProf getTargetObject(int i)
    {
        return target[i];
    }
}
