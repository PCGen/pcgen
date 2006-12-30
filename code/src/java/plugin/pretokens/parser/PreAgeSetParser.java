package plugin.pretokens.parser;

import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * @author perchrh
 *
 */
public class PreAgeSetParser extends AbstractPrerequisiteListParser
		implements PrerequisiteParserInterface
		{

	public String[] kindsHandled() {
		return new String[]{"PREAGESET"};
	}
	
}