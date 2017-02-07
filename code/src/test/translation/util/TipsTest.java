/*
 * Copyright 2012 Vincent Lhote
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
package translation.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * JUnit Tests for {@link translation.util.Tips}.
 */
@SuppressWarnings("nls")
public class TipsTest
{

	private static final String comment = "# This is a comment in the tips file";
	private static final String emptyLine = "";
	private static final String tip = "For each method, write a test method";
	private static final String tip2 = "Another tip for you";

	/**
	 * Test method for {@link translation.util.Tips#addTip(java.util.Set, java.lang.String)}.
	 */
	@Test
	public void testAddTip()
	{
		Set<String> t = new HashSet<>();
		Assert.assertThat(t.size(), is(0));
		Tips.addTip(t, tip);
		Assert.assertThat(t, hasItem(tip));
		Assert.assertThat(t.size(), is(1));
		Tips.addTip(t, tip);
		Assert.assertThat(t, hasItem(tip));
		Assert.assertThat(t.size(), is(1));
	}

	@Test
	public void isTip() throws Exception
	{
		Assert.assertThat(Tips.isTip(emptyLine), not(is(false)));
		Assert.assertThat(Tips.isTip(comment), not(is(false)));
		Assert.assertThat(Tips.isTip(tip), is(true));
		Assert.assertThat(Tips.isTip(tip2), is(true));
	}
	
	@Test
	public void removeEscapeTest()
	{
		Assert.assertThat(Tips.removeEscaped(""), is(""));
		Assert.assertThat(Tips.removeEscaped("a"), is("a"));
		Assert.assertThat(Tips.removeEscaped("l\\'eau"), is("l'eau"));
		Assert.assertThat(Tips.removeEscaped("\\\"quoted\\\""), is("\"quoted\""));
		Assert.assertThat(Tips.removeEscaped("\\\\"), is("\\"));
	}
	
	@Test
	public void escapeTest()
	{
		Assert.assertThat(Tips.escape(""), is(""));
		Assert.assertThat(Tips.escape("a"), is("a"));
		Assert.assertThat(Tips.escape("l'eau"), is("l\\'eau"));
		Assert.assertThat(Tips.escape("\"quoted\""), is("\\\"quoted\\\""));
		Assert.assertThat(Tips.escape("\\"), is("\\\\"));
	}
}
