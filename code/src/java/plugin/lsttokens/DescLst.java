/*
 * Created on Aug 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Description;
import pcgen.core.PObject;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Handles DESC token processing
 * 
 * @author djones4
 */
public class DescLst implements GlobalLstToken
{
	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "DESC"; //$NON-NLS-1$
	}

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject, java.lang.String, int)
	 */
	public boolean parse(final PObject obj, final String value,
		@SuppressWarnings("unused")
		int anInt)
	{
		if (value.startsWith(".CLEAR")) //$NON-NLS-1$
		{
			if (value.equals(".CLEAR")) //$NON-NLS-1$
			{
				obj.removeAllDescriptions();
			}
			else
			{
				obj.removeDescription(value.substring(7));
			}
			return true;
		}
		obj.addDescription(parseDescription(value));
		return true;
	}

	/**
	 * Parses the DESC tag into a Description object.
	 * 
	 * @param aDesc The LST tag
	 * @return A <tt>Description</tt> object
	 */
	public Description parseDescription(final String aDesc)
	{
		final StringTokenizer tok = new StringTokenizer(aDesc, Constants.PIPE);

		final Description desc =
				new Description(EntityEncoder.decode(tok.nextToken()));
		
		boolean isPre = false;
		while (tok.hasMoreTokens())
		{
			final String token = tok.nextToken();
			if (PreParserFactory.isPreReqString(token)) //$NON-NLS-1$
			{
				desc.addPrerequisites(token, '<');
				isPre = true;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": " + aDesc);
					Logging.errorPrint("  PRExxx must be at the END of the Token");
					isPre = false;
				}
				desc.addVariable(token);
			}
		}

		return desc;
	}
}
