package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with FOLLOWERALIGN Token
 */
public class FolloweralignToken implements DeityLstToken{

	public String getTokenName() {
		return "FOLLOWERALIGN";
	}

	public boolean parse(Deity deity, String value) {
		deity.setFollowerAlignments(value);
		return true;
	}
}
