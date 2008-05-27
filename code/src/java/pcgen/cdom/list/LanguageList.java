package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.Language;

public class LanguageList extends CDOMListObject<Language>
{

	public Class<Language> getListClass()
	{
		return Language.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
