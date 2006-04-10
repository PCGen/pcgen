/*
 * ExportHandler.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on March 07, 2002, 8:30 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.io;

import pcgen.core.*;
import pcgen.core.character.Follower;
import pcgen.core.utils.CoreUtility;
import pcgen.io.exporttoken.*;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.io.exporttoken.SkillToken;

import java.io.*;
import java.util.*;

/**
 * <code>ExportHandler</code>.
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public final class ExportHandler
{
	private static HashMap tokenMap = new HashMap();
	private static boolean tokenMapPopulated = false;

	// Processing state variables
	private boolean existsOnly = false;
	private boolean noMoreItems = false;

	private File templateFile;
	private final Map loopVariables = new HashMap();
	private String csheetTag2 = "\\";
	private boolean skipMath = false;
	private boolean canWrite = true;
	private boolean checkBefore = false;
	private boolean inLabel = false;

	/**
	 * Constructor.
	 *
	 * @param templateFile the template to use while exporting.
	 *                     <p/>
	 *                     <br>author: Thomas Behr 12-04-02
	 */
	public ExportHandler(File templateFile)
	{
		populateTokenMap();
		setTemplateFile(templateFile);
	}

	/**
	 * Replace the token, but skip the maths
	 * @param aPC
	 * @param aString
	 * @param output
	 */
	public void replaceTokenSkipMath(PlayerCharacter aPC, String aString, BufferedWriter output)
	{
		final boolean oldSkipMath = skipMath;
		skipMath = true;
		replaceToken(aString, output, aPC);
		skipMath = oldSkipMath;
	}

	/**
	 * Exports the contents of the given PlayerCharacter to a Writer
	 * according to the handler's template
	 * <p/>
	 * <br>author: Thomas Behr 12-04-02
	 *
	 * @param aPC the PlayerCharacter to write
	 * @param out   the Writer to be written to
	 */
	public void write(PlayerCharacter aPC, BufferedWriter out)
	{
		// Get the EquipSet used for output and calculations
		// possibly include equipment from temporary bonuses
		aPC.setCalcEquipmentList(aPC.getUseTempMods());

		// Make sure spell lists are setup
		aPC.getSpellList();

		FileAccess.setCurrentOutputFilter(templateFile.getName());

		aPC.getAllSkillList(true); //force refresh of skills

		int includeSkills = SettingsHandler.getIncludeSkills();

		// TODO Reference a constant
		if (includeSkills == 3)
		{
			includeSkills = SettingsHandler.getSkillsTab_IncludeSkills();
		}

		aPC.populateSkills(includeSkills);

		for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			aClass.getSpellSupport().sortCharacterSpellList();
		}

		aPC.determinePrimaryOffWeapon();
		aPC.modFromArmorOnWeaponRolls();
		aPC.adjustMoveRates();
		aPC.calcActiveBonuses();

		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), "UTF-8"));

			String aString;
			final StringBuffer inputLine = new StringBuffer();

			while ((aString = br.readLine()) != null)
			{
				if (aString.length() == 0)
				{
					inputLine.append(' ').append(Constants.s_LINE_SEP);
				}
				else if (aString.indexOf("||") < 0)
				{
					inputLine.append(aString).append(Constants.s_LINE_SEP);
				}
				else
				{
					// Adjacent separators get merged by StringTokenizer, so we break them up here
					int dblBarPos = aString.indexOf("||");

					while (dblBarPos >= 0)
					{
						inputLine.append(aString.substring(0, dblBarPos)).append("| |");
						aString = aString.substring(dblBarPos + 2);
						dblBarPos = aString.indexOf("||");
					}

					if (aString.length() > 0)
					{
						inputLine.append(aString);
					}

					inputLine.append(Constants.s_LINE_SEP);
				}
			}

			aString = inputLine.toString();

			final StringTokenizer aTok = new StringTokenizer(aString, "\r\n", false);

			final FileAccess fa = new FileAccess();

			// parse the template for and pre-process all the
			// FOR loops and IIF statements
			//
			final FORNode root = parseFORs(aTok);
			loopVariables.put(null, "0");
			existsOnly = false;
			noMoreItems = false;

			//
			// now actualy process the (new) template file
			//
			loopFOR(root, 0, 0, 1, out, fa, aPC);
			loopVariables.clear();
		}
		catch (IOException exc)
		{
			Logging.errorPrint("Error in ExportHandler::write", exc);
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					//TODO: If this should be ignored, add a comment here describing why.
				}
			}

			if (out != null)
			{
				try
				{
					out.flush();
				}
				catch (IOException e)
				{
					//TODO: If this should be ignored, add a comment here describing why.
				}
			}
		}

		csheetTag2 = "\\";

		// reset the EquipmentList without Temporary Bonus equipment
		aPC.setCalcEquipmentList(false);

		// Reset the skills back to the display prefs.
		aPC.populateSkills(SettingsHandler.getSkillsTab_IncludeSkills());
	}

	/**
	 * Exports a PlayerCharacter-Party to a Writer
	 * according to the handler's template
	 * <p/>
	 * <br>author: Thomas Behr 13-11-02
	 *
	 * @param PCs the Collection of PlayerCharacter instances which compromises the Party to write
	 * @param out the Writer to be written to
	 */
	public void write(Collection PCs, BufferedWriter out)
	{
		write((PlayerCharacter[]) PCs.toArray(new PlayerCharacter[PCs.size()]), out);
	}

	/**
	 * Discovers if a string is a attack routine. It must begin with a sign (+-),
	 * it must have only digits, and must have a delimiter
	 * May be optimized via Stringtokenizer, instead of a for
	 * @param aString
	 * @return true if it is an attack routine
	 */
	private static boolean isAttackRoutine(String aString)
	{
		final String signs = "+-";
		final String delimiter = "/";
		int typeBefore = 0; // 0=delimiter, 1=sign, 2=digit

		for (int i = 0; i < aString.length(); ++i)
		{
			if (signs.indexOf(aString.charAt(i)) > -1)
			{
				if (typeBefore != 0)
				{
					return false;
				}

				typeBefore = 1;
			}
			else if (delimiter.indexOf(aString.charAt(i)) > -1)
			{
				if (typeBefore != 2)
				{
					return false;
				}

				typeBefore = 0;
			}
			else if ((aString.charAt(i) >= '0') && (aString.charAt(i) <= '9'))
			{
				if ((typeBefore != 1) && (typeBefore != 2))
				{
					return false;
				}

				typeBefore = 2;
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the item description
	 * @param sType
	 * @param sKey
	 * @param sAlt
	 * @param aPC
	 * @return item description
	 */
	public static String getItemDescription(String sType, String sKey, String sAlt, PlayerCharacter aPC)
	{
		if (SettingsHandler.isROG())
		{
			if ("EMPTY".equals(aPC.getDescriptionLst()))
			{
				aPC.loadDescriptionFilesInDirectory("descriptions");
			}

			String aDescription = sAlt;
			final String aSearch = sType.toUpperCase() + ":" + sKey + Constants.s_LINE_SEP;
			final int pos = aPC.getDescriptionLst().indexOf(aSearch);

			if (pos >= 0)
			{
				aDescription = aPC.getDescriptionLst().substring(pos + aSearch.length());
				aDescription = aDescription.substring(0, aDescription.indexOf("####") - 1).trim();
			}

			return aDescription;
		}
		return sAlt;
	}

	/**
	 * Sets the template to use for export<br>
	 * Use this method to reset this handler, if it should be used
	 * to export to different/multiple templates
	 *
	 * @param templateFile the template to use while exporting.
	 */
	private void setTemplateFile(File templateFile)
	{
		this.templateFile = templateFile;
	}

	/**
	 * Returns the current templateFile being used
	 * @return templateFile
	 */
	public File getTemplateFile()
	{
		return templateFile;
	}
	
	private int getVarValue(String var, PlayerCharacter aPC)
	{
		char chC;

		for (int idx = -1;;)
		{
			idx = var.indexOf("COUNT[EQ", idx + 1);

			if (idx < 0)
			{
				break;
			}

			chC = var.charAt(idx + 8);

			if ((chC == '.') || ((chC >= '0') && (chC <= '9')))
			{
				final int i = var.indexOf(']', idx + 8);

				if (i >= 0)
				{
					String aString = var.substring(idx + 6, i);
					if(aString.indexOf("EQTYPE") > -1) {
						EqTypeToken token = new EqTypeToken();
						aString = token.getToken(aString, aPC, this);
					}
					else {
						EqToken token = new EqToken();
						aString = token.getToken(aString, aPC, this);
					}
					var = var.substring(0, idx) + aString + var.substring(i + 1);
				}
			}
		}

		for (int idx = -1;;)
		{
			idx = var.indexOf("STRLEN[", idx + 1);

			if (idx < 0)
			{
				break;
			}

			final int i = var.indexOf(']', idx + 7);

			if (i >= 0)
			{
				String aString = var.substring(idx + 7, i);
				StringWriter sWriter = new StringWriter();
				BufferedWriter aWriter = new BufferedWriter(sWriter);
				replaceToken(aString, aWriter, aPC);
				sWriter.flush();

				try
				{
					aWriter.flush();
				}
				catch (IOException e)
				{
					//TODO: If this should be ignored, add a comment here describing why. XXX
				}

				aString = sWriter.toString();
				var = var.substring(0, idx) + aString.length() + var.substring(i + 1);
			}
		}

		return aPC.getVariableValue(var, "").intValue();
	}

	/**
	 * select suits - shields
	 * @param armor
	 * @param property
	 * @param equipped
	 * @param len
	 * @param output
	 * @param merge
	 * @param aPC
	 * @return int
	 */
	private int _replaceTokenArmor(int armor, String property, int equipped, int len, BufferedWriter output, int merge, PlayerCharacter aPC)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Armor", equipped, merge);
		final List bArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shield", equipped, merge);

		for (Iterator e = bArrayList.iterator(); e.hasNext();)
		{
			aArrayList.remove(e.next());
		}

		if ((armor >= (aArrayList.size() - 1)) && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}

		if (armor < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(armor);
			_writeArmorProperty(eq, property, output, aPC);
		}

		return len;
	}

	/**
	 * select items, which improve AC but are not type ARMOR
	 * @param item
	 * @param subtype
	 * @param property
	 * @param equipped
	 * @param len
	 * @param output
	 * @param merge
	 * @param aPC
	 * @return int
	 */
	private int _replaceTokenArmorItem(int item, String subtype, String property, int equipped, int len,
									   BufferedWriter output, int merge, PlayerCharacter aPC)
	{
		// select all pieces of equipment of status==equipped
		// filter all AC relevant stuff
		final List aArrayList = new ArrayList();

		for (Iterator e = aPC.getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();

			if (("".equals(subtype) || eq.isType(subtype))
				&& ((equipped == 3) || ((equipped == 2) && !eq.isEquipped()) || ((equipped == 1) && eq.isEquipped())))
			{
				if (eq.getBonusListString("AC") && !eq.isArmor() && !eq.isShield())
				{
					aArrayList.add(eq);
				}
			}
		}

		if (item >= aArrayList.size())
		{
			len = 0;
			noMoreItems = true;
		}

		if (item < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(item);
			_writeArmorProperty(eq, property, output, aPC);
		}

		return len;
	}

	/**
	 * select shields
	 * @param shield
	 * @param subtype
	 * @param property
	 * @param equipped
	 * @param len
	 * @param output
	 * @param merge
	 * @param aPC
	 * @return int
	 */
	private int _replaceTokenArmorShield(int shield, String subtype, String property, int equipped, int len,
										 BufferedWriter output, int merge, PlayerCharacter aPC)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shield", subtype, equipped, merge);

		if ((shield >= (aArrayList.size() - 1)) && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}

		if (shield < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(shield);
			_writeArmorProperty(eq, property, output, aPC);
		}

		return len;
	}

	/**
	 * select shirts
	 * @param shirt
	 * @param subtype
	 * @param property
	 * @param equipped
	 * @param len
	 * @param output
	 * @param merge
	 * @param aPC
	 * @return int
	 */
	private int _replaceTokenArmorShirt(int shirt, String subtype, String property, int equipped, int len,
										BufferedWriter output, int merge, PlayerCharacter aPC)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shirt", subtype, equipped, merge);

		if ((shirt >= (aArrayList.size() - 1)) && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}

		if (shirt < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(shirt);
			_writeArmorProperty(eq, property, output, aPC);
		}

		return len;
	}

	/**
	 * select suits
	 * @param suit
	 * @param subtype
	 * @param property
	 * @param equipped
	 * @param len
	 * @param output
	 * @param merge
	 * @param aPC
	 * @return int
	 */
	private int _replaceTokenArmorSuit(int suit, String subtype, String property, int equipped, int len,
									   BufferedWriter output, int merge, PlayerCharacter aPC)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Suit", subtype, equipped, merge);

		//
		// Temporary hack until someone gets around to fixing it properly
		//
		//aArrayList.addAll(aPC.getEquipmentOfTypeInOutputOrder("Shirt", subtype, equipped));
		if ((suit >= (aArrayList.size() - 1)) && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}

		if (suit < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(suit);
			_writeArmorProperty(eq, property, output, aPC);
		}

		return len;
	}

	/**
	 * select various stuff, that improves AC
	 * @param index
	 * @param type
	 * @param subtype
	 * @param property
	 * @param equipped
	 * @param len
	 * @param output
	 * @param merge
	 * @param aPC
	 * @return int
	 */
	private int _replaceTokenArmorVarious(int index, String type, String subtype, String property, int equipped,
										  int len, BufferedWriter output, int merge, PlayerCharacter aPC)
	{
		Equipment eq;
		final List aArrayList = new ArrayList();

		for (Iterator mapIter = aPC.getEquipmentOfTypeInOutputOrder(type, subtype, equipped, merge).iterator();
			mapIter.hasNext();)
		{
			eq = (Equipment) mapIter.next();

			if (eq.getACMod(aPC).intValue() > 0)
			{
				aArrayList.add(eq);
			}
			else if (eq.getBonusListString("AC"))
			{
				aArrayList.add(eq);
			}
		}

		if ((index >= (aArrayList.size() - 1)) && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}

		if (index < aArrayList.size())
		{
			eq = (Equipment) aArrayList.get(index);
			_writeArmorProperty(eq, property, output, aPC);
		}

		return len;
	}

	private void _writeArmorProperty(Equipment eq, String property, BufferedWriter output, PlayerCharacter aPC)
	{
		if (property.startsWith("NAME"))
		{
			if (eq.isEquipped())
			{
				FileAccess.write(output, "*");
			}

			//FileAccess.encodeWrite(output, eq.getName());
			FileAccess.encodeWrite(output, eq.parseOutputName(eq.getOutputName(), aPC));
			FileAccess.encodeWrite(output, eq.getAppliedName());
		}
		else if (property.startsWith("OUTPUTNAME"))
		{
			if (eq.isEquipped())
			{
				FileAccess.write(output, "*");
			}

			FileAccess.encodeWrite(output, eq.parseOutputName(eq.getOutputName(), aPC));
			FileAccess.encodeWrite(output, eq.getAppliedName());
		}
		else if (property.startsWith("TOTALAC"))
		{
			// adjustments for new equipment modifier
			// EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
			//FileAccess.write(output, Delta.toString(eq.getACMod()));
			FileAccess.write(output, Delta.toString((int) eq.bonusTo(aPC, "COMBAT", "AC", true)));
		}
		else if (property.startsWith("BASEAC"))
		{
			// adjustments for new equipment modifier
			// EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
			//FileAccess.write(output, Delta.toString(eq.getACMod()));
			FileAccess.write(output, Delta.toString((int) eq.bonusTo("COMBAT", "AC", aPC, aPC)));
		}
		else if (property.startsWith("ACBONUS"))
		{
			FileAccess.write(output, Delta.toString((int) eq.bonusTo(aPC, "COMBAT", "AC", true)));
		}
		else if (property.startsWith("MAXDEX"))
		{
			final int iMax = eq.getMaxDex(aPC).intValue();

			if (iMax != Constants.MAX_MAXDEX)
			{
				FileAccess.write(output, Delta.toString(iMax));
			}
		}
		else if (property.startsWith("ACCHECK"))
		{
			FileAccess.write(output, Delta.toString(eq.acCheck(aPC)));
		}
		else if (property.startsWith("EDR"))
		{
			FileAccess.write(output, Delta.toString(eq.eDR(aPC)));
		}
		else if (property.startsWith("ISTYPE"))
		{
			FileAccess.write(output, (eq.isType(property.substring(property.indexOf(".") + 1))) ? "TRUE" : "FALSE");
		}
		else if (property.startsWith("SPELLFAIL"))
		{
			FileAccess.write(output, eq.spellFailure(aPC).toString());
		}
		else if (property.startsWith("MOVE"))
		{
			final StringTokenizer aTok = new StringTokenizer(eq.moveString(), ",", false);
			String tempString = "";

			if (("M".equals(aPC.getSize()) || "S".equals(aPC.getSize())) && (aTok.countTokens() > 0))
			{
				tempString = aTok.nextToken();

				if ("S".equals(aPC.getSize()) && (aTok.countTokens() > 1))
				{
					tempString = aTok.nextToken();
				}
			}

			FileAccess.write(output, tempString);
		}
		else if (property.startsWith("SPROP"))
		{
			FileAccess.encodeWrite(output, eq.getSpecialProperties(aPC));
		}
		else if (property.startsWith("TYPE"))
		{
			String typeString = "";

			if (eq.isLight())
			{
				typeString = "Light";
			}
			else if (eq.isMedium())
			{
				typeString = "Medium";
			}
			else if (eq.isHeavy())
			{
				typeString = "Heavy";
			}
			else if (eq.isShield())
			{
				typeString = "Shield";
			}
			else if (eq.isExtra())
			{
				typeString = "Extra";
			}

			FileAccess.write(output, typeString);
		}
		else if (property.startsWith("WT"))
		{
			FileAccess.write(output, BigDecimalHelper.trimZeros(eq.getWeight(aPC).toString()));
		}
	}

	/**
	 * Add to the token map, called mainly by the plugin loader
	 * @param newToken
	 */
	public static void addToTokenMap(Token newToken)
	{
		Object test = tokenMap.put(newToken.getTokenName(), newToken);

		if (test != null)
		{
			Logging.errorPrint("More than one Output Token has the same Token Name: '" + newToken.getTokenName() + "'");
		}
	}

	private boolean evaluateExpression(String expr, PlayerCharacter aPC)
	{
		if (expr.indexOf(".AND.") > 0)
		{
			String part1 = expr.substring(0, expr.indexOf(".AND."));
			String part2 = expr.substring(expr.indexOf(".AND.") + 5);

			return (evaluateExpression(part1, aPC) && evaluateExpression(part2, aPC));
		}

		if (expr.indexOf(".OR.") > 0)
		{
			String part1 = expr.substring(0, expr.indexOf(".OR."));
			String part2 = expr.substring(expr.indexOf(".OR.") + 4);

			return (evaluateExpression(part1, aPC) || evaluateExpression(part2, aPC));
		}

		for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
		{
			Object anObject = ivar.next();

			if (anObject == null)
			{
				continue;
			}

			String fString = anObject.toString();
			String rString = loopVariables.get(fString).toString();
			expr = CoreUtility.replaceAll(expr, fString, rString);
		}

		if (expr.startsWith("HASVAR:"))
		{
			expr = expr.substring(7).trim();

			return (aPC.getVariableValue(expr, "").intValue() > 0);
		}

		if (expr.startsWith("HASFEAT:"))
		{
			expr = expr.substring(8).trim();

			return (aPC.getFeatNamed(expr) != null);
		}

		if (expr.startsWith("HASSA:"))
		{
			expr = expr.substring(6).trim();

			return (aPC.hasSpecialAbility(expr));
		}

		if (expr.startsWith("HASEQUIP:"))
		{
			expr = expr.substring(9).trim();

			return (aPC.getEquipmentNamed(expr) != null);
		}

		if (expr.startsWith("SPELLCASTER:"))
		{
			// Could look like one of the following:
			// Arcane
			// Chaos
			// Divine
			// EleMage
			// Psionic
			// Wizard
			// Prepare
			// !Prepare
			// 0=Wizard    (%classNum=className)
			// 0=Divine    (%classNum=spell_type)
			// 0=Prepare   (%classNum=preparation_type)
			final String fString = expr.substring(12).trim();

			if (fString.indexOf('=') >= 0)
			{
				final StringTokenizer aTok = new StringTokenizer(fString, "=", false);
				final int i = Integer.parseInt(aTok.nextToken());
				final String cs = aTok.nextToken();
				final List cList = aPC.getClassList();

				if (i >= cList.size())
				{
					return false;
				}

				final PCClass aClass = (PCClass) cList.get(i);

				if (cs.equalsIgnoreCase(aClass.getSpellType()))
				{
					return true;
				}

				if (cs.equalsIgnoreCase(aClass.getName()))
				{
					return true;
				}

				if (cs.equalsIgnoreCase(aClass.getCastAs()))
				{
					return true;
				}

				if ("!Prepare".equalsIgnoreCase(cs) && aClass.getMemorizeSpells())
				{
					return true;
				}

				if ("Prepare".equalsIgnoreCase(cs) && (!aClass.getMemorizeSpells()))
				{
					return true;
				}
			}
			else
			{
				for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass) e.next();

					if (fString.equalsIgnoreCase(aClass.getSpellType()))
					{
						return true;
					}

					if (fString.equalsIgnoreCase(aClass.getName()))
					{
						return true;
					}

					if (fString.equalsIgnoreCase(aClass.getCastAs()))
					{
						return true;
					}

					if ("!Prepare".equalsIgnoreCase(fString) && aClass.getMemorizeSpells())
					{
						return true;
					}

					if ("Prepare".equalsIgnoreCase(fString) && (!aClass.getMemorizeSpells()))
					{
						return true;
					}
				}
			}
		}

		if (expr.startsWith("EVEN:"))
		{
			int i = 0;

			try
			{
				i = Integer.parseInt(expr.substring(5).trim());
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("EVEN:" + i);

				return true;
			}

			return ((i % 2) == 0);
		}

		if (expr.endsWith("UNTRAINED"))
		{
			final StringTokenizer aTok = new StringTokenizer(expr, ".");
			final String fString = aTok.nextToken();
			Skill aSkill = null;

			if (fString.length() > 5)
			{
				final int i = Integer.parseInt(fString.substring(5));
				final List pcSkills = aPC.getSkillListInOutputOrder();

				if (i <= (pcSkills.size() - 1))
				{
					aSkill = (Skill) pcSkills.get(i);
				}
			}

			if (aSkill == null)
			{
				return false;
			}
			else if ((aSkill.getUntrained().length() > 0) && (aSkill.getUntrained().charAt(0) == 'Y'))
			{
				return true;
			}

			return false;
		}

		// Before returning false, let's see if this is a valid token, like this:
		//
		// |IIF(WEAPON%weap.CATEGORY:Ranged)|
		// something 1
		// |ELSE|
		// something 2
		// |END IF|
		// It can theorically be used with any valid token, doing an equal compare
		// (integer or string equalities are valid)
		StringTokenizer aTok = new StringTokenizer(expr, ":");
		final String token;
		final String equals;

		final int tokenCount = aTok.countTokens();
		if (tokenCount == 1)
		{
			token = expr;
			equals = "TRUE";
		}
		else if (tokenCount != 2)
		{
			Logging.errorPrint("evaluateExpression: Incorrect syntax (missing parameter)");

			return false;
		}
		else
		{
			token = aTok.nextToken();
			equals = aTok.nextToken().toUpperCase();
		}

		StringWriter sWriter = new StringWriter();
		BufferedWriter aWriter = new BufferedWriter(sWriter);
		replaceToken(token, aWriter, aPC);
		sWriter.flush();

		try
		{
			aWriter.flush();
		}
		catch (IOException ignore)
		{
			// Don't have anything to do in this case
		}

		String aString = sWriter.toString();
		if (token.startsWith("VAR."))
		{
			aString = token.substring(4);
			aString = aPC.getVariableValue(token.substring(4), "").toString();
		}

		try
		{
			// integer values
			final int i = Integer.parseInt(aString);

			return (i != Integer.parseInt(equals)) ? false : true;
		}
		catch (NumberFormatException e)
		{
			// String values
			return (aString.toUpperCase().indexOf(equals) < 0) ? false : true;
		}
	}

	private void evaluateIIF(IIFNode node, BufferedWriter output, FileAccess fa, PlayerCharacter aPC)
	{
		//
		// Comma is a delimiter for a higher-level parser, so we'll use a semicolon and replace it with a comma for
		// expressions like:
		// |IIF(VAR.IF(var("COUNT[SKILLTYPE=Strength]")>0;1;0):1)|
		//
		String aString = CoreUtility.replaceAll(node.expr(), ";", ",");
		if (evaluateExpression(aString, aPC))
		{
			evaluateIIFChildren(node.trueChildren(), output, fa, aPC);
		}
		else
		{
			evaluateIIFChildren(node.falseChildren(), output, fa, aPC);
		}
	}

	private void evaluateIIFChildren(final List children, BufferedWriter output, FileAccess fa, PlayerCharacter aPC)
	{
		for (int y = 0; y < children.size(); ++y)
		{
			if (children.get(y) instanceof FORNode)
			{
				FORNode nextFor = (FORNode) children.get(y);
				loopVariables.put(nextFor.var(), new Integer(0));
				existsOnly = nextFor.exists();

				String minString = nextFor.min();
				String maxString = nextFor.max();
				String stepString = nextFor.step();
				String fString;
				String rString;

				for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
				{
					Object anObject = ivar.next();

					if (anObject == null)
					{
						continue;
					}

					fString = anObject.toString();
					rString = loopVariables.get(fString).toString();
					minString = CoreUtility.replaceAll(minString, fString, rString);
					maxString = CoreUtility.replaceAll(maxString, fString, rString);
					stepString = CoreUtility.replaceAll(stepString, fString, rString);
				}

				loopFOR(nextFor, getVarValue(minString, aPC), getVarValue(maxString, aPC), getVarValue(stepString, aPC), output, fa, aPC);
				existsOnly = nextFor.exists();
				loopVariables.remove(nextFor.var());
			}
			else if (children.get(y) instanceof IIFNode)
			{
				evaluateIIF((IIFNode) children.get(y), output, fa, aPC);
			}
			else
			{
				String lineString = (String) children.get(y);

				for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
				{
					Object anObject = ivar.next();

					if (anObject == null)
					{
						continue;
					}

					String fString = anObject.toString();
					String rString = loopVariables.get(fString).toString();
					lineString = CoreUtility.replaceAll(lineString, fString, rString);
				}

				replaceLine(lineString, output, aPC);

				// output a newline if output is allowed
//				if (canWrite)
//				{
//					FileAccess.newLine(output);
//				}
			}
		}
	}

	private void loopFOR(FORNode node, int min, int max, int step, BufferedWriter output, FileAccess fa, PlayerCharacter aPC)
	{
		for (int x = min; x <= max; x += step)
		{
			loopVariables.put(node.var(), new Integer(x));

			for (int y = 0; y < node.children().size(); ++y)
			{
				if (node.children().get(y) instanceof FORNode)
				{
					FORNode nextFor = (FORNode) node.children().get(y);
					loopVariables.put(nextFor.var(), new Integer(0));
					existsOnly = nextFor.exists();

					String minString = nextFor.min();
					String maxString = nextFor.max();
					String stepString = nextFor.step();
					String fString;
					String rString;

					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();

						if (anObject == null)
						{
							continue;
						}

						fString = anObject.toString();
						rString = loopVariables.get(fString).toString();
						minString = CoreUtility.replaceAll(minString, fString, rString);
						maxString = CoreUtility.replaceAll(maxString, fString, rString);
						stepString = CoreUtility.replaceAll(stepString, fString, rString);
					}

					final int varMin = getVarValue(minString, aPC);
					final int varMax = getVarValue(maxString, aPC);
					final int varStep = getVarValue(stepString, aPC);
					loopFOR(nextFor, varMin, varMax, varStep, output, fa, aPC);
					existsOnly = node.exists();
					loopVariables.remove(nextFor.var());
				}
				else if (node.children().get(y) instanceof IIFNode)
				{
					evaluateIIF((IIFNode) node.children().get(y), output, fa, aPC);
				}
				else
				{
					String lineString = (String) node.children().get(y);

					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();

						if (anObject == null)
						{
							continue;
						}

						String fString = anObject.toString();
						String rString = loopVariables.get(fString).toString();
						lineString = CoreUtility.replaceAll(lineString, fString, rString);
					}

					noMoreItems = false;
					replaceLine(lineString, output, aPC);

					// Karianna - These three lines were removed because they were creating extra spaces
					// But have been put back in as the Text Export had no \n characters which
					// meant that the text export was all on one line - Bug 1370444
					if (canWrite)
					{
						FileAccess.newLine(output);
					}

					// break out of loop if no more items
					if (existsOnly && noMoreItems)
					{
						x = max + 1;
					}
				}
			}
		}
	}

	/**
	 * Math Mode - Most of the code logic was copied from PlayerCharacter.getVariableValue
	 * included a treatment for math with attack routines (for example +6/+1 - 2 = +4/-1)
	 * 
	 * @param aString
	 * @param aPC
	 * @return String
	 */
	private String mathMode(String aString, PlayerCharacter aPC)
	{
		Float total = new Float(0.0);

		while (aString.lastIndexOf('(') >= 0)
		{
			int x = CoreUtility.innerMostStringStart(aString);
			int y = CoreUtility.innerMostStringEnd(aString);

			if (y < x)
			{
				// This was breaking some homebrew sheets. [Felipe - 13-may-03]
				//Logging.errorPrint("Missing closing parenthesis: " + aString);
				//return total.toString();
				break;
			}

			String bString = aString.substring(x + 1, y);

			// This will treat Knowledge (xx) kind of token
			if ((x > 0) && (aString.charAt(x - 1) == ' ')
				&& ((aString.charAt(y + 1) == '.') || (y == (aString.length() - 1))))
			{
				aString = aString.substring(0, x) + "[" + bString + "]" + aString.substring(y + 1);
			}
			else
			{
				aString = aString.substring(0, x) + mathMode(bString, aPC) + aString.substring(y + 1);
			}
		}

		aString = CoreUtility.replaceAll(aString, "[", "(");
		aString = CoreUtility.replaceAll(aString, "]", ")");

		final String delimiter = "+-/*";
		String valString = "";
		final int ADDITION_MODE = 0;
		final int SUBTRACTION_MODE = 1;
		final int MULTIPLICATION_MODE = 2;
		final int DIVISION_MODE = 3;
		int mode = ADDITION_MODE;
		int nextMode = 0;
		final int REGULAR_MODE = 0;
		final int INTVAL_MODE = 1;
		final int SIGN_MODE = 2;
		final int NO_ZERO_MODE = 3;
		int endMode = REGULAR_MODE;
		boolean attackRoutine = false;
		String attackData = "";

		for (int i = 0; i < aString.length(); ++i)
		{
			valString += aString.substring(i, i + 1);

			if ((i == (aString.length() - 1))
				|| ((delimiter.lastIndexOf(aString.charAt(i)) > -1) && (i > 0) && (aString.charAt(i - 1) != '.')))
			{
				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}

				if (i < aString.length())
				{
					if (valString.endsWith(".TRUNC"))
					{
						if (attackRoutine)
						{
							Logging.errorPrint("Math Mode Error: Using .TRUNC in Attack Mode.");
						}
						else
						{
							valString = String.valueOf(Float.valueOf(mathMode(valString.substring(0,
												valString.length() - 6), aPC)).intValue());
						}
					}

					if (valString.endsWith(".INTVAL"))
					{
						if (attackRoutine)
						{
							Logging.errorPrint("Math Mode Error: Using .INTVAL in Attack Mode.");
						}
						else
						{
							valString = mathMode(valString.substring(0, valString.length() - 7), aPC);
						}

						endMode = INTVAL_MODE;
					}

					if (valString.endsWith(".SIGN"))
					{
						valString = mathMode(valString.substring(0, valString.length() - 5), aPC);
						endMode = SIGN_MODE;
					}

					if (valString.endsWith(".NOZERO"))
					{
						valString = mathMode(valString.substring(0, valString.length() - 7), aPC);
						endMode = NO_ZERO_MODE;
					}

					if ((aString.length() > 0) && (aString.charAt(i) == '+'))
					{
						nextMode = ADDITION_MODE;
					}
					else if ((aString.length() > 0) && (aString.charAt(i) == '-'))
					{
						nextMode = SUBTRACTION_MODE;
					}
					else if ((aString.length() > 0) && (aString.charAt(i) == '*'))
					{
						nextMode = MULTIPLICATION_MODE;
					}
					else if ((aString.length() > 0) && (aString.charAt(i) == '/'))
					{
						nextMode = DIVISION_MODE;
					}

					StringWriter sWriter = new StringWriter();
					BufferedWriter aWriter = new BufferedWriter(sWriter);
					replaceTokenSkipMath(aPC, valString, aWriter);
					sWriter.flush();

					try
					{
						aWriter.flush();
					}
					catch (IOException e)
					{
						//TODO: Really ignore this? If so, explain why in a comment here. XXX
					}

					final String bString = sWriter.toString();

					try
					{
						// Float values
						valString = String.valueOf(Float.parseFloat(bString));
					}
					catch (NumberFormatException e)
					{
						// String values
						valString = bString;
					}

					if ((!attackRoutine) && isAttackRoutine(valString))
					{
						attackRoutine = true;
						attackData = valString;
						valString = "";
					}
				}

				try
				{
					if (valString.length() > 0)
					{
						if (attackRoutine)
						{
							StringTokenizer bTok = new StringTokenizer(attackData, "/");
							String newAttackData = "";

							if (bTok.countTokens() > 0)
							{
								while (bTok.hasMoreTokens())
								{
									final String bString = bTok.nextToken();

									switch (mode)
									{
										case ADDITION_MODE:
											newAttackData += ("/+"
											+ Integer.toString(new Float(Float.parseFloat(bString)
													+ Float.parseFloat(valString)).intValue()));

											break;

										case SUBTRACTION_MODE:
											newAttackData += ("/+"
											+ Integer.toString(new Float(Float.parseFloat(bString)
													- Float.parseFloat(valString)).intValue()));

											break;

										case MULTIPLICATION_MODE:
											newAttackData += ("/+"
											+ Integer.toString(new Float(
													Float.parseFloat(bString) * Float.parseFloat(valString)).intValue()));

											break;

										case DIVISION_MODE:
											newAttackData += ("/+"
											+ Integer.toString(new Float(
													Float.parseFloat(bString) / Float.parseFloat(valString)).intValue()));

											break;

										default:
											Logging.errorPrint("In mathMode the mode " + mode + " is unsupported.");

											break;
									}
								}

								attackData = CoreUtility.replaceAll(newAttackData.substring(1), "+-", "-");
							}
						}
						else
						{
							switch (mode)
							{
								case ADDITION_MODE:
									total = new Float(total.doubleValue() + Double.parseDouble(valString));

									break;

								case SUBTRACTION_MODE:
									total = new Float(total.doubleValue() - Double.parseDouble(valString));

									break;

								case MULTIPLICATION_MODE:
									total = new Float(total.doubleValue() * Double.parseDouble(valString));

									break;

								case DIVISION_MODE:
									total = new Float(total.doubleValue() / Double.parseDouble(valString));

									break;

								default:
									Logging.errorPrint("In mathMode the mode " + mode + " is unsupported.");

									break;
							}
						}
					}
				}
				catch (NumberFormatException exc)
				{
					//NEVER call agui here, this is the wrong layer, do it in the gui
					//GuiFacade.showMessageDialog(null, "Math error determining value for " + aString + " " + attackData + "(" + valString + ")", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					StringWriter sWriter = new StringWriter();
					BufferedWriter aWriter = new BufferedWriter(sWriter);
					replaceTokenSkipMath(aPC, aString, aWriter);
					sWriter.flush();

					try
					{
						aWriter.flush();
					}
					catch (IOException e)
					{
						//TODO: Really ignore this? If so, explain why in a comment here. XXX
					}

					return sWriter.toString();
				}

				mode = nextMode;
				nextMode = ADDITION_MODE;
				valString = "";
			}
		}

		if (attackRoutine)
		{
			return attackData;
		}
		if (endMode == INTVAL_MODE)
		{
			return Integer.toString(total.intValue());
		}

		if (endMode == SIGN_MODE)
		{
			final Integer valInt = new Integer(total.intValue());
			return Delta.toString(valInt);
		}

		if (endMode == NO_ZERO_MODE)
		{
			final Integer valInt = new Integer(total.intValue());
			if (valInt.intValue() == 0)
			{
				return "";
			}

			return Delta.toString(valInt);
		}

		return total.toString();
	}

	private void outputNonToken(String aString, BufferedWriter output)
	{
		//If something shouldn't be output, return.
		if (!canWrite)
		{
			return;
		}

		if (aString.length() > 0)
		{
			FileAccess.write(output, aString);
		}
	}

	private FORNode parseFORs(StringTokenizer tokens)
	{
		final FORNode root = new FORNode(null, "0", "0", "1", true);
		String line;

		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();

			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");

				if (newFor.countTokens() > 1)
				{
					newFor.nextToken();

					if (newFor.nextToken().startsWith("%"))
					{
						root.addChild(parseFORs(line, tokens));
					}
					else
					{
						root.addChild(line);
					}
				}
				else
				{
					root.addChild(line);
				}
			}
			else if (line.startsWith("|IIF(") && (line.lastIndexOf(',') < 0))
			{
				String expr = line.substring(5, line.lastIndexOf(')'));
				root.addChild(parseIIFs(expr, tokens));
			}
			else
			{
				root.addChild(line);
			}
		}

		return root;
	}

	private FORNode parseFORs(String forLine, StringTokenizer tokens)
	{
		final StringTokenizer forVars = new StringTokenizer(forLine, ",");
		forVars.nextToken();

		final String var = forVars.nextToken();
		final String min = forVars.nextToken();
		final String max = forVars.nextToken();
		final String step = forVars.nextToken();
		final String eTest = forVars.nextToken();
		boolean exists = false;

		if (((eTest.length() > 0) && (eTest.charAt(0) == '1')) || ((eTest.length() > 0) && (eTest.charAt(0) == '2')))
		{
			exists = true;
		}

		final FORNode node = new FORNode(var, min, max, step, exists);
		String line;

		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();

			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				newFor.nextToken();

				if (newFor.nextToken().startsWith("%"))
				{
					node.addChild(parseFORs(line, tokens));
				}
				else
				{
					node.addChild(line);
				}
			}
			else if (line.startsWith("|IIF(") && (line.lastIndexOf(',') < 0))
			{
				String expr = line.substring(5, line.lastIndexOf(')'));
				node.addChild(parseIIFs(expr, tokens));
			}
			else if (line.startsWith("|ENDFOR|"))
			{
				return node;
			}
			else
			{
				node.addChild(line);
			}
		}

		return node;
	}

	private IIFNode parseIIFs(String expr, StringTokenizer tokens)
	{
		final IIFNode node = new IIFNode(expr);
		String line;
		boolean childrenType = true;

		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();

			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				newFor.nextToken();

				if (newFor.nextToken().startsWith("%"))
				{
					if (childrenType)
					{
						node.addTrueChild(parseFORs(line, tokens));
					}
					else
					{
						node.addFalseChild(parseFORs(line, tokens));
					}
				}
				else
				{
					if (childrenType)
					{
						node.addTrueChild(line);
					}
					else
					{
						node.addFalseChild(line);
					}
				}
			}
			else if (line.startsWith("|IIF(") && (line.lastIndexOf(',') < 0))
			{
				String newExpr = line.substring(5, line.lastIndexOf(')'));

				if (childrenType)
				{
					node.addTrueChild(parseIIFs(newExpr, tokens));
				}
				else
				{
					node.addFalseChild(parseIIFs(newExpr, tokens));
				}
			}
			else if (line.startsWith("|ELSE|"))
			{
				childrenType = false;
			}
			else if (line.startsWith("|ENDIF|"))
			{
				return node;
			}
			else
			{
				if (childrenType)
				{
					node.addTrueChild(line);
				}
				else
				{
					node.addFalseChild(line);
				}
			}
		}

		return node;
	}

	private static void populateTokenMap()
	{
		if(!tokenMapPopulated) {
			addToTokenMap(new ACCheckToken());
			addToTokenMap(new AlignmentToken());
			addToTokenMap(new AttackToken());
			addToTokenMap(new BonusToken());
			addToTokenMap(new CheckToken());
			addToTokenMap(new DomainToken());
			addToTokenMap(new DRToken());
			addToTokenMap(new EqToken());
			addToTokenMap(new FeatToken());
			addToTokenMap(new FeatListToken());
			addToTokenMap(new GameModeToken());
			addToTokenMap(new HeightToken());
			addToTokenMap(new HPToken());
			addToTokenMap(new InitiativeMiscToken());
			addToTokenMap(new MovementToken());
			addToTokenMap(new ReachToken());
			addToTokenMap(new SizeLongToken());
			addToTokenMap(new SkillToken());
			addToTokenMap(new SkillpointsToken());
			addToTokenMap(new SpellFailureToken());
			addToTokenMap(new SRToken());
			addToTokenMap(new StatToken());
			addToTokenMap(new TotalToken());
			addToTokenMap(new VarToken());
			addToTokenMap(new WeaponToken());
			addToTokenMap(new WeaponhToken());
			addToTokenMap(new WeightToken());
			tokenMapPopulated = true;
		}
	}

	/*
	 * ####################################################################
	 * Various helper methods
	 * ####################################################################
	 */
	private void replaceLine(String aLine, BufferedWriter output, PlayerCharacter aPC)
	{
		boolean inPipe = false;
		boolean flag;
		StringBuffer tokString = new StringBuffer("");

		if (!inPipe && (aLine.lastIndexOf('|') < 0))
		{
			if (aLine.length() > 0)
			{
				outputNonToken(aLine, output);
			}
		}
		else if ((inPipe && (aLine.lastIndexOf('|') < 0)) || (!inPipe && (aLine.lastIndexOf('|') == 0)))
		{
			tokString.append(aLine.substring(aLine.lastIndexOf('|') + 1));
			inPipe = true;
		}
		else
		{
			if (!inPipe && (aLine.charAt(0) == '|'))
			{
				inPipe = true;
			}

			final StringTokenizer bTok = new StringTokenizer(aLine, "|", false);
			flag = bTok.countTokens() == 1;

			while (bTok.hasMoreTokens())
			{
				String bString = bTok.nextToken();

				if (!inPipe)
				{
					outputNonToken(bString, output);
				}
				else
				{
					if (bTok.hasMoreTokens() || flag
						|| (inPipe && !bTok.hasMoreTokens() && (aLine.charAt(aLine.length() - 1) == '|')))
					{
						replaceToken(tokString.toString() + bString, output, aPC);
						tokString = new StringBuffer("");
					}
					else
					{
						tokString.append(bString);
					}
				}

				if (bTok.hasMoreTokens() || flag)
				{
					inPipe = !inPipe;
				}
			}

			if (inPipe && (aLine.charAt(aLine.length() - 1) == '|'))
			{
				inPipe = false;
			}
		}
	}

	/**
	 * Replace the token with the value it represents
	 * @param aString
	 * @param output
	 * @param aPC
	 * @return value
	 */
	public int replaceToken(String aString, BufferedWriter output, PlayerCharacter aPC)
	{
		try
		{
			int len = 1;

			if (!canWrite && (aString.length() > 0) && (aString.charAt(0) != '%'))
			{
				return 0;
			}

			if ("%".equals(aString))
			{
				inLabel = false;
				canWrite = true;

				return 0;
			}

			FileAccess.maxLength(-1);

			//
			// Start the |%blah| token section
			//
			if ((aString.length() > 0) && (aString.charAt(0) == '%') && (aString.length() > 1)
				&& (aString.lastIndexOf('<') < 0) && (aString.lastIndexOf('>') < 0))
			{
				boolean found = false;
				canWrite = true;

				// check to see how we are merging equipment
				int merge = Constants.MERGE_ALL;

				if (aString.indexOf("MERGENONE") > 0)
				{
					merge = Constants.MERGE_NONE;
				}

				if (aString.indexOf("MERGELOC") > 0)
				{
					merge = Constants.MERGE_LOCATION;
				}

				if (aString.substring(1).startsWith("GAMEMODE:"))
				{
					if (aString.substring(10).endsWith(GameModeToken.getGameModeToken()))
					{
						canWrite = false;
					}

					return 0;
				}

				if ("REGION".equals(aString.substring(1)))
				{
					if (aPC.getRegion().equals(Constants.s_NONE))
					{
						canWrite = false;
					}

					return 0;
				}

				if ("NOTES".equals(aString.substring(1)))
				{
					if (aPC.getNotesList().size() <= 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("SKILLPOINTS".equals(aString.substring(1)))
				{
					if (SkillpointsToken.getUnusedSkillPoints(aPC) == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if (aString.substring(1).startsWith("TEMPLATE"))
				{
					// New token syntax |%TEMPLATE.x| instead of |%TEMPLATEx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					final List tList = aPC.getTemplateList();
					String fString = aTok.nextToken();
					final int index;

					if (aTok.hasMoreTokens())
					{
						index = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						// When removing old syntax, remove the else and leave the if
						if ("TEMPLATE".equals(fString))
						{
							if (tList.isEmpty())
							{
								canWrite = false;
							}

							return 0;
						}
						Logging.errorPrint("Old syntax %TEMPLATEx will be replaced for %TEMPLATE.x");
						index = Integer.parseInt(aString.substring(9));
					}

					if (index >= tList.size())
					{
						canWrite = false;

						return 0;
					}

					if ((((PCTemplate) tList.get(index)).isVisible() != 1)
						&& (((PCTemplate) tList.get(index)).isVisible() != 2))
					{
						canWrite = false;
					}

					return 0;
				}

				if ("FOLLOWER".equals(aString.substring(1)))
				{
					if (aPC.getFollowerList().isEmpty())
					{
						canWrite = false;
					}

					return 0;
				}

				if ("FOLLOWEROF".equals(aString.substring(1)))
				{
					if (aPC.getMasterPC() == null)
					{
						canWrite = false;
					}

					return 0;
				}

				if (aString.substring(1).startsWith("FOLLOWERTYPE."))
				{
					List aList = new ArrayList();

					for (Iterator iter = aPC.getFollowerList().iterator(); iter.hasNext();)
					{
						Follower aFollower = (Follower) iter.next();

						// only allow followers that
						// are currently loaded
						// Otherwise the stats a zero
						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter lPC = (PlayerCharacter) p.next();

							if (lPC.getFileName().equals(aFollower.getFileName()))
							{
								aList.add(aFollower);
							}
						}
					}

					StringTokenizer aTok = new StringTokenizer(aString, ".");
					aTok.nextToken(); // FOLLOWERTYPE

					String typeString = aTok.nextToken();

					for (int i = aList.size() - 1; i >= 0; --i)
					{
						final Follower fol = (Follower) aList.get(i);

						if (!fol.getType().equalsIgnoreCase(typeString))
						{
							aList.remove(i);
						}
					}

					if (aList.isEmpty())
					{
						canWrite = false;
					}

					return 0;
				}

				if ("PROHIBITEDLIST".equals(aString.substring(1)))
				{
					for (Iterator iter = aPC.getClassList().iterator(); iter.hasNext();)
					{
						PCClass aClass = (PCClass) iter.next();

						if (aClass.getLevel() > 0)
						{
							if (!aClass.getProhibitedString().equals(Constants.s_NONE))
							{
								return 0;
							}
						}
					}

					canWrite = false;

					return 0;
				}

				if ("CATCHPHRASE".equals(aString.substring(1)))
				{
					if (aPC.getCatchPhrase().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getCatchPhrase()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("LOCATION".equals(aString.substring(1)))
				{
					if (aPC.getLocation().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getLocation()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("RESIDENCE".equals(aString.substring(1)))
				{
					if (aPC.getResidence().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getResidence()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("PHOBIAS".equals(aString.substring(1)))
				{
					if (aPC.getPhobias().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getPhobias()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("INTERESTS".equals(aString.substring(1)))
				{
					if (aPC.getInterests().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getInterests()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("SPEECHTENDENCY".equals(aString.substring(1)))
				{
					if (aPC.getSpeechTendency().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getSpeechTendency()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("PERSONALITY1".equals(aString.substring(1)))
				{
					if (aPC.getTrait1().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getTrait1()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("PERSONALITY2".equals(aString.substring(1)))
				{
					if (aPC.getTrait2().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getTrait2()).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("MISC.FUNDS".equals(aString.substring(1)))
				{
					if (aPC.getMiscList().get(0).equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (((String) aPC.getMiscList().get(0)).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("COMPANIONS".equals(aString.substring(1)) || "MISC.COMPANIONS".equals(aString.substring(1)))
				{
					if (aPC.getMiscList().get(1).equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (((String) aPC.getMiscList().get(1)).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("MISC.MAGIC".equals(aString.substring(1)))
				{
					if (aPC.getMiscList().get(2).equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (((String) aPC.getMiscList().get(2)).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("DESC".equals(aString.substring(1)))
				{
					if (aPC.getDescription().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (aPC.getDescription().trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("BIO".equals(aString.substring(1)))
				{
					if (aPC.getBio().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (aPC.getBio().trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("SUBREGION".equals(aString.substring(1)))
				{
					if (aPC.getSubRegion().equals(Constants.s_NONE))
					{
						canWrite = false;
					}

					return 0;
				}

				if (aString.substring(1).startsWith("TEMPBONUS."))
				{
					StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					int index = -1;
					aTok.nextToken(); // discard first one

					if (aTok.hasMoreTokens())
					{
						index = Integer.parseInt(aTok.nextToken());
					}

					if (index > aPC.getNamedTempBonusList().size())
					{
						canWrite = false;

						return 0;
					}

					if (aPC.getUseTempMods())
					{
						canWrite = true;

						return 1;
					}
				}

				if (aString.substring(1).startsWith("ARMOR.ITEM"))
				{
					// New token syntax |%ARMOR.ITEM.x| instead of |%ARMOR.ITEMx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					aTok.nextToken(); // ARMOR

					String fString = aTok.nextToken();
					final int count;
					final List aArrayList = new ArrayList();

					for (Iterator e = aPC.getEquipmentListInOutputOrder().iterator(); e.hasNext();)
					{
						Equipment eq = (Equipment) e.next();

						if (eq.getBonusListString("AC") && (!eq.isArmor() && !eq.isShield()))
						{
							aArrayList.add(eq);
						}
					}

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %ARMOR.ITEMx will be replaced for %ARMOR.ITEM.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}

					if (count > aArrayList.size())
					{
						canWrite = false;
					}

					return 0;
				}

				if (aString.substring(1).startsWith("ARMOR.SHIELD"))
				{
					// New token syntax |%ARMOR.SHIELD.x| instead of |%ARMOR.SHIELDx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					aTok.nextToken(); // ARMOR

					String fString = aTok.nextToken();
					final int count;
					final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("SHIELD", 3);

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %ARMOR.SHIELDx will be replaced for %ARMOR.SHIELD.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}

					if (count > aArrayList.size())
					{
						canWrite = false;
					}

					return 0;
				}

				if (aString.substring(1).startsWith("ARMOR"))
				{
					// New token syntax |%ARMOR.x| instead of |%ARMORx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					final int count;
					List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("ARMOR", 3);

					//Get list of shields.  Remove any from list of armor
					//Since shields are included in the armor list they will appear twice and they really shouldn't be in the list of armor
					List shieldList = aPC.getEquipmentOfTypeInOutputOrder("SHIELD", 3);
					for (int z = 0; z < shieldList.size(); z++)
					{
						aArrayList.remove(shieldList.get(z));
					}

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %ARMORx will be replaced for %ARMOR.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}

					if (count > aArrayList.size())
					{
						canWrite = false;
					}

					return 0;
				}

				if ("WEAPONPROF".equals(aString.substring(1)))
				{
					if (!SettingsHandler.getWeaponProfPrintout())
					{
						canWrite = false;
					}

					return 0;
				}

				if (aString.substring(1).startsWith("WEAPON"))
				{
					// New token syntax |%WEAPON.x| instead of |%WEAPONx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					int count = 0;
					final List aArrayList = aPC.getExpandedWeapons(merge);

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %WEAPONx will be replaced for %WEAPON.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}

					if (count >= aArrayList.size())
					{
						canWrite = false;
					}

					return 0;
				}

				if (aString.substring(1).startsWith("DOMAIN"))
				{
					// New token syntax |%DOMAIN.x| instead of |%DOMAINx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					final int index;

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						index = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %DOMAINx will be replaced for %DOMAIN.x");

						index = Integer.parseInt(fString.substring(6));
					}

					canWrite = (index <= aPC.getCharacterDomainList().size());

					return 0;
				}

				if (aString.substring(1).startsWith("SPELLLISTBOOK"))
				{
					if (SettingsHandler.getPrintSpellsWithPC())
					{
						// New token syntax |%SPELLLISTBOOK.x| instead of |%SPELLLISTBOOKx|
						// To remove old syntax, keep the if and remove the else
						if (aString.charAt(14) == '.')
						{
							aString = aString.substring(15);
						}
						else
						{
							aString = aString.substring(14);
						}

						return replaceTokenSpellListBook(aString, aPC);
					}
					canWrite = false;

					return 0;
				}

				if (aString.substring(1).startsWith("VAR."))
				{
					replaceTokenVar(aString, aPC);
					return 0;
				}

				if (aString.substring(1).startsWith("COUNT["))
				{
					if (getVarValue(aString.substring(1), aPC) > 0)
					{
						canWrite = true;

						return 1;
					}

					canWrite = false;

					return 0;
				}

				// finaly, check for classes
				final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ",", false);

				while (aTok.hasMoreTokens())
				{
					String cString = aTok.nextToken();
					StringTokenizer bTok = new StringTokenizer(cString, "=", false);
					String bString = bTok.nextToken();
					int i = 0;

					if (bTok.hasMoreTokens())
					{
						i = Integer.parseInt(bTok.nextToken());
					}

					PCClass aClass = aPC.getClassNamed(bString);
					PCClass bClass = Globals.getClassNamed(bString);
					found = bClass != null;

					if ((bClass != null) && (aClass != null))
					{
						canWrite = (aClass.getLevel() >= i);
					}
					else if ((bClass != null) && (aClass == null))
					{
						canWrite = false;
					}
					else if (bString.startsWith("SPELLLISTCLASS"))
					{
						// New token syntax |%SPELLLISTCLASS.x| instead of |%SPELLLISTCLASSx|
						// To remove old syntax, keep the if and remove the else
						if (bString.charAt(14) == '.')
						{
							bString = bString.substring(15);
						}
						else
						{
							bString = bString.substring(14);
						}

						found = true;

						PObject aObject = aPC.getSpellClassAtIndex(Integer.parseInt(bString));
						canWrite = (aObject != null);
					}
				}

				if (found)
				{
					inLabel = true;

					return 0;
				}
				canWrite = false;
				inLabel = true;

				return 0;
			}

			// done with |%blah| tokens
			// now check for max length tokens
			// eg: |SUB10.ARMOR.AC|
			if ((aString.indexOf("SUB") == 0) && (aString.indexOf(".") > 3))
			{
				int iEnd = aString.indexOf(".");
				int maxLength = -1;

				try
				{
					maxLength = Integer.parseInt(aString.substring(3, iEnd));
				}
				catch (NumberFormatException ex)
				{
					// Hmm, no number?
					Logging.errorPrint("Number format error: " + aString);
					maxLength = -1;
				}

				if (maxLength > 0)
				{
					aString = aString.substring(iEnd + 1);
					FileAccess.maxLength(maxLength);
				}
			}

			//
			// now check for the rest of the tokens
			//
			populateTokenMap();

			// Correct old format tags such as SPELLLIST
			// so that they get processed correctly
			aString = correctOldFormatTag(aString);

			StringTokenizer tok = new StringTokenizer(aString, ".,", false);
			String firstToken = tok.nextToken();

			String testString = aString;
			if (testString.indexOf(',') > -1)
			{
				testString = testString.substring(0, testString.indexOf(','));
			}
			if(testString.indexOf('~') > -1)
			{
				testString = testString.substring(0, testString.indexOf('~'));
			}

			//Leave
			if (aString.startsWith("FOR.") || aString.startsWith("DFOR."))
			{
				FileAccess.maxLength(-1);

				existsOnly = false;
				noMoreItems = false;
				checkBefore = false;

				//skipMath = true;
				replaceTokenForDfor(aString, output, aPC);

				//skipMath = false;
				existsOnly = false;
				noMoreItems = false;

				return 0;
			}

			//Leave
			else if (aString.startsWith("OIF("))
			{
				replaceTokenIIF(aString, output, aPC);
			}

			//Leave
			else if (((testString.indexOf('(') >= 0) || (testString.indexOf('+') >= 0) || (testString.indexOf('-') >= 0)
				|| (testString.indexOf(".INTVAL") >= 0) || (testString.indexOf(".SIGN") >= 0)
				|| (testString.indexOf(".NOZERO") >= 0) || (testString.indexOf(".TRUNC") >= 0) || (testString.indexOf('*') >= 0)
				|| (testString.indexOf('/') >= 0)) && (!skipMath))
			{
				FileAccess.maxLength(-1);
				FileAccess.write(output, mathMode(aString, aPC));

				return 0;
			}

			//Leave
			else if (aString.startsWith("CSHEETTAG2."))
			{
				csheetTag2 = aString.substring(11, 12);
				FileAccess.maxLength(-1);

				return 0;
			}

			//Leave
			else if (tokenMap.get(firstToken) != null)
			{
				Token token = (Token) tokenMap.get(firstToken);
				if(token.isEncoded())
				{
					FileAccess.encodeWrite(output, token.getToken(aString, aPC, this));
				}
				else
				{
					FileAccess.write(output, token.getToken(aString, aPC, this));
				}
			}

			//TODO:DJ: Create pcgen.io.exportTokens.FollowerToken
			else if (aString.startsWith("FOLLOWER"))
			{
				replaceTokenFollowers(aString, output, aPC);
			}

			//TODO:DJ: Create pcgen.io.exportTokens.ArmorToken
			//
			// Not an ARMOR token unless ARMOR. or ARMOR#. ARMORQUI is most likely a variable name
			//
			else if (aString.startsWith("ARMOR") && ((aString.charAt(5) == '.') || Character.isDigit(aString.charAt(5))))
			{
				len = replaceTokenArmor(aString, len, output, aPC);
			}
			else
			{
				len = aString.trim().length();

				if (aString.length() > 0)
				{
					FileAccess.write(output, aString);
				}
			}

			FileAccess.maxLength(-1);

			return len;
		}
		catch (Exception exc)
		{
			Logging.errorPrint("Error replacing " + aString, exc);
			return 0;
		}
	}

	/**
	 * Take an old format tag, one without a 'full stop' separating the token from
	 * the first value and put it into a format that can be used with the
	 * export tokens
	 * 
	 * @param aString The tag to be checked
	 * @return The reformatted tag, if needed or the original tag if it was OK
	 */
	private String correctOldFormatTag(String aString)
	{
		StringBuffer converted = new StringBuffer();

		if (aString.startsWith("SPELLIST"))
		{
			final StringTokenizer aTok = new StringTokenizer(aString, ".");
			String fString = aTok.nextToken();

			if ((fString.charAt(fString.length() - 1) >= '0')
				&& (fString.charAt(fString.length() - 1) <= '9'))
			{
				if (aString.regionMatches(9, "TYPE", 0, 4) ||
						aString.regionMatches(9, "BOOK", 0, 4) ||
								aString.regionMatches(9, "CAST", 0, 4))
				{
					converted.append(aString.substring(0, 14));
					converted.append('.');
					converted.append(aString.substring(14));
				}
				else if (aString.regionMatches(9, "KNOWN", 0, 5)
					|| aString.regionMatches(9, "CLASS", 0, 5))
				{
					converted.append(aString.substring(0, 15));
					converted.append('.');
					converted.append(aString.substring(15));
				}
				else if (aString.regionMatches(9, "DCSTAT", 0, 6))
					{
						converted.append(aString.substring(0, 16));
						converted.append('.');
						converted.append(aString.substring(16));
					}
				else if (aString.regionMatches(9, "DC", 0, 2))
				{
					converted.append(aString.substring(0, 12));
					converted.append('.');
					converted.append(aString.substring(12));
				}
			}
		}
		else if (aString.startsWith("SPELLMEM"))
		{
			if (aString.length() > 8 && (aString.charAt(8) != '.'))
			{
				converted.append(aString.substring(0, 8));
				converted.append('.');
				converted.append(aString.substring(8));
			}
		}

		else if (aString.startsWith("SKILLSUBSET"))
		{
			if (aString.length() > 11 && (aString.charAt(11) != '.'))
			{
				converted.append(aString.substring(0, 11));
				converted.append('.');
				converted.append(aString.substring(11));
			}
		}
		else if (aString.startsWith("SKILLTYPE"))
		{
			if ((aString.length() > 9) && (aString.charAt(9) != '.') && (aString.charAt(9) != '='))
			{
				converted.append(aString.substring(0, 9));
				converted.append('.');
				converted.append(aString.substring(9));
			}
		}
		else if (aString.startsWith("SKILL")
			&& !aString.startsWith("SKILLLEVEL")
			&& !aString.startsWith("SKILLLISTMODS")
			&& !aString.startsWith("SKILLPOINTS")
			&& !aString.startsWith("SKILLSUBSET")
			&& !aString.startsWith("SKILLTYPE"))
		{
			if ((aString.length() > 5) && (aString.charAt(5) != '.') && (aString.charAt(5) != '('))
			{
				converted.append(aString.substring(0, 5));
				converted.append('.');
				converted.append(aString.substring(5));
			}
		}


		if (converted.length() > 0)
		{
			Logging.errorPrint("Old syntax '" + aString + "' replaced with '"
				+ converted.toString() + "'.");
			return converted.toString();
		}
		return aString;
	}

	/**
	 * See the PCGen Docs on Token Syntax
	 * 
	 * @param aString
	 * @param len
	 * @param output
	 * @param aPC
	 * @return int
	 */
	private int replaceTokenArmor(String aString, int len, BufferedWriter output, PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final String[] tokens = new String[aTok.countTokens()];

		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();
		}

		String property = "";

		// When removing old syntax, this if should be removed
		if (tokens.length > 0)
		{
			property = tokens[tokens.length - 1];
		}

		int equipped = 3;
		int index = 0;
		String type = "";
		String subtype = "";
		int merge = Constants.MERGE_ALL;

		for (int i = 0; i < tokens.length; ++i)
		{
			if ("ARMOR".equals(tokens[i]))
			{
				continue;
			}

			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("ARMOR"))
			{
				try
				{
					index = Integer.parseInt(tokens[i].substring(5));
				}
				catch (NumberFormatException nfe)
				{
					index = 0;
				}

				Logging.errorPrint("Old syntax ARMORx will be replaced for ARMOR.x");
			}
			else if ("ALL".equals(tokens[i]))
			{
				equipped = 3;
			}

			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("ALL"))
			{
				Logging.errorPrint("Old syntax ALLx will be replaced for ALL.x");

				index = Integer.parseInt(tokens[i].substring(3));
				equipped = 3;
			}
			else if ("EQUIPPED".equals(tokens[i]))
			{
				equipped = 1;
			}

			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("EQUIPPED"))
			{
				Logging.errorPrint("Old syntax EQUIPPEDx will be replaced for EQUIPPED.x");

				index = Integer.parseInt(tokens[i].substring(8));
				equipped = 1;
			}
			else if ("NOT_EQUIPPED".equals(tokens[i]))
			{
				equipped = 2;
			}

			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("NOT_EQUIPPED"))
			{
				Logging.errorPrint("Old syntax NOT_EQUIPPEDx will be replaced for NOT_EQUIPPED.x");

				index = Integer.parseInt(tokens[i].substring(12));
				equipped = 2;
			}
			else if (tokens[i].equals("MERGENONE"))
			{
				merge = Constants.MERGE_NONE;
			}
			else if (tokens[i].equals("MERGELOC"))
			{
				merge = Constants.MERGE_LOCATION;
			}
			else if (tokens[i].equals("MERGEALL"))
			{
				merge = Constants.MERGE_ALL;
			}
			else if (tokens[i].equals("ISTYPE"))
			{
				property = tokens[i] + "." + tokens[i + 1];

				break;
			}
			else if (i < (tokens.length - 1))
			{
				try
				{
					index = Integer.parseInt(tokens[i]);
				}
				catch (NumberFormatException exc)
				{
					if ("".equals(type))
					{
						type = tokens[i];
					}
					else
					{
						subtype = tokens[i];
					}
				}
			}
			else
			{
				property = tokens[i];
			}
		}

		if ("".equals(type))
		{
			return _replaceTokenArmor(index, property, equipped, len, output, merge, aPC);
		}
		else if ("SUIT".equals(type))
		{
			return _replaceTokenArmorSuit(index, subtype, property, equipped, len, output, merge, aPC);
		}
		else if ("SHIRT".equals(type))
		{
			return _replaceTokenArmorShirt(index, subtype, property, equipped, len, output, merge, aPC);
		}
		else if ("SHIELD".equals(type))
		{
			return _replaceTokenArmorShield(index, subtype, property, equipped, len, output, merge, aPC);
		}
		else if ("ITEM".equals(type) || "ACITEM".equals(type))
		{
			return _replaceTokenArmorItem(index, subtype, property, equipped, len, output, merge, aPC);
		}
		else
		{
			return _replaceTokenArmorVarious(index, type, subtype, property, equipped, len, output, merge, aPC);
		}
	}

	private void replaceTokenFollowers(String aString, BufferedWriter output, PlayerCharacter aPC)
	{
		/* syndaryl 24/07/2002 12:51PM: kitbashed an output format for followers, much like the FEATLIST tag */
		/* Will also need to cover COUNT[FOLLOWERS], not done yet. */
		final List followers = aPC.getFollowerList();

		if (!followers.isEmpty())
		{ /* if it's empty, do nothing */

			if ("FOLLOWERLIST".equals(aString))
			{
				int i;
				boolean lastflag = false;

				for (i = 0; i < followers.size(); ++i)
				{
					if (followers.get(i) instanceof Follower)
					{
						Follower aF = (Follower) followers.get(i);

						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter nPC = (PlayerCharacter) p.next();

							if (aF.getFileName().equals(nPC.getFileName()))
							{
								if (lastflag)
								{
									FileAccess.write(output, ", ");
								}

								FileAccess.encodeWrite(output, nPC.getName());
								lastflag = true;
							}
						}
					}
				}
			}
			else if (aString.startsWith("FOLLOWERTYPE."))
			{
				// Handle FOLLOWERTYPE.<type>x.subtag stuff
				// New token syntax FOLLOWERTYPE.<type>.x instead of FOLLOWERTYPE.<type>x
				StringTokenizer aTok = new StringTokenizer(aString, ".");
				aTok.nextToken(); // FOLLOWERTYPE

				String typeString = aTok.nextToken();
				String restString = "";
				int followerIndex = -1;

				if (aTok.hasMoreTokens())
				{
					restString = aTok.nextToken();

					// When removing old token syntax, remove the catch code
					try
					{
						followerIndex = Integer.parseInt(restString);
						restString = "";
					}
					catch (NumberFormatException exc)
					{
						// Error, not debug.  We want users to report
						// use of the deprecated syntax so we can fix
						// them as they are found.
						Logging.errorPrint("Old syntax FOLLOWERTYPEx will be replaced for FOLLOWERTYPE.x");

						int numCharToRemove = 0;

						for (int i = typeString.length() - 1; i > 0; i--)
						{
							if ((typeString.charAt(i) >= '0') && (typeString.charAt(i) <= '9'))
							{
								followerIndex = Integer.parseInt(typeString.substring(i));
								numCharToRemove++;
							}
							else
							{
								i = 0;
							}
						}

						if (numCharToRemove > 0)
						{
							typeString = typeString.substring(0, typeString.length() - numCharToRemove);
						}
					}

					while (aTok.hasMoreTokens())
					{
						restString = restString + "." + aTok.nextToken();
					}

					if (restString.indexOf(".") == 0)
					{
						restString = restString.substring(1);
					}
				}

				List aList = new ArrayList();

				for (int i = followers.size() - 1; i >= 0; --i)
				{
					final Follower fol = (Follower) followers.get(i);

					if (fol.getType().equalsIgnoreCase(typeString))
					{
						aList.add(fol);
					}
				}

				if (followerIndex < aList.size())
				{
					if (aList.get(followerIndex) instanceof Follower)
					{
						final Follower aF = (Follower) aList.get(followerIndex);
						PlayerCharacter newPC;

						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter nPC = (PlayerCharacter) p.next();

							if (aF.getFileName().equals(nPC.getFileName()))
							{
								newPC = nPC;

								if (restString.equals(""))
								{
									restString = "NAME";
								}

								nPC = aPC;
								aPC = newPC;
								Globals.setCurrentPC(aPC);
								replaceToken(restString, output, aPC);
								aPC = nPC;
								Globals.setCurrentPC(aPC);
							}
						}
					}
				}
			}
			else
			{
				/* FOLLOWER%.subtag stuff handled in here*/

				// New token syntax FOLLOWER.x instead of FOLLOWERx
				StringTokenizer aTok = new StringTokenizer(aString, ".");
				String fString = aTok.nextToken(); // FOLLOWER
				final int i;

				if ("FOLLOWER".equals(fString))
				{
					i = Integer.parseInt(aTok.nextToken());
				}
				else
				{
					Logging.errorPrint("Old syntax FOLLOWERx will be replaced for FOLLOWER.x");

					i = Integer.parseInt(aString.substring(8, aString.indexOf('.')));
				}

				if (i < followers.size())
				{
					if (followers.get(i) instanceof Follower)
					{
						final Follower aF = (Follower) followers.get(i);
						PlayerCharacter newPC;

						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter nPC = (PlayerCharacter) p.next();

							if (aF.getFileName().equals(nPC.getFileName()))
							{
								newPC = nPC;

								String aLabel;

								if (aTok.hasMoreTokens())
								{
									aLabel = aTok.nextToken();
								}
								else
								{
									aLabel = "NAME";
								}

								nPC = aPC;
								aPC = newPC;
								Globals.setCurrentPC(aPC);
								replaceToken(aLabel, output, aPC);
								aPC = nPC;
								Globals.setCurrentPC(aPC);
							}
						}
					}
				}
			}
		}
	}

	private void replaceTokenForDfor(String aString, BufferedWriter output, PlayerCharacter aPC)
	{
		int x = 0;
		int i = 0;
		StringTokenizer aTok;

		if (aString.startsWith("DFOR."))
		{
			aTok = new StringTokenizer(aString.substring(5), ",", false);
		}
		else
		{
			aTok = new StringTokenizer(aString.substring(4), ",", false);
		}

		int cMin = 0;
		int cMax = 100;
		int cStep = 1;
		int cStepLine = 1;
		int cStepLineMax = 0;
		String bString;
		String cString = "";
		String cStartLineString = "";
		String cEndLineString = "";
		boolean isDFor = false;

		while (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();

			switch (i++)
			{
				case 0:
					cMin = getVarValue(bString, aPC);

					break;

				case 1:
					cMax = getVarValue(bString, aPC);

					break;

				case 2:
					cStep = getVarValue(bString, aPC);

					if (aString.startsWith("DFOR."))
					{
						isDFor = true;
						bString = aTok.nextToken();
						cStepLineMax = getVarValue(bString, aPC);
						bString = aTok.nextToken();
						cStepLine = getVarValue(bString, aPC);
					}

					break;

				case 3:
					cString = bString;

					break;

				case 4:
					cStartLineString = bString;

					break;

				case 5:
					cEndLineString = bString;

					break;

				case 6:
					existsOnly = (!"0".equals(bString));

					if ("2".equals(bString))
					{
						checkBefore = true;
					}

					break;

				default:
					Logging.errorPrint("ExportHandler.replaceTokenForDfor can't handle token number " + i);

					break;
			}
		}

		if ("COMMA".equals(cStartLineString))
		{
			cStartLineString = ",";
		}

		if ("COMMA".equals(cEndLineString))
		{
			cEndLineString = ",";
		}

		if ("NONE".equals(cStartLineString))
		{
			cStartLineString = "";
		}

		if ("NONE".equals(cEndLineString))
		{
			cEndLineString = "";
		}

		if ("CRLF".equals(cStartLineString))
		{
			cStartLineString = Constants.s_LINE_SEP;
		}

		if ("CRLF".equals(cEndLineString))
		{
			cEndLineString = Constants.s_LINE_SEP;
		}

		int iStart = cMin;
		int iNow;

		while (iStart < cMax)
		{
			if (x++ == 0)
			{
				FileAccess.write(output, cStartLineString);
			}

			iNow = iStart;

			if (!isDFor)
			{
				cStepLineMax = iNow + cStep;
			}

			if ((cStepLineMax > cMax) && !isDFor)
			{
				cStepLineMax = cMax;
			}

			while ((iNow < cStepLineMax) || (isDFor && (iNow < cMax)))
			{
				boolean insideToken = false;

				if (cString.startsWith(csheetTag2))
				{
					insideToken = true;
				}

				aTok = new StringTokenizer(cString, csheetTag2, false);

				int j = 0;

				while (aTok.hasMoreTokens())
				{
					String eString = aTok.nextToken();
					String fString;
					String gString = "";
					String hString = eString;
					int index = 0;

					while (hString.indexOf('%', index) > 0)
					{
						index = hString.indexOf('%', index);

						if (index == -1)
						{
							break;
						}

						if ((index < (hString.length() - 1)) && (hString.charAt(index + 1) != '.'))
						{
							index++;

							continue;
						}

						fString = hString.substring(0, index);

						if ((index + 1) < eString.length())
						{
							gString = hString.substring(index + 1);
						}

						hString = fString + Integer.toString(iNow) + gString;
					}

					if ("%0".equals(eString) || "%1".equals(eString))
					{
						final int cInt = iNow + Integer.parseInt(eString.substring(1));
						FileAccess.write(output, Integer.toString(cInt));
					}
					else
					{
						if (insideToken)
						{
							replaceToken(hString, output, aPC);
						}
						else
						{
							boolean oldSkipMath = skipMath;
							skipMath = true;
							replaceToken(hString, output, aPC);
							skipMath = oldSkipMath;
						}
					}

					if (checkBefore && noMoreItems)
					{
						iNow = cMax;
						iStart = cMax;

						if (j == 0)
						{
							existsOnly = false;
						}

						break;
					}

					++j;
					insideToken = !insideToken;
				}

				iNow += cStepLine;

				if (cStepLine == 0)
				{
					break;
				}
			}

			if ((cStepLine > 0) || ((cStepLine == 0) && (x == cStep)) || (existsOnly == noMoreItems))
			{
				FileAccess.write(output, cEndLineString);
				x = 0;

				if (existsOnly && noMoreItems)
				{
					return;
				}
			}

			iStart += cStep;
		}
	}

	private void replaceTokenIIF(String aString, BufferedWriter output, PlayerCharacter aPC)
	{
		int iParenCount = 0;
		final String[] aT = new String[3];
		int i;
		int iParamCount = 0;
		int iStart = 4;

		// OIF(expr,truepart,falsepart)
		// {|OIF(HASFEAT:Armor Prof (Light), <b>Yes</b>, <b>No</b>)|}
		for (i = iStart; i < aString.length(); ++i)
		{
			if (iParamCount == 3)
			{
				break;
			}

			switch (aString.charAt(i))
			{
				case '(':
					iParenCount += 1;

					break;

				case ')':
					iParenCount -= 1;

					if (iParenCount == -1)
					{
						if (iParamCount == 2)
						{
							aT[iParamCount++] = aString.substring(iStart, i).trim();
							iStart = i + 1;
						}
						else
						{
							Logging.errorPrint("IIF: not enough parameters (" + Integer.toString(iParamCount) + ')');
							for (int j = 0; j < iParamCount; ++j)
							{
								Logging.errorPrint("  " + Integer.toString(j) + ':' + aT[j]);
							}
						}
					}

					break;

				case ',':

					if (iParenCount == 0)
					{
						if (iParamCount < 2)
						{
							aT[iParamCount] = aString.substring(iStart, i).trim();
							iStart = i + 1;
						}
						else
						{
							Logging.errorPrint("IIF: too many parameters");
						}

						iParamCount += 1;
					}

					break;

				default:
					break;
			}
		}

		if (iParamCount != 3)
		{
			Logging.errorPrint("IIF: invalid parameter count: " + iParamCount);
		}
		else
		{
			aString = aString.substring(iStart);
			iStart = 2;

			if (evaluateExpression(aT[0], aPC))
			{
				iStart = 1;
			}

			FileAccess.write(output, aT[iStart]);
		}

		if (aString.length() > 0)
		{
			Logging.errorPrint("IIF: extra characters on line: " + aString);
			FileAccess.write(output, aString);
		}
	}

	private int replaceTokenSpellListBook(String aString, PlayerCharacter aPC)
	{
		int sbookNum = 0;

		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int levelNum = Integer.parseInt(aTok.nextToken());

		if (aTok.hasMoreTokens())
		{
			sbookNum = Integer.parseInt(aTok.nextToken());
		}

		String bookName = Globals.getDefaultSpellBook();

		if (sbookNum > 0)
		{
			bookName = (String) aPC.getSpellBooks().get(sbookNum);
		}

		canWrite = false;

		final PObject aObject = aPC.getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			final List aList = aObject.getSpellSupport().getCharacterSpell(null, bookName, levelNum);
			canWrite = !aList.isEmpty();
		}

		return 0;
	}

	private void replaceTokenVar(String aString, PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aString.substring(5), ".", false);
		final String varName = aTok.nextToken();
		String bString = "EQ";

		if (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
		}

		String value = "0";

		if (aTok.hasMoreTokens())
		{
			value = aTok.nextToken();
		}

		final Float varval = aPC.getVariable(varName, true, true, "", "", 0);
		final Float valval = aPC.getVariableValue(value, "");

		if ("GTEQ".equals(bString))
		{
			canWrite = varval.doubleValue() >= valval.doubleValue();
		}
		else if ("GT".equals(bString))
		{
			canWrite = varval.doubleValue() > valval.doubleValue();
		}
		else if ("LTEQ".equals(bString))
		{
			canWrite = varval.doubleValue() <= valval.doubleValue();
		}
		else if ("LT".equals(bString))
		{
			canWrite = varval.doubleValue() < valval.doubleValue();
		}
		else if ("NEQ".equals(bString))
		{
			canWrite = !CoreUtility.doublesEqual(varval.doubleValue(), valval.doubleValue());
		}
		else
		{
			canWrite = !CoreUtility.doublesEqual(varval.doubleValue(), valval.doubleValue());
		}
	}

	/**
	 * Exports a PlayerCharacter-Party to a Writer
	 * according to the handler's template
	 * <p/>
	 * <br>author: Thomas Behr 13-11-02
	 *
	 * @param PCs the PlayerCharacter[] which compromises the Party to write
	 * @param out the Writer to be written to
	 */
	private void write(PlayerCharacter[] PCs, BufferedWriter out)
	{
		FileAccess.setCurrentOutputFilter(templateFile.getName());

		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), "UTF-8"));

			boolean flag;
			boolean inPipe = false;
			StringBuffer tokString = new StringBuffer();
			int charNum;

			String aLine;

			while ((aLine = br.readLine()) != null)
			{
				if (!inPipe && (aLine.lastIndexOf('|') < 0))
				{
					FileAccess.write(out, aLine);
					FileAccess.newLine(out);
				}
				else if ((inPipe && (aLine.lastIndexOf('|') < 0)) || (!inPipe && (aLine.lastIndexOf('|') == 0)))
				{
					tokString.append(aLine.substring(aLine.lastIndexOf('|') + 1));
					inPipe = true;
				}
				else
				{
					if (!inPipe && (aLine.charAt(0) == '|'))
					{
						inPipe = true;
					}

					final StringTokenizer bTok = new StringTokenizer(aLine, "|", false);
					flag = bTok.countTokens() == 1;

					String bString;

					while (bTok.hasMoreTokens())
					{
						bString = bTok.nextToken();

						if (!inPipe)
						{
							FileAccess.write(out, bString);
						}
						else
						{
							if (bTok.hasMoreTokens() || flag)
							{
								int i;

								String aString = tokString.toString() + bString;

								if (aString.startsWith("FOR."))
								{
									int x = 0;
									int j = 0;
									final PStringTokenizer pTok = new PStringTokenizer(aString.substring(4), ",",
											"\\\\", "\\\\");
									Integer cMin = new Integer(0);
									Integer cMax = new Integer(100);
									Integer cStep = new Integer(1);
									String cString = "";
									String cStartLineString = "";
									String cEndLineString = "";
									bString = null;

									boolean _existsOnly = false;
									boolean _noMoreItems = false;

									while (pTok.hasMoreTokens())
									{
										bString = pTok.nextToken();

										switch (j++)
										{
											case 0:
												cMin = Delta.decode(bString);

												break;

											case 1:
												cMax = Delta.decode(bString);

												break;

											case 2:
												cStep = Delta.decode(bString);

												break;

											case 3:
												cString = bString;

												break;

											case 4:
												cStartLineString = bString;

												break;

											case 5:
												cEndLineString = bString;

												break;

											case 6:
												_existsOnly = !("0".equals(bString));

												break;

											default:
												Logging.errorPrint(
													"In Party.print there is an unhandled case in a switch (the value is "
													+ j + ".");

												break;
										}
									}

									if ((cMax.intValue() >= PCs.length) && _existsOnly)
									{
										cMax = new Integer(PCs.length);
									}

									for (int k = cMin.intValue(); k < cMax.intValue(); k++)
									{
										if (x++ == 0)
										{
											FileAccess.write(out, cStartLineString);
										}

										String dString = cString;
										String eString;

										while (dString.length() > 0)
										{
											eString = "";

											for (int l = 0; l < (dString.length() - 1); l++)
											{
												if ((dString.charAt(l) == '\\') && (dString.charAt(l + 1) == '\\'))
												{
													eString = dString.substring(0, l);
													dString = dString.substring(l + 2);

													break;
												}
											}

											if ("".equals(eString))
											{
												eString = dString;
												dString = "";
											}

											if (eString.startsWith("%."))
											{
												charNum = k;

												if ((charNum >= 0) && (charNum < PCs.length))
												{
													PlayerCharacter currPC = PCs[charNum];
													Globals.setCurrentPC(currPC);

													if (currPC != null)
													{
														replaceToken(eString.substring(2), out, currPC);
													}
													else
													{
														_noMoreItems = true;
													}
												}
												else
												{
													_noMoreItems = true;
												}
											}
											else
											{
												FileAccess.write(out, eString);
											}
										}

										if ((x == cStep.intValue()) || (_existsOnly == _noMoreItems))
										{
											FileAccess.write(out, cEndLineString);
//											FileAccess.newLine(out);
											x = 0;

											if (_existsOnly == _noMoreItems)
											{
												break;
											}
										}
									}
								}
								else
								{
									charNum = -1;

									for (i = 0; i < aString.length(); i++)
									{
										if ((aString.charAt(i) < '0') || (aString.charAt(i) > '9'))
										{
											break;
										}
									}

									if (i > 0)
									{
										charNum = Delta.parseInt(aString.substring(0, i));
									}

									if ((charNum >= 0) && (charNum < Globals.getPCList().size()))
									{
										PlayerCharacter currPC = PCs[charNum];
										Globals.setCurrentPC(currPC);
										replaceToken(aString, out, currPC);
									}
									else if (aString.startsWith("EXPORT"))
									{
										// We can safely do EXPORT tags with no PC
										replaceToken(aString, out, null);
									}
								}

								tokString = new StringBuffer("");
							}
							else
							{
								tokString.append(bString);
							}
						}

						if (bTok.hasMoreTokens() || flag)
						{
							inPipe = !inPipe;
						}
					}

					if (inPipe && (aLine.charAt(aLine.length() - 1) == '|'))
					{
						inPipe = false;
					}
				}

//				if (!inPipe)
//				{
//					FileAccess.newLine(out);
//				}
			}
		}
		catch (IOException exc)
		{
			//TODO: If this should be ignored, add a comment here describing why. XXX
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException ignore)
				{
					// nothing to do about it
				}
			}
		}
	}

	/*
	 * ##########################################################################
	 * various helper methods
	 * ##########################################################################
	 */

	/**
	 * @param canWrite The canWrite to set.
	 */
	public final void setCanWrite(boolean canWrite)
	{
		this.canWrite = canWrite;
	}
	/**
	 * @return Returns the checkBefore.
	 */
	public final boolean getCheckBefore()
	{
		return checkBefore;
	}
	/**
	 * @return Returns the inLabel.
	 */
	public final boolean getInLabel()
	{
		return inLabel;
	}

	/**
	 * @return Returns the existsOnly.
	 */
	public final boolean getExistsOnly()
	{
		return existsOnly;
	}

	/**
	 * @param noMoreItems The noMoreItems value to set.
	 */
	public final void setNoMoreItems(boolean noMoreItems)
	{
		this.noMoreItems = noMoreItems;
	}

	/**
	 * Get the token string
	 * @param aPC
	 * @param aString
	 * @return token string
	 */
	public static final String getTokenString(final PlayerCharacter aPC, final String aString)
	{
		final StringTokenizer tok = new StringTokenizer(aString, ".,", false);
		final String firstToken = tok.nextToken();

		//
		// Make sure the token list has been populated
		//
		populateTokenMap();

		final Token token = (Token) tokenMap.get(firstToken);
		if (token != null)
		{
			return token.getToken(aString, aPC, null);
		}
		return "";
	}

	/*
	 * ######################################################
	 * inner classes
	 * ######################################################
	 */

	/**
	 * <code>PStringTokenizer</code>
	 *
	 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
	 * @version $Revision$
	 */
	private static final class PStringTokenizer
	{
		private String _andThat = "";
		private String _delimiter = "";
		private String _forThisString = "";
		private String _ignoreBetweenThis = "";

		PStringTokenizer(String forThisString, String delimiter, String ignoreBetweenThis, String andThat)
		{
			_forThisString = forThisString;
			_delimiter = delimiter;
			_ignoreBetweenThis = ignoreBetweenThis;
			_andThat = andThat;
		}

		/**
		 * Return true if we have more tokens
		 * @return true if we have
		 */
		public boolean hasMoreTokens()
		{
			return (_forThisString.length() > 0);
		}

		/**
		 * Return the next token
		 * @return next token
		 */
		public String nextToken()
		{
			String aString;
			int ignores = 0;

			if (_forThisString.lastIndexOf(_delimiter) == -1)
			{
				aString = _forThisString;
				_forThisString = "";
			}
			else
			{
				int i;
				final StringBuffer b = new StringBuffer();

				for (i = 0; i < _forThisString.length(); i++)
				{
					if (_forThisString.substring(i).startsWith(_delimiter) && (ignores == 0))
					{
						break;
					}

					if (_forThisString.substring(i).startsWith(_ignoreBetweenThis) && (ignores == 0))
					{
						ignores = 1;
					}
					else if (_forThisString.substring(i).startsWith(_andThat))
					{
						ignores = 0;
					}

					b.append(_forThisString.substring(i, i + 1));
				}

				aString = b.toString();
				_forThisString = _forThisString.substring(i + 1);
			}

			return aString;
		}
	}
}
