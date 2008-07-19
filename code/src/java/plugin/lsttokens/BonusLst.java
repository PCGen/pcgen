/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

import java.util.regex.Pattern;

/**
 * @author djones4
 */
public class BonusLst implements GlobalLstToken
{

	/*
	 * FIXME When this token is converted to the new syntax, a change needs to
	 * take place in BonusToken, which is currently calling PObjectLoader
	 */
	/*
	 * Template's LevelToken adjustment performed by adding getBonusList() to
	 * PCTemplate
	 * 
	 * TODO need to do an update (to getBonusList()?) in PCClass once this is a
	 * new token
	 */
	/**
	 * Returns token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "BONUS";
	}

	/**
	 * Parse BONUS token and use it to add a bonus to a PObject
	 * 
	 * @param obj the object to make the bonus a part of
	 * @param value the text of the bonus 
	 * @param anInt the level to add the bonus at 
	 * @return true if the bonus added to the PObject is non null or false otherwise
	 */
	public boolean parse(final PObject obj,
	                     final String value, 
	                     final int anInt)
	{
		final String v = value.replaceAll(Pattern.quote("<this>"), obj.getKeyName());
		return (anInt > -9) ?
		       obj.addBonusList(anInt + "|" + v) :
		       obj.addBonusList(v);
	}
}
