package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with TEMPLATE Token
 */
public class TemplateToken implements PCClassLstToken {

	public String getTokenName() {
		return "TEMPLATE";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addTemplate(PCClassLoader.fixParameter(level, value));
		return true;
	}
}
