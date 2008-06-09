/*
 * AbilityInfoPanel.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.util.Logging;

/**
 * This class represents a generic description field.
 * 
 * <p>The class supports the description object having one or more prerequisites
 * as well as performing variable substitution on the string itself.
 * 
 * <p>Variable substitution is performed by replacing a placeholder indicated
 * by %# with the #th variable in the variable list.  For example, the string
 * <br /><code>&quot;This is %1 variable %3 %2&quot;</code>
 * <br />would be replaced with the string &quot;This is a variable substitution
 * string&quot; if the variable list was &quot;a&quot;,&quot;string&quot;, 
 * &quot;substitution&quot;.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class Description extends ConcretePrereqObject
{
	private List<String> theComponents = new ArrayList<String>();
	private List<String> theVariables = null;
	
	private static final String VAR_NAME = "%NAME"; //$NON-NLS-1$
	private static final String VAR_CHOICE = "%CHOICE"; //$NON-NLS-1$
	private static final String VAR_LIST = "%LIST"; //$NON-NLS-1$
	private static final String VAR_FEATS = "%FEAT="; //$NON-NLS-1$
	
	private static final String VAR_MARKER = "$$VAR:"; //$NON-NLS-1$
	
	/**
	 * Default constructor.
	 * 
	 * @param aString The description string.
	 */
	public Description( final String aString )
	{
		int currentInd = 0;
		int percentInd = -1;
		while ( (percentInd = aString.indexOf('%', currentInd)) != -1 )
		{
			final String preText = aString.substring(currentInd, percentInd);
			if ( preText.length() > 0 )
			{
				theComponents.add(preText);
			}
			if ( percentInd == aString.length() - 1)
			{
				theComponents.add("%"); //$NON-NLS-1$
				return;
			}
			if ( aString.charAt(percentInd + 1) == '{' )
			{
				// This is a bracketed placeholder.  The replacement parameter
				// is contained within the {}
				currentInd = aString.indexOf('}', percentInd + 1) + 1;
				final String replacement = aString.substring(percentInd + 1, currentInd);
				// For the time being we will only support numerics here.
				try
				{
					Integer.parseInt(replacement);
				}
				catch (NumberFormatException nfe )
				{
					Logging.errorPrintLocalised("Errors.Description.InvalidVariableReplacement", replacement); //$NON-NLS-1$
				}
				theComponents.add(VAR_MARKER + replacement);
			}
			else if ( aString.charAt(percentInd + 1) == '%' )
			{
				// This is an escape sequence so we can actually print a %
				currentInd = percentInd + 2;
				theComponents.add("%"); //$NON-NLS-1$
			}
			else
			{
				// In this case we have an unbracketed placeholder.  We will
				// walk the string until such time as we no longer have a number
				currentInd = percentInd + 1;
				while ( currentInd < aString.length() )
				{
					final char val = aString.charAt(currentInd);
					try
					{
						Integer.parseInt(String.valueOf(val));
						currentInd++;
					}
					catch (NumberFormatException nfe)
					{
						break;
					}
				}
				if ( currentInd > percentInd + 1 )
				{
					theComponents.add(VAR_MARKER + aString.substring(percentInd+1, currentInd));
				}
				else
				{
					// We broke out of the variable finding loop without finding
					// even a single integer.  Assume we have a DESC field that
					// is using a % unescaped.
					theComponents.add(aString.substring(percentInd, percentInd+1));
				}
			}
		}
		theComponents.add(aString.substring(currentInd));
	}
	
	/**
	 * Adds a variable to use in variable substitution.
	 * 
	 * @param aVariable
	 */
	public void addVariable( final String aVariable )
	{
		if ( theVariables == null )
		{
			theVariables = new ArrayList<String>();
		}
		theVariables.add( aVariable );
	}
	
	/**
	 * Gets the description string after having tested all prereqs and 
	 * substituting all variables.
	 * 
	 * @param aPC The PlayerCharacter used to evaluate formulas.
	 * 
	 * @return The fully substituted description string.
	 */
	public String getDescription( final PlayerCharacter aPC, PObject theOwner )
	{
		final StringBuffer buf = new StringBuffer();
		
		if (this.qualifies(aPC))
		{
			if ( theOwner instanceof Ability )
			{
				theOwner = aPC.getAbilityMatching((Ability)theOwner);
			}
			for ( final String comp : theComponents )
			{
				if ( comp.startsWith(VAR_MARKER) )
				{
					final int ind = Integer.parseInt(comp.substring(VAR_MARKER.length()));
					if ( theVariables == null || ind > theVariables.size() )
					{
						buf.append(Constants.EMPTY_STRING);
						continue;
					}
					final String var = theVariables.get(ind - 1);
					if ( var.equals(VAR_NAME) )
					{
						if ( theOwner != null )
						{
							buf.append(theOwner.getOutputName());
						}
					}
					else if ( var.equals(VAR_CHOICE) )
					{
						if ( theOwner != null && theOwner.getAssociatedCount() > 0 )
						{
							buf.append(theOwner.getAssociated(0));
						}
					}
					else if ( var.equals(VAR_LIST) )
					{
						if ( theOwner != null )
						{
							for ( int i = 0; i < theOwner.getAssociatedCount(true); i++ )
							{
								if ( i > 0 )
								{
									if (theOwner.getAssociatedCount(true) != 2)
									{
										buf.append(Constants.COMMA + ' ');
									}
									if (i == theOwner.getAssociatedCount(true) - 1)
									{
										buf.append(" and ");
									}
								}
								buf.append(theOwner.getAssociated(i, true));
							}
						}
					}
					else if ( var.startsWith(VAR_FEATS) )
					{
						final String featName = var.substring(VAR_FEATS.length());
						if (featName.startsWith("TYPE=") || featName.startsWith("TYPE."))
						{
							final List<Ability> feats = aPC.getAggregateAbilityList(AbilityCategory.FEAT);
							boolean first = true;
							for ( final Ability feat : feats )
							{
								if (feat.isType(featName.substring(5)))
								{
									if (!first)
									{
										buf.append(Constants.COMMA + ' ');
									}
									buf.append(feat.getDescription(aPC));
									first = false;
								}
							}
						}
						else
						{
							final Ability feat = aPC.getAbilityKeyed(AbilityCategory.FEAT, featName);
							buf.append(feat.getDescription(aPC));
						}
					}
					else if ( var.startsWith("\"") ) //$NON-NLS-1$
					{
						buf.append(var.substring(1, var.length() - 1));
					}
					else
					{
						buf.append(aPC.getVariableValue(var, "Description").intValue()); //$NON-NLS-1$
					}
				}
				else
				{
					buf.append(comp);
				}
			}
		}
		return buf.toString();
	}
	
	/**
	 * Gets the Description tag in PCC format.
	 * 
	 * @return A String in LST file format for this description.
	 * 
	 * @see pcgen.cdom.base.PrereqObject#getPCCText()
	 */
	@Override
	public String getPCCText()
	{
		final StringBuffer buf = new StringBuffer();
		
		for ( final String str : theComponents )
		{
			if ( str.startsWith(VAR_MARKER) )
			{
				final int ind = Integer.parseInt(str.substring(VAR_MARKER.length()));
				buf.append('%' + String.valueOf(ind));
			}
			else
			{
				buf.append(str);
			}
		}
		if ( theVariables != null )
		{
			for ( final String var : theVariables )
			{
				buf.append(Constants.PIPE);
				buf.append(var);
			}
		}
		
		buf.append(super.getPCCText());
		return buf.toString();
	}
	
	@Override
	public String toString()
	{
		return getPCCText();
	}
}
