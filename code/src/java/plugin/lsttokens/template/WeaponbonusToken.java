package plugin.lsttokens.template;

import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "WEAPONBONUS";
	}

	public boolean parse(PCTemplate template, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			template.addWeaponProfBonus(aTok.nextToken());
		}

		return true;
	}
}
