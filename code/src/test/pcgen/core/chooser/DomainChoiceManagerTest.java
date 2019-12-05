/*
 * Copyright 2005 (C) Andrew Wilson <nuance@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 * $Author: nuance $
 * $Date: 2006-03-26 08:00:03 +0100 (Sun, 26 Mar 2006) $
 * $Revision: 471 $
 */
package pcgen.core.chooser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;

import org.hamcrest.Matchers;

/**
 * {@code DomainChoiceManagerTest} test that the DomainChoiceManager class is
 * functioning correctly.
 */

public class DomainChoiceManagerTest extends AbstractCharacterTestCase
{

    /**
     * Test the constructor
     */
    public void test001()
    {
        Race pObj = new Race();
        pObj.setName("My PObject");
        LoadContext context = Globals.getContext();
        Domain foo = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Foo");
        Domain bar = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Bar");
        Domain baz = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Baz");
        Domain qux = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Qux");
        Domain quux = context.getReferenceContext().constructCDOMObject(Domain.class, "KEY_Quux");
        context.unconditionallyProcess(pObj, "CHOOSE",
                "DOMAIN|KEY_Foo|KEY_Bar|KEY_Baz|KEY_Qux|KEY_Quux");
        assertThat(context.getReferenceContext().resolveReferences(null), Matchers.is(true));
        assertThat(pObj.get(ObjectKey.CHOOSE_INFO), notNullValue());
        pObj.put(FormulaKey.NUMCHOICES, FormulaFactory.getFormulaFor(4));
        PlayerCharacter aPC = getCharacter();

        ChoiceManagerList choiceManager = ChooserUtilities.getChoiceManager(
                pObj, aPC);
        assertThat("Found the chooser", choiceManager, notNullValue());

        List<Domain> aList = new ArrayList<>();
        List<Domain> sList = new ArrayList<>();
        choiceManager.getChoices(aPC, aList, sList);
        assertThat(aList.size(), Matchers.is(5));
        assertThat(aList, hasItem(foo));
        assertThat(aList, hasItem(bar));
        assertThat(aList, hasItem(baz));
        assertThat(aList, hasItem(qux));
        assertThat(aList, hasItem(quux));

        assertThat(sList.size(), Matchers.is(0));
    }

}
