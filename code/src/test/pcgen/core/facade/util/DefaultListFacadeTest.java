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

import pcgen.facade.util.DefaultListFacade;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;

/**
 * The Class <code></code> ...
 *
 * <br/>
 * 
 */

public class DefaultListFacadeTest
{

	/**
	 * Test method for {@link pcgen.core.facade.util.DefaultListFacade#updateContents(java.util.List)}.
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
		assertEquals("Lists have not been made the same", newElements, theList.getContents());
		assertEquals("Incorrect number of adds", 1, listener.addCount);
		assertEquals("Incorrect number of removes", 1, listener.removeCount);
		assertEquals("Incorrect number of changes", 0, listener.changeCount);
		assertEquals("Incorrect number of modifies", 0, listener.modifyCount);
	}

	/**
	 * Test method for {@link pcgen.core.facade.util.DefaultListFacade#updateContents(java.util.List)}.
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
		assertEquals("Lists have not been made the same", newElements, theList.getContents());
		assertEquals("Incorrect number of adds", 5, listener.addCount);
		assertEquals("Incorrect number of removes", 4, listener.removeCount);
		assertEquals("Incorrect number of changes", 0, listener.changeCount);
		assertEquals("Incorrect number of modifies", 0, listener.modifyCount);
	}

	/**
	 * Test method for {@link pcgen.core.facade.util.DefaultListFacade#updateContents(java.util.List)}.
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
		assertEquals("Lists have not been made the same", newElements, theList.getContents());
		assertEquals("Incorrect number of adds", 0, listener.addCount);
		assertEquals("Incorrect number of removes", 0, listener.removeCount);
		assertEquals("Incorrect number of changes", 1, listener.changeCount);
		assertEquals("Incorrect number of modifies", 0, listener.modifyCount);
	}

	/**
	 * Test method for {@link pcgen.core.facade.util.DefaultListFacade#updateContents(java.util.List)}.
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
		assertEquals("Lists have not been made the same", newElements, theList.getContents());
		assertEquals("Incorrect number of adds", 0, listener.addCount);
		assertEquals("Incorrect number of removes", 0, listener.removeCount);
		assertEquals("Incorrect number of changes", 1, listener.changeCount);
		assertEquals("Incorrect number of modifies", 0, listener.modifyCount);
	}

	/**
	 * Test method for {@link pcgen.core.facade.util.DefaultListFacade#updateContents(java.util.List)}.
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
		assertEquals("Lists have not been made the same", newElements, theList.getContents());
		assertEquals("Incorrect number of adds", 0, listener.addCount);
		assertEquals("Incorrect number of removes", 0, listener.removeCount);
		assertEquals("Incorrect number of changes", 1, listener.changeCount);
		assertEquals("Incorrect number of modifies", 0, listener.modifyCount);
	}

	/**
	 * Test method for {@link pcgen.core.facade.util.DefaultListFacade#updateContents(java.util.List)}.
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
		assertEquals("Lists have not been made the same", newElements, theList.getContents());
		assertEquals("Incorrect number of adds", 0, listener.addCount);
		assertEquals("Incorrect number of removes", 0, listener.removeCount);
		assertEquals("Incorrect number of changes", 1, listener.changeCount);
		assertEquals("Incorrect number of modifies", 0, listener.modifyCount);
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
