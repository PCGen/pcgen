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
 *
 */
package gmgen.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.PCAttribute;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.character.CharacterSpell;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.DescriptionFormatting;
import pcgen.core.display.SkillDisplay;
import pcgen.core.spell.Spell;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.RequestOpenPlayerCharacterMessage;
import pcgen.util.Logging;
import pcgen.util.enumeration.View;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;

/**
 */
public class PcgCombatant extends Combatant
{
	protected PlayerCharacter pc;
	private CharacterDisplay display;
	protected PcRenderer renderer;
	private float crAdj = 0;
	private final PCGenMessageHandler messageHandler;

	/**
	 *  Creates new PcgCombatant
	 *
	 *@param  pc  PCGen pc that this combatant represents
	 */
	public PcgCombatant(PlayerCharacter pc, PCGenMessageHandler mh)
	{
		messageHandler = mh;
		this.pc = pc;
		display = pc.getDisplay();
		this.init = new PcgSystemInitiative(pc);

		PCStat stat = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class, "CON");
		this.hitPoints = new SystemHP(new SystemAttribute("Constitution", pc.getTotalStatFor(stat)), pc.hitPoints(),
			pc.hitPoints());
		setCombatantType("PC");
	}

	/**
	 *  Constructor for the PcgCombatant object
	 *
	 *@param  pc    PCGen pc that this combatant represents
	 *@param  type  PC/Enemy/Ally/Non Combatant
	 */
	public PcgCombatant(PlayerCharacter pc, String type, PCGenMessageHandler mh)
	{
		this(pc, mh);
		setCombatantType(type);
	}

	public PcgCombatant(Element combatant, PCGenMessageHandler comp, PCGenMessageHandler mh)
	{
		messageHandler = mh;
		try
		{
			String pcgFilename = combatant.getChild("PCG").getAttribute("file").getValue();
			if (StringUtils.isNotBlank(pcgFilename))
			{
				File pcgFile = new File(pcgFilename);
				RequestOpenPlayerCharacterMessage msg = new RequestOpenPlayerCharacterMessage(comp, pcgFile, true);
				messageHandler.handleMessage(msg);
				this.pc = msg.getPlayerCharacter();
			}
			if (pc == null)
			{
				pc = new PlayerCharacter();
				pc.setName(combatant.getAttributeValue("name"));
			}
			this.display = pc.getDisplay();
			this.init = new PcgSystemInitiative(pc);

			PCStat stat =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class, "CON");
			this.hitPoints = new SystemHP(new SystemAttribute("Constitution", pc.getTotalStatFor(stat)), pc.hitPoints(),
				pc.hitPoints());

			setStatus(State.getState(combatant.getAttribute("status").getValue()));
			setCombatantType(combatant.getAttribute("type").getValue());

			init.setBonus(combatant.getChild("Initiative").getAttribute("bonus").getIntValue());

			try
			{
				init.setCurrentInitiative(combatant.getChild("Initiative").getAttribute("current").getIntValue());
			}
			catch (Exception e)
			{
				//Not necessarily set
			}

			hitPoints.setMax(combatant.getChild("HitPoints").getAttribute("max").getIntValue());
			hitPoints.setCurrent(combatant.getChild("HitPoints").getAttribute("current").getIntValue());
			hitPoints.setSubdual(combatant.getChild("HitPoints").getAttribute("subdual").getIntValue());
			hitPoints.setState(State.getState(combatant.getChild("HitPoints").getAttribute("state").getValue()));
		}
		catch (Exception e)
		{
			Logging.errorPrint("Initiative", e);
		}
	}

	/**
	 * Adjusts the CR for this combatant
	 * 
	 * TODO  I'm not sure that it should be current - the newly calculated or an entire replacement
	 * It appears it's called from 2 different places, one which adjusts the CR, the other ?
	 * 
	 * In the case of adjusting the CR the calculation is wrong anyhow,
	 * surely it should be calculated - the value passed in?
	 *
	 * @param  cr  new CR value
	 */
	@Override
	public void setCR(float cr)
	{
		this.crAdj = cr - pc.getDisplay().calcCR();
	}

	/**
	 * Gets the CR for the character
	 *
	 * @return    CR
	 */
	@Override
	public float getCR()
	{
		return pc.getDisplay().calcCR() + crAdj;
	}

	/**
	 *  Sets the name of the character
	 *
	 *@param  name  The new name
	 */
	@Override
	public void setName(String name)
	{
		pc.setName(name);
	}

	/**
	 *  Gets the name of the PC
	 *
	 *@return    The name
	 */
	@Override
	public String getName()
	{
		return display.getName();
	}

	/**
	 *  Gets the PCGen PC of the PcgCombatant object
	 *
	 *@return    The PCGen PC
	 */
	public PlayerCharacter getPC()
	{
		return pc;
	}

	/**
	 *  Sets the player's name of the PcgCombatant object
	 *
	 *@param  player  The new player's name
	 */
	public void setPlayer(String player)
	{
		pc.setPCAttribute(PCAttribute.PLAYERSNAME, player);
	}

	/**
	 *  Gets the player's name of the PcgCombatant object
	 *
	 *@return    The player's name
	 */
	@Override
	public String getPlayer()
	{
		return display.getPlayersName();
	}

	@Override
	public Element getSaveElement()
	{
		Element retElement = new Element("PcgCombatant");
		Element initiative = new Element("Initiative");
		Element hp = new Element("HitPoints");
		Element pcg = new Element("PCG");

		pcg.setAttribute("file", pc.getFileName());
		retElement.addContent(pcg);

		initiative.setAttribute("bonus", String.valueOf(init.getModifier()));

		if (init.getCurrentInitiative() > 0)
		{
			initiative.setAttribute("current", String.valueOf(init.getCurrentInitiative()));
		}

		retElement.addContent(initiative);

		hp.setAttribute("current", String.valueOf(hitPoints.getCurrent()));
		hp.setAttribute("subdual", String.valueOf(hitPoints.getSubdual()));
		hp.setAttribute("max", String.valueOf(hitPoints.getMax()));
		hp.setAttribute("state", String.valueOf(hitPoints.getState()));
		retElement.addContent(hp);

		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("status", getStatus().name());
		retElement.setAttribute("type", getCombatantType());

		return retElement;
	}

	/**
	 *  Set the experience value for this character
	 *
	 *@param  experience  Experience value
	 */
	@Override
	public void setXP(int experience)
	{
		pc.setXP(experience);
	}

	/**
	 *  Gets the experience value for the character
	 *
	 *@return    Experience value
	 */
	@Override
	public int getXP()
	{
		return pc.getXP();
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
		Integer intData = Integer.valueOf(strData);
		//Determine which row was edited
		switch (columnName)
		{
			case "Name":
				// Character's Name
				setName(strData);
				break;
			case "Player":
				// Player's Name
				setPlayer(strData);
				break;
			case "Status":
				// XML Combatant's Status
				setStatus(State.getStateLocalised(strData));
				break;
			case "+":
				// Initiative bonus
				
				init.setBonus(intData.intValue());
				break;
			case "Init":
				// Initiative
				init.setCurrentInitiative(intData.intValue());
				break;
			case "#":
				// Number (for tokens)
				setNumber(intData.intValue());
				break;
			case "HP":
				// Current Hit Points
				hitPoints.setCurrent(intData.intValue());
				break;
			case "HP Max":
				// Maximum Hit Points
				hitPoints.setMax(intData.intValue());
				break;
			case "Dur":
				// Duration
				setDuration(intData.intValue());
				break;
			case "Type":
				// Type
				setCombatantType(strData);
				break;
			default:
				//Case not caught, should this cause an error?
				break;
		}
	}

	@Override
	public String toHtmlString()
	{
		if (renderer == null)
		{
			renderer = new PcRenderer();
		}
		return renderer.getHtmlText();
	}

	protected class PcRenderer
	{
		protected String htmlString;
		protected int serial = 0;

		/**
		 * <p>
		 * This sets the text of the JTextPane for the specified PC. It uses an
		 * output sheet template, specified by the templateName option; it uses
		 * {@code pcgen.io.ExportHandler} to transform the template file
		 * into an StringWriter, and then sets the text of the text pane as html.
		 * This allows us easy access to changing the content or format of the stat
		 * block, and also allows us to easily use a different output format if
		 * necessary.
		 * </p>
		 * @return HTML text
		 */
		public String getHtmlText()
		{
			if (serial < pc.getSerial() || htmlString == null)
			{
				StringBuilder statBuf = new StringBuilder();

				statBuf.append("<html>");
				statBuf.append(getStatBlockHeader());
				statBuf.append("<body class='Normal' lang='EN-US'>");
				statBuf.append(getStatBlockTitle());
				statBuf.append(getStatBlockCore());
				statBuf.append("<DIV style='MARGIN: 0px 10px'>");
				statBuf.append(getStatBlockLineSkills());
				statBuf.append(getStatBlockLinePossessions());

				try
				{
					statBuf.append(getStatBlockLineSpells());
				}
				catch (Exception e)
				{
					Logging.errorPrint(e.getMessage(), e);
				}

				statBuf.append("</DIV>");

				statBuf.append("<br>");

				statBuf.append("</html>");

				serial = pc.getSerial();
				htmlString = statBuf.toString();
			}
			return htmlString;
		}

		protected String getStatBlockCore()
		{
			StringBuilder statBuf = new StringBuilder();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<font class='type'>CR</font> ");
			statBuf.append(pcOut.getCR()); //|CR|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Size</font> ");
			statBuf.append(pcOut.getSize()); //|SIZE|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Type</font> ");
			statBuf.append(pcOut.getRaceType()); //|TYPE|
			statBuf.append("; ");

			statBuf.append("<font class='type'>HD</font> ");
			statBuf.append(pcOut.getHitDice()); //|HITDICE|
			statBuf.append("; ");

			statBuf.append("<font class='type'>hp</font> ");
			statBuf.append(pcOut.getHitPoints()); //|HP|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Init</font> <font class='highlight'>");
			statBuf.append(pcOut.getInitTotal()); //|INITIATIVEMOD|
			statBuf.append("</font> (");
			statBuf.append(pcOut.getInitStatMod()); //|STAT.1.MOD|
			statBuf.append(" Dex, ");
			statBuf.append(pcOut.getInitMiscMod()); //|INITIATIVEMISC|
			statBuf.append(" Misc); ");

			statBuf.append("<font class='type'>Spd</font> ");
			statBuf.append(pcOut.getSpeed()); //|MOVEMENT|
			statBuf.append(";<br>");

			statBuf.append("<font class='type'>AC</font> <font class='highlight'>");
			statBuf.append(pcOut.getAC()); //|AC.Total|
			statBuf.append("</font> (flatfooted <font class='highlight'>");
			statBuf.append(pcOut.getACFlatFooted()); //|AC.Flatfooted|
			statBuf.append("</font>, touch <font class='highlight'>");
			statBuf.append(pcOut.getACTouch()); //|AC.Touch|
			statBuf.append("</font>);<br>");

			statBuf.append("<font class='type'>Melee:</font> <a href='attack:Melee\\");
			statBuf.append(pcOut.getMeleeTotal()); //|ATTACK.MELEE.TOTAL|
			statBuf.append("' class='highlight'>");
			statBuf.append(pcOut.getMeleeTotal()); //|ATTACK.MELEE.TOTAL|
			statBuf.append("</a>; ");

			statBuf.append("<font class='type'>Ranged:</font> <a href='attack:Ranged\\");
			statBuf.append(pcOut.getRangedTotal()); //|ATTACK.RANGED.TOTAL|
			statBuf.append("' class='highlight'>");
			statBuf.append(pcOut.getRangedTotal()); //|ATTACK.RANGED.TOTAL|
			statBuf.append("</a>; ");

			statBuf.append("<font class='type'>Weapons:</font>");

			List<Equipment> weaponList = pc.getExpandedWeapons(Constants.MERGE_ALL);

			for (int i = 0; i < weaponList.size(); i++)
			{
				Equipment eq = weaponList.get(i);
				statBuf.append("<a href=" + '"' + "attack:");
				statBuf.append(PlayerCharacterOutput.getWeaponName(eq)); //|WEAPON.%weap.NAME|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponToHit(i)); //|WEAPON.%weap.TOTALHIT|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponRange(eq)); //|WEAPON.%weap.RANGE|
				statBuf.append("\\");
				statBuf.append(PlayerCharacterOutput.getWeaponType(eq)); //|WEAPON.%weap.TYPE|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponDamage(i)); //|WEAPON.%weap.DAMAGE|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponCritRange(i)); //|WEAPON.%weap.CRIT|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponCritMult(i)); //|WEAPON.%weap.MULT|
				statBuf.append("\\");
				statBuf.append(PlayerCharacterOutput.getWeaponHand(eq)); //|WEAPON.%weap.HAND|
				statBuf.append("\\");
				statBuf.append(PlayerCharacterOutput.getWeaponSize(eq)); //|WEAPON.%weap.SIZE|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponSpecialProperties(eq)); //|WEAPON.%weap.SPROP|
				statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

				statBuf.append(PlayerCharacterOutput.getWeaponName(eq)); //|WEAPON.%weap.NAME|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponToHit(i)); //|WEAPON.%weap.TOTALHIT|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponRange(eq)); //|WEAPON.%weap.RANGE|
				statBuf.append("/");
				statBuf.append(PlayerCharacterOutput.getWeaponType(eq)); //|WEAPON.%weap.TYPE|
				statBuf.append(" (");
				statBuf.append(pcOut.getWeaponDamage(i)); //|WEAPON.%weap.DAMAGE|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponCritRange(i)); //|WEAPON.%weap.CRIT|
				statBuf.append("/x");
				statBuf.append(pcOut.getWeaponCritMult(i)); //|WEAPON.%weap.MULT|
				statBuf.append(" ");
				statBuf.append(PlayerCharacterOutput.getWeaponHand(eq)); //|WEAPON.%weap.HAND|
				statBuf.append(" ");
				statBuf.append(PlayerCharacterOutput.getWeaponSize(eq)); //|WEAPON.%weap.SIZE|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponSpecialProperties(eq)); //|WEAPON.%weap.SPROP|
				statBuf.append(") </a> or ");
			}

			//Unarmed attack
			statBuf.append("<a href=" + '"' + "attack:Unarmed\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.TOTALHIT")); //|WEAPONH.TOTALHIT|
			statBuf.append("\\\\B\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

			statBuf.append("Unarmed ");
			statBuf.append(pcOut.getExportToken("WEAPONH.TOTALHIT")); //|WEAPONH.TOTALHIT|
			statBuf.append(" (");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append(" ");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("/x");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append(") </a> or ");
			//End unarmed attack

			//Grapple
			statBuf.append("<a href=" + '"' + "attack:Grapple\\");
			statBuf.append(pcOut.getExportToken("ATTACK.GRAPPLE.TOTAL")); //|WEAPONH.TOTALHIT|
			statBuf.append("\\\\B\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

			statBuf.append("Grapple ");
			statBuf.append(pcOut.getExportToken("ATTACK.GRAPPLE.TOTAL")); //|WEAPONH.TOTALHIT|
			statBuf.append(" (");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append(" ");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("/x");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append(")</a>;<br>");
			//End Grapple

			statBuf.append("<font class='type'>SA:</font> ");
			statBuf.append(pcOut.getSpecialAbilities()); //|SPECIALLIST|

			int turnTimes = pc.getVariableValue("TurnTimesUndead", "").intValue();
			if (turnTimes > 0)
			{
				int turnDieNumber = pc.getVariableValue("TurnDiceUndead", "").intValue();
				int turnDieSize = pc.getVariableValue("TurnDieSizeUndead", "").intValue();
				int turnDamage = pc.getVariableValue("TurnDamagePlusUndead", "").intValue();
				int turnLevel = pc.getVariableValue("TurnLevelUndead", "").intValue();
				int turnCheck = pc.getVariableValue("TurnCheckUndead", "").intValue();

				statBuf.append("; <font class='type'>Turn/Rebuke Undead:</font> Turning level " + "<a href=" + '"'
					+ "dice:Turn Undead (Max HD Affected)\\" + "max(min(max((ceil((1d20" + (turnCheck > 0 ? "+" : "")
					+ turnCheck + ")/3)-4),-4),4)+" + turnLevel + ",0)" + '"' + " class=" + '"' + "dialog" + '"' + "> "
					+ turnLevel + "</a>, Turn Damage: " + "<a href=" + '"' + "dice:Turn Damage (Total HD Affected)\\"
					+ "max(" + turnDieNumber + "d" + turnDieSize + (turnDamage > 0 ? "+" : "") + turnDamage + ",0)"
					+ '"' + " class=" + '"' + "dialog" + '"' + "> " + turnDieNumber + "d" + turnDieSize
					+ (turnDamage > 0 ? "+" : "") + turnDamage + "</a>, " + turnTimes + "/day");
			}
			statBuf.append("; ");

			statBuf.append("<font class='type'>Vision:</font> ");
			statBuf.append(pcOut.getVision()); //|VISION|
			statBuf.append(" ");

			statBuf.append("<font class='type'>AL:</font> ");
			statBuf.append(pcOut.getAlignment()); //|ALIGNMENT.SHORT|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Sv:</font> ");
			boolean firstChk = true;
			for (PCCheck chk : Globals.getContext().getReferenceContext().getSortkeySortedCDOMObjects(PCCheck.class))
			{
				if (!firstChk)
				{
					statBuf.append(", ");
				}
				firstChk = false;
				statBuf.append(chk.getDisplayName());
				statBuf.append(" <font class='highlight'>");
				statBuf.append("<a href='save:").append(chk.getDisplayName()).append("\\");
				statBuf.append(pc.calculateSaveBonus(chk, "TOTAL")); //|CHECK.FORTITUDE.TOTAL|
				statBuf.append("' class='highlight'> ");
				statBuf.append(pc.calculateSaveBonus(chk, "TOTAL")); //|CHECK.FORTITUDE.TOTAL|
				statBuf.append("</a></font>");
			}
			statBuf.append(";<br>");

			List<PCStat> statList = new ArrayList<>(pcOut.getUnmodifiableStatList());
			statList.sort(Comparator.comparing(SortKeyRequired::getSortKey));

			for (PCStat stat : statList)
			{
				String statAbb = stat.getKeyName();
				if (display.isNonAbility(stat))
				{
					statBuf.append("<font class='type'>");
					statBuf.append(statAbb); //|STAT.%stat.NAME|
					statBuf.append("</font>");

					statBuf.append("*"); //|STAT.%stat|
					statBuf.append("&nbsp;(");
					statBuf.append("0"); //|STAT.%stat.MOD|
					statBuf.append(") ");
				}
				else
				{
					statBuf.append("<font class='type'>");
					statBuf.append(statAbb); //|STAT.%stat.NAME|
					statBuf.append("</font> ");

					statBuf.append(pcOut.getStat(stat)); //|STAT.%stat|
					statBuf.append("&nbsp;(");
					statBuf.append("<a href='check:");
					statBuf.append(statAbb); //|STAT.%stat.NAME|
					statBuf.append("\\1d20");
					statBuf.append(pcOut.getStatMod(stat)); //|STAT.%stat.MOD|
					statBuf.append("' class='dialog'>");
					statBuf.append(pcOut.getStatMod(stat)); //|STAT.%stat.MOD|
					statBuf.append("</a>) ");
				}
			}

			statBuf.append("</p>");

			return statBuf.toString();
		}

		protected String getStatBlockHeader()
		{
			StringBuilder statBuf = new StringBuilder();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<head><title>");
			statBuf.append(pcOut.getName()); //|NAME|
			statBuf.append(" - ");
			statBuf.append(display.getPlayersName()); //|PLAYERNAME|
			statBuf.append("(");
			statBuf.append(pc.getCostPool()); //|POOL.COST|
			statBuf.append(" Points) in GMGEN Statblock Format");
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

		protected String getStatBlockLinePossessions()
		{
			StringBuilder statBuf = new StringBuilder();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<p><font class='type'>Possessions:</font>&nbsp;");
			statBuf.append(pcOut.getEquipmentList());
			//|FOR.0,(COUNT[EQUIPMENT]+1),1,&nbsp;\EQ.%.QTY\&nbsp;\EQ.%.NAME\, ,COMMA,1|
			statBuf.append("</p>");

			return statBuf.toString();
		}

		String getStatBlockLineSkills()
		{
			StringBuilder statBuf = new StringBuilder();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<p><font class='type'>Skills and Feats:</font>&nbsp;");

			//force refresh of skills
			List<Skill> skillList =
					SkillDisplay.getSkillListInOutputOrder(pc, display.getPartialSkillList(View.VISIBLE_EXPORT));
			boolean firstLine = true;

			for (Skill skill : skillList)
			{
				if (!firstLine)
				{
					statBuf.append(", ");
				}

				firstLine = false;

				CDOMSingleRef<PCStat> keyStat = skill.get(ObjectKey.KEY_STAT);
				if (keyStat != null)
				{
					int modSkill = SkillModifier.modifier(skill, pc).intValue() - pc.getStatModFor(keyStat.get());
					Logging.debugPrint("modSkill: " + modSkill);
				}

				int temp = SkillModifier.modifier(skill, pc).intValue()
					+ SkillRankControl.getTotalRank(pc, skill).intValue();

				statBuf.append("<a href='skill:");
				statBuf.append(skill.getOutputName()); //|SKILL.%skill|
				statBuf.append("\\1d20");
				statBuf.append(((temp < 0) ? Integer.toString(temp) : "+" + temp)); //|SKILL.%skill.TOTAL|
				statBuf.append("' class='dialog'> ");

				statBuf.append(skill.getOutputName()); //|SKILL.%skill|
				statBuf.append(" (");
				statBuf.append(temp); //|SKILL.%skill.TOTAL|
				statBuf.append(")</a>");
			}

			statBuf.append("; ");
			statBuf.append(pcOut.getFeatList()); //|FEATLIST|
			statBuf.append("</p>");

			return statBuf.toString();
		}

		protected String getStatBlockLineSpells()
		{
			StringBuilder statBuf = new StringBuilder();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);
			if (display.hasDomains())
			{
				//Domains
				//Deity
				statBuf.append("<p>");
				statBuf.append("<font class='type'>Deity:</font>");
				statBuf.append(pcOut.getDeity());
				statBuf.append("<br>");
				statBuf.append("<font class='type'>Domains:</font>&nbsp;");

				//Domain List with powers
				boolean firstLine = true;

				for (Domain dom : display.getSortedDomainSet())
				{
					if (!firstLine)
					{
						statBuf.append(", ");
					}

					firstLine = false;

					statBuf.append(PlayerCharacterOutput.getDomainName(dom)); //|DOMAIN|
					statBuf.append(" (");
					statBuf.append(
						DescriptionFormatting.piWrapDesc(dom, pc.getDescription(dom), true)); //|DOMAIN.POWER|
					statBuf.append(")");
				}

				statBuf.append("</p>");
			}

			statBuf.append("<p>");

			ArrayList<PObject> classList = new ArrayList<>(display.getClassSet());
			classList.add(display.getRace());

			Set<String> bookList = new HashSet<>(pc.getDisplay().getSpellBookNames());
			bookList.add(Globals.getDefaultSpellBook());
			for (String book : bookList)
			{
				statBlockLineSpellBook(pc, statBuf, classList, book);
			}

			return statBuf.toString();
		}

		protected void statBlockLineSpellBook(PlayerCharacter aPC, StringBuilder statBuf, Collection<PObject> classList,
			String spellBookName)
		{
			Set<PObject> classes = new HashSet<>(classList);

			for (PObject pObj : classes)
			{
				if (pObj != null)
				{
					int maxLevel = 100;
					if (pObj instanceof PCClass)
					{
						PCClass theClass = (PCClass) pObj;
						maxLevel = (aPC.getDisplay().getLevel(theClass) == 0) ? maxLevel
							: aPC.getSpellSupport(theClass).getMaxCastLevel(aPC);
					}
					StringBuilder spellBuff = new StringBuilder();
					for (int level = 0; level <= maxLevel; level++)
					{
						List<CharacterSpell> spellList = aPC.getCharacterSpells(pObj, null, spellBookName, level);

						if (!spellList.isEmpty())
						{
							spellBuff.append("<font class='type'>Level " + level + ":</font> ");

							boolean firstLine = true;

							for (CharacterSpell cs : spellList)
							{
								if (!firstLine)
								{
									spellBuff.append(", ");
								}

								firstLine = false;

								Spell spell = cs.getSpell();
								spellBuff.append("<a href=" + '"' + "spell:");
								spellBuff.append(spell.getDisplayName());
								spellBuff.append("\\");
								spellBuff.append(aPC.parseSpellString(cs, aPC.getDescription(spell)));
								spellBuff.append("\\");
								spellBuff.append(StringUtil.join(spell.getListFor(ListKey.RANGE), ", "));
								spellBuff.append("\\");
								spellBuff.append(spell.getListAsString(ListKey.CASTTIME));
								spellBuff.append("\\");
								spellBuff
									.append(StringUtil.join(spell.getListFor(ListKey.SAVE_INFO), ", "));
								spellBuff.append("\\");
								spellBuff.append(aPC.parseSpellString(cs, spell.getListAsString(ListKey.DURATION)));
								spellBuff.append("\\");
								spellBuff.append(aPC.parseSpellString(cs, spell.getSafe(StringKey.TARGET_AREA)));
								spellBuff.append('"' + " class=" + '"' + "dialog" + '"' + ">");

								spellBuff.append(spell.getDisplayName());
								spellBuff.append("</a>");
							}
							spellBuff.append("<br>");
						}
					}
					if (spellBuff.length() > 0)
					{
						statBuf.append("<br><font class='type'>" + spellBookName + ":</font><br> ");
						statBuf.append("<font class='type'>" + pObj.getDisplayName() + ":</font><br> ");
						statBuf.append(spellBuff);
					}
				}
			}
		}

		String getStatBlockTitle()
		{
			StringBuilder statBuf = new StringBuilder();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<p class='gork'><font size='+1'><b>");
			statBuf.append(pcOut.getName()); //|NAME|
			statBuf.append(", ");
			statBuf.append(pcOut.getGender()); //|GENDER|
			statBuf.append(" ");
			statBuf.append(pcOut.getRaceName()); //|RACE|
			statBuf.append(" ");

			String region = pcOut.getRegion(); //|REGION|.|%|

			if ((region != null) && !region.isEmpty() && !"None".equals(region))
			{
				statBuf.append(" From " + region + " ");
			}

			statBuf.append(pcOut.getClasses() + " "); //|CLASSLIST|
			statBuf.append("</b></font></p>");

			return statBuf.toString();
		}
	}
}
