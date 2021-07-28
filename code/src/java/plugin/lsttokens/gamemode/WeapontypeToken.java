package plugin.lsttokens.gamemode;

import java.io.File;
import java.net.URI;

import pcgen.cdom.enumeration.Type;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class WeapontypeToken extends AbstractToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "WEAPONTYPE";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		ParseResult pr = checkForIllegalSeparator('|', value);
		if (!pr.passed())
		{
			pr.printMessages(new File(gameMode.getFolderName()).toURI());
			return false;
		}
		int pipeLoc = value.indexOf('|');
		Type type = Type.getConstant(value.substring(0, pipeLoc));
		gameMode.addWeaponType(type, value.substring(pipeLoc + 1));
		return true;
	}
}
