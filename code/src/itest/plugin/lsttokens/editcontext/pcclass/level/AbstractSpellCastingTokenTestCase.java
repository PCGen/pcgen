/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext.pcclass.level;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;

public abstract class AbstractSpellCastingTokenTestCase extends
        AbstractPCClassLevelTokenTestCase
{

    @Override
    public void runRoundRobin(String... str) throws PersistenceLayerException
    {
        // Default is not to write out anything
        assertNull(getToken().unparse(primaryContext, primaryProf1));
        assertNull(getToken().unparse(primaryContext, primaryProf2));
        assertNull(getToken().unparse(primaryContext, primaryProf3));
        // Ensure the graphs are the same at the start
        assertTrue(primaryContext.getListContext().masterListsEqual(
                secondaryContext.getListContext()));
        // Set value
        for (String s : str)
        {
            assertTrue(getToken().parseToken(primaryContext, primaryProf2, s).passed());
            primaryContext.commit();
        }
        // Doesn't pollute other levels
        assertNull(getToken().unparse(primaryContext, primaryProf1));
        // Get back the appropriate token:
        String[] unparsed = getToken().unparse(primaryContext, primaryProf2);
        assertArrayEquals(str, unparsed);

        // And doesn't overwrite subsequent levels
        assertNull(getToken().unparse(primaryContext, primaryProf3));

        // Do round Robin
        StringBuilder unparsedBuilt = new StringBuilder();
        for (String s : unparsed)
        {
            unparsedBuilt.append(getToken().getTokenName()).append(':').append(
                    s).append('\t');
        }
        loader.parseLine(secondaryContext, secondaryProf2,
                unparsedBuilt.toString(), testCampaign.getURI());

        // Ensure the objects are the same
        assertEquals(primaryProf1, secondaryProf1);
        assertEquals(primaryProf2, secondaryProf2);
        assertEquals(primaryProf3, secondaryProf3);

        // Ensure the graphs are the same
        assertTrue(primaryContext.getListContext().masterListsEqual(
                secondaryContext.getListContext()));

        // And that it comes back out the same again
        // Doesn't pollute other levels
        assertNull(getToken().unparse(secondaryContext, secondaryProf1));
        String[] sUnparsed =
                getToken().unparse(secondaryContext, secondaryProf2);
        assertArrayEquals(sUnparsed, unparsed);
    }

//	@Test
//	public void testInvalidListEmpty() throws PersistenceLayerException
//	{
//		assertFalse(getToken().parse(primaryContext, primaryProf2, ""));
//	}
//
//	@Test
//	public void testInvalidListEnd() throws PersistenceLayerException
//	{
//		assertFalse(getToken().parse(primaryContext, primaryProf2, "1,"));
//	}
//
//	@Test
//	public void testInvalidListStart() throws PersistenceLayerException
//	{
//		assertFalse(getToken().parse(primaryContext, primaryProf2, ",1"));
//	}
//
//	@Test
//	public void testInvalidListDoubleJoin() throws PersistenceLayerException
//	{
//		assertFalse(getToken().parse(primaryContext, primaryProf2, "1,,2"));
//	}
//
//	@Test
//	public void testInvalidListNegativeNumber()
//		throws PersistenceLayerException
//	{
//		assertFalse(getToken().parse(primaryContext, primaryProf2, "1,-2"));
//	}

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("3");
    }

//	@Test
//	public void testRoundRobinList() throws PersistenceLayerException
//	{
//		runRoundRobin("3,2,1");
//	}

}
