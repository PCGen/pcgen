package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCTemplateLstToken
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.PCTemplateLstToken#parse(pcgen.core.PCTemplate,
	 *      java.lang.String)
	 */
	public boolean parse(PCTemplate template, String value)
	{
		if (value.startsWith("DISPLAY"))
		{
			// 514 abbreviation cleanup
//			if (!value.equals("DISPLAY"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "DISPLAY (exact String, upper case)");
//			}
			template.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (value.startsWith("EXPORT"))
		{
			// 514 abbreviation cleanup
//			if (!value.equals("EXPORT"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "EXPORT (exact String, upper case)");
//			}
			template.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (value.startsWith("NO"))
		{
			// 514 abbreviation cleanup
//			if (!value.equals("NO"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "NO (exact String, upper case)");
//			}
			template.setVisibility(Visibility.HIDDEN);
		}
		else
		{
			// 514 abbreviation cleanup
//			if (!value.equals("ALWAYS") && !value.equals("YES"))
//			{
//				Logging.errorPrint(getErrorMsgPrefix(template, value)
//					+ "DISPLAY, EXPORT, NO, YES or ALWAYS "
//					+ "(exact String, upper case)");
//			}
			template.setVisibility(Visibility.DEFAULT);
		}
		return true;
	}

	/**
	 * Produce the standard start of an error message for an invalid visible
	 * tag.
	 * 
	 * @param template
	 *            The template the tag is for.
	 * @param value
	 *            The value of the visible tag.
	 * @return The error message prefix
	 */
//	private String getErrorMsgPrefix(PCTemplate template, String value)
//	{
//		StringBuffer buff = new StringBuffer();
//		buff.append("In template ");
//		buff.append(template.getDisplayName());
//		buff.append(", token ");
//		buff.append(getTokenName());
//		buff.append(", use of '");
//		buff.append(value);
//		buff.append("' is not valid, please use ");
//		return buff.toString();
//	}
}
