/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  XMLCombatant.java
 */
package plugin.initiative;

import java.util.ArrayList;
import java.util.List;

import gmgen.plugin.Combatant;
import gmgen.plugin.State;
import gmgen.plugin.SystemAttribute;
import gmgen.plugin.SystemHP;
import gmgen.plugin.SystemInitiative;
import pcgen.util.Logging;

import org.apache.commons.lang3.math.Fraction;
import org.jdom2.Element;

public class XMLCombatant extends Combatant
{
	/** Challenge rating of the combatant */
	protected float cr;
	/** Experience points of the combatant */
	protected int xp;

	/** The JDOM element from which the combatant was derived. */
	private final Element combatant;

	/**
	 *  Creates new XMLCombatant from a JDOM Element.
	 *
	 *@param  combatant  XML Element containing one combatant.
	 */
	public XMLCombatant(Element combatant)
	{
		this.combatant = combatant;

		try
		{
			combatant.getChild("Attributes").getChild("Strength").getAttribute("value").getIntValue();
			int dexVal = combatant.getChild("Attributes").getChild("Dexterity").getAttribute("value").getIntValue();
			int conVal = combatant.getChild("Attributes").getChild("Constitution").getAttribute("value").getIntValue();
			combatant.getChild("Attributes").getChild("Intelligence").getAttribute("value").getIntValue();
			combatant.getChild("Attributes").getChild("Wisdom").getAttribute("value").getIntValue();
			combatant.getChild("Attributes").getChild("Charisma").getAttribute("value").getIntValue();

			xp = combatant.getChild("General").getChild("Experience").getAttribute("total").getIntValue();
			cr = combatant.getChild("General").getChild("CR").getAttribute("value").getFloatValue();

			int hpVal = combatant.getChild("Combat").getChild("HitPoints").getAttribute("max").getIntValue();
			int hpCurrVal = combatant.getChild("Combat").getChild("HitPoints").getAttribute("current").getIntValue();
			int hpSubdual = 0;

			try
			{
				hpSubdual = combatant.getChild("Combat").getChild("HitPoints").getAttribute("subdual").getIntValue();
			}
			catch (Exception e)
			{
				//Subdual is not set, we will jsut use 0
			}

			try
			{
				setCombatantType(combatant.getAttribute("type").getValue());
			}
			catch (Exception e)
			{
				//Combatant type not necessarily set.
			}

			String sInitBonus = combatant.getChild("Combat").getChild("Initiative").getAttribute("Misc").getValue();
			int initBonus = 0;

			if (sInitBonus.startsWith("+"))
			{
				sInitBonus = sInitBonus.substring(1);
				initBonus = Integer.parseInt(sInitBonus);
			}
			else
			{
				initBonus = Integer.parseInt(sInitBonus);
			}

			createSystemVals(dexVal, conVal, hpVal, hpCurrVal, hpSubdual, initBonus);

			try
			{
				init.setCurrentInitiative(
					combatant.getChild("Combat").getChild("Initiative").getAttribute("current").getIntValue());
			}
			catch (Exception e)
			{
				//Current initiative not necessarily set
			}

			try
			{
				// XXX might not be working anymore
				hitPoints.setState(State
					.getState(combatant.getChild("Combat").getChild("HitPoints").getAttribute("subdual").getValue()));
			}
			catch (Exception e)
			{
				//Hit Point state not necessarily set
			}
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Used for creating the object without an XML representation.
	 * </p>
	 *
	 * @param name       Combatant name
	 * @param player     Player name
	 * @param dexVal     Value of dexterity attribute (used for initiative bonus)
	 * @param conVal     Value of the constitution bonus (used for death/dying)
	 * @param hpVal      Max hit points
	 * @param hpCurrVal  Current hit points
	 * @param hpSubdual  Subdual damage information
	 * @param initBonus  Initiative bonus (excluding dexterity)
	 * @param type       Type of combatant
	 * @param cr         Challenge rating
	 */
	public XMLCombatant(String name, String player, int dexVal, int conVal, int hpVal, int hpCurrVal, int hpSubdual,
		int initBonus, String type, float cr)
	{
		this(name, player, 10, dexVal, conVal, 10, 10, 10, 0, 0, 0, hpVal, hpCurrVal, hpSubdual, initBonus, type, cr,
			0);
	}

	/**
	 * <p>
	 * Used for creating the object without an XML representation.
	 * </p>
	 *
	 * @param name       Combatant name
	 * @param player     Player Name
	 * @param strVal     Strength
	 * @param dexVal     Dexterity
	 * @param conVal     Constitution
	 * @param intVal     Intelligence
	 * @param wisVal     Wisdom
	 * @param chaVal     Charisma
	 * @param fortSave   Fortitude save
	 * @param refSave    Reflex Save
	 * @param willSave   Will save
	 * @param hpVal      Maximum hit points
	 * @param hpCurrVal  Current hit points
	 * @param hpSubdual  Current subdual damage
	 * @param initBonus  Initiative bonus
	 * @param type       Combatant type
	 * @param cr         Challenge rating
	 * @param xp		 The experience points
	 */
	public XMLCombatant(String name, String player, int strVal, int dexVal, int conVal, int intVal, int wisVal,
		int chaVal, int fortSave, int refSave, int willSave, int hpVal, int hpCurrVal, int hpSubdual, int initBonus,
		String type, float cr, int xp)
	{
		this.comType = type;
		this.xp = xp;
		createSystemVals(dexVal, conVal, hpVal, hpCurrVal, hpSubdual, initBonus);

		int dexMod = init.getAttribute().getModifier();
		int miscMod = initBonus - dexMod;
		combatant = new Element("Character");
		combatant.setAttribute("name", name);
		combatant.setAttribute("player", player);

		Element attributes = new Element("Attributes");
		attributes.addContent(new Element("Strength").setAttribute("value", Integer.toString(strVal)));
		attributes.addContent(new Element("Dexterity").setAttribute("value", Integer.toString(dexVal)));
		attributes.addContent(new Element("Constitution").setAttribute("value", Integer.toString(conVal)));
		attributes.addContent(new Element("Intelligence").setAttribute("value", Integer.toString(intVal)));
		attributes.addContent(new Element("Wisdom").setAttribute("value", Integer.toString(wisVal)));
		attributes.addContent(new Element("Charisma").setAttribute("value", Integer.toString(chaVal)));
		combatant.addContent(attributes);

		Element saves = new Element("Saves");
		saves.addContent(new Element("Fortitude").setAttribute("total", Integer.toString(fortSave)));
		saves.addContent(new Element("Reflex").setAttribute("total", Integer.toString(refSave)));
		saves.addContent(new Element("Will").setAttribute("total", Integer.toString(willSave)));
		combatant.addContent(saves);

		Element general = new Element("General");
		general.addContent(new Element("Experience").setAttribute("total", Integer.toString(0)));
		general.addContent(new Element("CR").setAttribute("value", Float.toString(cr)));
		combatant.addContent(general);
		this.cr = cr;

		Element combat = new Element("Combat");
		combat.addContent(new Element("Initiative").setAttribute("mod", formatBonus(initBonus))
			.setAttribute("Dex", formatBonus(dexMod)).setAttribute("Misc", formatBonus(miscMod)));
		combat.addContent(new Element("HitPoints").setAttribute("max", Integer.toString(hpVal)).setAttribute("current",
			Integer.toString(hpCurrVal)));
		combatant.addContent(combat);
	}

	/**
	 * <p>
	 * Gets the requested attribute value
	 * </p>
	 *
	 * @param name Name of the attribute
	 * @return The value of the attribute
	 */
	public int getAttribute(String name)
	{
		try
		{
			return combatant.getChild("Attributes").getChild(name).getAttribute("value").getIntValue();
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);

			return 10;
		}
	}

	/**
	 * <p>
	 * Sets the challenge rating
	 * </p>
	 *
	 * @param cr   The challenge rating
	 */
	@Override
	public void setCR(float cr)
	{
		this.cr = cr;
	}

	/**
	 * <p>Gets the CR value for the character</p>
	 * @return CR value
	 */
	@Override
	public float getCR()
	{
		return cr;
	}

	/**
	 * 
	 * TODO Much of this code is repeated in CRToken, Race, XMLCombatant and PlayerCharacterOutput
	 * 
	 * <p>Gets the CR value for the character for output</p>
	 * @return CR value
	 */
	public String getCRForOutput()
	{
		String retString = "";
		String crAsString = Float.toString(cr);
		String decimalPlaceValue = crAsString.substring(crAsString.length() - 2);

		// If the CR is a fractional CR then we convert to a 1/x format
		if (cr > 0 && cr < 1)
		{
			Fraction fraction = Fraction.getFraction(cr); // new Fraction(CR);
			int denominator = fraction.getDenominator();
			int numerator = fraction.getNumerator();
			retString = numerator + "/" + denominator;
		}
		else if (cr >= 1 || cr == 0)
		{
			int newCr = -99;
			if (decimalPlaceValue.equals(".0"))
			{
				newCr = (int) cr;
			}

			if (newCr > -99)
			{
				retString = retString + newCr;
			}
			else
			{
				retString = retString + cr;
			}
		}
		return retString;
	}

	/**
	 * <p>
	 * Sets the Initative Bonues of the XMLCombatant
	 * </p>
	 *
	 * @param  initBonus  The new Init Bonus value
	 */
	public void setInitBonus(int initBonus)
	{
		init.setBonus(initBonus);
	}

	/**
	 * <p>
	 * Sets the name attribute of the XMLCombatant
	 * </p>
	 *
	 * @param  name  The new name value
	 */
	@Override
	public void setName(String name)
	{
		combatant.getAttribute("name").setValue(name);
	}

	/**
	 *  Gets the name attribute of the XMLCombatant
	 *
	 *@return    The name value
	 */
	@Override
	public String getName()
	{
		return combatant.getAttribute("name").getValue();
	}

	/**
	 *  Sets the player attribute of the XMLCombatant
	 *
	 *@param  player  The new player value
	 */
	public void setPlayer(String player)
	{
		combatant.getAttribute("player").setValue(player);
	}

	/**
	 *  Gets the player attribute of the XMLCombatant
	 *
	 *@return    The player value
	 */
	@Override
	public String getPlayer()
	{
		return combatant.getAttribute("player").getValue();
	}

	/**
	 * <p>
	 * Sets the value of the specified save
	 * </p>
	 *
	 * @param name  Name of the save type
	 * @param value The save bonus
	 */
	public void setSave(String name, int value)
	{
		try
		{
			combatant.getChild("Saves").getChild(name).setAttribute("total", Integer.toString(value));
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Gets the save bonus for the requested save
	 * </p>
	 *
	 * @param name Save type name
	 * @return Save bonus
	 */
	public int getSave(String name)
	{
		try
		{
			String saveBonus = combatant.getChild("Saves").getChild(name).getAttribute("total").getValue();

			if (saveBonus.startsWith("+"))
			{
				saveBonus = saveBonus.substring(1);
			}

			return Integer.parseInt(saveBonus);
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);

			return 0;
		}
	}

	/**
	 *  gets a new XML Element from the XMLCombatant, intended to be added to a new XML Document.
	 *  Used for saving out to file.
	 *
	 *@return    The New Element
	 */
	@Override
	public Element getSaveElement()
	{
		Element retElement = combatant.clone();
		retElement.detach();

		int dexMod = init.getAttribute().getModifier();
		int initBonus = init.getModifier();
		int miscMod = init.getBonus();
		retElement.getChild("Attributes").getChild("Dexterity").setAttribute("value",
			Integer.toString(init.getAttribute().getValue()));
		retElement.getChild("Attributes").getChild("Constitution").setAttribute("value",
			Integer.toString(hitPoints.getAttribute().getValue()));
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("max", Integer.toString(hitPoints.getMax()));
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("current",
			Integer.toString(hitPoints.getCurrent()));
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("subdual",
			Integer.toString(hitPoints.getSubdual()));
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("state", hitPoints.getState().name());
		retElement.getChild("Combat").getChild("Initiative").setAttribute("mod", formatBonus(initBonus));
		retElement.getChild("Combat").getChild("Initiative").setAttribute("Dex", formatBonus(dexMod));
		retElement.getChild("Combat").getChild("Initiative").setAttribute("Misc", formatBonus(miscMod));

		if (init.getCurrentInitiative() > 0)
		{
			retElement.getChild("Combat").getChild("Initiative").setAttribute("current",
				Integer.toString(init.getCurrentInitiative()));
		}

		retElement.getChild("General").getChild("CR").setAttribute("value", Float.toString(cr));
		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("type", getCombatantType());

		return retElement;
	}

	/**
	 * Sets the XP value for the character
	 *
	 * @param xp The XP value
	 */
	@Override
	public void setXP(int xp)
	{
		this.xp = xp;
	}

	/**
	 * Gets the experience value for the character
	 * @return Experience value
	 */
	@Override
	public int getXP()
	{
		return xp;
	}

	/**
	 *  changes the value of a table field in the backend data set
	 *
	 *@param  columnOrder  A list of columns in order for the table
	 *@param  colNumber    What column number has been edited
	 *@param  data         The new value for the field
	 */
	@Override
	public void editRow(List<String> columnOrder, int colNumber, Object data)
	{
		String columnName = columnOrder.get(colNumber);
		String strData = String.valueOf(data);

		//Determine which row was edited
		if (columnName.equals("Name"))
		{ // Character's Name
			setName(strData);
		}
		else if (columnName.equals("Player"))
		{ // Player's Name
			setPlayer(strData);
		}
		else if (columnName.equals("Status"))
		{ // XML Combatant's Status
			setStatus(State.getState(strData));
		}
		else if (columnName.equals("+"))
		{ // Initative bonus
			setInitBonus(Integer.parseInt(strData));
		}
		else if (columnName.equals("Init"))
		{ // Initative
			init.setCurrentInitiative(Integer.parseInt(strData));
		}
		else if (columnName.equals("#"))
		{ // Number (for tokens)
			setNumber(Integer.parseInt(strData));
		}
		else if (columnName.equals("HP"))
		{ // Current Hit Points
			hitPoints.setCurrent(Integer.parseInt(strData));
		}
		else if (columnName.equals("HP Max"))
		{ // Maximum Hit Points
			hitPoints.setMax(Integer.parseInt(strData));
		}
		else if (columnName.equals("Dur"))
		{ // Duration
			setDuration(Integer.parseInt(strData));
		}
		else if (columnName.equals("Type"))
		{
			// Type
			setCombatantType(strData);
		}
	}

	/**
	 *
	 * <p>Creates system HP and system initiative values</p>
	 *
	 * @param dexVal
	 * @param conVal
	 * @param hpVal
	 * @param hpCurrVal
	 * @param subdual
	 * @param initBonus
	 */
	private void createSystemVals(int dexVal, int conVal, int hpVal, int hpCurrVal, int subdual, int initBonus)
	{
		init = new SystemInitiative(new SystemAttribute("Dexterity", dexVal), initBonus);
		hitPoints = new SystemHP(new SystemAttribute("Constitution", conVal), hpVal, hpCurrVal);
		hitPoints.setSubdual(subdual);
	}

	/**
	 *
	 * <p>Returns a string with a + or - in front of it.</p>
	 *
	 * @param bonus
	 * @return A string representation of the number
	 */
	private String formatBonus(int bonus)
	{
		if (bonus >= 0)
		{
			return "+" + bonus;
		}
		return String.valueOf(bonus);
	}

	/**
	 * Return as HTML String
	 * @return PCRenderer as HTML string
	 */
	@Override
	public String toHtmlString()
	{
		return new PcRenderer().getHtmlText();
	}

	protected class PcRenderer
	{

		/**
		 * Get the HTML text
		 * @return HTML text
		 */
		public String getHtmlText()
		{
			StringBuilder statBuf = new StringBuilder();

			statBuf.append("<html>");
			statBuf.append(getStatBlockHeader());
			statBuf.append("<body class='Normal' lang='EN-US'>");
			statBuf.append(getStatBlockTitle());
			statBuf.append(getStatBlockCore());
			statBuf.append("</html>");
			return statBuf.toString();
		}

		protected String getStatBlockHeader()
		{
			StringBuilder statBuf = new StringBuilder();

			statBuf.append("<head><title>");
			statBuf.append(getName());
			statBuf.append(" - ");
			statBuf.append(getPlayer());
			statBuf.append(" in GMGEN Statblock Format");
			statBuf.append("</title>");
			statBuf.append("<style type='text/css'>");
			statBuf.append("a:link {color: #006699}");
			statBuf.append("a:visited {color: #006699}");
			statBuf.append("a:hover {color: #006699}");
			statBuf.append("a:active {color: #006699}");
			statBuf.append(".type {color:#555555;font-weight:bold}");
			statBuf.append(".highlight {color:#FF0000}");
			statBuf.append(".dialog {color:#006699}");
			statBuf.append("</style></head>");

			return statBuf.toString();
		}

		protected String getStatBlockTitle()
		{
			StringBuilder statBuf = new StringBuilder();

			statBuf.append("<p class='gork'><font size='+1'><b>");
			statBuf.append(getName());
			statBuf.append("</b></font></p>");

			return statBuf.toString();
		}

		protected String getStatBlockCore()
		{
			StringBuilder statBuf = new StringBuilder();

			statBuf.append("<font class='type'>CR</font> ");
			// statBuf.append(getCR());
			statBuf.append(getCRForOutput());
			statBuf.append("; ");

			statBuf.append("<font class='type'>hp</font> ");
			statBuf.append(hitPoints.getCurrent());
			statBuf.append('/');
			statBuf.append(hitPoints.getMax());

			statBuf.append("<br>");

			statBuf.append("<font class='type'>Init</font> <font class='highlight'>");
			statBuf.append(init.getCurrentInitiative() >= 0 ? "+" : "");
			statBuf.append(init.getCurrentInitiative());
			statBuf.append("</font> (");
			statBuf.append(init.getAttribute().getModifier() >= 0 ? "+" : "");
			statBuf.append(init.getAttribute().getModifier());
			statBuf.append(" Dex, ");
			statBuf.append(init.getBonus() >= 0 ? "+" : "");
			statBuf.append(init.getBonus());
			statBuf.append(" Misc) ");

			statBuf.append("<br>");

			statBuf.append("<font class='type'>Saves:</font> Fort <font class='highlight'>");
			statBuf.append("<a href='save:FORTITUDE\\");
			final int fortitudeSave = getSave("Fortitude");
			statBuf.append(fortitudeSave >= 0 ? "+" : "");
			statBuf.append(fortitudeSave);
			statBuf.append("' class='highlight'>");
			statBuf.append(fortitudeSave >= 0 ? "+" : "");
			statBuf.append(fortitudeSave);
			statBuf.append("</a>");
			statBuf.append("</font>, Ref <font class='highlight'>");
			statBuf.append("<a href='save:REFLEX\\");
			final int reflexSave = getSave("Reflex");
			statBuf.append(reflexSave >= 0 ? "+" : "");
			statBuf.append(reflexSave);
			statBuf.append("' class='highlight'>");
			statBuf.append(reflexSave >= 0 ? "+" : "");
			statBuf.append(reflexSave);
			statBuf.append("</a>");
			statBuf.append("</font>, Will <font class='highlight'>");
			statBuf.append("<a href='save:WILL\\");
			final int willSave = getSave("Will");
			statBuf.append(willSave >= 0 ? "+" : "");
			statBuf.append(willSave);
			statBuf.append("' class='highlight'>");
			statBuf.append(willSave >= 0 ? "+" : "");
			statBuf.append(willSave);
			statBuf.append("</a>");
			statBuf.append("</font> ");

			statBuf.append("<br>");

			List<SystemAttribute> statList = new ArrayList<>();
			statList.add(new SystemAttribute("Str", getAttribute("Strength")));
			statList.add(new SystemAttribute("Con", getAttribute("Constitution")));
			statList.add(new SystemAttribute("Dex", getAttribute("Dexterity")));
			statList.add(new SystemAttribute("Int", getAttribute("Intelligence")));
			statList.add(new SystemAttribute("Wis", getAttribute("Wisdom")));
			statList.add(new SystemAttribute("Cha", getAttribute("Charisma")));

			for (SystemAttribute stat : statList)
			{
				statBuf.append("<font class='type'>");
				statBuf.append(stat.getName());
				statBuf.append("</font> ");
				statBuf.append(stat.getValue());
				statBuf.append("&nbsp;(");
				statBuf.append("<a href='check:");
				statBuf.append(stat.getName());
				statBuf.append("\\1d20");
				final int statModifier = stat.getModifier();
				statBuf.append(statModifier >= 0 ? "+" : "");
				statBuf.append(statModifier);
				statBuf.append("' class='dialog'>");
				statBuf.append(statModifier >= 0 ? "+" : "");
				statBuf.append(statModifier);
				statBuf.append("</a>) ");
			}

			statBuf.append("</p>");

			return statBuf.toString();
		}
	}
}
