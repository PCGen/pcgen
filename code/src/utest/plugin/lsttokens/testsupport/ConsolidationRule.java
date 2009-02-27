package plugin.lsttokens.testsupport;

import pcgen.base.lang.StringUtil;

public interface ConsolidationRule
{

	public String[] getAnswer(String... strings);

	public static final ConsolidationRule OVERWRITE = new ConsolidationRule()
	{
		public String[] getAnswer(String... strings)
		{
			return new String[] { strings[strings.length - 1] };
		}
	};

	public static final ConsolidationRule SEPARATE = new ConsolidationRule()
	{
		public String[] getAnswer(String... strings)
		{
			return strings;
		}
	};

	public static class AppendingConsolidation implements ConsolidationRule
	{
		private final String join;

		public AppendingConsolidation(char joinCharacter)
		{
			join = Character.toString(joinCharacter);
		}

		public String[] getAnswer(String... strings)
		{
			return new String[] { StringUtil.join(strings, join) };
		}

	}
}
