/*
 * Copyright 2003 (C) Ross M. Lodge
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
package plugin.dicebag.gui;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import javax.swing.JOptionPane;

import pcgen.core.RollingMethods;
import pcgen.util.Logging;

import gmgen.GMGenSystem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * <p>The base data class of the DiceBag plugin.  This class maintains a single "Dice Bag," which
 * consists of an ordered list of dice expressions.</p>
 *
 */
class DiceBagModel extends Observable
{
	/** List of dice strings. */
	private List<String> m_dice = new ArrayList<>();

	/** File path of the last file this bag was saved to, or loaded from. */
	private String m_filePath;

	/** Name of this dice bag. */
	private String m_name = "";

	/** Whether or not this bag has been altered since creation, load, or save. */
	private boolean m_changed = false;

	/**
	 * <p>Default constructor.</p>
	 */
	DiceBagModel()
	{
	}

	/**
	 * <p>Constructs a new dicebag and loads its contents
	 * from the specified xml file.</p>
	 *
	 * @param file File to load the dicebag data from.
	 */
	DiceBagModel(File file)
	{
		this();
		loadFromFile(file);
	}

	/**
	 * <p>Whether or not the bag is empty.</p>
	 *
	 * @return true/false
	 */
	boolean isBagEmpty()
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
	String setDie(int index, String element)
	{
		m_changed = true;

		return m_dice.set(index, element);
	}

	/**
	 * <p>Returns the dice expression at the specified incdex.</p>
	 *
	 * @param index die to retrieve
	 * @return The dice expression
	 */
	public String getDie(int index)
	{
		return m_dice.get(index);
	}

	/**
	 * <p>Returns the file path.</p>
	 *
	 * @return File path loaded/last saved to.
	 */
	String getFilePath()
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
	boolean addDie(String o)
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
	int diceCount()
	{
		return m_dice.size();
	}

	/**
	 * <p>Removes the die at the specified index.</p>
	 *
	 * @param index index to remove die at
	 * @return The die removed.
	 */
	String removeDie(int index)
	{
		m_changed = true;

		return m_dice.remove(index);
	}

	/**
	 * <p>Rolls the die expression at the specified index.</p>
	 *
	 * @param index Die to roll
	 * @return The double value of the expression
	 */
	double rollDie(int index)
	{
		double returnValue = 0;

		if (index < m_dice.size())
		{
			returnValue = RollingMethods.roll(m_dice.get(index));
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
	static double rollDie(String expression)
	{
		return RollingMethods.roll(expression);
	}

	/**
	 * <p>Saves the dicebag to the specified file as a UTF-8 xml file, with the format
	 * specified above in {@code loadFromFile()}</p>
	 *
	 * @param file File to save to.
	 */
	void saveToFile(File file)
	{
		try
		{
			Document doc = new Document();
			saveToDocument(doc);

			XMLOutputter xmlOut = new XMLOutputter();
			xmlOut.setFormat(Format.getPrettyFormat());
			FileWriter fr = new FileWriter(file);
			xmlOut.output(doc, fr);
			fr.flush();
			fr.close();
			m_filePath = file.getPath();
			m_changed = false;
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(GMGenSystem.inst, "File load error: "
				+ file.getName());
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
			JOptionPane.showMessageDialog(GMGenSystem.inst, "File load error: "
				+ file.getName());
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

		for (String dieString : m_dice)
		{
			Element die = new Element("dice-roll");
			die.addContent(dieString);
			party.addContent(die);
		}

		doc.setRootElement(party);
	}
}
