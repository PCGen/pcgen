/*
 * InfoLabelTextBuilderTest.java
 * Copyright 2007 (C) Koen Van Daele
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
 *
 * Created on 4-feb-07
 *
 * $Id$
 */
package pcgen.gui.utils;

import java.util.Locale;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.LocaleDependentTestCase;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * <code>InfoLabelTextBuilderTest</code> tests the InfoLabelTextBuilder.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Koen Van Daele <vandaelek@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("nls")
public class InfoLabelTextBuilderTest extends TestCase
{
	public static void main(String args[])
	{
		TestRunner.run(InfoLabelTextBuilderTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(InfoLabelTextBuilderTest.class);
	}
	
	/**
	 * Test adding a string.
	 */
	public void testAppendString()
	{
		InfoLabelTextBuilder b = new InfoLabelTextBuilder();
		
		b.append("Test");
		
		assertEquals("<html>Test</html>" , b.toString());
	}
	
	/**
	 * Test adding a simple element with a key and a value.
	 */
	public void testAppendElement()
	{
		InfoLabelTextBuilder b = new InfoLabelTextBuilder();
		
		b.appendElement("HP" , "25");
		
		assertEquals("<html><b>HP:</b>&nbsp;25</html>" , b.toString());
	}
	
	/**
	 * Test adding an element that gets its key from the language properties.
	 */
	public void testAppendI18nElement()
	{
		InfoLabelTextBuilder b = new InfoLabelTextBuilder();
		LocaleDependentTestCase.before(Locale.FRENCH);
		b.appendI18nElement("in_player" , "Koen");
		LocaleDependentTestCase.after();
		
		LocaleDependentTestCase.before(Locale.US);
		assertEquals("<html><b>Joueur:</b>&nbsp;Koen</html>" , b.toString());
		EnUsLocaleDependentTestCase.after();
	}
	
	/**
	 * Test building a string with some different options.
	 */
	public void testAppendComplex()
	{
		InfoLabelTextBuilder b = new InfoLabelTextBuilder("Character");
		
		LocaleDependentTestCase.before(Locale.FRENCH);
		b.appendLineBreak().appendI18nElement("in_player" , "Koen");
		LocaleDependentTestCase.after();
		
		LocaleDependentTestCase.before(Locale.US);
		assertEquals("<html><b><font size=+1>Character</font></b>"+
				"<br><b>Joueur:</b>&nbsp;Koen</html>" , b.toString());
		EnUsLocaleDependentTestCase.after();
	}
	

}
