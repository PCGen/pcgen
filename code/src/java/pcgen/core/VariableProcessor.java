/*
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 */
package pcgen.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.character.CachedVariable;
import pcgen.core.character.CharacterSpell;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import pcgen.util.PjepPool;

/**
 * {@code VariableProcessor} is the base class for PCGen variable
 * processors. These are classes that convert a formula or variable
 * into a value and are used extensively both in defintions of objects
 * and for output to output sheets.
 *
 *
 */
public abstract class VariableProcessor
{
	/** A simple mathematical operation. */
	private enum MATH_OP
	{
		PLUS, MINUS, MULTIPLY, DIVIDE
	}

    /** The current indenting to be used for debug output of jep evaluations. */
	private String jepIndent = "";
	protected PlayerCharacter pc;

	private int cachePaused;
	private int serial;

	private final Map<String, CachedVariable<String>> sVariableCache = new HashMap<>();
	private final Map<String, CachedVariable<Float>> fVariableCache = new HashMap<>();

	protected Float convertToFloat(String element, String foo)
	{
		Float d = null;
		try
		{
			d = Float.valueOf(foo);
		}
		catch (NumberFormatException nfe)
		{
			// What we got back was not a number
		}

		Float retVal = null;
		if (d != null && !d.isNaN())
		{
			retVal = d;
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(jepIndent + "export variable for: '"
						+ element + "' = " + d);
			}
		}

		return retVal;
	}

	/**
	 * {@code CachableResult} encapsulates a result returned from JEP processing
	 * allowing us to retrieve both the result and its cachability.
	 */
	private static class CachableResult
	{
		final Float result;
		final boolean cachable;

		CachableResult(Float result, boolean cachable)
		{
			this.result = result;
			this.cachable = cachable; // unreliable!
		}
	}

	/**
	 * Create a new Variable Processor instance.
	 * @param pc The character the processor is for.
	 */
	public VariableProcessor(PlayerCharacter pc)
	{
		this.pc = pc;
	}

	/**
	 * Evaluates a variable for this character.
	 * e.g: getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 *
	 * @param aSpell     This is specifically to compute bonuses to CASTERLEVEL
	 *                   for a specific spell.
	 * @param varString  The variable to be evaluated
	 * @param src        The source within which the variable is evaluated
	 * @param spellLevelTemp The temporary spell level
	 * @return The value of the variable
	 */
	public Float getVariableValue(final CharacterSpell aSpell, String varString, String src, int spellLevelTemp)
	{
		Float result = getJepOnlyVariableValue(aSpell, varString, src, spellLevelTemp);

		if (null == result)
		{
			result = processBrokenParser(aSpell, varString, src, spellLevelTemp);

			String cacheString = makeCacheString(aSpell == null ? null : aSpell, varString, src, spellLevelTemp);

			addCachedVariable(cacheString, result);
		}

		return result;
	}

	/**
	 * Evaluates a JEP variable for this character.
	 * e.g: getJepOnlyVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 *
	 * @param aSpell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @param varString The variable to be evaluated
	 * @param src     The source within which the variable is evaluated
	 * @param spellLevelTemp The temporary spell level
	 * @return The value of the variable, or null if the formula is not JEP
	 */
	public Float getJepOnlyVariableValue(final CharacterSpell aSpell, String varString, String src, int spellLevelTemp)
	{
		// First try to just parse it as a number.
		try
		{
			return Float.valueOf(varString);
		}
		catch (NumberFormatException e)
		{
			// Nothing to handle here, we're attempting to see if varString was a
			// number, If we got here it wasn't
		}

		String cacheString = makeCacheString(aSpell == null ? null : aSpell, varString, src, spellLevelTemp);

		Float total = getCachedVariable(cacheString);
		if (total != null)
		{
			return total;
		}

		CachableResult cRes = processJepFormula(aSpell, varString, src);
		if (cRes != null)
		{
			if (cRes.cachable)
			{
				addCachedVariable(cacheString, cRes.result);
			}
			return cRes.result;
		}
		return null;
	}

	private String makeCacheString(CharacterSpell aSpell, String varString, String src, int spellLevelTemp)
	{
		StringBuilder cS = new StringBuilder(varString).append('#').append(src);

		if (aSpell != null)
		{
			if (aSpell.getSpell() != null)
			{
				cS.append(aSpell.getSpell().getKeyName());
			}
			cS.append(aSpell.getFixedCasterLevel());
		}

		if (spellLevelTemp > 0)
		{
			cS.append(spellLevelTemp);
		}

		return cS.toString();
	}

	/**
	 * Evaluate the variable using the old non-JEP variable parser. Use of this
	 * parser is being phased out.
	 *
	 * @param aSpell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @param aString The variable to be evaluated
	 * @param src     The source within which the variable is evaluated
	 * @param spellLevelTemp The temporary spell level
	 * @return The value of the variable
	 */
	private Float processBrokenParser(final CharacterSpell aSpell, String aString, String src, int spellLevelTemp)
	{
		Float total = (float) 0.0;
		aString = aString.toUpperCase();
		src = src.toUpperCase();

		while (aString.lastIndexOf('(') >= 0)
		{
			final int x = CoreUtility.innerMostStringStart(aString);
			final int y = CoreUtility.innerMostStringEnd(aString);

			if (y < x)
			{
				Logging.errorPrint("Missing closing parenthesis: " + aString);

				return total;
			}

			final String bString = aString.substring(x + 1, y);
			aString = aString.substring(0, x) + getVariableValue(aSpell, bString, src, spellLevelTemp)
				+ aString.substring(y + 1);
		}

		final String delimiter = "+-/*";
		String valString = "";
		MATH_OP mode = MATH_OP.PLUS;
		MATH_OP nextMode = MATH_OP.PLUS;

		if (aString.startsWith(".IF."))
		{
			final StringTokenizer aTok = new StringTokenizer(aString.substring(4), ".", true);
			StringBuilder bString = new StringBuilder();
			Float val1 = null; // first value
			Float val2 = null; // other value in comparison
			Float valt = null; // value if comparison is true
			final Float valf; // value if comparison is false
			int comp = 0;

			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();

				if ("GT".equals(cString) || "GTEQ".equals(cString) || "EQ".equals(cString) || "LTEQ".equals(cString)
					|| "LT".equals(cString))
				{
					// Truncate final . character
					val1 = getVariableValue(aSpell, bString.substring(0, bString.length() - 1), src, spellLevelTemp); 
					aTok.nextToken(); // discard next . character
					bString = new StringBuilder();

					if ("LT".equals(cString))
					{
						comp = 1;
					}
					else if ("LTEQ".equals(cString))
					{
						comp = 2;
					}
					else if ("EQ".equals(cString))
					{
						comp = 3;
					}
					else if ("GT".equals(cString))
					{
						comp = 4;
					}
					else if ("GTEQ".equals(cString))
					{
						comp = 5;
					}
				}
				else if ("THEN".equals(cString))
				{
					// Truncate final . character
					val2 = getVariableValue(aSpell, bString.substring(0, bString.length() - 1), src, spellLevelTemp); 
					aTok.nextToken(); // discard next . character
					bString = new StringBuilder();
				}
				else if ("ELSE".equals(cString))
				{
					// Truncate final . character
					valt = getVariableValue(aSpell, bString.substring(0, bString.length() - 1), src, spellLevelTemp);
					aTok.nextToken(); // discard next . character
					bString = new StringBuilder();
				}
				else
				{
					bString.append(cString);
				}
			}

			if ((val1 != null) && (val2 != null) && (valt != null))
			{
				valf = getVariableValue(aSpell, bString.toString(), src, spellLevelTemp);
				total = valt;

				switch (comp)
				{
					case 1: // LT

						if (val1.doubleValue() >= val2.doubleValue())
						{
							total = valf;
						}

						break;

					case 2: // LTEQ

						if (val1.doubleValue() > val2.doubleValue())
						{
							total = valf;
						}

						break;

					case 3: // EQ

						if (!CoreUtility.doublesEqual(val1.doubleValue(), val2.doubleValue()))
						{
							total = valf;
						}

						break;

					case 4: // GT

						if (val1.doubleValue() <= val2.doubleValue())
						{
							total = valf;
						}

						break;

					case 5: // GTEQ

						if (val1.doubleValue() < val2.doubleValue())
						{
							total = valf;
						}

						break;

					default:
						Logging.errorPrint("ERROR - badly formed statement:" + aString + ':' + val1.toString() + ':'
							+ val2.toString() + ':' + comp);

						return (float) 0.0;
				}

				return total;
			}
		}

		for (int i = 0; i < aString.length(); ++i)
		{
			valString += aString.substring(i, i + 1);

			if (
			// end of string
			(i == (aString.length() - 1))

				// have found one of +, -, *, /
				|| (delimiter.lastIndexOf(aString.charAt(i)) > -1))
			{
				if ((valString.length() == 1) && (delimiter.lastIndexOf(aString.charAt(i)) > -1))
				{
					continue;
				}

				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}

				final Float tmp = lookupVariable(valString, src, aSpell);
				if (tmp != null)
				{
					valString = tmp.toString();
				}

				if (i < aString.length())
				{
					if (!aString.isEmpty() && aString.charAt(i) == '+')
					{
						nextMode = MATH_OP.PLUS;
					}
					else if (!aString.isEmpty() && aString.charAt(i) == '-')
					{
						nextMode = MATH_OP.MINUS;
					}
					else if (!aString.isEmpty() && aString.charAt(i) == '*')
					{
						nextMode = MATH_OP.MULTIPLY;
					}
					else if (!aString.isEmpty() && aString.charAt(i) == '/')
					{
						nextMode = MATH_OP.DIVIDE;
					}
				}

				if (!valString.isEmpty())
				{
					float valFloat = 0.0f;
					try
					{
						valFloat = Float.parseFloat(valString);
					}
					catch (NumberFormatException exc)
					{
						// Don't care, as it's just zero
						//Logging.debugPrint("Will use default for total: " + total, exc);
					}

					switch (mode)
					{
						case PLUS:
							total += valFloat;

							break;

						case MINUS:
							total -= valFloat;

							break;

						case MULTIPLY:
							total *= valFloat;

							break;

						case DIVIDE:
							total /= valFloat;

							break;

						default:
							Logging.errorPrint(
								"In PlayerCharacter.getVariableValue the mode " + mode + " is unsupported.");

							break;
					}
				}

				mode = nextMode;
				nextMode = MATH_OP.PLUS;
				valString = "";
			}
		}

		return total;
	}

	/**
	 * Evaluate the forumla using the JEP parser. This will always be tried before
	 * using the old non-JEP parser and null will be returned if the forumla is not
	 * a recognised JEP formula.
	 *
	 * @param spell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @param formula The formula to be evaluated
	 * @param src     The source within which the variable is evaluated
	 * @return The value of the variable encapsulated in a CachableResult
	 */
	private CachableResult processJepFormula(final CharacterSpell spell, final String formula, final String src)
	{
		final String DEBUG_FORMULA_PREFIX = "CLASSLEVEL";
		if (Logging.isLoggable(Logging.DEBUG) && formula.startsWith(DEBUG_FORMULA_PREFIX))
		{
			Logging.debugPrint(jepIndent + "getJepVariable: " + formula);
		}
		jepIndent += "    ";
		PJEP parser = null;

		try
		{
			parser = PjepPool.getInstance().aquire(this, src);
			parser.parseExpression(formula);
			if (parser.hasError())
			{
				if (Logging.isLoggable(Logging.DEBUG) && formula.startsWith(DEBUG_FORMULA_PREFIX))
				{
					Logging.debugPrint(jepIndent + "not a JEP expression: " + formula);
				}
				return null;
			}

            for (final String element : (Iterable<String>) parser.getSymbolTable().keySet()) {
                if ("e".equals(element) || "FALSE".equals(element) || "pi".equals(element) || "TRUE".equals(element)) {
                    continue;
                }

                Float d = lookupVariable(element, src, spell);
                if (d != null) {
                    parser.addVariable(element, d.doubleValue());
                } else {
                    // we could not get a value for all of the variables, so it must not have been a JEP function
                    // after all...
                    return null;
                }
            }

			final Object result = parser.getValueAsObject();
			if (result != null)
			{
				if (Logging.isLoggable(Logging.DEBUG) && formula.startsWith(DEBUG_FORMULA_PREFIX))
				{
					Logging.debugPrint(jepIndent + "Result '" + formula + "' = " + result);
				}
				try
				{
					return new CachableResult(Float.valueOf(result.toString()), parser.isResultCachable());
				}
				catch (NumberFormatException nfe)
				{
					if (Logging.isLoggable(Logging.DEBUG) && formula.startsWith(DEBUG_FORMULA_PREFIX))
					{
						Logging.debugPrint(jepIndent + "Result '" + formula + "' = " + result + " was not a number...");
					}
					return null;
				}
			}
			if (parser.hasError())
			{
				Logging.errorPrint("Failed to process formula " + formula + " due to error: " + parser.getErrorInfo());
			}
			if (Logging.isLoggable(Logging.DEBUG) && formula.startsWith(DEBUG_FORMULA_PREFIX))
			{
				Logging.debugPrint(jepIndent + "Result '" + formula + "' was null...");
			}
			return null;
		}
		finally
		{
			if (jepIndent != null && jepIndent.length() >= 4)
			{
				jepIndent = jepIndent.substring(4);
			}
			PjepPool.getInstance().release(parser);
		}
	}

	abstract Float getInternalVariable(final CharacterSpell aSpell, String valString, final String src);

	/**
	 * Get a value for the term as evaluated in the context of the PC that
	 * owns this VariableEvaluator (getPc()) the term itself and the source
	 * of the term e.g. RACE:Halfling.  If the term is CASTERLEVEL the
	 * Spell parameter is also used, if not it is ignored and may be null.  
	 * 
	 * @param term
	 *          The string to be evaluated
	 * @param src
	 *          The source of the term
	 * @param spell
	 *          A spell which is only used if the term is related to CASTERLEVEL
	 * 
	 * @return a Float value for this term
	 */
	public Float lookupVariable(String term, String src, CharacterSpell spell)
	{
		Float retVal = null;
		if (pc.hasVariable(term))
		{
			final Float value = pc.getVariable(term, true);
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(jepIndent + "variable for: '" + term
						+ "' = " + value);
			}
			retVal = (float) value.doubleValue();
		}

		if (retVal == null)
		{
			retVal = getInternalVariable(spell, term, src);
		}

		if (retVal == null)
		{
			final String evReturn = getExportVariable(term);
			if (evReturn != null)
			{
				retVal = convertToFloat(term, evReturn);
			}
		}

		return retVal;
	}

	/**
	 * Attempt to retrieve a cached value of a variable.
	 *
	 * @param lookup The name of the variable to be checked.
	 * @return The value of the variable
	 */
	public Float getCachedVariable(final String lookup)
	{
		if (isCachePaused())
		{
			return null;
		}

		final CachedVariable<Float> cached = fVariableCache.get(lookup);

		if (cached != null)
		{
			if (cached.getSerial() >= getSerial())
			{
				return cached.getValue();
			}
			fVariableCache.remove(lookup);
		}
		return null;
	}

	/**
	 * Add a new variable to the cache.
	 *
	 * @param lookup The name of the variable to be added.
	 * @param value The value of the variable
	 */
	public void addCachedVariable(final String lookup, final Float value)
	{
		if (isCachePaused())
		{
			return;
		}
		final CachedVariable<Float> cached = new CachedVariable<>();
		cached.setSerial(getSerial());
		cached.setValue(value);
		//		if (lookup.equals("floor(SCORE/2)-5#STAT:CHA"))
		//		{
		//			Logging.errorPrint("At " + cached.getSerial() + " caching " + lookup + " of " + value);
		//		}

		fVariableCache.put(lookup, cached);
	}

	/**
	 * Restart caching of variable values. Used after caching has
	 * been paused by a call to pauseCache.
	 */
	public void restartCache()
	{
		serial = cachePaused;
		cachePaused = 0;
	}

	/**
	 * Pause caching of variable values. Normally used when making temporary
	 * changes to a character.
	 */
	public void pauseCache()
	{
		cachePaused = serial;
	}

	/**
	 * Identify if the cache is current paused or not.
	 * @return True if the cache is currently paused, false otherwise.
	 */
	public boolean isCachePaused()
	{
		return cachePaused > 0;
	}

	/**
	 * Retrieve the current cache serial. This value identifies the currency
	 * of the cache and can be compared against the serial of entries in the
	 * cache to detemrine if they have expired.
	 *
	 * @return The current cache serial.
	 */
	public int getSerial()
	{
		return serial;
	}

	/**
	 * Set the current cache serial. This value identifies the currency
	 * of the cache and is generally set to match the PC's serial value.
	 * @param serial The new serial value to set.
	 */
	public void setSerial(int serial)
	{
		this.serial = serial;
	}

	/**
	 * Retrieve a value from the cache. This method will not return
	 * expired values, but instead removes them from the cache if
	 * they are found.
	 *
	 * @param lookup The name of the variable (or the formula) to retrieve.
	 * @return String The value of the variable, or null if a current value is not present in the cache.
	 */
	String getCachedString(final String lookup)
	{
		if (isCachePaused())
		{
			return null;
		}

		final CachedVariable<String> cached = sVariableCache.get(lookup);

		if (cached != null)
		{
			if (cached.getSerial() >= getSerial())
			{
				return cached.getValue();
			}
			sVariableCache.remove(lookup);
		}
		return null;
	}

	/**
	 * Add a value to the cache. If the cache is paused, the value will
	 * not be added.
	 *
	 * @param lookup The name of the variable (or the formula) to cache.
	 * @param value  The value of the variable or formula.
	 */
	public void addCachedString(final String lookup, final String value)
	{
		if (isCachePaused())
		{
			return;
		}
		final CachedVariable<String> cached = new CachedVariable<>();
		cached.setSerial(getSerial());
		cached.setValue(value);

		sVariableCache.put(lookup, cached);
	}

	/**
	 * Returns a float value representing a variable used by the
	 * export process, for example, any token that is used in an outputsheet.
	 *
	 * @param valString   The name of the token to process. i.e. "LOCK.CON"
	 * @return   The evaluated value of valString as a String.
	 */
	public String getExportVariable(String valString)
	{
		final StringWriter sWriter = new StringWriter();
		final BufferedWriter aWriter = new BufferedWriter(sWriter);
		final ExportHandler aExport = ExportHandler.createExportHandler(new File(""));
		aExport.replaceTokenSkipMath(pc, valString, aWriter);
		sWriter.flush();

		try
		{
			aWriter.flush();
		}
		catch (IOException e)
		{
			Logging.errorPrint("Couldn't flush the StringWriter used in PlayerCharacter.getVariableValue.", e);
		}

		final String bString = sWriter.toString();

		String result;
		try
		{
			// Float values
			result = String.valueOf(Float.parseFloat(bString));
		}
		catch (NumberFormatException e)
		{
			// String values
			result = bString;
		}
		return result;
	}

	/**
	 * Retrieve the PlayerCharacter object that this VariableProcessor
	 * instance serves.
	 *
	 * @return The PlayerCharacter instance.
	 */
	public PlayerCharacter getPc()
	{
		return pc;
	}
}
