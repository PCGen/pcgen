package plugin.lsttokens.spell;

import java.util.StringTokenizer;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with VARIANTS Token
 */
public class VariantsToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "VARIANTS";
	}

	public boolean parse(Spell spell, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String variant = aTok.nextToken();

			if (variant.equals(".CLEAR"))
			{
				spell.clearVariants();
			}
			else
			{
				spell.addVariant(variant);
			}
		}
		return true;
	}
}
