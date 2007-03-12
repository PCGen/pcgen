/*
 * ParameterTreeTest.java
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
 *
 * Created on 10 March 2007
 *
 * $Id: ParameterTreeTest.java 2489 2007-03-12 20:50:11Z nuance $
 *
 */

package pcgen.util;

import java.util.regex.Matcher;

import org.junit.Test;
import org.nfunk.jep.ParseException;

import pcgen.PCGenTestCase;
import pcgen.util.ParameterTree;

/**
 * <code>ParameterTreeTest</code> is ...
 *
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2007-03-12 20:50:11 +0000 (Mon, 12 Mar 2007) $
 *
 * @author andrew wilson <nuance@users.sourceforge.net>
 * @version $Revision: 2489 $
 */
public class ParameterTreeTest extends PCGenTestCase 
{


	/**
	 * Test method for {@link pcgen.util.ParameterTree#ParameterTree(java.lang.String)}.
	 */
	@Test
	public final void testParameterTree()
	{
		ParameterTree t1 = new ParameterTree("Test Node1");
		is(t1.getContents(), strEq("Test Node1"), "New ParamterTrre has correct contents");
		is(t1.getLeftTree(), eqnull(), "New ParamterTrre has null left subtree");
		is(t1.getRightTree(), eqnull(), "New ParamterTrre has null right subtree");

		ParameterTree t2 = new ParameterTree("Test Node2");
		t2.setLeftTree(t1);
		is(t2.getContents(), strEq("Test Node2"), "New ParamterTrre has correct contents");
		is(t2.getLeftTree().getContents(), strEq("Test Node1"), "New ParamterTrre has null left subtree");
		is(t1.getRightTree(), eqnull(), "New ParamterTrre has null right subtree");
	}

	@Test
	public final void testMakeTree1()
	{
		String s = "TYPE=Foo";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try {
			t1 = ParameterTree.makeTree(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq(s), "New ParamterTree has correct contents");
	}
	
	@Test
	public final void testMakeTree2()
	{
		String s = "(TYPE=Foo)";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try {
			t1 = ParameterTree.makeTree(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("TYPE=Foo"), "New ParamterTree has correct contents");
	}

	@Test
	public final void testMakeTree3()
	{
		String s = "((TYPE=Foo))";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try {
			t1 = ParameterTree.makeTree(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("TYPE=Foo"), "New ParamterTree has correct contents");
	}

	@Test
	public final void testMakeTree4()
	{
		String s = "TYPE=Foo||TYPE=Bar";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try {
			t1 = ParameterTree.makeTree(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("||"),                      "New ParamterTree has correct contents");
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
		String s = "(TYPE=Foo||TYPE=Bar)";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try {
			t1 = ParameterTree.makeTree(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		}
		
		is(t1.getContents(), strEq("||"),                      "New ParamterTree has correct contents");
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
		String s = "(TYPE=Foo||TYPE=Bar&&String3)";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try {
			t = ParameterTree.makeTree(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		ParameterTree tl  = t.getLeftTree();
		ParameterTree tr  = t.getRightTree();
		ParameterTree tll = tl.getLeftTree();
		ParameterTree tlr = tl.getRightTree();

		// expected branch nodes
		is(t.getContents(), strEq("&&"),   "t1 ParamterTree has correct contents");
		is(tl.getContents(), strEq("||"),  "tl ParamterTree has correct contents");

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

		String s = "TYPE=Foo||(TYPE=Bar&&String3)";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try {
			t = ParameterTree.makeTree(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		ParameterTree tl  = t.getLeftTree();
		ParameterTree tr  = t.getRightTree();
		ParameterTree trl = tr.getLeftTree();
		ParameterTree trr = tr.getRightTree();

		// expected branch nodes
		is(t, not(eqnull()),   "t  not null");
		is(tr, not(eqnull()),  "tr not null");

		is(t.getContents(),  strEq("||"),  "t  has correct contents '||'");
		is(tr.getContents(), strEq("&&"),  "tr has correct contents '&&'");

		// expected leaf nodes
		is(tl,  not(eqnull()), "tl  not null");
		is(trl, not(eqnull()), "trl not null");
		is(trr, not(eqnull()), "trr not null");

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

		String s = "TYPE=Foo||((CATEGORY=FEAT||NATURE=AUTO)&&TYPE=Bar)";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		} 


		ParameterTree tl  = t.getLeftTree();
		ParameterTree tr  = t.getRightTree();

		ParameterTree trl = tr.getLeftTree();
		ParameterTree trr = tr.getRightTree();
		
		ParameterTree trll = trl.getLeftTree();
		ParameterTree trlr = trl.getRightTree();

		
		// expected branch nodes
		is(t, not(eqnull()),   "t  not null");
		is(tr, not(eqnull()),  "tr not null");
		is(trl, not(eqnull()), "trl not null");

		is(t.getContents(),   strEq("||"),  "t  has correct contents '||'");
		is(tr.getContents(),  strEq("&&"),  "tr has correct contents '&&'");
		is(trl.getContents(), strEq("||"),  "trl has correct contents '||'");

		// expected leaf nodes
		is(tl,  not(eqnull()), "tl  not null");
		is(trr, not(eqnull()), "trr not null");

		is(trll, not(eqnull()), "trll not null");
		is(trlr, not(eqnull()), "trlr not null");
		
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

		String s = "TYPE=Foo||((CATEGORY=FEAT||NATURE=AUTO||CATEGORY=SA)&&TYPE=Bar)";
		Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e) {
			e.printStackTrace();
			fail("Threw a parse exception");
		} 


		ParameterTree tl    = t.getLeftTree();
		ParameterTree tr    = t.getRightTree();

		ParameterTree trl   = tr.getLeftTree();
		ParameterTree trr   = tr.getRightTree();
		
		ParameterTree trll  = trl.getLeftTree();
		ParameterTree trlr  = trl.getRightTree();

		ParameterTree trlll = trll.getLeftTree();
		ParameterTree trllr = trll.getRightTree();

		
		// expected branch nodes
		is(t, not(eqnull()),    "t  not null");
		is(tr, not(eqnull()),   "tr not null");
		is(trl, not(eqnull()),  "trl not null");
		is(trll, not(eqnull()), "trll not null");

		is(t.getContents(),    strEq("||"), "t    has correct contents '||'");
		is(tr.getContents(),   strEq("&&"), "tr   has correct contents '&&'");
		is(trl.getContents(),  strEq("||"), "trl  has correct contents '||'");
		is(trll.getContents(), strEq("||"), "trll has correct contents '||'");
		
		// expected leaf nodes
		is(tl,  not(eqnull()), "tl  not null");
		is(trr, not(eqnull()), "trr not null");

		is(trlr,  not(eqnull()), "trlr not null");
		is(trlll, not(eqnull()), "trlll not null");
		is(trllr, not(eqnull()), "trllr not null");

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
