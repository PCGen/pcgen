/*
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
 *
 *
 */
package pcgen.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.enumeration.Region;
import pcgen.core.AbilityCategory;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.Follower;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.SkillDisplay;
import pcgen.core.utils.CoreUtility;
import pcgen.io.exporttoken.AbilityListToken;
import pcgen.io.exporttoken.AbilityToken;
import pcgen.io.exporttoken.BonusToken;
import pcgen.io.exporttoken.EqToken;
import pcgen.io.exporttoken.EqTypeToken;
import pcgen.io.exporttoken.GameModeToken;
import pcgen.io.exporttoken.MovementToken;
import pcgen.io.exporttoken.SkillToken;
import pcgen.io.exporttoken.SkillpointsToken;
import pcgen.io.exporttoken.StatToken;
import pcgen.io.exporttoken.Token;
import pcgen.io.exporttoken.TotalToken;
import pcgen.io.exporttoken.WeaponToken;
import pcgen.io.exporttoken.WeaponhToken;
import pcgen.system.PluginLoader;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.enumeration.View;

/**
 * This class deals with exporting a PC to various types of output sheets 
 * including XML, HTML, PDF and Text.
 * 
 * Very basically it takes a PC (or PCs) and replaces tokens in a character 
 * sheet template with the appropriate values from the PC (PCs).  Much of the 
 * code in here deals with replacing tokens and dealing with the FOR and IIF 
 * constructs that can be found in the character sheet templates. 
 *
 */
public abstract class ExportHandler
{
	public static ExportHandler createExportHandler(File templateFile)
	{
		if(templateFile != null && templateFile.getName().toLowerCase().endsWith(".ftl"))
		{
			return new FreeMarkerExportHandler(templateFile);
		}
		else
		{
			return new PCGenExportHandler(templateFile);
		}
	}

	/** A constant stating that we are using JEP parsing */
	private static final Float JEP_TRUE = 1.0f;

	/** A map of output tokens to export */
	private static final Map<String, Token> TOKEN_MAP = new HashMap<>();

	/** 
	 * A variable to hold the state of whether or not the output token map to
	 * be exported is populated or not. 
	 */
	private static boolean tokenMapPopulated;

	// Processing state variables

	/** TODO What is this used for? */
	boolean existsOnly;

	/** A state variable to indicate whether there are more items to process */
	boolean noMoreItems;

	/** A state variable to indicate whether the OS author controls whitespace */
	private boolean manualWhitespace;

	/** The template file to use for exporting (effectively the sheet to use) */
	private final File templateFile;

	/**
	 * These maps hold the loop variables and parameters of FOR constructs that 
	 * will be replaced by their actual values when evaluated.
	 */
	protected final Map<Object, Object> loopVariables = new HashMap<>();
	private final Map<Object, Object> loopParameters = new HashMap<>();

	/** The delimiter used by embedded DFOR/FOR loops */
	private String csheetTag2 = "\\";

	/** A state variable to indicate whether we skip processing the math */
	private boolean skipMath;

	/**
	 * A state variable to indicate whether we should write out what we are currently 
	 * processing, would be set to false for example if we were filtering some output
	 *  
	 * defaults to true.
	 */
	private boolean canWrite = true;

	/** TODO What is this used for? */
	private boolean checkBefore;


	/**
	 * Constructor.  Populates the token map (a list of possible output tokens) and 
	 * sets the character sheet template we are using.
	 *
	 * @param templateFile the template to use while exporting.
	 */
	protected ExportHandler(File templateFile)
	{
		populateTokenMap();
		this.templateFile = templateFile;
	}

	/**
	 * Replace the token, but deliberately skip the math
	 * 
	 * @param aPC The PC being exported
	 * @param aString the string which will have its tokens replaced 
	 * @param output the object that represents the sheet we are exporting
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
	 * 
	 * <br>author: Thomas Behr 12-04-02
	 *
	 * @param aPC the PlayerCharacter to write
	 * @param out the Writer to be written to
	 * @throws ExportException If the export fails.
	 */
	public abstract void write(PlayerCharacter aPC, BufferedWriter out) throws ExportException;

	/**
	 * Exports a PlayerCharacter-Party to a Writer
	 * according to the handler's template
	 * 
	 * <br>author: Thomas Behr 13-11-02
	 *
	 * @param PCs the Collection of PlayerCharacter instances which compromises the Party to write
	 * @param out the Writer to be written to
	 */
	public void write(Collection<PlayerCharacter> PCs, BufferedWriter out)
	{
		write(PCs.toArray(new PlayerCharacter[0]), out);
	}

	/**
	 * Returns the current templateFile being used
	 * @return templateFile
	 */
	public File getTemplateFile()
	{
		return templateFile;
	}

	/**
	 * Get variable value from the variable string passed in, this might be 
	 * an old style variable string (COUNT[EQ and STRLEN) or a new style 
	 * (JEP formula)
	 * 
	 * @param varString Variable string that we want to calculate value from
	 * @param aPC The PC that holds the data that we need to get the info from
	 * @return The result
	 */
	private int getVarValue(String varString, PlayerCharacter aPC)
	{
		// While COUNT[EQ tokens exist, build up a string
		String vString = processCountEquipmentTokens(varString, aPC);

		// While STRLEN[ tokens exist, build up a string
		vString = processStringLengthTokens(vString, aPC);

		// If it is the new JEP style variable then deal with that
		String valueString = vString;
		if (varString.startsWith("${") && varString.endsWith("}"))
		{
			String jepString = varString.substring(2, varString.length() - 1);
			valueString = jepString.replace(';', ',');
		}

		return aPC.getVariableValue(valueString, "").intValue();
	}

	/**
	 * Helper method for getting the variable value out of a variable string
	 * 
	 * @param vString The variable String
	 * @param aPC The PC to get the token from
	 * @return the altered variable string
	 */
	private String processCountEquipmentTokens(String vString, PlayerCharacter aPC)
	{
		int countIndex = vString.indexOf("COUNT[EQ");
		while (countIndex >= 0)
		{
			char chC = vString.charAt(countIndex + 8);

			// If the character after COUNT[EQ is . or [1-9]  
			if ((chC == '.') || ((chC >= '0') && (chC <= '9')))
			{
				final int i = vString.indexOf(']', countIndex + 8);

				if (i >= 0)
				{
					String aString = vString.substring(countIndex + 6, i);

					// Either deal with an EQTYPE or a straight EQ token
					EqToken token = null;
					if (aString.contains("EQTYPE"))
					{
						token = new EqTypeToken();
					}
					else
					{
						token = new EqToken();
					}

					String baString = token.getToken(aString, aPC, this);

					vString = vString.substring(0, countIndex) + baString + vString.substring(i + 1);
				}
			}
			countIndex = vString.indexOf("COUNT[EQ", countIndex + 1);
		}

		return vString;
	}

	/**
	 * Helper method for getting the variable value out of a variable string, 
	 * deals with STRLEN tokens
	 * 
	 * @param vString The variable string to get the values out of
	 * @param aPC The PC to get the token value out of
	 * @return The altered variable string
	 */
	private String processStringLengthTokens(String vString, PlayerCharacter aPC)
	{
		int strlenIndex = vString.indexOf("STRLEN[");
		while (strlenIndex >= 0)
		{

			final int i = vString.indexOf(']', strlenIndex + 7);

			if (i >= 0)
			{
				String aString = vString.substring(strlenIndex + 7, i);
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
					Logging.errorPrint("Error flushing outputstream in ExportHandler::getVarValue", e);
				}

				String result = sWriter.toString();
				vString = vString.substring(0, strlenIndex) + result.length() + vString.substring(i + 1);
			}
			strlenIndex = vString.indexOf("STRLEN[", strlenIndex + 1);
		}
		return vString;
	}

	/**
	 * Add to the token map, called mainly by the plugin loader
	 * 
	 * @param newToken the token to add
	 */
	public static void addToTokenMap(Token newToken)
	{
		Token test = TOKEN_MAP.put(newToken.getTokenName(), newToken);
		if (test != null)
		{
			Logging.errorPrint("More than one Output Token has the same Token Name: '" + newToken.getTokenName() + "'");
		}
	}

	public static PluginLoader getPluginLoader()
	{
		return new PluginLoader()
		{

			@Override
			public void loadPlugin(Class<?> clazz) throws Exception
			{
				Token pl = (Token) clazz.newInstance();
				addToTokenMap(pl);
			}

			@Override
			public Class[] getPluginClasses()
			{
				return new Class[]{Token.class};
			}
		};
	}

	private static class VariableComparator implements Comparator<Object>, Serializable
	{
		@Override
		public int compare(Object o1, Object o2)
		{
			final String s1 = (o1 == null ? "" : o1.toString());
			final String s2 = (o2 == null ? "" : o2.toString());

			if (s1.length() > s2.length())
			{
				return -1;
			}
			else if (s1.length() < s2.length())
			{
				return 1;
			}
			else
			{
				return s1.compareTo(s2);
			}
		}
	}

	private static String replaceVariables(String expr, Map<Object, Object> variables)
	{
		List<Object> keys = new ArrayList<>(variables.keySet());
		keys.sort(new VariableComparator());

		for (final Object anObject : keys)
		{
			if (anObject != null)
			{
				final String fString = anObject.toString();
				final String rString = variables.get(fString).toString();
				expr = expr.replaceAll(Pattern.quote(fString), rString);
			}
		}
		return expr;
	}

	/**
	 * Helper method to evaluate an expression, used by OIF and IIF tokens
	 * 
	 * @param expr Expression to evaluate
	 * @param aPC PC containing values to help evaluate the expression
	 * @return true if the expression was evaluated successfully, else false
	 */
	private boolean evaluateExpression(final String expr, final PlayerCharacter aPC)
	{
		// Deal with the AND case
		if (expr.indexOf(".AND.") > 0)
		{
			final String part1 = expr.substring(0, expr.indexOf(".AND."));
			final String part2 = expr.substring(expr.indexOf(".AND.") + 5);

			return (evaluateExpression(part1, aPC) && evaluateExpression(part2, aPC));
		}

		// Deal with the OR case
		if (expr.indexOf(".OR.") > 0)
		{
			final String part1 = expr.substring(0, expr.indexOf(".OR."));
			final String part2 = expr.substring(expr.indexOf(".OR.") + 4);

			return (evaluateExpression(part1, aPC) || evaluateExpression(part2, aPC));
		}

		/* 
		 * Deal with objects held in the loopVariables and loopParameters
		 * sets, e.g. replace the key place holder with the actual value
		 */
		String expr1 = expr;
		expr1 = replaceVariables(expr1, loopParameters);
		expr1 = replaceVariables(expr1, loopVariables);

		// Deal with HASVAR:
		if (expr1.startsWith("HASVAR:"))
		{
			expr1 = expr1.substring(7).trim();
			return (aPC.getVariableValue(expr1, "").intValue() > 0);
		}

		// Deal with HASFEAT:
		if (expr1.startsWith("HASFEAT:"))
		{
			expr1 = expr1.substring(8).trim();
			return (aPC.hasAbilityKeyed(AbilityCategory.FEAT, expr1));
		}

		// Deal with HASSA:
		if (expr1.startsWith("HASSA:"))
		{
			expr1 = expr1.substring(6).trim();
			return (aPC.hasSpecialAbility(expr1));
		}

		// Deal with HASEQUIP:
		if (expr1.startsWith("HASEQUIP:"))
		{
			expr1 = expr1.substring(9).trim();
			return (aPC.getEquipmentNamed(expr1) != null);
		}

		if (expr1.startsWith("SPELLCASTER:"))
		{
			return processSpellcasterExpression(expr1, aPC);
		}

		// Deal with EVEN:
		if (expr1.startsWith("EVEN:"))
		{
			int i = 0;

			try
			{
				i = Integer.parseInt(expr1.substring(5).trim());
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("EVEN:" + i);

				return true;
			}

			return ((i % 2) == 0);
		}

		// Deal with UNTRAINED (skills)
		if (expr1.endsWith("UNTRAINED") && !expr1.startsWith("SKILLSIT."))
		{
			final StringTokenizer aTok = new StringTokenizer(expr1, ".");
			final String fString = aTok.nextToken();
			Skill aSkill = null;

			if (fString.length() > 5)
			{
				final int i = Integer.parseInt(fString.substring(5));
				final List<Skill> pcSkills = SkillDisplay.getSkillListInOutputOrder(aPC);

				if (i <= (pcSkills.size() - 1))
				{
					aSkill = pcSkills.get(i);
				}
			}

			if (aSkill == null)
			{
				return false;
			}
			else return aSkill.getSafe(ObjectKey.USE_UNTRAINED);

		}

		// Deal with JEP formula 
		final Float res = aPC.getVariableProcessor().getJepOnlyVariableValue(null, expr1, "", 0);
		if (res != null)
		{
			return res.equals(JEP_TRUE);
		}

		/* 
		 * Deal with anything else
		 * 
		 * Before returning a default false, let's see if this is a valid token, like this:
		 *
		 * |IIF(WEAPON%weap.CATEGORY:Ranged)|
		 * something 1
		 * |ELSE|
		 * something 2
		 * |ENDIF|
		 * 
		 * It can theoretically be used with any valid token, doing an equal compare
		 * (integer or string equalities are valid)
		 * 
		 * Can now contain a token on the right side as well, so two tokens can be
		 * compared to each other. Comparison is case-insensitive.
		 */
		final StringTokenizer aTok = new StringTokenizer(expr1, ":");
		final String leftToken;
		final String rightToken;

		final int tokenCount = aTok.countTokens();
		if (tokenCount == 1)
		{
			leftToken = expr1;
			rightToken = "TRUE";
		}
		else if (tokenCount != 2)
		{
			Logging.errorPrint("evaluateExpression: Incorrect syntax (missing parameter)");
			return false;
		}
		else
		{
			leftToken = aTok.nextToken();
			rightToken = aTok.nextToken();
		}

		final StringWriter sLeftWriter = new StringWriter();
		final BufferedWriter leftWriter = new BufferedWriter(sLeftWriter);
		replaceToken(leftToken, leftWriter, aPC);
		sLeftWriter.flush();

		final StringWriter sRightWriter = new StringWriter();
		final BufferedWriter rightWriter = new BufferedWriter(sRightWriter);
		replaceToken(rightToken, rightWriter, aPC);
		sRightWriter.flush();

		// Try to flush the output writer
		try
		{
			leftWriter.flush();
			rightWriter.flush();
		}
		catch (IOException ignore)
		{
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Could not flush output buffer in evaluateExpression", ignore);
			}
		}

		String leftString = sLeftWriter.toString();
		if (leftToken.startsWith("VAR."))
		{
			leftString = aPC.getVariableValue(leftToken.substring(4), "").toString();
		}

		String rightString = sRightWriter.toString();
		if (rightToken.startsWith("VAR."))
		{
			rightString = aPC.getVariableValue(rightToken.substring(4), "").toString();
		}

		try
		{
			// integer values
			final int left = Integer.parseInt(leftString);
			final int right = Integer.parseInt(rightString);
			return left == right;
		}
		catch (NumberFormatException e)
		{
			// String values
			// if right string starts with =, test exact match, otherwise test substring match
			if (rightString.startsWith("="))
			{
				return leftString.equals(rightString.substring(1));
			}
			return leftString.toUpperCase().contains(rightString.toUpperCase());
		}
	}

	/**
	 * Deal with SPELLCASTER.
	 *  
	 * Could look like one of the following:
	 * 
	 * Arcane
	 * Chaos
	 * Divine
	 * EleMage
	 * Psionic
	 * Wizard
	 * Prepare
	 * !Prepare
	 * 0=Wizard    (%classNum=className)
	 * 0=Divine    (%classNum=spell_type)
	 * 0=Prepare   (%classNum=preparation_type)
	 *
	 * @param expr1 Expression to evaluate
	 * @param aPC PC containing values to help evaluate the expression
	 * @return true if the expression was evaluated successfully, else false
	 */
	private static boolean processSpellcasterExpression(String expr1, PlayerCharacter aPC)
	{
		final String fString = expr1.substring(12).trim();

		// If the SPELLCASTER expression has an '=' sign
		if (fString.indexOf('=') != -1)
		{
			final StringTokenizer aTok = new StringTokenizer(fString, "=", false);
			final int i = Integer.parseInt(aTok.nextToken());
			final String cs = aTok.nextToken();
			final List<PCClass> cList = aPC.getClassList();

			if (i >= cList.size())
			{
				return false;
			}

			final PCClass aClass = cList.get(i);

			if (cs.equalsIgnoreCase(aClass.getSpellType()))
			{
				return true;
			}

			if (cs.equalsIgnoreCase(aClass.getKeyName()))
			{
				return true;
			}

			if ("!Prepare".equalsIgnoreCase(cs) && aClass.getSafe(ObjectKey.MEMORIZE_SPELLS))
			{
				return true;
			}

			if ("Prepare".equalsIgnoreCase(cs) && (!aClass.getSafe(ObjectKey.MEMORIZE_SPELLS)))
			{
				return true;
			}
		}
		else
		{
			for (final PCClass pcClass : aPC.getClassSet())
			{
				if (fString.equalsIgnoreCase(pcClass.getSpellType()))
				{
					return true;
				}

				if (fString.equalsIgnoreCase(pcClass.getKeyName()))
				{
					return true;
				}

				if ("!Prepare".equalsIgnoreCase(fString) && pcClass.getSafe(ObjectKey.MEMORIZE_SPELLS))
				{
					return true;
				}

				if ("Prepare".equalsIgnoreCase(fString) && (!pcClass.getSafe(ObjectKey.MEMORIZE_SPELLS)))
				{
					return true;
				}
			}
		}
		Logging.errorPrint("Should have exited before this in ExportHandler::processSpellcasterExpression");
		return false;
	}

	/**
	 * Helper method to evaluate a IIF token
	 * 
	 * @param node The IIFNode to evaluate
	 * @param output The output to write to (character sheet template)
	 * @param aPC The PC we are outputting
	 */
	private void evaluateIIF(final IIFNode node, final BufferedWriter output, final PlayerCharacter aPC)
	{
		// Comma is a delimiter for a higher-level parser, so 
		// we'll use a semicolon and replace it with a comma for
		// expressions like:
		// |IIF(VAR.IF(var("COUNT[SKILLTYPE=Strength]")>0;1;0):1)|
		final String aString = node.expr().replaceAll(Pattern.quote(";"), ",");

		// If we can evaluate the expression then evaluate its children
		if (evaluateExpression(aString, aPC))
		{
			evaluateIIFChildren(node.trueChildren(), output, aPC);
		}
		else
		{
			evaluateIIFChildren(node.falseChildren(), output, aPC);
		}
	}

	/**
	 * Helper method to evaluate the results of a IIF child node
	 * 
	 * @param children The list of children for the IIF node
	 * @param output The output to write to (filling in the character sheet template)
	 * @param aPC THe PC to output
	 */
	private void evaluateIIFChildren(final List<?> children, final BufferedWriter output, final PlayerCharacter aPC)
	{
		for (Object aChild : children)
		{
			if (aChild instanceof FORNode)
			{
				// If the child is a FORNode then put it in the loopVariables map as 
				// a key with a corresponding value of 0
				final FORNode nextFor = (FORNode) aChild;
				loopVariables.put(nextFor.var(), 0);
				existsOnly = nextFor.exists();

				String minString = nextFor.min();
				String maxString = nextFor.max();
				String stepString = nextFor.step();

				// Go through the list of objects in the loopVariables and loopParameters
				// sets and set the values in place of keys for min, max and step
				minString = replaceVariables(minString, loopParameters);
				minString = replaceVariables(minString, loopVariables);
				maxString = replaceVariables(maxString, loopParameters);
				maxString = replaceVariables(maxString, loopVariables);
				stepString = replaceVariables(stepString, loopParameters);
				stepString = replaceVariables(stepString, loopVariables);

				int minValue = getVarValue(minString, aPC);
				int maxValue = getVarValue(maxString, aPC);
				int stepValue = getVarValue(stepString, aPC);
				String var = nextFor.var();
				loopParameters.put(var + "!MIN", minValue);
				loopParameters.put(var + "!MAX", maxValue);
				loopParameters.put(var + "!STEP", stepValue);

				loopFOR(nextFor, minValue, maxValue, stepValue, output, aPC);
				loopParameters.remove(var + "!MIN");
				loopParameters.remove(var + "!MAX");
				loopParameters.remove(var + "!STEP");

				existsOnly = nextFor.exists();
				loopVariables.remove(nextFor.var());
			}
			// If child is an IIFNode, then evaluate that 
			else if (aChild instanceof IIFNode)
			{
				evaluateIIF((IIFNode) aChild, output, aPC);
			}
			// Else it's something to be processed
			else
			{
				String lineString = (String) aChild;
				lineString = replaceVariables(lineString, loopParameters);
				lineString = replaceVariables(lineString, loopVariables);

				replaceLine(lineString, output, aPC);

				// Each time we replace a line that is part of an IIF statement
				// we output a newline if we are allowed to write and the 
				// whitespace is not controlled by the OS author
				if (canWrite && !manualWhitespace)
				{
					FileAccess.newLine(output);
				}
			}
		}
	}

	/**
	 * Loop through a set of output as required by a FOR loop.
	 * 
	 * @param node The node being processed
	 * @param start The starting value of the loop
	 * @param end The ending value of the loop
	 * @param step The amount by which the counter should be changed each iteration.
	 * @param output The writer output is to be sent to.
	 * @param aPC The character being processed.
	 */
	protected void loopFOR(final FORNode node, final int start, final int end, final int step,
						   final BufferedWriter output, final PlayerCharacter aPC)
	{
		for (int x = start; ((step < 0) ? x >= end : x <= end); x += step)
		{
			if (processLoop(node, output, aPC, x))
			{
				break;
			}
		}
	}

	/**
	 * Process an iteration of a FOR loop.
	 * 
	 * @param node The node being processed
	 * @param output The writer output is to be sent to.
	 * @param aPC The character being processed.
	 * @param index The current value of the loop index
	 * @return true if the loop should be stopped.
	 */
	private boolean processLoop(FORNode node, BufferedWriter output, PlayerCharacter aPC, int index)
	{
		loopVariables.put(node.var(), index);
		int numberOfChildrenNodes = node.children().size();
		for (int y = 0; y < numberOfChildrenNodes; ++y)
		{
			if (node.children().get(y) instanceof FORNode)
			{
				FORNode nextFor = (FORNode) node.children().get(y);
				loopVariables.put(nextFor.var(), 0);
				existsOnly = nextFor.exists();

				String minString = nextFor.min();
				String maxString = nextFor.max();
				String stepString = nextFor.step();

				minString = replaceVariables(minString, loopParameters);
				minString = replaceVariables(minString, loopVariables);
				maxString = replaceVariables(maxString, loopParameters);
				maxString = replaceVariables(maxString, loopVariables);
				stepString = replaceVariables(stepString, loopParameters);
				stepString = replaceVariables(stepString, loopVariables);

				final int varMin = getVarValue(minString, aPC);
				final int varMax = getVarValue(maxString, aPC);
				final int varStep = getVarValue(stepString, aPC);
				String var = nextFor.var();
				loopParameters.put(var + "!MIN", varMin);
				loopParameters.put(var + "!MAX", varMax);
				loopParameters.put(var + "!STEP", varMax);

				loopFOR(nextFor, varMin, varMax, varStep, output, aPC);
				loopParameters.remove(var + "!MIN");
				loopParameters.remove(var + "!MAX");
				loopParameters.remove(var + "!STEP");

				existsOnly = node.exists();
				loopVariables.remove(nextFor.var());
			}
			else if (node.children().get(y) instanceof IIFNode)
			{
				evaluateIIF((IIFNode) node.children().get(y), output, aPC);
			}
			else
			{
				String lineString = (String) node.children().get(y);
				lineString = replaceVariables(lineString, loopParameters);
				lineString = replaceVariables(lineString, loopVariables);

				noMoreItems = false;
				replaceLine(lineString, output, aPC);

				// If the output sheet author has no control 
				// over the whitespace then print a newline.
				if (canWrite && !manualWhitespace)
				{
					FileAccess.newLine(output);
				}

				// break out of loop if no more items
				if (existsOnly && noMoreItems)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Math Mode - Most of the code logic was copied from PlayerCharacter.getVariableValue
	 * included a treatment for math with attack routines (for example +6/+1 - 2 = +4/-1)
	 *
	 * @param aString The string to be converted
	 * @param aPC the PC being exported
	 * @return String
	 */
	private String mathMode(String aString, PlayerCharacter aPC)
	{
		String str = aString;

		// Deal with Knowledge () type tokens
		str = processBracketedTokens(str, aPC);

		// Replace all square brackets with curved ones
		str = str.replaceAll(Pattern.quote("["), "(");
		str = str.replaceAll(Pattern.quote("]"), ")");

		// A list of mathematical delimiters
		final String delimiter = "+-/*";
		String valString = "";
		final int ADDITION_MODE = 0;
		final int SUBTRACTION_MODE = 1;
		final int MULTIPLICATION_MODE = 2;
		final int DIVISION_MODE = 3;
		// Mode is addition mode by default
		int mode = ADDITION_MODE;

		int nextMode = 0;
		final int REGULAR_MODE = 0;
		final int INTVAL_MODE = 1;
		final int SIGN_MODE = 2;
		final int NO_ZERO_MODE = 3;
		int endMode = REGULAR_MODE;
		boolean attackRoutine = false;
		String attackData = "";

		Float total = 0.0f;
		for (int i = 0; i < str.length(); ++i)
		{
			valString += str.substring(i, i + 1);

			if ((i == (str.length() - 1))
				|| ((delimiter.lastIndexOf(str.charAt(i)) > -1) && (i > 0) && (str.charAt(i - 1) != '.')))
			{
				if (delimiter.lastIndexOf(str.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}

				{
					// Deal with .TRUNC
					if (valString.endsWith(".TRUNC"))
					{
						if (attackRoutine)
						{
							Logging.errorPrint("Math Mode Error: Not allowed to use .TRUNC in Attack Mode.");
						}
						else
						{
							valString = String.valueOf(Float
								.valueOf(mathMode(valString.substring(0, valString.length() - 6), aPC)).intValue());
						}
					}

					// Deal with .INTVAL
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

					// Deal with .SIGN
					if (valString.endsWith(".SIGN"))
					{
						valString = mathMode(valString.substring(0, valString.length() - 5), aPC);
						endMode = SIGN_MODE;
					}

					// Deal with .NOZERO
					if (valString.endsWith(".NOZERO"))
					{
						valString = mathMode(valString.substring(0, valString.length() - 7), aPC);
						endMode = NO_ZERO_MODE;
					}

					// Set the next mode based on the mathematical sign
					if ((!str.isEmpty()) && (str.charAt(i) == '+'))
					{
						nextMode = ADDITION_MODE;
					}
					else if ((!str.isEmpty()) && (str.charAt(i) == '-'))
					{
						nextMode = SUBTRACTION_MODE;
					}
					else if ((!str.isEmpty()) && (str.charAt(i) == '*'))
					{
						nextMode = MULTIPLICATION_MODE;
					}
					else if ((!str.isEmpty()) && (str.charAt(i) == '/'))
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
						Logging.errorPrint("Failed to flush oputput in MathMode.", e);
					}

					final String bString = sWriter.toString();

					try
					{
						// Float values
						DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
						DecimalFormat decimalFormat = new DecimalFormat("#,##0.##", decimalFormatSymbols);
						valString = String.valueOf(decimalFormat.parse(bString));
					}
					catch (ParseException e)
					{
						// String values
						valString = bString;
					}

					if ((!attackRoutine) && Pattern.matches("^([-+]\\d+/)*[-+]\\d+$", valString))
					{
						attackRoutine = true;
						attackData = valString;
						valString = "";
					}
				}

				try
				{
					if (!valString.isEmpty())
					{
						if (attackRoutine)
						{
							StringTokenizer bTok = new StringTokenizer(attackData, "/");

							if (bTok.countTokens() > 0)
							{
								StringBuilder newAttackData = new StringBuilder();
								while (bTok.hasMoreTokens())
								{
									final String bString = bTok.nextToken();

									float bf = Float.parseFloat(bString);
									float vf = Float.parseFloat(valString);
									switch (mode)
									{
										case ADDITION_MODE:
											float addf = bf + vf;
											newAttackData.append("/+").append(Integer.toString((int) addf));

											break;

										case SUBTRACTION_MODE:
											float subf = bf - vf;
											newAttackData.append("/+").append(Integer.toString((int) subf));

											break;

										case MULTIPLICATION_MODE:
											float multf = bf * vf;
											newAttackData.append("/+").append(Integer.toString((int) multf));

											break;

										case DIVISION_MODE:
											float divf = bf / vf;
											newAttackData.append("/+").append(Integer.toString((int) divf));

											break;

										default:
											Logging.errorPrint("In mathMode the mode " + mode + " is unsupported.");

											break;
									}
								}

								attackData = newAttackData.substring(1).replaceAll(Pattern.quote("+-"), "-");
							}
						}
						else
						{
							switch (mode)
							{
								case ADDITION_MODE:
									total = (float) (total.doubleValue() + Double.parseDouble(valString));

									break;

								case SUBTRACTION_MODE:
									total = (float) (total.doubleValue() - Double.parseDouble(valString));

									break;

								case MULTIPLICATION_MODE:
									total = (float) (total.doubleValue() * Double.parseDouble(valString));

									break;

								case DIVISION_MODE:
									total = (float) (total.doubleValue() / Double.parseDouble(valString));

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
					StringWriter sWriter = new StringWriter();
					BufferedWriter aWriter = new BufferedWriter(sWriter);
					replaceTokenSkipMath(aPC, str, aWriter);
					sWriter.flush();

					try
					{
						aWriter.flush();
					}
					catch (IOException e)
					{
						Logging.errorPrint("Math Mode Error: Could not flush output.");
					}

					return sWriter.toString();
				}

				mode = nextMode;
				// Set the nextMode back to the default
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
			return Delta.toString(total.intValue());
		}

		if (endMode == NO_ZERO_MODE)
		{
			final int totalIntValue = total.intValue();
			if (totalIntValue == 0)
			{
				return "";
			}

			return Delta.toString(totalIntValue);
		}

		return total.toString();
	}

	/**
	 * Helper method to process the math for Knowledge (xx) types of tokens
	 * 
	 * @param str String to process
	 * @param aPC PC we are exporting
	 * @return Processed string
	 */
	private String processBracketedTokens(String str, PlayerCharacter aPC)
	{
		while (str.lastIndexOf('(') != -1)
		{
			int x = CoreUtility.innerMostStringStart(str);
			int y = CoreUtility.innerMostStringEnd(str);

			// If the end is before the start we have a problem
			if (y < x)
			{
				// This was breaking some homebrew sheets. [Felipe - 13-may-03]
				Logging.debugPrint(
					"End is before start for string processing.  We are skipping the processing of this item.");
				break;
			}

			String bString = str.substring(x + 1, y);

			// This will treat Knowledge (xx) kind of token
			if ((x > 0) && (str.charAt(x - 1) == ' ') && ((str.charAt(y + 1) == '.') || (y == (str.length() - 1))))
			{
				str = str.substring(0, x) + "[" + bString + "]" + str.substring(y + 1);
			}
			else
			{
				str = str.substring(0, x) + mathMode(bString, aPC) + str.substring(y + 1);
			}
		}
		return str;
	}

	/**
	 * Helper class to output normal text
	 * 
	 * @param nonToken
	 * @param output
	 */
	private void outputNonToken(String nonToken, java.io.Writer output)
	{
		// Do nothing if something shouldn't be output.
		if (canWrite && !nonToken.isEmpty())
		{
			String finalToken = null;
			// If we have manual white space then remove an tab characters
			if (manualWhitespace)
			{
				finalToken = nonToken.replaceAll("[ \\t]", "");
			}
			else
			{
				finalToken = nonToken;
			}

			FileAccess.write(output, finalToken);
		}
	}

	/**
	 * Helper method to parse |FOR tokens (pre-processing for a template)
	 * 
	 * @param forLine
	 * @param tokens
	 * @return A FORNode of the parsed tokens
	 */
	protected FORNode parseFORs(String forLine, StringTokenizer tokens)
	{
		final List<String> forVars = getParameters(forLine);
		final String var = forVars.get(1);
		final String min = forVars.get(2);
		final String max = forVars.get(3);
		final String step = forVars.get(4);
		final String eTest = forVars.get(5);
		boolean exists = false;

		if (((!eTest.isEmpty()) && (eTest.charAt(0) == '1')) || ((!eTest.isEmpty()) && (eTest.charAt(0) == '2')))
		{
			exists = true;
		}

		final FORNode node = new FORNode(var, min, max, step, exists);

		while (tokens.hasMoreTokens())
		{
			final String line = tokens.nextToken();

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
			else if (line.startsWith("|IIF(") && (line.lastIndexOf(',') == -1))
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

	/**
	 * Retrieve the parameters of a comma separated command such as a 
	 * FOR token. Commas inside brackets are ignored, thus allowing JEP 
	 * functions with multiple parameters to be included in FOR loops.
	 *  
	 * @param forToken The token to be broken up. 
	 * @return The token parameters.
	 */
	public static List<String> getParameters(String forToken)
	{
		String[] splitStr = forToken.split(",");
		List<String> result = new ArrayList<>();
		StringBuilder buf = new StringBuilder();
		boolean inFormula = false;
		for (String string : splitStr)
		{
			if (string.contains("(") && (string.indexOf(')') < string.indexOf('(')))
			{
				inFormula = true;
				buf.append(string);
			}
			else if (inFormula && string.contains(")"))
			{
				inFormula = false;
				buf.append(",");
				buf.append(string);
				result.add(buf.toString());
				buf = new StringBuilder();
			}
			else if (inFormula)
			{
				buf.append(",");
				buf.append(string);
			}
			else
			{
				result.add(string);
			}
		}
		return result;
	}

	/**
	 * Helper method to parse the IIF tokens, includes dealing with a 
	 * |FOR child, |IIF child, ELSE, END IF and plain text
	 * 
	 * @param expr
	 * @param tokens
	 * @return IIFNode representing the parsed tokens
	 */
	protected IIFNode parseIIFs(String expr, StringTokenizer tokens)
	{
		final IIFNode node = new IIFNode(expr);

		// Flag to indicate whether we are adding the 
		// true case (e.g.  The IF) or the false case 
		// (e.g.  The ELSE)
		boolean trueCase = true;

		while (tokens.hasMoreTokens())
		{
			final String line = tokens.nextToken();

			// It's a |FOR child
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				newFor.nextToken();
				// It's the first type of |FOR, e.g.  With a variable name, 
				// see PCGen docs for |FOR token
				if (newFor.nextToken().startsWith("%"))
				{
					if (trueCase)
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
					if (trueCase)
					{
						node.addTrueChild(line);
					}
					else
					{
						node.addFalseChild(line);
					}
				}
			}
			// It's a child IIF, make a recursive call
			else if (line.startsWith("|IIF(") && (line.lastIndexOf(',') == -1))
			{
				String newExpr = line.substring(5, line.lastIndexOf(')'));
				if (trueCase)
				{
					node.addTrueChild(parseIIFs(newExpr, tokens));
				}
				else
				{
					node.addFalseChild(parseIIFs(newExpr, tokens));
				}
			}
			// Set the flag so that the false case is added next
			else if (line.startsWith("|ELSE|"))
			{
				trueCase = false;
			}
			// We're done, so exit
			else if (line.startsWith("|ENDIF|"))
			{
				return node;
			}
			else
			{
				if (trueCase)
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

	/**
	 * Populate the token map (if not already done so), e.g. Add all 
	 * of the types of Output Tokens to the map
	 */
	private static void populateTokenMap()
	{
		if (!tokenMapPopulated)
		{
			addToTokenMap(new AbilityToken());
			addToTokenMap(new AbilityListToken());
			addToTokenMap(new BonusToken());
			addToTokenMap(new EqToken());
			addToTokenMap(new EqTypeToken());
			addToTokenMap(new GameModeToken());
			addToTokenMap(new MovementToken());
			addToTokenMap(new SkillToken());
			addToTokenMap(new SkillpointsToken());
			addToTokenMap(new StatToken());
			addToTokenMap(new TotalToken());
			addToTokenMap(new WeaponToken());
			addToTokenMap(new WeaponhToken());
			tokenMapPopulated = true;
		}
	}

	/**
	 * This method performs some work on a given character sheet template line, 
	 * namely replacing tokens, dealing with Malformed lines and simply outputting 
	 * plain text.
	 *  
	 * @param aLine The line to do the work on
	 * @param output The output buffer that is effectively the character sheet template
	 * @param aPC The PC that we are outputting
	 */
	private void replaceLine(String aLine, BufferedWriter output, PlayerCharacter aPC)
	{
		// Find the last index of the | character
		int lastIndex = aLine.lastIndexOf('|');

		// If there are no pipes and it's a non empty string, just output the fixed text
		if (lastIndex < 0 && !aLine.isEmpty())
		{
			outputNonToken(aLine, output);
		}

		/*
		 * When the line starts with a pipe and that pipe is the only
		 * one on the line, this operation ignores the line.  This is
		 * because the token is malformed.  Malformed because it should be
		 * between pipes.
		 */
		if (lastIndex >= 1)
		{
			final StringTokenizer aTok = new StringTokenizer(aLine, "|", false);

			boolean inPipe = false;
			if (aLine.charAt(0) == '|')
			{
				inPipe = true;
			}

			boolean lastIsPipe = false;
			if (aLine.charAt(aLine.length() - 1) == '|')
			{
				lastIsPipe = true;
			}

			while (aTok.hasMoreTokens())
			{
				String tok = aTok.nextToken();

				if (inPipe)
				{
					if (aTok.hasMoreTokens() || lastIsPipe)
					{
						replaceToken(tok, output, aPC);
					}
					/*
					 * No else condition because we should be between
					 * pipes at this point i.e. this should be a token but
					 * it appears to be malformed.  Malformed because there
					 * are no more tokens and the last character of the string
					 * is not a pipe
					 */
				}
				else
				{
					outputNonToken(tok, output);
				}
				// Reverse the inPipe state, causing the next token to 
				// take the other decision path
				if (aTok.hasMoreTokens())
				{
					inPipe = !inPipe;
				}
			}
		}
	}

	/**
	 * Replace the token with the value it represents
	 * 
	 * @param aString The string containing the token to be replaced
	 * @param output The object that will capture the output
	 * @param aPC The PC currently being exported
	 * @return value
	 */
	public int replaceToken(String aString, BufferedWriter output, PlayerCharacter aPC)
	{
		try
		{
			// If it is plain text then there's no replacement necessary
			if (isPlainText(aString))
			{
				return 0;
			}

			// If it is purely a filter everything (not a filter on a specific token)
			// then there is nothing to replace so return 0
			if ("%".equals(aString))
			{
				canWrite = true;
				return 0;
			}

			// If the line starts with ${ and ends with } then write the JEP variable
			// and return the length of the line (minus any whitespace)
			if (aString.startsWith("${") && aString.endsWith("}"))
			{
				String jepString = aString.substring(2, aString.length() - 1);
				String variableValue = aPC.getVariableValue(jepString, "").toString();
				FileAccess.write(output, variableValue);
				return aString.trim().length();
			}

			// TODO Why?
			FileAccess.maxLength(-1);

			// Start the |%blah| token section, e.g. Deal with filtering tokens
			// (e.g.  If it doesn't meet a criteria then don't write)
			// If the string is a non empty filter and does not have a '<' or a '>' in it then replace the token
			if (isFilterToken(aString))
			{
				return dealWithFilteredTokens(aString, aPC);
			}

			String tokenString = aString;

			// now check for max length tokens
			// e.g: |SUB10.ARMOR.AC|
			if (isValidSubToken(tokenString))
			{
				tokenString = replaceSubToken(tokenString);
			}

			// Now check for the rest of the tokens
			populateTokenMap();

			StringTokenizer tok = new StringTokenizer(tokenString, ".,", false);
			String firstToken = tok.nextToken();

			// Get the remaining token/test string 
			// TODO Understand this
			String testString = tokenString;
			if (testString.indexOf(',') > -1)
			{
				testString = testString.substring(0, testString.indexOf(','));
			}
			if (testString.indexOf('~') > -1)
			{
				testString = testString.substring(0, testString.indexOf('~'));
			}

			int len = 1;

			// Deal with FOR/DFOR token
			if (isForOrDForToken(tokenString))
			{
				processLoopToken(tokenString, output, aPC);
				return 0;
			}
			// Deal with OIF token
			else if (tokenString.startsWith("OIF("))
			{
				replaceTokenOIF(tokenString, output, aPC);
			}
			// Deal with mathematical tokenLeave
			else if (containsMathematicalToken(testString) && (!skipMath))
			{
				FileAccess.maxLength(-1);
				FileAccess.write(output, mathMode(tokenString, aPC));
				return 0;
			}
			// Deal with CSHEETTAG2.
			else if (tokenString.startsWith("CSHEETTAG2."))
			{
				csheetTag2 = tokenString.substring(11, 12);
				FileAccess.maxLength(-1);
				return 0;
			}
			// Else if the token is in the list of valid output tokens
			else if (TOKEN_MAP.get(firstToken) != null)
			{
				Token token = TOKEN_MAP.get(firstToken);
				if (token.isEncoded())
				{
					FileAccess.encodeWrite(output, token.getToken(tokenString, aPC, this));
				}
				else
				{
					FileAccess.write(output, token.getToken(tokenString, aPC, this));
				}
			}
			// Default case
			else
			{
				len = tokenString.trim().length();

				if (manualWhitespace)
				{
					tokenString = tokenString.replaceAll("[ \\t]", "");
					if (len > 0)
					{
						FileAccess.write(output, tokenString);
					}
				}
				else
				{
					FileAccess.write(output, tokenString);
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
	 * Helper method to determine if a line of text needs replacing or not
	 * 
	 * @param aString
	 * @return true If it is plain text (e.g. Does not need replacing)
	 */
	private boolean isPlainText(String aString)
	{
		// If we 'cannot write' and the string is non-empty, non-filter token then 
		// there is nothing to replace so return 0
		if (!canWrite && (!aString.isEmpty()) && (aString.charAt(0) != '%'))
		{
			return true;
		}
		return aString.isEmpty();
	}

	/**
	 * Helper method to determine if a token is a filter token or not
	 * 
	 * @param aString token to evaluate
	 * @return true if it is a filter token
	 */
	private boolean isFilterToken(String aString)
	{
		return (!aString.isEmpty()) && (aString.charAt(0) == '%') && (aString.length() > 1)
			&& (aString.lastIndexOf('<') == -1) && (aString.lastIndexOf('>') == -1);
	}

	/**
	 * Helper method, determines if a token is a valid SUB token
	 * 
	 * @param tokenString token to evaluate
	 * @return true if it is a valid SUB token
	 */
	private boolean isValidSubToken(String tokenString)
	{
		return tokenString.indexOf("SUB") == 0 && (tokenString.indexOf(".") > 3);
	}

	/**
	 * Helper method to detect if a token is a DFOR or FOR token
	 * 
	 * @param tokenString token to check
	 * @return true if it is a DFOR or FOR token 
	 */
	private boolean isForOrDForToken(String tokenString)
	{
		return tokenString.startsWith("FOR.") || tokenString.startsWith("DFOR.");
	}

	/**
	 * Helper method to determine if a string contains a mathematical token
	 * 
	 * @param testString String to test
	 * @return true if it 
	 */
	private boolean containsMathematicalToken(String testString)
	{
		return (testString.indexOf('+') >= 0) || (testString.indexOf('-') >= 0) || (testString.contains(".INTVAL"))
			|| (testString.contains(".SIGN")) || (testString.contains(".NOZERO")) || (testString.contains(".TRUNC"))
			|| (testString.indexOf('*') >= 0) || (testString.indexOf('/') >= 0);
	}

	/**
	 * Helper method, deals with replacing the SUB token
	 * 
	 * @param tokenString the SUB token
	 * @return The altered SUB token
	 */
	private String replaceSubToken(String tokenString)
	{
		int iEnd = tokenString.indexOf('.');
		int maxLength;

		try
		{
			maxLength = Integer.parseInt(tokenString.substring(3, iEnd));
		}
		catch (NumberFormatException ex)
		{
			// Hmm, no number?
			Logging.errorPrint("Number format error: " + tokenString);
			maxLength = -1;
		}

		if (maxLength > 0)
		{
			tokenString = tokenString.substring(iEnd + 1);
			FileAccess.maxLength(maxLength);
		}

		return tokenString;
	}

	/**
	 * Helper method that deals with Processing the FOR./DFOR. tokens as a 
	 * DFOR loop
	 * 
	 * @param tokenString the token to loop over
	 * @param output The writer we write to
	 * @param aPC The PC we are exporting
	 */
	private void processLoopToken(String tokenString, BufferedWriter output, PlayerCharacter aPC)
	{
		FileAccess.maxLength(-1);

		existsOnly = false;
		noMoreItems = false;
		checkBefore = false;

		replaceTokenForDfor(tokenString, output, aPC);

		existsOnly = false;
		noMoreItems = false;
	}

	/**
	 * Helper method for replaceToken, deals with the filter tokens e.g. %DOMAIN, basically 
	 * returns 0 if we should not be writing something out, e.g. It's filtered out
	 * 
	 * @param aString
	 * @param aPC
	 * @return 0 If we should not be writing something out 
	 */
	private int dealWithFilteredTokens(String aString, PlayerCharacter aPC)
	{
		// Start by stating that we are allowed to write
		canWrite = true;

		// Get the merge strategy for equipment for later use
		int merge = getEquipmentMergingStrategy(aString);

		// Filter out on GAMEMODE
		if (aString.substring(1).startsWith("GAMEMODE:"))
		{
			if (aString.substring(10).endsWith(GameModeToken.getGameModeToken()))
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out REGION
		CharacterDisplay display = aPC.getDisplay();
		if ("REGION".equals(aString.substring(1)))
		{
			if (display.getRegion().isPresent()
				&& display.getRegion().get().equals(Region.NONE))
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out NOTES
		if ("NOTES".equals(aString.substring(1)))
		{
			if (aPC.getDisplay().getNotesCount() <= 0)
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out SKILLPOINTS
		if ("SKILLPOINTS".equals(aString.substring(1)))
		{
			if (SkillpointsToken.getUnusedSkillPoints(aPC) == 0)
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out TEMPLATE
		if (aString.substring(1).startsWith("TEMPLATE"))
		{
			// New token syntax |%TEMPLATE.x| instead of |%TEMPLATEx|
			final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
			final List<PCTemplate> tList = new ArrayList<>(aPC.getTemplateSet());
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

			final PCTemplate template = tList.get(index);
			if (!template.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_EXPORT))
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out FOLLOWER
		if ("FOLLOWER".equals(aString.substring(1)))
		{
			if (!aPC.hasFollowers())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out FOLLOWEROF
		if ("FOLLOWEROF".equals(aString.substring(1)))
		{
			if (aPC.getMasterPC() == null)
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out FOLLOWERTYPE.
		if (aString.substring(1).startsWith("FOLLOWERTYPE."))
		{
			List<Follower> aList = new ArrayList<>();

			for (Follower follower : aPC.getFollowerList())
			{
				// only allow followers that currently loaded
				// Otherwise the stats a zero
				for (PlayerCharacter pc : Globals.getPCList())
				{
					if (pc.getFileName().equals(follower.getFileName()))
					{
						aList.add(follower);
					}
				}
			}

			StringTokenizer aTok = new StringTokenizer(aString, ".");
			aTok.nextToken(); // FOLLOWERTYPE

			String typeString = aTok.nextToken();

			for (int i = aList.size() - 1; i >= 0; --i)
			{
				final Follower fol = aList.get(i);

				if (!fol.getType().getKeyName().equalsIgnoreCase(typeString))
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

		// Filter out PROHIBITEDLIST
		if ("PROHIBITEDLIST".equals(aString.substring(1)))
		{
			for (PCClass pcClass : aPC.getClassSet())
			{
				if (aPC.getLevel(pcClass) > 0)
				{
					if (pcClass.containsListFor(ListKey.PROHIBITED_SPELLS) || aPC.containsProhibitedSchools(pcClass))
					{
						return 0;
					}
				}
			}

			canWrite = false;

			return 0;
		}

		// Filter out CATCHPHRASE
		if ("CATCHPHRASE".equals(aString.substring(1)))
		{
			String catchPhrase = display.getCatchPhrase();
			if (catchPhrase.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (catchPhrase.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out LOCATION
		if ("LOCATION".equals(aString.substring(1)))
		{
			String location = display.getLocation();
			if (location.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (location.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out RESIDENCE
		if ("RESIDENCE".equals(aString.substring(1)))
		{
			String residence = aPC.getSafeStringFor(PCStringKey.RESIDENCE);
			if (residence.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (residence.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out PHOBIAS
		if ("PHOBIAS".equals(aString.substring(1)))
		{
			String phobias = display.getSafeStringFor(PCStringKey.PHOBIAS);
			if (phobias.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (phobias.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out INTERESTS
		if ("INTERESTS".equals(aString.substring(1)))
		{
			String interests = display.getInterests();
			if (interests.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (interests.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out SPEECHTENDENCY
		if ("SPEECHTENDENCY".equals(aString.substring(1)))
		{
			String speechTendency = display.getSpeechTendency();
			if (speechTendency.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (speechTendency.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out PERSONALITY1
		if ("PERSONALITY1".equals(aString.substring(1)))
		{
			String trait1 = display.getTrait1();
			if (trait1.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (trait1.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out PERSONALITY2
		if ("PERSONALITY2".equals(aString.substring(1)))
		{
			String trait2 = display.getTrait2();
			if (trait2.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (trait2.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out MISC.FUNDS
		if ("MISC.FUNDS".equals(aString.substring(1)))
		{
			if (aPC.getSafeStringFor(PCStringKey.ASSETS).equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if ((aPC.getSafeStringFor(PCStringKey.ASSETS)).trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out MISC.COMPANIONS and COMPANIONS
		if ("COMPANIONS".equals(aString.substring(1)) || "MISC.COMPANIONS".equals(aString.substring(1)))
		{
			if (aPC.getSafeStringFor(PCStringKey.COMPANIONS).equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (aPC.getSafeStringFor(PCStringKey.COMPANIONS).trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out MISC.MAGIC
		if ("MISC.MAGIC".equals(aString.substring(1)))
		{
			if (aPC.getSafeStringFor(PCStringKey.MAGIC).equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (aPC.getSafeStringFor(PCStringKey.MAGIC).trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out DESC
		if ("DESC".equals(aString.substring(1)))
		{
			String description = display.getSafeStringFor(PCStringKey.DESCRIPTION);
			if (description.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (description.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out BIO
		if ("BIO".equals(aString.substring(1)))
		{
			String bio = display.getBio();
			if (bio.equals(Constants.NONE))
			{
				canWrite = false;
			}
			else if (bio.trim().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out SUBREGION
		if ("SUBREGION".equals(aString.substring(1)))
		{
			if (display.getSubRegion().isEmpty())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out TEMPBONUS.
		if (aString.substring(1).startsWith("TEMPBONUS."))
		{
			StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
			aTok.nextToken(); // discard first one

			int index = -1;
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

		// Filter out ARMOR.ITEM
		if (aString.substring(1).startsWith("ARMOR.ITEM"))
		{
			// New token syntax |%ARMOR.ITEM.x| instead of |%ARMOR.ITEMx|
			final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
			aTok.nextToken(); // ARMOR

			String fString = aTok.nextToken();
			final Collection<Equipment> aArrayList = new ArrayList<>();

			for (Equipment eq : aPC.getEquipmentListInOutputOrder())
			{
				if (eq.altersAC(aPC) && (!eq.isArmor() && !eq.isShield()))
				{
					aArrayList.add(eq);
				}
			}

			// When removing old syntax, remove the else and leave the if
			final int count;
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

		// Filter out ARMOR.SHIELD
		if (aString.substring(1).startsWith("ARMOR.SHIELD"))
		{
			// New token syntax |%ARMOR.SHIELD.x| instead of |%ARMOR.SHIELDx|
			final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
			aTok.nextToken(); // ARMOR

			String fString = aTok.nextToken();
			final int count;
			final List<Equipment> aArrayList = aPC.getEquipmentOfTypeInOutputOrder("SHIELD", 3);

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

		// Filter out ARMOR
		if (aString.substring(1).startsWith("ARMOR"))
		{
			// New token syntax |%ARMOR.x| instead of |%ARMORx|
			final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
			String fString = aTok.nextToken();
			List<Equipment> aArrayList = aPC.getEquipmentOfTypeInOutputOrder("ARMOR", 3);

			// Get list of shields.  Remove any from list of armor
			// Since shields are included in the armor list they will appear twice
			// and they really shouldn't be in the list of armor
			List<Equipment> shieldList = aPC.getEquipmentOfTypeInOutputOrder("SHIELD", 3);

			int z = 0;
			while (z < shieldList.size())
			{
				aArrayList.remove(shieldList.get(z));
				z++;
			}

			// When removing old syntax, remove the else and leave the if
			final int count;
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

		// Filter out WEAPONPROF
		if ("WEAPONPROF".equals(aString.substring(1)))
		{
			if (!SettingsHandler.getWeaponProfPrintout())
			{
				canWrite = false;
			}
			return 0;
		}

		// Filter out WEAPON
		if (aString.substring(1).startsWith("WEAPON"))
		{
			// New token syntax |%WEAPON.x| instead of |%WEAPONx|
			final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
			String fString = aTok.nextToken();
			final List<Equipment> aArrayList = aPC.getExpandedWeapons(merge);

			int count;

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

		// Filter out DOMAIN
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

			canWrite = (index <= display.getDomainCount());

			return 0;
		}

		// Filter out SPELLLISTBOOK
		if (aString.substring(1).startsWith("SPELLLISTBOOK"))
		{
			if (SettingsHandler.getPrintSpellsWithPC())
			{
				// New token syntax |%SPELLLISTBOOK.x| instead of |%SPELLLISTBOOKx|
				// To remove old syntax, replace i with 15
				int i;
				if (aString.charAt(14) == '.')
				{
					i = 15;
				}
				else
				{
					i = 14;
				}

				return replaceTokenSpellListBook(aString.substring(i), aPC);
			}
			canWrite = false;
			return 0;
		}

		// Filter out VAR.
		if (aString.substring(1).startsWith("VAR."))
		{
			replaceTokenVar(aString, aPC);
			return 0;
		}

		// Filter out COUNT[
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

		// finally, check for classes
		final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ",", false);

		boolean found = false;
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

			PCClass aClass = aPC.getClassKeyed(bString);
			found = aClass != null;

			if (aClass == null)
			{
				canWrite = false;
			}
			else
			{
				canWrite = (aPC.getLevel(aClass) >= i);
			}

			// Filter out SPELLLISTCLASS			
			if (bString.startsWith("SPELLLISTCLASS"))
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

				CDOMObject aObject = aPC.getSpellClassAtIndex(Integer.parseInt(bString));
				canWrite = (aObject != null);
			}
		}

		if (found)
		{
			return 0;
		}
		canWrite = false;

		Logging.debugPrint("Return 0 (don't write/no replacement) for an undetermined filter token.");
		return 0;
	}

	/**
	 * Helper method to get the equipment merging strategy
	 * 
	 * @param aString
	 * @return merging strategy constant
	 */
	private int getEquipmentMergingStrategy(String aString)
	{
		// Set how we are merging equipment, default is to merge all
		int merge = Constants.MERGE_ALL;
		if (aString.indexOf("MERGENONE") > 0)
		{
			merge = Constants.MERGE_NONE;
		}

		if (aString.indexOf("MERGELOC") > 0)
		{
			merge = Constants.MERGE_LOCATION;
		}
		return merge;
	}

	/**
	 * Helper method to deal with DFOR token, e.g.
	 * 
	 * DFOR.0,(COUNT[SKILLS]+1)/2,1,COUNT[SKILLS],(COUNT[SKILLS]+1)/2,<td>\SKILL%\</td>
	 * <td>\SKILL%.TOTAL\</td><td>\SKILL%.RANK\</td>
	 * <td>\SKILL%.ABILITY\</td><td>\SKILL%.MOD\,<tr align="center">,</tr>,0
	 * 
	 * Produces a 2 column row table of all skills.
	 * 
	 * @param aString String to process
	 * @param output Output we are writing to
	 * @param aPC PC we are exporting
	 */
	private void replaceTokenForDfor(String aString, BufferedWriter output, PlayerCharacter aPC)
	{
		StringTokenizer aTok;

		// Split after DFOR. or DFOR by the ',' delimiter
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
		String cString = "";
		String cStartLineString = "";
		String cEndLineString = "";
		boolean isDFor = false;

		int i = 0;

		// While there are more tokens
		while (aTok.hasMoreTokens())
		{
			String tokA = aTok.nextToken();

			switch (i)
			{
				case 0:
					cMin = getVarValue(tokA, aPC);
					break;
				case 1:
					cMax = getVarValue(tokA, aPC);
					break;
				case 2:
					cStep = getVarValue(tokA, aPC);
					if (aString.startsWith("DFOR."))
					{
						isDFor = true;
						cStepLineMax = getVarValue(aTok.nextToken(), aPC);
						cStepLine = getVarValue(aTok.nextToken(), aPC);
					}
					break;
				case 3:
					cString = tokA;
					break;
				case 4:
					cStartLineString = tokA;
					break;
				case 5:
					cEndLineString = tokA;
					break;
				case 6:
					existsOnly = (!"0".equals(tokA));
					if ("2".equals(tokA))
					{
						checkBefore = true;
					}
					break;
				default:
					Logging.errorPrint("ExportHandler.replaceTokenForDfor can't handle token number " + i
						+ " this probably means you've passed in too many parameters.");
					break;
			}
			i++;
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
			cStartLineString = Constants.LINE_SEPARATOR;
		}

		if ("CRLF".equals(cEndLineString))
		{
			cEndLineString = Constants.LINE_SEPARATOR;
		}

		int iStart = cMin;

		int x = 0;
		while (iStart < cMax)
		{
			if (x == 0)
			{
				FileAccess.write(output, cStartLineString);
			}
			x++;

			int iNow = iStart;

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

						String fString = hString.substring(0, index);

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

	/**
	 * Helper method to parse OIF token, e.g.
	 * 
	 * OIF(expr,truepart,falsepart)
	 * OIF(HASFEAT:Armor Prof (Light), <b>Yes</b>, <b>No</b>)
	 * 
	 * If the character has the Light Armor proficiency, then returns "Yes". 
	 * Otherwise it returns "No".
	 * 
	 * @param aString String to parse
	 * @param output output to write to
	 * @param aPC PC we are exporting
	 */
	private void replaceTokenOIF(String aString, java.io.Writer output, PlayerCharacter aPC)
	{
		int iParenCount = 0;
		final String[] tokenizedString = new String[3];
		int iParamCount = 0;
		int iStart = 4;

		for (int i = iStart; i < aString.length(); ++i)
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
							tokenizedString[iParamCount] = aString.substring(iStart, i).trim();
							iParamCount++;
							iStart = i + 1;
						}
						else
						{
							Logging.errorPrint("OIF: not enough parameters (" + Integer.toString(iParamCount) + ')');
							for (int j = 0; j < iParamCount; ++j)
							{
								Logging.errorPrint("  " + Integer.toString(j) + ':' + tokenizedString[j]);
							}
						}
					}
					break;
				case ',':

					if (iParenCount == 0)
					{
						if (iParamCount < 2)
						{
							tokenizedString[iParamCount] = aString.substring(iStart, i).trim();
							iStart = i + 1;
						}
						else
						{
							Logging.errorPrint("OIF: too many parameters");
						}

						iParamCount += 1;
					}

					break;

				default:
					break;
			}
		}

		String remainder = "";

		// Actually evaluate the expression
		if (iParamCount != 3)
		{
			Logging.errorPrint("OIF: invalid parameter count: " + iParamCount);
		}
		else
		{
			remainder = aString.substring(iStart);
			int i = 0;
			if (evaluateExpression(tokenizedString[0], aPC))
			{
				i = 1;
			}
			else
			{
				i = 2;
			}

			// Write out the true or false case
			FileAccess.write(output, tokenizedString[i]);
		}

		if (!remainder.isEmpty())
		{
			Logging.errorPrint("OIF: extra characters on line: " + remainder);
			FileAccess.write(output, remainder);
		}
	}

	/**
	 * Helper method to deal with the SpellListBook token
	 * 
	 * @param aString
	 * @param aPC
	 * @return 0
	 */
	private int replaceTokenSpellListBook(String aString, PlayerCharacter aPC)
	{
		int sbookNum = 0;

		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int levelNum = Integer.parseInt(aTok.nextToken());

		// Get the spell book number
		if (aTok.hasMoreTokens())
		{
			sbookNum = Integer.parseInt(aTok.nextToken());
		}

		// Set the spell book name
		String bookName = Globals.getDefaultSpellBook();
		if (sbookNum > 0)
		{
			bookName = aPC.getDisplay().getSpellBookNames().get(sbookNum);
		}

		canWrite = false;
		final PObject aObject = aPC.getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			final List<CharacterSpell> aList = aPC.getCharacterSpells(aObject, null, bookName, levelNum);
			canWrite = !aList.isEmpty();
		}

		return 0;
	}

	/**
	 * Helper method for replacing token variables
	 * 
	 * @param aString String to process
	 * @param aPC PC we are exporting
	 */
	private void replaceTokenVar(String aString, PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aString.substring(5), ".", false);
		final String varName = aTok.nextToken();
		String bString = "EQ";
		boolean intVal = false;
		boolean maxVal = true;

		if (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
		}
		while ("INTVAL".equals(bString) || "MINVAL".equals(bString))
		{
			if ("INTVAL".equals(bString))
			{
				intVal = true;
			}
			else if ("MINVAL".equals(bString))
			{
				maxVal = false;
			}
			if (aTok.hasMoreTokens())
			{
				bString = aTok.nextToken();
			}
			else
			{
				Logging.errorPrint("Missing comparison type in VAR filter " + aString + " assuming NEQ");
				bString = "NEQ";
			}
		}

		String value = "0";

		if (aTok.hasMoreTokens())
		{
			value = aTok.nextToken();
		}

		Float varval = aPC.getVariable(varName, maxVal);
		if (intVal)
		{
			varval = (float) Math.floor(varval);
		}
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
			Logging.errorPrint("Unknown comparison type: " + bString + " in VAR filter " + aString + " assuming NEQ");
			canWrite = !CoreUtility.doublesEqual(varval.doubleValue(), valval.doubleValue());
		}
	}

	/**
	 * Exports a PlayerCharacter-Party to a Writer
	 * according to the handler's template
	 * 
	 * <br>author: Thomas Behr 13-11-02
	 *
	 * @param PCs the PlayerCharacter[] which compromises the Party to write
	 * @param out the Writer to be written to
	 */
	private void write(PlayerCharacter[] PCs, BufferedWriter out)
	{
		// Set an output filter based on the type of template in use.
		FileAccess.setCurrentOutputFilter(templateFile.getName());


		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(templateFile),
				StandardCharsets.UTF_8
		));)
		{
			boolean betweenPipes = false;
			StringBuilder textBetweenPipes = new StringBuilder();

			// Starts with pipe pattern
			Pattern pat1 = Pattern.compile("^\\Q|");
			// Ends with pipe pattern
			Pattern pat2 = Pattern.compile("\\Q|\\E$");

			String aLine = br.readLine();

			while (aLine != null)
			{
				int lastPipeIndex = aLine.lastIndexOf('|');

				// If not inside a TAG and there is no | character on this line
				if (!betweenPipes && lastPipeIndex == -1)
				{
					// If output sheet author controls new lines 
					// then replace tabs with empty space.
					if (manualWhitespace)
					{
						aLine = aLine.replaceAll("[ \\t]", "");
						FileAccess.write(out, aLine);
					}
					else
					{
						FileAccess.write(out, aLine);
						FileAccess.newLine(out);
					}
				}

				// Else if we are Inside a tag but we are not at the finish of 
				// the tag e.g. 
				//
				// |
				// x
				// |
				// 
				// Or we are at the start of a tag that wraps onto the next line e.g. 
				// 
				// |x
				// 
				// Collect this text (without the pipe)
				// to be passed for replacement later.
				else if (lastPipeIndex == (betweenPipes ? -1 : 0))
				{
					textBetweenPipes.append(aLine.substring(lastPipeIndex + 1));
					betweenPipes = true;
				}
				// See if we are between pipes or not
				else
				{
					Matcher mat1 = pat1.matcher(textBetweenPipes);
					Matcher mat2 = pat2.matcher(textBetweenPipes);
					boolean startsWithPipe = mat1.find();
					boolean endsWithPipe = mat2.find();

					// not currently in a pipe enclosed section, but first
					// char starts one.
					if (!betweenPipes && startsWithPipe)
					{
						betweenPipes = true;
					}

					betweenPipes = processPipedLine(PCs, aLine, textBetweenPipes, out, betweenPipes);

					if (betweenPipes && endsWithPipe)
					{
						betweenPipes = false;
					}
				}

				aLine = br.readLine();
			}
		} catch (IOException exc)
		{
			Logging.errorPrint("Error in ExportHandler::write", exc);
		}
	}

	/**
	 * Helper method to process a line that begins with a | (and may end with a |)
	 * 
	 * @param PCs List of PCs to output
	 * @param aLine Line to parse
	 * @param buf 
	 * @param out character sheet we are building up
	 * @param between Whether we are processing a line between pipes
	 * @return true if we processed successfully
	 */
	private boolean processPipedLine(PlayerCharacter[] PCs, String aLine, StringBuilder buf, BufferedWriter out,
		boolean between)
	{
		final StringTokenizer aTok = new StringTokenizer(aLine, "|", false);

		boolean noPipes = false;
		if (aTok.countTokens() == 1)
		{
			noPipes = true;
		}

		boolean betweenPipes = between;

		while (aTok.hasMoreTokens())
		{
			String tok = aTok.nextToken();

			// If we're not between pipes then just write to the output 
			// removing tab characters if asked to do so
			if (!betweenPipes)
			{
				if (manualWhitespace)
				{
					tok = tok.replaceAll("[ \\t]", "");
				}
				FileAccess.write(out, tok);
			}

			// Guaranteed to be between pipes here
			else if (!noPipes && !aTok.hasMoreTokens())
			{
				buf.append(tok);
			}

			else
			{
				buf.append(tok);
				String aString = buf.toString();

				// We have finished dealing with section
				// between the pipe characters so clear out the
				// StringBuilder
				int l = buf.length();
				buf.delete(0, l);

				if (aString.startsWith("FOR."))
				{
					doPartyForToken(PCs, out, aString);
				}
				else
				{

					Matcher mat = Pattern.compile("^(\\d+)").matcher(aString);
					int charNum = mat.matches() ? Integer.parseInt(mat.group()) : -1;

					// This seems bizarre since we haven't stripped the 
					// integer from the front of this string which means
					// that it will not be recognised as a token and will
					// just be written to the output verbatim
					if ((charNum >= 0) && (charNum < Globals.getPCList().size()))
					{
						PlayerCharacter currPC = PCs[charNum];
						replaceToken(aString, out, currPC);
					}
					else if (aString.startsWith("EXPORT"))
					{
						// We can safely do EXPORT tags with no PC
						replaceToken(aString, out, null);
					}
				}
			}

			if (aTok.hasMoreTokens() || noPipes)
			{
				betweenPipes = !betweenPipes;
			}
		}
		return betweenPipes;
	}

	/**
	 * Deal with the FOR. token, but at a party level
	 * 
	 * @param PCs The PCs to export
	 * @param out The Output we are writing to
	 * @param tokenString The token string to process
	 */
	private void doPartyForToken(PlayerCharacter[] PCs, BufferedWriter out, String tokenString)
	{
		PartyForParser forParser = new PartyForParser(tokenString, PCs.length);

		int x = 0;
		for (int i = forParser.min(); i < forParser.max(); i++)
		{
			if (x == 0)
			{
				FileAccess.write(out, forParser.startOfLine());
			}

			PlayerCharacter currPC = ((i >= 0) && (i < PCs.length)) ? PCs[i] : null;

			String[] tokens = forParser.tokenString().split("\\\\\\\\");

			for (String tok : tokens)
			{
				if (tok.startsWith("%."))
				{
					if (currPC != null)
					{
						replaceToken(tok.substring(2), out, currPC);
					}
				}
				else
				{
					FileAccess.write(out, tok);
				}
			}

			// Note: This was changed from == to && since I can't see how
			// == could possibly be correct behaviour.  If we were not
			// just printing characters that exist the loop would
			// terminate after printing one character. 
			boolean breakloop = (forParser.existsOnly() && (currPC == null));

			++x;
			if (x == forParser.step() || breakloop)
			{
				x = 0;
				FileAccess.write(out, forParser.endOfLine());

				if (breakloop)
				{
					break;
				}
			}
		}
	}

	/*
	 * ##########################################################################
	 * various getters and setters
	 * ##########################################################################
	 */

	/**
	 * @param canWrite The canWrite flag to set.
	 */
	public void setCanWrite(boolean canWrite)
	{
		this.canWrite = canWrite;
	}

	/**
	 * @return Returns the checkBefore flag.
	 */
	public boolean getCheckBefore()
	{
		return checkBefore;
	}

	/**
	 * @return Returns the existsOnly flag.
	 */
	public boolean getExistsOnly()
	{
		return existsOnly;
	}

	/**
	 * @param noMoreItems The noMoreItems flag to set.
	 */
	public void setNoMoreItems(boolean noMoreItems)
	{
		this.noMoreItems = noMoreItems;
	}

	/**
	 * @param manualWhitespace Set the manualWhitespace flag.
	 */
	public void setManualWhitespace(boolean manualWhitespace)
	{
		this.manualWhitespace = manualWhitespace;
	}

	/**
	 * Get the token string
	 * 
	 * @param aPC the PC being exported
	 * @param aString The token string to convert
	 * @return token string
	 */
	public static String getTokenString(final PlayerCharacter aPC, final String aString)
	{
		final StringTokenizer tok = new StringTokenizer(aString, ".,", false);
		final String firstToken = tok.nextToken();

		// Make sure the token list has been populated
		populateTokenMap();

		final Token token = TOKEN_MAP.get(firstToken);
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
	 * {@code PStringTokenizer}
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
			return (!_forThisString.isEmpty());
		}

		/**
		 * Return the next token
		 * @return next token
		 */
		public String nextToken()
		{
			String aString;

			if (_forThisString.lastIndexOf(_delimiter) == -1)
			{
				aString = _forThisString;
				_forThisString = "";
			}
			else
			{
				int i;
				final StringBuilder b = new StringBuilder();

				int ignores = 0;
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

					b.append(_forThisString, i, i + 1);
				}

				aString = b.toString();
				_forThisString = _forThisString.substring(i + 1);
			}

			return aString;
		}
	}

	private static final class PartyForParser
	{
		final PStringTokenizer pTok;

		private final Integer cMin;
		private final Integer cMax;
		private final Integer cStep;

		private final String tokenString;
		private final String stringForStartOfLine;
		private final String stringForEndOfLine;

		private final boolean existsOnly;

		private PartyForParser(String aString, final Integer numOfPCs)
		{
			pTok = new PStringTokenizer(aString.substring(4), ",", "\\\\", "\\\\");

			cMin = pTok.hasMoreTokens() ? Delta.decode(pTok.nextToken()) : 0;

			Integer max = pTok.hasMoreTokens() ? Delta.decode(pTok.nextToken()) : 100;

			cStep = pTok.hasMoreTokens() ? Delta.decode(pTok.nextToken()) : 1;

			tokenString = pTok.hasMoreTokens() ? pTok.nextToken() : "";

			stringForStartOfLine = pTok.hasMoreTokens() ? pTok.nextToken() : "";

			stringForEndOfLine = pTok.hasMoreTokens() ? pTok.nextToken() : "";

			existsOnly = pTok.hasMoreTokens() && !("0".equals(pTok.nextToken()));

			cMax = (max >= numOfPCs) && existsOnly ? numOfPCs : max;

			if (pTok.hasMoreTokens())
			{
				String log = "In Party.print there is an unhandled case in a "
						+ "switch (the value is " + pTok.nextToken()
						+ ".";
				Logging.errorPrint(log);
			}
		}

		public Integer min()
		{
			return cMin;
		}

		public Integer max()
		{
			return cMax;
		}

		public Integer step()
		{
			return cStep;
		}

		private String tokenString()
		{
			return tokenString;
		}

		private String startOfLine()
		{
			return stringForStartOfLine;
		}

		private String endOfLine()
		{
			return stringForEndOfLine;
		}

		private boolean existsOnly()
		{
			return existsOnly;
		}
	}

	public static void clear()
	{
		TOKEN_MAP.clear();
	}
}
