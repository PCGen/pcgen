/*
 * Copyright James Dempsey, 2012
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
package pcgen.core.facade.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;

import org.junit.jupiter.api.Test;

class DefaultListFacadeTest
{

    /**
     * Test method for {@link pcgen.facade.util.DefaultListFacade#updateContents(List)}
     */
    @Test
    public void testUpdateContents()
    {
        TestListener listener = new TestListener();
        DefaultListFacade<String> theList =
                new DefaultListFacade<>(Arrays.asList("A",
                        "B", "C", "E"));
        theList.addListListener(listener);
        List<String> newElements = Arrays.asList("A",
                "C", "D", "E");
        theList.updateContents(newElements);
        assertEquals(newElements, theList.getContents(), "Lists have not been made the same");
        assertEquals(1, listener.addCount, "Incorrect number of adds");
        assertEquals(1, listener.removeCount, "Incorrect number of removes");
        assertEquals(0, listener.changeCount, "Incorrect number of changes");
        assertEquals(0, listener.modifyCount, "Incorrect number of modifies");
    }

    /**
     * Test method for {@link pcgen.facade.util.DefaultListFacade#updateContents(List)}.
     */
    @Test
    public void testUpdateContentsDisparate()
    {
        TestListener listener = new TestListener();
        DefaultListFacade<String> theList = new DefaultListFacade<>(Arrays.asList("A",
                "B", "C", "E"));
        theList.addListListener(listener);
        List<String> newElements = Arrays.asList("F",
                "G", "H", "I", "M");
        theList.updateContents(newElements);
        assertEquals(newElements, theList.getContents(), "Lists have not been made the same");
        assertEquals(5, listener.addCount, "Incorrect number of adds");
        assertEquals(4, listener.removeCount, "Incorrect number of removes");
        assertEquals(0, listener.changeCount, "Incorrect number of changes");
        assertEquals(0, listener.modifyCount, "Incorrect number of modifies");
    }

    /**
     * Test method for {@link pcgen.facade.util.DefaultListFacade#updateContents(List)}.
     */
    @Test
    public void testUpdateContentsFromEmpty()
    {
        TestListener listener = new TestListener();
        DefaultListFacade<String> theList =
                new DefaultListFacade<>();
        theList.addListListener(listener);
        List<String> newElements = Arrays.asList("A",
                "C", "D", "E");
        theList.updateContents(newElements);
        assertEquals(newElements, theList.getContents(), "Lists have not been made the same");
        assertEquals(0, listener.addCount, "Incorrect number of adds");
        assertEquals(0, listener.removeCount, "Incorrect number of removes");
        assertEquals(1, listener.changeCount, "Incorrect number of changes");
        assertEquals(0, listener.modifyCount, "Incorrect number of modifies");
    }

    /**
     * Test method for {@link pcgen.facade.util.DefaultListFacade#updateContents(List)}.
     */
    @Test
    public void testUpdateContentsToEmpty()
    {
        DefaultListFacade<String> theList = new DefaultListFacade<>(Arrays.asList("A",
                "B", "C", "E"));
        TestListener listener = new TestListener();
        theList.addListListener(listener);
        List<String> newElements = Collections.emptyList();
        theList.updateContents(newElements);
        assertEquals(newElements, theList.getContents(), "Lists have not been made the same");
        assertEquals(0, listener.addCount, "Incorrect number of adds");
        assertEquals(0, listener.removeCount, "Incorrect number of removes");
        assertEquals(1, listener.changeCount, "Incorrect number of changes");
        assertEquals(0, listener.modifyCount, "Incorrect number of modifies");
    }

    /**
     * Test method for {@link pcgen.facade.util.DefaultListFacade#updateContents(List)}.
     */
    @Test
    public void testUpdateContentsTooLarge()
    {
        TestListener listener = new TestListener();
        DefaultListFacade<String> theList =
                new DefaultListFacade<>(Arrays.asList("A",
                        "B", "C", "E"));
        theList.addListListener(listener);
        List<String> newElements =
                Arrays.asList("A", "C", "D", "E", "F", "G", "H",
                        "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                        "U", "V", "W", "X", "Y", "Z");
        theList.updateContents(newElements);
        assertEquals(newElements, theList.getContents(), "Lists have not been made the same");
        assertEquals(0, listener.addCount, "Incorrect number of adds");
        assertEquals(0, listener.removeCount, "Incorrect number of removes");
        assertEquals(1, listener.changeCount, "Incorrect number of changes");
        assertEquals(0, listener.modifyCount, "Incorrect number of modifies");
    }

    /**
     * Test method for {@link pcgen.facade.util.DefaultListFacade#updateContents(List)}.
     */
    @Test
    public void testUpdateContentsTooLargeFrom()
    {
        TestListener listener = new TestListener();
        DefaultListFacade<String> theList =
                new DefaultListFacade<>(
                        Arrays.asList("A", "C", "D", "E", "F", "G",
                                "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                                "S", "T", "U", "V", "W", "X", "Y", "Z"));
        theList.addListListener(listener);
        List<String> newElements =
                Arrays.asList("A", "B", "C", "E");
        theList.updateContents(newElements);
        assertEquals(newElements, theList.getContents(), "Lists have not been made the same");
        assertEquals(0, listener.addCount, "Incorrect number of adds");
        assertEquals(0, listener.removeCount, "Incorrect number of removes");
        assertEquals(1, listener.changeCount, "Incorrect number of changes");
        assertEquals(0, listener.modifyCount, "Incorrect number of modifies");
    }

    private static class TestListener implements ListListener<String>
    {
        int addCount = 0;
        int removeCount = 0;
        int changeCount = 0;
        int modifyCount = 0;

        @Override
        public void elementAdded(ListEvent<String> e)
        {
            addCount++;
        }

        @Override
        public void elementRemoved(ListEvent<String> e)
        {
            removeCount++;
        }

        @Override
        public void elementsChanged(ListEvent<String> e)
        {
            changeCount++;
        }

        @Override
        public void elementModified(ListEvent<String> e)
        {
            modifyCount++;
        }

    }
}
