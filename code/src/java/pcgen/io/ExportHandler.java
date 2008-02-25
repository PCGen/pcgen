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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.core.Constants;
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
import pcgen.core.utils.CoreUtility;
import pcgen.io.exporttoken.ACCheckToken;
import pcgen.io.exporttoken.AbilityListToken;
import pcgen.io.exporttoken.AbilityToken;
import pcgen.io.exporttoken.AlignmentToken;
import pcgen.io.exporttoken.AttackToken;
import pcgen.io.exporttoken.BonusToken;
import pcgen.io.exporttoken.CheckToken;
import pcgen.io.exporttoken.DRToken;
import pcgen.io.exporttoken.DomainToken;
import pcgen.io.exporttoken.EqToken;
import pcgen.io.exporttoken.EqTypeToken;
import pcgen.io.exporttoken.GameModeToken;
import pcgen.io.exporttoken.HPToken;
import pcgen.io.exporttoken.HeightToken;
import pcgen.io.exporttoken.InitiativeMiscToken;
import pcgen.io.exporttoken.MovementToken;
import pcgen.io.exporttoken.ReachToken;
import pcgen.io.exporttoken.SRToken;
import pcgen.io.exporttoken.SizeLongToken;
import pcgen.io.exporttoken.SkillToken;
import pcgen.io.exporttoken.SkillpointsToken;
import pcgen.io.exporttoken.SpellFailureToken;
import pcgen.io.exporttoken.StatToken;
import pcgen.io.exporttoken.Token;
import pcgen.io.exporttoken.TotalToken;
import pcgen.io.exporttoken.VarToken;
import pcgen.io.exporttoken.WeaponToken;
import pcgen.io.exporttoken.WeaponhToken;
import pcgen.io.exporttoken.WeightToken;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>ExportHandler</code>.
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public final class ExportHandler
{
	private static final Float JEP_TRUE        = new Float(1.0);
	private static final NumberFormat NUM_FMT  = NumberFormat.getInstance();
	private static Map<String, Token> tokenMap = new HashMap<String, Token>();
	private static boolean tokenMapPopulated   = false;

	// Processing state variables
	private boolean existsOnly       = false;
	private boolean noMoreItems      = false;
	private boolean manualWhitespace = false;

	private File templateFile;

	// This is pretty ugly.  No idea what sort of junk could be in here.
	private final Map<Object, Object> loopVariables = 
			new HashMap<Object, Object>();

	private String  csheetTag2  = "\\";
	private boolean skipMath    = false;
	private boolean canWrite    = true;
	private boolean checkBefore = false;
	private boolean inLabel     = false;

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
	 * Replace the token, but skip the math
	 * 
	 * @param aPC The PC being exported
	 * @param aString the string which will have its tokens replaced 
	 * @param output the object that collects the output
	 */
	public void replaceTokenSkipMath(
			PlayerCharacter aPC,
			String aString,
			BufferedWriter output)
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
	 * @param out the Writer to be written to
	 */
	public void write(
			PlayerCharacter aPC,
			BufferedWriter out)
	{
		aPC.preparePCForOutput();

		FileAccess.setCurrentOutputFilter(templateFile.getName());

		BufferedReader br = null;

		try
		{
			br = new BufferedReader(
					new InputStreamReader(
						new FileInputStream(templateFile), 
						"UTF-8"));

			Pattern pat     = Pattern.compile(Pattern.quote("||"));
			String  rep     = Matcher.quoteReplacement("| |");
			String  aString = br.readLine();
			
			final StringBuffer inputLine = new StringBuffer();

			while (aString != null)
			{
				if (aString.length() == 0)
				{
					inputLine.append(' ');
				}
				else
				{
					// Adjacent separators get merged by StringTokenizer,
					// so we break them up here
					Matcher mat = pat.matcher(aString);
					inputLine.append(mat.replaceAll(rep));
				}

				inputLine.append(Constants.s_LINE_SEP);
				aString = br.readLine();
			}

			aString = inputLine.toString();

			final StringTokenizer aTok =
					new StringTokenizer(aString, "\r\n", false);

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
	public void write(Collection<PlayerCharacter> PCs, BufferedWriter out)
	{
		write(PCs.toArray(new PlayerCharacter[PCs.size()]), out);
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

	private int getVarValue(String varString, PlayerCharacter aPC)
	{

		String vString = varString;
		int countIndex = vString.indexOf("COUNT[EQ", 0);
		            
		while (countIndex >= 0)
		{

			char chC = vString.charAt(countIndex + 8);

			if ((chC == '.') || ((chC >= '0') && (chC <= '9')))
			{
				final int i = vString.indexOf(']', countIndex + 8);

				if (i >= 0)
				{
					String aString  = vString.substring(countIndex + 6, i);
					EqToken token   = (aString.indexOf("EQTYPE") > -1) ? new EqTypeToken() : new EqToken();
					String baString = token.getToken(aString, aPC, this);
					vString = vString.substring(0, countIndex) + baString + vString.substring(i + 1);
				}
			}
			countIndex = vString.indexOf("COUNT[EQ", countIndex + 1);
		}

		int strlenIndex = vString.indexOf("STRLEN[", 0);

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
					//TODO: If this should be ignored, add a comment here describing why. XXX
				}

				String result = sWriter.toString();
				vString = vString.substring(0, strlenIndex) + result.length()
							+ vString.substring(i + 1);
			}
			strlenIndex = vString.indexOf("STRLEN[", strlenIndex + 1);
		}

		return aPC.getVariableValue(vString, "").intValue();
	}

	/**
	 * Add to the token map, called mainly by the plugin loader
	 * @param newToken
	 */
	public static void addToTokenMap(Token newToken)
	{
		Token test = tokenMap.put(newToken.getTokenName(), newToken);

		if (test != null)
		{
			Logging
				.errorPrint("More than one Output Token has the same Token Name: '"
					+ newToken.getTokenName() + "'");
		}
	}

	private boolean evaluateExpression(final String expr, 
	                                   final PlayerCharacter aPC)
	{
		if (expr.indexOf(".AND.") > 0)
		{
			final String part1 = expr.substring(0, expr.indexOf(".AND."));
			final String part2 = expr.substring(expr.indexOf(".AND.") + 5);

			return (evaluateExpression(part1, aPC) && 
			        evaluateExpression(part2, aPC));
		}

		if (expr.indexOf(".OR.") > 0)
		{
			final String part1 = expr.substring(0, expr.indexOf(".OR."));
			final String part2 = expr.substring(expr.indexOf(".OR.") + 4);

			return (evaluateExpression(part1, aPC) || 
			        evaluateExpression(part2, aPC));
		}

		String expr1 = expr;
		for (final Object anObject : loopVariables.keySet())
		{
			if (anObject == null)
			{
				continue;
			}

			final String fString = anObject.toString();
			final String rString = loopVariables.get(fString).toString();
			expr1 = expr1.replaceAll(Pattern.quote(fString), rString);
		}

		if (expr1.startsWith("HASVAR:"))
		{
			expr1 = expr1.substring(7).trim();

			return (aPC.getVariableValue(expr1, "").intValue() > 0);
		}

		if (expr1.startsWith("HASFEAT:"))
		{
			expr1 = expr1.substring(8).trim();

			return (aPC.getFeatNamed(expr1) != null);
		}

		if (expr1.startsWith("HASSA:"))
		{
			expr1 = expr1.substring(6).trim();

			return (aPC.hasSpecialAbility(expr1));
		}

		if (expr1.startsWith("HASEQUIP:"))
		{
			expr1 = expr1.substring(9).trim();

			return (aPC.getEquipmentNamed(expr1) != null);
		}

		if (expr1.startsWith("SPELLCASTER:"))
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
			final String fString = expr1.substring(12).trim();

			if (fString.indexOf('=') >= 0)
			{
				final StringTokenizer aTok =
						new StringTokenizer(fString, "=", false);
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

				if (cs.equalsIgnoreCase(aClass.getCastAs()))
				{
					return true;
				}

				if ("!Prepare".equalsIgnoreCase(cs)
				    && aClass.getMemorizeSpells())
				{
					return true;
				}

				if ("Prepare".equalsIgnoreCase(cs)
				    && (!aClass.getMemorizeSpells()))
				{
					return true;
				}
			}
			else
			{
				for (final PCClass pcClass : aPC.getClassList())
				{
					if (fString.equalsIgnoreCase(pcClass.getSpellType()))
					{
						return true;
					}

					if (fString.equalsIgnoreCase(pcClass.getKeyName()))
					{
						return true;
					}

					if (fString.equalsIgnoreCase(pcClass.getCastAs()))
					{
						return true;
					}

					if ("!Prepare".equalsIgnoreCase(fString)
					    && pcClass.getMemorizeSpells())
					{
						return true;
					}

					if ("Prepare".equalsIgnoreCase(fString)
					    && (!pcClass.getMemorizeSpells()))
					{
						return true;
					}
				}
			}
		}

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

		if (expr1.endsWith("UNTRAINED"))
		{
			final StringTokenizer aTok = new StringTokenizer(expr1, ".");
			final String fString = aTok.nextToken();
			Skill aSkill = null;

			if (fString.length() > 5)
			{
				final int i = Integer.parseInt(fString.substring(5));
				final List<Skill> pcSkills = aPC.getSkillListInOutputOrder();

				if (i <= (pcSkills.size() - 1))
				{
					aSkill = pcSkills.get(i);
				}
			}

			if (aSkill == null)
			{
				return false;
			}
			else if (aSkill.isUntrained())
			{
				return true;
			}

			return false;
		}

		// Test for JEP formula 
		final Float res =
				aPC.getVariableProcessor().getJepOnlyVariableValue(null, expr1, "", 0);
		if (res != null)
		{
			return res.equals(JEP_TRUE);
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
		final StringTokenizer aTok = new StringTokenizer(expr1, ":");
		final String token;
		final String equals;

		final int tokenCount = aTok.countTokens();
		if (tokenCount == 1)
		{
			token = expr1;
			equals = "TRUE";
		}
		else if (tokenCount != 2)
		{
			Logging
					.errorPrint("evaluateExpression: Incorrect syntax (missing parameter)");

			return false;
		}
		else
		{
			token = aTok.nextToken();
			equals = aTok.nextToken().toUpperCase();
		}

		final StringWriter sWriter = new StringWriter();
		final BufferedWriter aWriter = new BufferedWriter(sWriter);
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
			aString = aPC.getVariableValue(token.substring(4), "").toString();
		}

		try
		{
			// integer values
			final int i = Integer.parseInt(aString);

			return i == Integer.parseInt(equals);
		}
		catch (NumberFormatException e)
		{
			// String values
			return 0 <= aString.toUpperCase().indexOf(equals);
		}
	}

	private void evaluateIIF(
			final IIFNode node,
			final BufferedWriter output,
			final FileAccess fa,
			final PlayerCharacter aPC)
	{
		//
		// Comma is a delimiter for a higher-level parser, so 
		// we'll use a semicolon and replace it with a comma for
		// expressions like:
		// |IIF(VAR.IF(var("COUNT[SKILLTYPE=Strength]")>0;1;0):1)|
		//
		final String aString = node.expr().replaceAll(Pattern.quote(";"), ",");
		if (evaluateExpression(aString, aPC))
		{
			evaluateIIFChildren(node.trueChildren(), output, fa, aPC);
		}
		else
		{
			evaluateIIFChildren(node.falseChildren(), output, fa, aPC);
		}
	}

	private void evaluateIIFChildren(
			final List<?> children,
			final BufferedWriter output,
			final FileAccess fa, 
			final PlayerCharacter aPC)
	{
		for (int y = 0; y < children.size(); ++y)
		{
			if (children.get(y) instanceof FORNode)
			{
				final FORNode nextFor = (FORNode) children.get(y);
				loopVariables.put(nextFor.var(), 0);
				existsOnly = nextFor.exists();

				String minString = nextFor.min();
				String maxString = nextFor.max();
				String stepString = nextFor.step();

				for (final Object anObject : loopVariables.keySet())
				{
					if (anObject == null)
					{
						continue;
					}

					final String fString = anObject.toString();
					final String rString = loopVariables.get(fString).toString();
					minString  = minString.replaceAll(Pattern.quote(fString), rString);
					maxString  = maxString.replaceAll(Pattern.quote(fString), rString); 
					stepString = stepString.replaceAll(Pattern.quote(fString), rString);
				}

				loopFOR(nextFor, 
				        getVarValue(minString, aPC), 
				        getVarValue(maxString, aPC), 
				        getVarValue(stepString, aPC), 
				        output,
				        fa, 
				        aPC);
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

				for (final Object anObject : loopVariables.keySet())
				{
					if (anObject == null)
					{
						continue;
					}

					final String fString = anObject.toString();
					final String rString = loopVariables.get(fString).toString();
					lineString = lineString.replaceAll(Pattern.quote(fString), rString);
				}

				replaceLine(lineString, output, aPC);

				// output a newline if output is allowed
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
	 * @param end The ending value fo the loop
	 * @param step The amount by which the counter should be changed each iteration.
	 * @param output The writer output is to be sent to.
	 * @param fa The FileAccess instance to be used to manage the output.
	 * @param aPC The character being processed.
	 */
	private void loopFOR(
			final FORNode node, 
			final int start, 
			final int end, 
			final int step,
			final BufferedWriter output, 
			final FileAccess fa, 
			final PlayerCharacter aPC)
	{
		for (int x = start; ((step < 0) ? x >= end : x <= end); x += step)
		{
			if (processLoop(node, output, fa, aPC, x))
			{
				break;
			}
		}
	}

	/**
	 * Process an iteration of a FOR loop.
	 * @param node The node being processed
	 * @param output The writer output is to be sent to.
	 * @param fa The FileAccess instance to be used to manage the output.
	 * @param aPC The character being processed.
	 * @param index The current value of the loop index
	 * @return true if the loop should be stopped.
	 */
	private boolean processLoop(FORNode node, BufferedWriter output,
		FileAccess fa, PlayerCharacter aPC, int index)
	{
		loopVariables.put(node.var(), index);
		for (int y = 0; y < node.children().size(); ++y)
		{
			if (node.children().get(y) instanceof FORNode)
			{
				FORNode nextFor = (FORNode) node.children().get(y);
				loopVariables.put(nextFor.var(), 0);
				existsOnly = nextFor.exists();

				String minString = nextFor.min();
				String maxString = nextFor.max();
				String stepString = nextFor.step();

				for (Object anObject : loopVariables.keySet())
				{
					if (anObject == null)
					{
						continue;
					}

					String fString = anObject.toString();
					String rString = loopVariables.get(fString).toString();
					minString  = minString.replaceAll(Pattern.quote(fString), rString);
					maxString  = maxString.replaceAll(Pattern.quote(fString), rString);
					stepString = stepString.replaceAll(Pattern.quote(fString), rString);
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

				for (Object anObject : loopVariables.keySet())
				{
					if (anObject == null)
					{
						continue;
					}

					String fString = anObject.toString();
					String rString = loopVariables.get(fString).toString();
					lineString = lineString.replaceAll(Pattern.quote(fString), rString);
				}

				noMoreItems = false;
				replaceLine(lineString, output, aPC);

				// Allow the output sheet author to control new lines.
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
	 * @param aPC the pc being exported
	 * @return String
	 */
	private String mathMode(String aString, PlayerCharacter aPC)
	{
		String str = aString;
		while (str.lastIndexOf('(') >= 0)
		{
			int x = CoreUtility.innerMostStringStart(str);
			int y = CoreUtility.innerMostStringEnd(str);

			if (y < x)
			{
				// This was breaking some homebrew sheets. [Felipe - 13-may-03]
				//Logging.errorPrint("Missing closing parenthesis: " + aString);
				//return total.toString();
				break;
			}

			String bString = str.substring(x + 1, y);

			// This will treat Knowledge (xx) kind of token
			if ((x > 0)
				&& (str.charAt(x - 1) == ' ')
				&& ((str.charAt(y + 1) == '.') || (y == (str.length() - 1))))
			{
				str =
						str.substring(0, x) + "[" + bString + "]"
							+ str.substring(y + 1);
			}
			else
			{
				str =
						str.substring(0, x) + mathMode(bString, aPC)
							+ str.substring(y + 1);
			}
		}

		str = str.replaceAll(Pattern.quote("["), "("); 
		str = str.replaceAll(Pattern.quote("]"), ")");

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

		Float total = new Float(0.0);
		for (int i = 0; i < str.length(); ++i)
		{
			valString += str.substring(i, i + 1);

			if ((i == (str.length() - 1))
				|| ((delimiter.lastIndexOf(str.charAt(i)) > -1) && (i > 0) && (
					str
					.charAt(i - 1) != '.')))
			{
				if (delimiter.lastIndexOf(str.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}

				if (i < str.length())
				{
					if (valString.endsWith(".TRUNC"))
					{
						if (attackRoutine)
						{
							Logging
								.errorPrint("Math Mode Error: Using .TRUNC in Attack Mode.");
						}
						else
						{
							valString =
									String.valueOf(Float.valueOf(
										mathMode(valString.substring(0,
											valString.length() - 6), aPC))
										.intValue());
						}
					}

					if (valString.endsWith(".INTVAL"))
					{
						if (attackRoutine)
						{
							Logging
								.errorPrint("Math Mode Error: Using .INTVAL in Attack Mode.");
						}
						else
						{
							valString =
									mathMode(valString.substring(0, valString
										.length() - 7), aPC);
						}

						endMode = INTVAL_MODE;
					}

					if (valString.endsWith(".SIGN"))
					{
						valString =
								mathMode(valString.substring(0, valString
									.length() - 5), aPC);
						endMode = SIGN_MODE;
					}

					if (valString.endsWith(".NOZERO"))
					{
						valString =
								mathMode(valString.substring(0, valString
									.length() - 7), aPC);
						endMode = NO_ZERO_MODE;
					}

					if ((str.length() > 0) && (str.charAt(i) == '+'))
					{
						nextMode = ADDITION_MODE;
					}
					else if ((str.length() > 0)
						&& (str.charAt(i) == '-'))
					{
						nextMode = SUBTRACTION_MODE;
					}
					else if ((str.length() > 0)
						&& (str.charAt(i) == '*'))
					{
						nextMode = MULTIPLICATION_MODE;
					}
					else if ((str.length() > 0)
						&& (str.charAt(i) == '/'))
					{
						nextMode = DIVISION_MODE;
					}

					//TODO: Check if this is a JEP formula If it is process that.
					//					Logging.setDebugMode(true);
					//					Float res =
					//							aPC.getVariableProcessor().getJepOnlyVariableValue(null,
					//								aString, "", 0);
					//					if (res != null)
					//					{
					//						Logging.setDebugMode(false);
					//						valString = NUM_FMT.format(res);
					//					}
					//					else
					//					{
					//						Logging.setDebugMode(false);

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

					if ((!attackRoutine) && Pattern.matches("^([-+]\\d+/)*[-+]\\d+$", valString))
					{
						attackRoutine = true;
						attackData = valString;
						valString = "";
					}
				}
				//				}

				try
				{
					if (valString.length() > 0)
					{
						if (attackRoutine)
						{
							StringTokenizer bTok =
									new StringTokenizer(attackData, "/");
							String newAttackData = "";

							if (bTok.countTokens() > 0)
							{
								while (bTok.hasMoreTokens())
								{
									final String bString = bTok.nextToken();

									switch (mode)
									{
										case ADDITION_MODE:
											newAttackData +=
													("/+" + Integer
														.toString(new Float(
															Float
																.parseFloat(bString)
																+ Float
																	.parseFloat(valString))
															.intValue()));

											break;

										case SUBTRACTION_MODE:
											newAttackData +=
													("/+" + Integer
														.toString(new Float(
															Float
																.parseFloat(bString)
																- Float
																	.parseFloat(valString))
															.intValue()));

											break;

										case MULTIPLICATION_MODE:
											newAttackData +=
													("/+" + Integer
														.toString(new Float(
															Float
																.parseFloat(bString)
																* Float
																	.parseFloat(valString))
															.intValue()));

											break;

										case DIVISION_MODE:
											newAttackData +=
													("/+" + Integer
														.toString(new Float(
															Float
																.parseFloat(bString)
																/ Float
																	.parseFloat(valString))
															.intValue()));

											break;

										default:
											Logging
												.errorPrint("In mathMode the mode "
													+ mode + " is unsupported.");

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
									total =
											new Float(total.doubleValue()
												+ Double.parseDouble(valString));

									break;

								case SUBTRACTION_MODE:
									total =
											new Float(total.doubleValue()
												- Double.parseDouble(valString));

									break;

								case MULTIPLICATION_MODE:
									total =
											new Float(total.doubleValue()
												* Double.parseDouble(valString));

									break;

								case DIVISION_MODE:
									total =
											new Float(total.doubleValue()
												/ Double.parseDouble(valString));

									break;

								default:
									Logging.errorPrint("In mathMode the mode "
										+ mode + " is unsupported.");

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
					replaceTokenSkipMath(aPC, str, aWriter);
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

	private void outputNonToken(String nonToken, java.io.Writer output)
	{
		// Do nothing if something shouldn't be output.
		if (canWrite && nonToken.length() != 0)
		{
			String finalToken = manualWhitespace ?
								nonToken.replaceAll("[ \\t]", "") : 
								nonToken;
			FileAccess.write(output, finalToken);
		}
	}

	private FORNode parseFORs(StringTokenizer tokens)
	{
		final FORNode root = new FORNode(null, "0", "0", "1", false);

		while (tokens.hasMoreTokens())
		{
			final String line = tokens.nextToken();

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
		final List<String> forVars = getParameters(forLine);
		final String var = forVars.get(1);
		final String min = forVars.get(2);
		final String max = forVars.get(3);
		final String step = forVars.get(4);
		final String eTest = forVars.get(5);
		boolean exists = false;

		if (((eTest.length() > 0) && (eTest.charAt(0) == '1'))
			|| ((eTest.length() > 0) && (eTest.charAt(0) == '2')))
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

	/**
	 * Retrieve the parameters of a comma seperated command such as a 
	 * FOR token. Commas inside brackets are ignored, thus allowing JEP 
	 * functions with multiple parameters to be included in FOR loops.
	 *  
	 * @param forToken The token to be broken up. 
	 * @return The token parameters.
	 */
	public static List<String> getParameters(String forToken)
	{
		String splitStr[] = forToken.split(",");
		List<String> result = new ArrayList<String>();
		StringBuffer buf = new StringBuffer();
		boolean inFormula = false;
		for (String string : splitStr)
		{
			if (string.indexOf("(") >= 0
				&& (string.indexOf(")") < string.indexOf("(")))
			{
				inFormula = true;
				buf.append(string);
			}
			else if (inFormula && string.indexOf(")") >= 0)
			{
				inFormula = false;
				buf.append(",");
				buf.append(string);
				result.add(buf.toString());
				buf = new StringBuffer();
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

	private IIFNode parseIIFs(String expr, StringTokenizer tokens)
	{
		final IIFNode node = new IIFNode(expr);
		boolean childrenType = true;

		while (tokens.hasMoreTokens())
		{
			final String line = tokens.nextToken();

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
		if (!tokenMapPopulated)
		{
			addToTokenMap(new AbilityToken());
			addToTokenMap(new AbilityListToken());
			addToTokenMap(new ACCheckToken());
			addToTokenMap(new AlignmentToken());
			addToTokenMap(new AttackToken());
			addToTokenMap(new BonusToken());
			addToTokenMap(new CheckToken());
			addToTokenMap(new DomainToken());
			addToTokenMap(new DRToken());
			addToTokenMap(new EqToken());
			addToTokenMap(new EqTypeToken());
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
	private void replaceLine(
			String          aLine, 
			BufferedWriter  output,
			PlayerCharacter aPC)
	{
		int lastIndex = aLine.lastIndexOf('|');

		// No pipes and non empty string so just output the fixed text
		if (lastIndex < 0 && aLine.length() > 0)
		{
			outputNonToken(aLine, output);
		}

		/*
		 When the line starts with a pipe and that pipe is the only
		 one on the line, this operation ignores the line.  This is
		 because the token is malformed.  Malformed because it shoud be
		 between pipes.
		*/

		if (lastIndex >= 1)
		{
			final StringTokenizer aTok = new StringTokenizer(aLine, "|", false);

			boolean inPipe     = aLine.charAt(0) == '|';
			boolean lastIsPipe = aLine.charAt(aLine.length() - 1) == '|';

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
 					 no else condition because we should be between
					 pipes at this point i.e. this should be a token but
					 it appears to be malformed.  Malformed because there
					 are no more tokens and the last character of the string
					 is not a pipe
					*/
				}
				else
				{
					outputNonToken(tok, output);
				}

				if (aTok.hasMoreTokens())
				{
					inPipe = !inPipe;
				}
			}
		}
	}

	/**
	 * Replace the token with the value it represents
	 * @param aString The string containing the token to be replaced
	 * @param output The object that will capture the output
	 * @param aPC The PC currently being exported
	 * @return value
	 */
	public int replaceToken(
			String aString,
			BufferedWriter output,
			PlayerCharacter aPC)
	{
		try
		{

			if (!canWrite && (aString.length() > 0)
				&& (aString.charAt(0) != '%'))
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
			if ((aString.length() > 0) && (aString.charAt(0) == '%')
				&& (aString.length() > 1) && (aString.lastIndexOf('<') < 0)
				&& (aString.lastIndexOf('>') < 0))
			{
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
					if (aString.substring(10).endsWith(
						GameModeToken.getGameModeToken()))
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
					final StringTokenizer aTok =
							new StringTokenizer(aString.substring(1), ".");
					final List<PCTemplate> tList = aPC.getTemplateList();
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
						Logging
							.errorPrint("Old syntax %TEMPLATEx will be replaced for %TEMPLATE.x");
						index = Integer.parseInt(aString.substring(9));
					}

					if (index >= tList.size())
					{
						canWrite = false;

						return 0;
					}

					final PCTemplate template = tList.get(index);
					if (template.getVisibility() != Visibility.DEFAULT
						&& template.getVisibility() != Visibility.OUTPUT_ONLY)
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
					List<Follower> aList = new ArrayList<Follower>();

					for (Follower follower : aPC.getFollowerList())
					{
						// only allow followers that
						// are currently loaded
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
					for (PCClass pcClass : aPC.getClassList())
					{
						if (pcClass.getLevel() > 0)
						{
							if (pcClass.getProhibitedSchools() != null)
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
					else if ((aPC.getMiscList().get(0)).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}

				if ("COMPANIONS".equals(aString.substring(1))
					|| "MISC.COMPANIONS".equals(aString.substring(1)))
				{
					if (aPC.getMiscList().get(1).equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (aPC.getMiscList().get(1).trim().length() == 0)
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
					else if (aPC.getMiscList().get(2).trim().length() == 0)
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
					StringTokenizer aTok =
							new StringTokenizer(aString.substring(1), ".");
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

				if (aString.substring(1).startsWith("ARMOR.ITEM"))
				{
					// New token syntax |%ARMOR.ITEM.x| instead of |%ARMOR.ITEMx|
					final StringTokenizer aTok =
							new StringTokenizer(aString.substring(1), ".");
					aTok.nextToken(); // ARMOR

					String fString = aTok.nextToken();
					final Collection<Equipment> aArrayList = 
							new ArrayList<Equipment>();

					for (Equipment eq : aPC.getEquipmentListInOutputOrder())
					{
						if (eq.getBonusListString("AC")
							&& (!eq.isArmor() && !eq.isShield()))
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
						Logging
							.errorPrint("Old syntax %ARMOR.ITEMx will be replaced for %ARMOR.ITEM.x");

						count = Integer.parseInt(fString.substring(fString
									.length() - 1));
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
					final StringTokenizer aTok =
							new StringTokenizer(aString.substring(1), ".");
					aTok.nextToken(); // ARMOR

					String fString = aTok.nextToken();
					final int count;
					final List<Equipment> aArrayList =
							aPC.getEquipmentOfTypeInOutputOrder("SHIELD", 3);

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging
							.errorPrint("Old syntax %ARMOR.SHIELDx will be replaced for %ARMOR.SHIELD.x");

						count =
								Integer.parseInt(fString.substring(fString
									.length() - 1));
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
					final StringTokenizer aTok =
							new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					List<Equipment> aArrayList =
							aPC.getEquipmentOfTypeInOutputOrder("ARMOR", 3);

					//Get list of shields.  Remove any from list of armor
					//Since shields are included in the armor list they will appear twice and they really shouldn't be in the list of armor
					List<Equipment> shieldList =
							aPC.getEquipmentOfTypeInOutputOrder("SHIELD", 3);

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
						Logging
							.errorPrint("Old syntax %ARMORx will be replaced for %ARMOR.x");

						count =
								Integer.parseInt(fString.substring(fString
									.length() - 1));
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
					final StringTokenizer aTok =
							new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					final List<Equipment> aArrayList =
							aPC.getExpandedWeapons(merge);

					int count;

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging
							.errorPrint("Old syntax %WEAPONx will be replaced for %WEAPON.x");

						count = Integer.parseInt(
									fString.substring(fString.length() - 1));
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
					final StringTokenizer aTok =
							new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					final int index;

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						index = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging
							.errorPrint("Old syntax %DOMAINx will be replaced for %DOMAIN.x");

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
						// To remove old syntax, rteplace i with 15
						int i = (aString.charAt(14) == '.') ? 15 : 14; 

						return replaceTokenSpellListBook(
								aString.substring(i),
								aPC);
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
				final StringTokenizer aTok =
						new StringTokenizer(aString.substring(1), ",", false);
				         
				boolean found = false;
				while (aTok.hasMoreTokens())
				{
					String cString = aTok.nextToken();
					StringTokenizer bTok =
							new StringTokenizer(cString, "=", false);
					String bString = bTok.nextToken();
					int i = 0;

					if (bTok.hasMoreTokens())
					{
						i = Integer.parseInt(bTok.nextToken());
					}

					PCClass aClass = aPC.getClassKeyed(bString);
					found = aClass != null;

					if ((aClass != null))
					{
						canWrite = (aClass.getLevel() >= i);
					}
					else if ((aClass == null))
					{
						canWrite = false;
					}
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

						PObject aObject =
								aPC.getSpellClassAtIndex(Integer
									.parseInt(bString));
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

			
			String tokenString = aString;
			
			// done with |%blah| tokens
			// now check for max length tokens
			// eg: |SUB10.ARMOR.AC|
			if ((tokenString.indexOf("SUB") == 0) && (tokenString.indexOf(".") > 3))
			{
				int iEnd = tokenString.indexOf(".");
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
			}

			//
			// now check for the rest of the tokens
			//
			populateTokenMap();

			// Correct old format tags such as SPELLLIST
			// so that they get processed correctly
			tokenString = correctOldFormatTag(tokenString);

			StringTokenizer tok = new StringTokenizer(tokenString, ".,", false);
			String firstToken = tok.nextToken();

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

			//Leave
			if (tokenString.startsWith("FOR.") || tokenString.startsWith("DFOR."))
			{
				FileAccess.maxLength(-1);

				existsOnly = false;
				noMoreItems = false;
				checkBefore = false;

				//skipMath = true;
				replaceTokenForDfor(tokenString, output, aPC);

				//skipMath = false;
				existsOnly = false;
				noMoreItems = false;

				return 0;
			}

			//Leave
			else if (tokenString.startsWith("OIF("))
			{
				replaceTokenOIF(tokenString, output, aPC);
			}

			//Leave
			else if ((
					//(testString.indexOf('(') >= 0)
				//|| 
				(testString.indexOf('+') >= 0)
				|| (testString.indexOf('-') >= 0)
				|| (testString.indexOf(".INTVAL") >= 0)
				|| (testString.indexOf(".SIGN") >= 0)
				|| (testString.indexOf(".NOZERO") >= 0)
				|| (testString.indexOf(".TRUNC") >= 0)
				|| (testString.indexOf('*') >= 0) || (testString.indexOf('/') >= 0))
				&& (!skipMath))
			{
				FileAccess.maxLength(-1);
				FileAccess.write(output, mathMode(tokenString, aPC));

				return 0;
			}

			//Leave
			else if (tokenString.startsWith("CSHEETTAG2."))
			{
				csheetTag2 = tokenString.substring(11, 12);
				FileAccess.maxLength(-1);

				return 0;
			}

			//Leave
			else if (tokenMap.get(firstToken) != null)
			{
				Token token = tokenMap.get(firstToken);
				if (token.isEncoded())
				{
					FileAccess.encodeWrite(output, token.getToken(tokenString, aPC,
						this));
				}
				else
				{
					FileAccess
						.write(output, token.getToken(tokenString, aPC, this));
				}
			}

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
				if (aString.regionMatches(9, "TYPE", 0, 4)
					|| aString.regionMatches(9, "BOOK", 0, 4)
					|| aString.regionMatches(9, "CAST", 0, 4))
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
			if ((aString.length() > 9) && (aString.charAt(9) != '.')
				&& (aString.charAt(9) != '='))
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
			if ((aString.length() > 5) && (aString.charAt(5) != '.')
				&& (aString.charAt(5) != '('))
			{
				converted.append(aString.substring(0, 5));
				converted.append('.');
				converted.append(aString.substring(5));
			}
		}
		else if (aString.startsWith("FOLLOWER")
			&& !aString.startsWith("FOLLOWERLIST")
			&& !aString.startsWith("FOLLOWEROF")
			&& !aString.startsWith("FOLLOWERTYPE"))
		{
			if ((aString.length() > 8) && (aString.charAt(8) != '.')
				&& (aString.charAt(8) != '('))
			{
				converted.append(aString.substring(0, 8));
				converted.append('.');
				converted.append(aString.substring(8));
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

	private void replaceTokenForDfor(String aString, BufferedWriter output,
		PlayerCharacter aPC)
	{
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
		String cString = "";
		String cStartLineString = "";
		String cEndLineString = "";
		boolean isDFor = false;

		int i = 0;
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
						isDFor       = true;
						cStepLineMax = getVarValue(aTok.nextToken(), aPC);
						cStepLine    = getVarValue(aTok.nextToken(), aPC);
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
					Logging
						.errorPrint("ExportHandler.replaceTokenForDfor can't handle token number "
							+ i);

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
			cStartLineString = Constants.s_LINE_SEP;
		}

		if ("CRLF".equals(cEndLineString))
		{
			cEndLineString = Constants.s_LINE_SEP;
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

						if ((index < (hString.length() - 1))
							&& (hString.charAt(index + 1) != '.'))
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
						final int cInt =
								iNow + Integer.parseInt(eString.substring(1));
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

			if ((cStepLine > 0) || ((cStepLine == 0) && (x == cStep))
				|| (existsOnly == noMoreItems))
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

	private void replaceTokenOIF(
			String aString,
			java.io.Writer output,
			PlayerCharacter aPC)
	{
		int iParenCount = 0;
		final String[] aT = new String[3];
		int iParamCount = 0;
		int iStart = 4;

		// OIF(expr,truepart,falsepart)
		// {|OIF(HASFEAT:Armor Prof (Light), <b>Yes</b>, <b>No</b>)|}
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
							aT[iParamCount] =
									aString.substring(iStart, i).trim();
							iParamCount++;
							iStart = i + 1;
						}
						else
						{
							Logging.errorPrint("OIF: not enough parameters ("
								+ Integer.toString(iParamCount) + ')');
							for (int j = 0; j < iParamCount; ++j)
							{
								Logging.errorPrint("  " + Integer.toString(j)
									+ ':' + aT[j]);
							}
						}
					}

					break;

				case ',':

					if (iParenCount == 0)
					{
						if (iParamCount < 2)
						{
							aT[iParamCount] =
									aString.substring(iStart, i).trim();
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

		String remainder = "";

		if (iParamCount != 3)
		{
			Logging.errorPrint("OIF: invalid parameter count: " + iParamCount);
		}
		else
		{
			remainder = aString.substring(iStart);

			int i = evaluateExpression(aT[0], aPC) ? 1 : 2; 

			FileAccess.write(output, aT[i]);
		}
			
		if (remainder.length() > 0)
		{
			Logging.errorPrint("OIF: extra characters on line: " + remainder);
			FileAccess.write(output, remainder);
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
			bookName = aPC.getSpellBooks().get(sbookNum);
		}

		canWrite = false;

		final PObject aObject = aPC.getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			final List<CharacterSpell> aList =
					aObject.getSpellSupport().getCharacterSpell(null, bookName,
						levelNum);
			canWrite = !aList.isEmpty();
		}

		return 0;
	}

	private void replaceTokenVar(String aString, PlayerCharacter aPC)
	{
		final StringTokenizer aTok =
				new StringTokenizer(aString.substring(5), ".", false);
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
			canWrite =
					!CoreUtility.doublesEqual(varval.doubleValue(), valval
						.doubleValue());
		}
		else
		{
			canWrite =
					!CoreUtility.doublesEqual(varval.doubleValue(), valval
						.doubleValue());
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
			br = new BufferedReader(
					new InputStreamReader(
						new FileInputStream(templateFile), "UTF-8"));

			boolean betweenPipes = false;
			StringBuffer textBetweenPipes = new StringBuffer();

			Pattern pat1 = Pattern.compile("^\\Q|");
			Pattern pat2 = Pattern.compile("\\Q|\\E$");
			
			String aLine = br.readLine();

			while (aLine != null)
			{
				int lastIndex          = aLine.lastIndexOf('|');

				// not inside a piped enclosed section and no pipe on the
				//  line
				if (!betweenPipes && lastIndex < 0)
				{
					// Allow the output sheet author to control new lines.
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
				
				// inside a pipe enclosed section and no pipes on the line
				// or not in a pipe enclosed section and the only pipe is
				// at char zero. Collect this text (without the pipe)
				// to be passed for replacement later.
				else if (betweenPipes && lastIndex < 0 || !betweenPipes && lastIndex == 0)
				{
					textBetweenPipes.append(aLine.substring(lastIndex + 1));
					betweenPipes = true;
				}

				
				else
				{
					Matcher mat1 = pat1.matcher(textBetweenPipes);
					Matcher mat2 = pat2.matcher(textBetweenPipes);
					boolean startsWithPipe = mat1.find();
					boolean endsWithPipe   = mat2.find();
				
					// not currently in a pipe enclosed section, but first
					// char starts one.
					if (!betweenPipes && startsWithPipe)
					{
						betweenPipes = true;
					}

					betweenPipes = processPipedLine(
							PCs, aLine, textBetweenPipes, out, betweenPipes);

					if (betweenPipes && endsWithPipe)
					{
						betweenPipes = false;
					}
				}

				aLine = br.readLine();
			}
		}
		catch (IOException exc)
		{
			// TODO: If this should be ignored, add a comment here
			// describing why. XXX
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

	private boolean processPipedLine(
			PlayerCharacter[] PCs, String aLine, StringBuffer buf,
			BufferedWriter out, boolean between)
	{
		final StringTokenizer aTok = new StringTokenizer(aLine, "|", false);
		
		boolean noPipes      = aTok.countTokens() == 1;
		boolean betweenPipes = between;

		while (aTok.hasMoreTokens())
		{
			String tok = aTok.nextToken();

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
				// between the pipe charcters so clear out the
				// StringBuffer
				int l = buf.length();
				buf.delete(0, l);

				if (aString.startsWith("FOR."))
				{
					doPartyForToken(PCs, out, aString);
				}
				else
				{

					Matcher mat = Pattern.compile("^(\\d+)").matcher(aString);
					int charNum =
							mat.matches() ? Integer.parseInt(mat.group()) : -1;

					// This seems bizarre since we haven't stripped the 
					// integer from the front of this string which means
					// that it will not be recognised as a token and will
					// just be written to the output verbatim
					if ((charNum >= 0) && (charNum < Globals.getPCList()
							.size()))
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

	private void doPartyForToken(
			PlayerCharacter[] PCs,
			BufferedWriter out,
			String tokenString)
	{
		PartyForParser forParser = new PartyForParser(tokenString, PCs.length);

		int x = 0;
		for (int i = forParser.min(); i < forParser.max(); i++)
		{
			if (x == 0)
			{
				FileAccess.write(out, forParser.startOfLine());
			}

			PlayerCharacter currPC = (0 <= i && i < PCs.length) ? PCs[i] : null;
			// Globals.setCurrentPC(currPC);

			String[] tokens = forParser.tokenString().split("\\\\");

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
			

			// note: I changed this from == to && since I can't see how
			// == could possibly be correct behaviour.  If we were not
			// just printing characters that exist the loop would
			// terminate after printing one character. 
			boolean breakloop = (forParser.existsOnly() && (currPC == null));
			
			if (++x == forParser.step() || breakloop)
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
	 * @return Returns the manualWhitespace.
	 */
	public final boolean isManualWhitespace()
	{
		return manualWhitespace;
	}

	/**
	 * @param manualWhitespace The manualWhitespace to set.
	 */
	public final void setManualWhitespace(boolean manualWhitespace)
	{
		this.manualWhitespace = manualWhitespace;
	}

	/**
	 * Get the token string
	 * @param aPC the PC being exported
	 * @param aString The token string to convert
	 * @return token string
	 */
	public static String getTokenString(
			final PlayerCharacter aPC,
			final String aString)
	{
		final StringTokenizer tok = new StringTokenizer(aString, ".,", false);
		final String firstToken = tok.nextToken();

		//
		// Make sure the token list has been populated
		//
		populateTokenMap();

		final Token token = tokenMap.get(firstToken);
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

		PStringTokenizer(
				String forThisString, 
				String delimiter,
				String ignoreBetweenThis,
				String andThat)
		{
			_forThisString 		= forThisString;
			_delimiter     		= delimiter;
			_ignoreBetweenThis 	= ignoreBetweenThis;
			_andThat 			= andThat;
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

			if (_forThisString.lastIndexOf(_delimiter) == -1)
			{
				aString = _forThisString;
				_forThisString = "";
			}
			else
			{
				int i;
				final StringBuffer b = new StringBuffer();

				int ignores = 0;
				for (i = 0; i < _forThisString.length(); i++)
				{
					if (_forThisString.substring(i).startsWith(_delimiter)
						&& (ignores == 0))
					{
						break;
					}

					if (_forThisString.substring(i).startsWith(
						_ignoreBetweenThis)
						&& (ignores == 0))
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

		PartyForParser(String aString, final Integer numOfPCs)
		{
			pTok =
				new PStringTokenizer(aString.substring(4), ",", "\\\\", "\\\\");
			
			cMin = 
				pTok.hasMoreTokens() ? 
				Delta.decode(pTok.nextToken()) : 
				0;
			
			Integer max =
				pTok.hasMoreTokens() ? 
				Delta.decode(pTok.nextToken()) : 
				100;
			
			cStep = 
				pTok.hasMoreTokens() ? 
				Delta.decode(pTok.nextToken()) : 
				1;
			
			tokenString = 
				pTok.hasMoreTokens() ? 
				pTok.nextToken() : 
				"";
			
			stringForStartOfLine = 
				pTok.hasMoreTokens() ? 
				pTok.nextToken() : 
				"";
			
			stringForEndOfLine = 
				pTok.hasMoreTokens() ? 
				pTok.nextToken() : 
				"";

			existsOnly =
				pTok.hasMoreTokens()
				&& !("0".equals(pTok.nextToken()));

			cMax = (max >= numOfPCs) && existsOnly ? numOfPCs : max;
			

			if (pTok.hasMoreTokens())
			{
				StringBuffer sBuf = new StringBuffer();
				sBuf.append("In Party.print there is an unhandled case in a ");
				sBuf.append("switch (the value is ").append(pTok.nextToken());
				sBuf.append(".");
				String log = sBuf.toString(); 
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

		public String tokenString()
		{
			return tokenString;
		}

		public String startOfLine()
		{
			return stringForStartOfLine;
		}

		public String endOfLine()
		{
			return stringForEndOfLine;
		}

		public boolean existsOnly()
		{
			return existsOnly;
		}
	}
	
}
