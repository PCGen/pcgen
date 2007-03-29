/*
 * ParameterTree.java
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
 * $Id$
 *
 */
package pcgen.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nfunk.jep.ParseException;

public class ParameterTree
{
	String        data;
	ParameterTree left;
	ParameterTree right;
	static public String orString  = "[or]";
	static public String andString = "[and]";
	static String orPatString  = "\\[or\\]";
	static String andPatString = "\\[and\\]";
	
	static String patString = "(\\(|\\)|" + orPatString + "|" + andPatString + ")";
		
	static Pattern pat = Pattern.compile(patString);


	public static ParameterTree makeTree (final String source) throws ParseException
	{
		Matcher mat = ParameterTree.pat.matcher(source);
	
		if (mat.find()) {
			return ParameterTree.makeTree(null, source.substring(0, mat.start()), mat, source, 0);
		} else {
			return new ParameterTree(source);
		}
	}

	static ParameterTree makeTree (ParameterTree outertree, String beforeop, Matcher m, String s, int start) throws ParseException
	{
		ParameterTree newt = null;

		if (m.group().equalsIgnoreCase("("))
		{
			if (m.start() != start)
			{
				throw new ParseException("found ( with no preceeding operator at char " + start + " of " + s);
			}
	
			if (m.find())
			{
				newt = makeTree(null, s.substring(start + 1, m.start()), m, s, start + 1);
				
			}
			else
			{
				throw new ParseException("We should have matched a ')' to close the inner expression.");
			}
		}
		
		
		ParameterTree root = outertree;
	
		if (beforeop.equalsIgnoreCase(""))
		{
			if (null == newt)
			{
				throw new ParseException("Apparently empty operand at char " + start + " of " + s);
			}
			else
			{
				if (root == null) {
					root = newt;
				}
				else
				{
					root.setRightTree(newt);
				}
			}
		}
		else
		{
			if (root == null) {
				root = new ParameterTree(beforeop);
			}
			else
			{
				root.setRightTree(new ParameterTree(beforeop));
			}
		}
	
		int nextstart = 0;
		
		// we can't just check for ")" because that will close all recursed subtrees
		// if we match the ), then we try to match again, this resets the match object
		// for the calling instance of this method.  This may mean that when the caller
		// hits this piece of code, the match object is in an invalid state. 
		try
		{
			nextstart = m.end();
			
			if (m.group().equalsIgnoreCase(")"))
			{
				// because this find may not work, the match object in the caller needs this try block
				if (m.find())
				{
					if (nextstart != m.start())
					{
						throw new ParseException("a close bracket must be followed by an operator or another close bracket");
					}
				}
				return root;
			}
		}
		catch (IllegalStateException e)
		{
			return root;
		}

		final ParameterTree op = new ParameterTree(m.group());
	
		op.setLeftTree(root);
		root = op;
	
		// grab the start of the right operand incase we need it next
		nextstart = m.end();

		if (m.find()) {
			
			ParameterTree t1 = makeTree(root, s.substring(nextstart, m.start()), m, s, nextstart);
			return t1;
			
		} else {
			root.setRightTree(new ParameterTree(s.substring(nextstart)));
			return root;
		}
	}

	public Double processTree (Enum e)
	{
		
		return new Double(0.0);
	}

	/**
	 * @param data
	 */
	public ParameterTree(String data) {
		super();
		this.data = data;
		left      = null;
		right     = null;
	}

	/**
	 * @return the Contents
	 */
	public String getContents() {
		return data;
	}


	/**
	 * @return the left subtree
	 */
	public ParameterTree getLeftTree() {
		return left;
	}

	/**
	 * @param l the ParameterTree to add as the left sub tree
	 */
	public void setLeftTree(ParameterTree l) {
		this.left = l;
	}
	
	/**
	 * @return the right subtree
	 */
	public ParameterTree getRightTree() {
		return right;
	}
	
	/**
	 * @param r the ParameterTree to add as the right sub tree
	 */
	public void setRightTree(ParameterTree r) {
		this.right = r;
	}
}