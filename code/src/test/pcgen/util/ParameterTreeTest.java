/*
 * Copyright 2007 (C) Andrew Wilson <nuance@users.sourceforge.net>
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
 */

package pcgen.util;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

import pcgen.PCGenTestCase;

import org.junit.Assert;
import org.junit.Test;
import org.nfunk.jep.ParseException;

/**
 * {@code ParameterTreeTest} is ...
 */
public class ParameterTreeTest extends PCGenTestCase 
{
	/**
	 * Test method for {@link pcgen.util.ParameterTree#ParameterTree(java.lang.String)}.
	 */
	@Test
	public final void testParameterTree()
	{
        final ParameterTree t1 = new ParameterTree("Test Node1");
        is(t1.getContents(), strEq("Test Node1"), "New ParameterTree has correct contents");
		is(t1.getLeftTree(), eqnull(), "New ParameterTree has null left subtree");
		is(t1.getRightTree(), eqnull(), "New ParameterTree has null right subtree");

		final ParameterTree t2 = new ParameterTree("Test Node2");
		t2.setLeftTree(t1);
		is(t2.getContents(), strEq("Test Node2"), "New ParameterTree has correct contents");
		is(t2.getLeftTree().getContents(), strEq("Test Node1"), "New ParameterTree has null left subtree");
		is(t1.getRightTree(), eqnull(), "New ParameterTree has null right subtree");
	}

	@Test
	public final void testMakeTree1()
	{
		final String s = "TYPE=Foo";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq(s), "New ParamterTree has correct contents");
	}
	
	@Test
	public final void testMakeTree2()
	{
		final String s = "(TYPE=Foo)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("TYPE=Foo"), "New ParamterTree has correct contents");
	}

	@Test
	public final void testMakeTree3()
	{
		final String s = "((TYPE=Foo))";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("TYPE=Foo"), "New ParamterTree has correct contents");
	}

	@Test
	public final void testMakeTree4()
	{
		final String s = "TYPE=Foo[or]TYPE=Bar";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("[or]"),                      "New ParamterTree has correct contents");
		is(t1.getLeftTree().getContents(),  strEq("TYPE=Foo"), "New ParamterTree has correct left tree contents");
		is(t1.getLeftTree().getLeftTree(),  eqnull(), "New ParamterTree has correct left tree, left tree contents");
		is(t1.getLeftTree().getRightTree(), eqnull(), "New ParamterTree has correct left tree, right tree contents");
		
		is(t1.getRightTree().getContents(), strEq("TYPE=Bar"), "New ParamterTree has correct right tree contents");
		is(t1.getRightTree().getLeftTree(),  eqnull(), "New ParamterTree has correct left tree, left tree contents");
		is(t1.getRightTree().getRightTree(), eqnull(), "New ParamterTree has correct left tree, right tree contents");
	}


	@Test
	public final void testMakeTree5()
	{
		final String s = "(TYPE=Foo[or]TYPE=Bar)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("[or]"),                      "New ParamterTree has correct contents");
		is(t1.getLeftTree().getContents(),  strEq("TYPE=Foo"), "New ParamterTree has correct left tree contents");
		is(t1.getLeftTree().getLeftTree(),  eqnull(), "New ParamterTree has correct left tree, left tree contents");
		is(t1.getLeftTree().getRightTree(), eqnull(), "New ParamterTree has correct left tree, right tree contents");
		
		is(t1.getRightTree().getContents(), strEq("TYPE=Bar"), "New ParamterTree has correct right tree contents");
		is(t1.getRightTree().getLeftTree(),  eqnull(), "New ParamterTree has correct left tree, left tree contents");
		is(t1.getRightTree().getRightTree(), eqnull(), "New ParamterTree has correct left tree, right tree contents");
	}

	@Test
	public final void testMakeTree6()
	{
		final String s = "(TYPE=Foo[or]TYPE=Bar[and]String3)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		final ParameterTree tl  = t.getLeftTree();
		final ParameterTree tr  = t.getRightTree();
		final ParameterTree tll = tl.getLeftTree();
		final ParameterTree tlr = tl.getRightTree();

		// expected branch nodes
		is(t.getContents(), strEq("[and]"),   "t1 ParamterTree has correct contents");
		is(tl.getContents(), strEq("[or]"),  "tl ParamterTree has correct contents");

		// expected leaf nodes
		is(tr.getContents(), strEq("String3"),  "tr ParamterTree has correct contents");
		is(tll.getContents(), strEq("TYPE=Foo"), "tll ParamterTree has correct contents");
		is(tlr.getContents(), strEq("TYPE=Bar"), "tlr ParamterTree has correct contents");
		
		// check that leaves really are leaves
		is(tr.getLeftTree(),  eqnull(), "tr left tree is null (i.e. is a leaf node)");
		is(tr.getRightTree(), eqnull(), "tr right tree is null (i.e. is a leaf node)");
		
		is(tll.getLeftTree(),  eqnull(), "tll left tree is null (i.e. is a leaf node)");
		is(tll.getRightTree(), eqnull(), "tll right tree is null (i.e. is a leaf node)");

		is(tlr.getLeftTree(),  eqnull(), "tlr left tree is null (i.e. is a leaf node)");
		is(tlr.getRightTree(), eqnull(), "tlr right tree is null (i.e. is a leaf node)");
	}

	@Test
	public final void testMakeTree7()
	{
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 7 --- \n\n");

		final String s = "TYPE=Foo[or](TYPE=Bar[and]String3)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		final ParameterTree tl  = t.getLeftTree();
		final ParameterTree tr  = t.getRightTree();
		final ParameterTree trl = tr.getLeftTree();
		final ParameterTree trr = tr.getRightTree();

		// expected branch nodes
		assertThat("t  not null", t, not(eqnull()));
		assertThat("tr  not null", tr, not(eqnull()));

		is(t.getContents(),  strEq("[or]"),  "t  has correct contents '[or]'");
		is(tr.getContents(), strEq("[and]"),  "tr has correct contents '[and]'");

		// expected leaf nodes
		Assert.assertNotNull("tl  not null", tl);
		Assert.assertNotNull("trl  not null", trl);
		Assert.assertNotNull("trr  not null", trr);

		is(tl.getContents(),  strEq("TYPE=Foo"), "tl  has correct contents 'TYPE=Foo'");
		is(trl.getContents(), strEq("TYPE=Bar"), "trl has correct contents 'TYPE=Bar'");
		is(trr.getContents(), strEq("String3"),  "trr has correct contents 'String3'");
		
		// check that leaves really are leaves
		is(tl.getLeftTree(),  eqnull(), "tl left tree is null (i.e. is a leaf node)");
		is(tl.getRightTree(), eqnull(), "tl right tree is null (i.e. is a leaf node)");
		
		is(trl.getLeftTree(),  eqnull(), "trl left tree is null (i.e. is a leaf node)");
		is(trl.getRightTree(), eqnull(), "trl right tree is null (i.e. is a leaf node)");

		is(trr.getLeftTree(),  eqnull(), "trr left tree is null (i.e. is a leaf node)");
		is(trr.getRightTree(), eqnull(), "trr right tree is null (i.e. is a leaf node)");
	}


	@Test
	public final void testMakeTree8()
	{
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 8 --- \n\n");

		final String s = "TYPE=Foo[or]((CATEGORY=FEAT[or]NATURE=AUTO)[and]TYPE=Bar)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		} 


		final ParameterTree tl  = t.getLeftTree();
		final ParameterTree tr  = t.getRightTree();

		final ParameterTree trl = tr.getLeftTree();
		final ParameterTree trr = tr.getRightTree();
		
		final ParameterTree trll = trl.getLeftTree();
		final ParameterTree trlr = trl.getRightTree();

		Assert.assertNotNull("t not null", t);
		Assert.assertNotNull("tr not null", tr);
		Assert.assertNotNull("trl not null", trl);

		is(t.getContents(),   strEq("[or]"),  "t  has correct contents '[or]'");
		is(tr.getContents(),  strEq("[and]"),  "tr has correct contents '[and]'");
		is(trl.getContents(), strEq("[or]"),  "trl has correct contents '[or]'");

		// expected leaf nodes
		Assert.assertNotNull("tl not null", tl);
		Assert.assertNotNull("trr not null", trr);

		Assert.assertNotNull("trll not null", trll);
		Assert.assertNotNull("trlr not null", trlr);

		is(tl.getContents(),  strEq("TYPE=Foo"), "tl  has correct contents 'TYPE=Foo'");
		is(trr.getContents(), strEq("TYPE=Bar"), "trr has correct contents 'TYPE=Bar'");

		is(trll.getContents(), strEq("CATEGORY=FEAT"), "trl has correct contents 'CATEGORY=FEAT'");
		is(trlr.getContents(), strEq("NATURE=AUTO"), "trl has correct contents 'NATURE=AUTO'");

		// check that leaves really are leaves
		is(tl.getLeftTree(),  eqnull(), "tl left tree is null (i.e. is a leaf node)");
		is(tl.getRightTree(), eqnull(), "tl right tree is null (i.e. is a leaf node)");

		is(trr.getLeftTree(),  eqnull(), "trr left tree is null (i.e. is a leaf node)");
		is(trr.getRightTree(), eqnull(), "trr right tree is null (i.e. is a leaf node)");

		is(trll.getLeftTree(),  eqnull(), "trl left tree is null (i.e. is a leaf node)");
		is(trll.getRightTree(), eqnull(), "trl right tree is null (i.e. is a leaf node)");

		is(trlr.getLeftTree(),  eqnull(), "trll left tree is null (i.e. is a leaf node)");
		is(trlr.getRightTree(), eqnull(), "trlr right tree is null (i.e. is a leaf node)");
	}

	@Test
	public final void testMakeTree9()
	{
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 9 --- \n\n");

		final String s = "TYPE=Foo[or]((CATEGORY=FEAT[or]NATURE=AUTO[or]CATEGORY=SA)[and]TYPE=Bar)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			Assert.fail("Threw a parse exception");
		} 


		final ParameterTree tl    = t.getLeftTree();
		final ParameterTree tr    = t.getRightTree();

		final ParameterTree trl   = tr.getLeftTree();
		final ParameterTree trr   = tr.getRightTree();

		final ParameterTree trll  = trl.getLeftTree();
		final ParameterTree trlr  = trl.getRightTree();
        
		final ParameterTree trlll = trll.getLeftTree();
		final ParameterTree trllr = trll.getRightTree();

		// expected branch nodes
		Assert.assertNotNull("t not null", t);
		Assert.assertNotNull("tr not null", tr);
		Assert.assertNotNull("trl not null", trl);
		Assert.assertNotNull("trll not null", trll);

		is(t.getContents(),    strEq("[or]"), "t    has correct contents '[or]'");
		is(tr.getContents(),   strEq("[and]"), "tr   has correct contents '[and]'");
		is(trl.getContents(),  strEq("[or]"), "trl  has correct contents '[or]'");
		is(trll.getContents(), strEq("[or]"), "trll has correct contents '[or]'");
		
		// expected leaf nodes
		Assert.assertNotNull("tl not null", tl);
		Assert.assertNotNull("trr not null", trr);

		Assert.assertNotNull("trlr not null", trlr);
		Assert.assertNotNull("trlll not null", trlll);
		Assert.assertNotNull("trllr not null", trllr);

		is(tl.getContents(),    strEq("TYPE=Foo"), "tl  has correct contents 'TYPE=Foo'");
		is(trr.getContents(),   strEq("TYPE=Bar"), "trr has correct contents 'TYPE=Bar'");

		is(trlr.getContents(),  strEq("CATEGORY=SA"),   "trlr has correct contents 'CATEGORY=SA'");
		is(trlll.getContents(), strEq("CATEGORY=FEAT"), "trlr has correct contents 'CATEGORY=FEAT'");
		is(trllr.getContents(), strEq("NATURE=AUTO"),   "trlr has correct contents 'NATURE=AUTO'");

		
		// check that leaves really are leaves
		is(tl.getLeftTree(),     eqnull(), "tl left tree is null (i.e. is a leaf node)");
		is(tl.getRightTree(),    eqnull(), "tl right tree is null (i.e. is a leaf node)");

		is(trr.getLeftTree(),    eqnull(), "trr left tree is null (i.e. is a leaf node)");
		is(trr.getRightTree(),   eqnull(), "trr right tree is null (i.e. is a leaf node)");

		is(trlr.getLeftTree(),   eqnull(), "trll left tree is null (i.e. is a leaf node)");
		is(trlr.getRightTree(),  eqnull(), "trlr right tree is null (i.e. is a leaf node)");

		is(trlll.getLeftTree(),  eqnull(), "trlll left tree is null (i.e. is a leaf node)");
		is(trlll.getRightTree(), eqnull(), "trlll right tree is null (i.e. is a leaf node)");

		is(trllr.getLeftTree(),  eqnull(), "trlll left tree is null (i.e. is a leaf node)");
		is(trllr.getRightTree(), eqnull(), "trlll right tree is null (i.e. is a leaf node)");
	}
}
