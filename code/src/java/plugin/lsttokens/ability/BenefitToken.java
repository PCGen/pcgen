package plugin.lsttokens.ability;

import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.Description;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * This class deals with the BENEFIT Token
 */
public class BenefitToken implements AbilityLstToken
{

	public String getTokenName()
	{
		return "BENEFIT";
	}

	public boolean parse(Ability ability, String value)
	{
		if (value.startsWith(".CLEAR")) //$NON-NLS-1$
		{
			if (value.equals(".CLEAR")) //$NON-NLS-1$
			{
				ability.removeAllBenefits();
			}
			else
			{
				ability.removeBenefit(value.substring(7));
			}
			return true;
		}
		ability.addBenefit(parseBenefit(value));
		return true;
	}

	/**
	 * Parses the BENEFIT tag into a Description object.
	 * 
	 * @param aDesc The LST tag
	 * @return A <tt>Description</tt> object
	 */
	public Description parseBenefit(final String aDesc)
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
