/*
 *  $Id$
 *
 *  gmgen.plugin.dicebag - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2003 RossLodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  DiceBagModel.java
 *
 *  Created on Oct 17, 2003, 2:54:09 PM
 */
package plugin.dicebag.gui;

import com.electronicmuse.djep.JEP;
import gmgen.GMGenSystem;
import gmgen.util.LogUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import pcgen.util.Logging;
import plugin.dicebag.DiceBagPlugin;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * <p>The base data class of the DiceBag plugin.  This class maintains a single "Dice Bag," which
 * consists of an ordered list of dice expressions.</p>
 *
 * @author Ross Lodge
 */
public class DiceBagModel extends Observable
{
	/** The dJEP instance for this bag. */
	private JEP m_jep = new JEP();

	/** List of dice strings. */
	private List m_dice = new ArrayList();

	/** File path of the last file this bag was saved to, or loaded from. */
	private String m_filePath;

	/** Name of this dice bag. */
	private String m_name = "";

	/** Whether or not this bag has been altered since creation, load, or save. */
	private boolean m_changed = false;

	/**
	 * <p>Default constructor.</p>
	 */
	public DiceBagModel()
	{
		m_jep.addStandardFunctions();
	}

	/**
	 * <p>Constructs a dicebag with the requested name.</p>
	 * @param name Name for new dicebag.
	 */
	public DiceBagModel(String name)
	{
		this();
		m_name = name;
	}

	/**
	 * <p>Constructs a new dicebag and loads its contents
	 * from the specified xml file.</p>
	 *
	 * @param file File to load the dicebag data from.
	 */
	public DiceBagModel(File file)
	{
		this();
		loadFromFile(file);
	}

	/**
	 * <p>Whether or not the bag is empty.</p>
	 *
	 * @return true/false
	 */
	public boolean isBagEmpty()
	{
		return m_dice.isEmpty();
	}

	/**
	 * <p>Returns true or false based on whether or not the dice
	 * bag has been changed.</p>
	 *
	 * @return true/false has bag been modified
	 */
	public boolean isChanged()
	{
		return m_changed;
	}

	/**
	 * <p>Sets the die expression at the specified index.</p>
	 *
	 * @param index index to set value of
	 * @param element Expression to set
	 * @return the expression replaced
	 */
	public String setDie(int index, String element)
	{
		m_changed = true;

		return (String) m_dice.set(index, element);
	}

	/**
	 * <p>Returns the dice expression at the specified incdex.</p>
	 *
	 * @param index die to retrieve
	 * @return The dice expression
	 */
	public String getDie(int index)
	{
		return (String) m_dice.get(index);
	}

	/**
	 * <p>Returns the file path.</p>
	 *
	 * @return File path loaded/last saved to.
	 */
	public String getFilePath()
	{
		return m_filePath;
	}

	/**
	 * <p>Sets the name of the dice bag.</p>
	 *
	 * @param string New name of dice bag.
	 */
	public void setName(String string)
	{
		m_changed = true;
		m_name = string;
	}

	/**
	 * <p>Get the name of the dicebag.</p>
	 *
	 * @return Name of diecbag
	 */
	public String getName()
	{
		return m_name;
	}

	/**
	 * <p>Adds a dice expression at the specified index.</p>
	 *
	 * @param index Index to add at.
	 * @param element Dice expression
	 */
	public void addDie(int index, String element)
	{
		m_dice.add(index, element);
		m_changed = true;
	}

	/**
	 * <p>Adds a dice expression at the end of the list.</p>
	 *
	 * @param o Dice expression
	 * @return success/failure of operation.
	 */
	public boolean addDie(String o)
	{
		m_changed = true;

		return m_dice.add(o);
	}

	/**
	 * <p>Zaps all the dice in the dice bag.</p>
	 */
	public void clearDice()
	{
		m_changed = true;
		m_dice.clear();
	}

	/**
	 * <p>Gets the count of dice in teh bag.</p>
	 *
	 * @return Count of dice in bag.
	 */
	public int diceCount()
	{
		return m_dice.size();
	}

	/**
	 * <p>Removes the die at the specified index.</p>
	 *
	 * @param index index to remove die at
	 * @return The die removed.
	 */
	public String removeDie(int index)
	{
		m_changed = true;

		return (String) m_dice.remove(index);
	}

	/**
	 * <p>Rolls the die expression at the specified index.</p>
	 *
	 * @param index Die to roll
	 * @return The double value of the expression
	 */
	public double rollDie(int index)
	{
		double returnValue = 0;

		if (index < m_dice.size())
		{
			String expression = (String) m_dice.get(index);
			m_jep.parseExpression(expression);
			returnValue = m_jep.getValue();
			if (m_jep.hasError())
			{
				JOptionPane.showMessageDialog(GMGenSystem.inst,m_jep.getErrorInfo());
				LogUtilities.inst().logMessage(DiceBagPlugin.LOG_NAME, "Parse error: " + expression + ": " + m_jep.getErrorInfo());
			}
			else
			{
				LogUtilities.inst().logMessage(DiceBagPlugin.LOG_NAME, "Roll of: " + expression + ": " + returnValue);
			}
		}

		return returnValue;
	}

	/**
	 * <p>Executes the specified die expression and returns
	 * the double result.</p>
	 *
	 * @param expression Die expression to parse
	 * @return The double value of the expression.
	 */
	public double rollDie(String expression)
	{
		double returnValue = 0;
		m_jep.parseExpression(expression);
		returnValue = m_jep.getValue();
		if (m_jep.hasError())
		{
			JOptionPane.showMessageDialog(GMGenSystem.inst,m_jep.getErrorInfo());
			LogUtilities.inst().logMessage(DiceBagPlugin.LOG_NAME, "Parse error: " + expression + ": " + m_jep.getErrorInfo());
		}
		else
		{
			LogUtilities.inst().logMessage(DiceBagPlugin.LOG_NAME, "Roll of: " + expression + ": " + returnValue);
		}

		return returnValue;
	}

	/**
	 * <p>Saves the dicebag to the specified file as a UTF-8 xml file, with the format
	 * specified above in <code>loadFromFile()</code></p>
	 *
	 * @param file File to save to.
	 */
	public void saveToFile(File file)
	{
		try
		{
			Document doc = new Document();
			saveToDocument(doc);

			XMLOutputter xmlOut = new XMLOutputter();
			xmlOut.setIndent("   ");
			xmlOut.setNewlines(true);

			FileWriter fr = new FileWriter(file);
			xmlOut.setEncoding("UTF-8");
			xmlOut.output(doc, fr);
			fr.flush();
			fr.close();
			m_filePath = file.getPath();
			m_changed = false;
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(GMGenSystem.inst, "File load error: " + file.getName());
			Logging.errorPrint("File Load Error" + file.getName());
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**
	 * <p>Loads the dicebag data from the specified jdom document.</p>
	 *
	 * @param doc A JDOM document.
	 */
	private void loadFromDocument(Document doc)
	{
		m_dice.clear();

		Element root = doc.getRootElement();

		if (root.getName().equals("dice-bag"))
		{
			m_name = root.getAttributeValue("name");

			List children = root.getChildren("dice-roll");

			for (Iterator i = children.iterator(); i.hasNext();)
			{
				Element die = (Element) i.next();
				m_dice.add(die.getTextNormalize());
			}
		}
	}

	/**
	 * <p>Loads the data from the specified file.  The document must be a valid
	 * xml document with the following form:</p>
	 * <p>
	 * <code>
	 * <dice-bag name="[Some name]">
	 *    <dice-roll>[a dice expression]</dice-roll>
	 * </dice-bag>
	 * </code>
	 * </p>
	 *
	 * @param file The file to open from.
	 */
	private void loadFromFile(File file)
	{
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(file);
			m_filePath = file.getPath();
			loadFromDocument(doc);
			m_changed = false;
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(GMGenSystem.inst, "File load error: " + file.getName());
			Logging.errorPrint("File Load Error" + file.getName());
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**
	 * <p>Loads the current dicebag's information into the
	 * given JDOM document.</p>
	 *
	 * @param doc
	 */
	private void saveToDocument(Document doc)
	{
		Element party = new Element("dice-bag");
		party.setAttribute("name", m_name);

		for (Iterator i = m_dice.iterator(); i.hasNext();)
		{
			Element die = new Element("dice-roll");
			die.addContent(i.next().toString());
			party.addContent(die);
		}

		doc.setRootElement(party);
	}
}
