package plugin.lsttokens.template;

import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with REPEATLEVEL Token
 */
public class RepeatlevelToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "REPEATLEVEL";
	}

	public boolean parse(PCTemplate template, String value) {
		//
		// x|y|z:level:<level assigned item>
		//
		final int endRepeat = value.indexOf(':');
		if (endRepeat > 0)
		{
			final int endLevel = value.indexOf(':', endRepeat + 1);
			if (endLevel > 0) {
				final StringTokenizer repeatToken = new StringTokenizer(value.substring(0, endRepeat), "|");
				if (repeatToken.countTokens() == 3) {
					try {
						final int lvlIncrement = Integer.parseInt(repeatToken.nextToken());
						final int consecutive  = Integer.parseInt(repeatToken.nextToken());
						final int maxLevel     = Integer.parseInt(repeatToken.nextToken());
						int iLevel = Integer.parseInt(value.substring(endRepeat + 1, endLevel));
	
						if ((iLevel > 0) && (lvlIncrement > 0) && (maxLevel > 0) && (consecutive >= 0)) {
							int count = consecutive;
							for(; iLevel <= maxLevel; iLevel += lvlIncrement) {
								if ((consecutive == 0) || (count != 0)) {
									final StringTokenizer tok = new StringTokenizer(value.substring(endLevel + 1));
									final String type = tok.nextToken();
									
									template.addLevelAbility(iLevel, type, tok.nextToken());
								}
								if (consecutive != 0) {
									if (count == 0) {
										count = consecutive;
									}
									else {
										--count;
									}
								}
							}
						}
	
						return true;
					}
					catch(NumberFormatException nfe) {
						return false;
					}
				}
			}
		}
		return false;
	}
}