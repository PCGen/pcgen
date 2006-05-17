/*
 * VariableProcessor.java
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
 * Created on 13-Dec-2004
 */
package pcgen.core;

import pcgen.core.character.CachedVariable;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import pcgen.util.PjepPool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <code>VariableProcessor</code> is the base class for PCGen variable
 * processors. These are classes that convert a formula or variable
 * into a value and are used extensively both in defintions of objects
 * and for output to output sheets.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Chris Ward <frugal@purplewombat.co.uk>
 * @version $Revision$
 */
public abstract class VariableProcessor
{
	/** The current indenting to be used for debug output of jep evaluations. */
	protected String jepIndent = "";
	//TODO: This never gets set - perhaps it can be removed?
	protected int decrement;
	private PlayerCharacter pc;

	/**
	 * <code>CachableResult</code> encapsulates a result returned from JEP processing
	 * allowing us to retrieve both the result and its cachability.
	 */
	private static class CachableResult
	{
		final Float result;
		final boolean cachable;

		CachableResult(Float result, boolean cachable)
		{
			this.result = result;
			this.cachable = cachable;
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
	 * @param aSpell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @param aString The variable to be evaluated
	 * @param src     The source within which the variable is evaluated
	 * @param spellLevelTemp The temporary spell level
	 * @return The value of the variable
	 */
	public Float getVariableValue(final Spell aSpell, String aString, String src, int spellLevelTemp)
	{
		// First try to just parse it as a number.
		try
		{
			final Float total = new Float(aString);
			return total;
		}
		catch (NumberFormatException e)
		{
			// Nothing to handle here, we're attempting to see if aString was a
			// number, If we got here it wasn't
		}


		String cacheString = aString+"#"+src;
		if (aSpell != null)
		{
			cacheString += aSpell.getKeyName();
		}

		if (spellLevelTemp > 0)
		{
			cacheString += spellLevelTemp;
		}

		Float total = getCachedVariable(cacheString);
		if (total != null)
		{
			return total;
		}


		CachableResult cRes = processJepFormula(aSpell, aString, src);
		if (cRes != null)
		{
			if (cRes.cachable)
			{
				addCachedVariable(cacheString, cRes.result);
			}
			return cRes.result;
		}


		total = processBrokenParser(aSpell, aString, src, spellLevelTemp);
		addCachedVariable(cacheString, total);
		return total;
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
	private Float processBrokenParser(final Spell aSpell, String aString, String src, int spellLevelTemp)
	{
		Float total = new Float(0.0);
		Float total1 = null;
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
			aString = aString.substring(0, x) + getVariableValue(aSpell, bString, src, spellLevelTemp) + aString.substring(y + 1);
		}

		final String delimiter = "+-/*";
		String valString = "";
		int mode = 0; //0=plus, 1=minus, 2=mult, 3=div
		int nextMode = 0;
		int endMode = 0; //1,11=min, 2,12=max, 3,13=req, 10 = int

		if (aString.startsWith(".IF."))
		{
			final StringTokenizer aTok = new StringTokenizer(aString.substring(4), ".", true);
			String bString = "";
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
					val1 = getVariableValue(aSpell, bString.substring(0, bString.length() - 1), src, spellLevelTemp); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";

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
					val2 = getVariableValue(aSpell, bString.substring(0, bString.length() - 1), src, spellLevelTemp); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else if ("ELSE".equals(cString))
				{
					valt = getVariableValue(aSpell, bString.substring(0, bString.length() - 1), src, spellLevelTemp); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else
				{
					bString += cString;
				}
			}

			if ((val1 != null) && (val2 != null) && (valt != null))
			{
				valf = getVariableValue(aSpell, bString, src, spellLevelTemp);
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
						Logging.errorPrint("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":"
							+ val2.toString() + ":" + comp);

						return new Float(0.0);
				}

				return total;
			}
		}

		for (int i = 0; i < aString.length(); ++i)
		{
			valString += aString.substring(i, i + 1);

			if ((i == (aString.length() - 1)) || (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				|| ((valString.length() > 3)
				&& (valString.endsWith("MIN") || (!valString.startsWith("MODEQUIP") && valString.endsWith("MAX"))
				|| valString.endsWith("REQ"))))
			{
				if ((valString.length() == 1) && (delimiter.lastIndexOf(aString.charAt(i)) > -1))
				{
					continue;
				}

				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}


				final Float tmp= lookupVariable(valString, src, aSpell);
				if (tmp != null)
				{
					valString = tmp.toString();
				}

				if (i < aString.length())
				{
					if (valString.endsWith(".TRUNC"))
					{
						valString = String.valueOf(getVariableValue(aSpell, valString.substring(0, valString.length() - 6), "", spellLevelTemp)
								.intValue());
					}

					if (valString.endsWith(".INTVAL"))
					{
						valString = getVariableValue(aSpell, valString.substring(0, valString.length() - 7), "", spellLevelTemp).toString();
						endMode += 10;
					}

					if (valString.endsWith("MIN"))
					{
						valString = getVariableValue(aSpell, valString.substring(0, valString.length() - 3), "", spellLevelTemp).toString();
						nextMode = 0;
						++endMode;
					}
					else if (valString.endsWith("MAX"))
					{
						valString = getVariableValue(aSpell, valString.substring(0, valString.length() - 3), "", spellLevelTemp).toString();
						nextMode = 0;
						endMode += 2;
					}
					else if (valString.endsWith("REQ"))
					{
						valString = getVariableValue(aSpell, valString.substring(0, valString.length() - 3), "", spellLevelTemp).toString();
						nextMode = 0;
						endMode += 3;
					}
					else if ((aString.length() > 0) && (aString.charAt(i) == '+'))
					{
						nextMode = 0;
					}
					else if ((aString.length() > 0) && (aString.charAt(i) == '-'))
					{
						nextMode = 1;
					}
					else if ((aString.length() > 0) && (aString.charAt(i) == '*'))
					{
						nextMode = 2;
					}
					else if ((aString.length() > 0) && (aString.charAt(i) == '/'))
					{
						nextMode = 3;
					}
				}

				if (valString.length() > 0)
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
						case 0:
							total = new Float(total.floatValue() + valFloat);

							break;

						case 1:
							total = new Float(total.floatValue() - valFloat);

							break;

						case 2:
							total = new Float(total.floatValue() * valFloat);

							break;

						case 3:
							total = new Float(total.floatValue() / valFloat);

							break;

						default:
							Logging.errorPrint("In PlayerCharacter.getVariableValue the mode " + mode
								+ " is unsupported.");

							break;
					}
				}

				mode = nextMode;
				nextMode = 0;
				valString = "";

				if ((total1 == null) && ((endMode % 10) != 0))
				{
					total1 = total;
					total = new Float(0.0);
				}
			}
		}

		if (total1 != null)
		{
			if ((endMode % 10) == 1)
			{
				total = new Float(Math.min(total.doubleValue(), total1.doubleValue()));
			}

			if ((endMode % 10) == 2)
			{
				total = new Float(Math.max(total.doubleValue(), total1.doubleValue()));
			}

			if ((endMode % 10) == 3)
			{
				if (total1.doubleValue() < total.doubleValue())
				{
					total = new Float(0.0);
				}
				else
				{
					total = total1;
				}
			}
		}

		if ((endMode / 10) > 0)
		{
			total = new Float(total.intValue());
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
	private CachableResult processJepFormula(final Spell spell, final String formula, final String src)
	{
		Logging.debugPrint(jepIndent + "getJepVariable: " + formula);
		jepIndent += "    ";
		PJEP parser = null;

		try
		{
			parser = PjepPool.getInstance().aquire(this, src);
			parser.parseExpression(formula);
			if (parser.hasError())
			{
				Logging.debugPrint(jepIndent + "not a JEP expression: " + formula);
				return null;
			}

			for (Iterator iter = parser.getSymbolTable().keySet().iterator(); iter.hasNext();)
			{
				final String element = (String) iter.next();
				if (element.equals("TRUE") || element.equals("FALSE") || element.equals("e") || element.equals("pi"))
				{
					continue;
				}

				Float d = lookupVariable(element, src, spell);
				if (d != null)
				{
					parser.addVariable(element, d.doubleValue());
				}
				else
				{
					// we could not get a value for all of the variables, so it must not have been a JEP function
					// after all...
					return null;
				}
			}

			final Object result = parser.getValueAsObject();
			if (result != null)
			{
				Logging.debugPrint(jepIndent + "Result '" + formula + "' = " + result);
				try
				{
					return new CachableResult(new Float(result.toString()),
						parser.isResultCachable());
				}
				catch (NumberFormatException nfe)
				{
					Logging.debugPrint(jepIndent + "Result '" + formula + "' = " + result + " was not a number...");
					return null;
				}
			}
			Logging.debugPrint(jepIndent + "Result '" + formula + "' was null...");
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


	/**
	 * Lookup the value of a variable
	 *
	 * @param element The variable to be evaluated.
	 * @param src     The source within which the variable is evaluated
	 * @param spell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @return The value of the variable
	 */
	abstract Float lookupVariable(String element, String src, Spell spell);



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
		final CachedVariable cached = (CachedVariable) variableCache.get(lookup);
		if (cached != null)
		{
			if (cached.getSerial()>=getSerial())
			{
				return (Float) cached.getValue();
			}
			variableCache.remove(lookup);
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
		final CachedVariable cached = new CachedVariable();
		cached.setSerial( getSerial() );
		cached.setValue(value);

		variableCache.put(lookup, cached);
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
		return cachePaused>0;
	}

	private int cachePaused;
	private int serial = 0;
	private Map variableCache = new HashMap();

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
		final CachedVariable cached = (CachedVariable) variableCache.get(lookup);
		if (cached != null)
		{
			if (cached.getSerial()>=getSerial())
			{
				return cached.getValue().toString();
			}
			variableCache.remove(lookup);
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
		final CachedVariable cached = new CachedVariable();
		cached.setSerial( getSerial() );
		cached.setValue(value);

		variableCache.put(lookup, cached);
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
		final ExportHandler aExport = new ExportHandler(new File(""));
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
		return valString;
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
