package pcgen.cdom.content;

import pcgen.cdom.base.ChoiceSet;

public class TransitionChoice<T>
{

	private final ChoiceSet<? extends T> choices;
	private final int choiceCount;

	public TransitionChoice(ChoiceSet<? extends T> cs, int count)
	{
		choices = cs;
		choiceCount = count;
	}

	public ChoiceSet<? extends T> getChoices()
	{
		return choices;
	}

	public int getCount()
	{
		return choiceCount;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TransitionChoice)
		{
			TransitionChoice<?> other = (TransitionChoice<?>) obj;
			return choiceCount == other.choiceCount
					&& choices.equals(other.choices);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return choiceCount * 29 + choices.hashCode();
	}

}
