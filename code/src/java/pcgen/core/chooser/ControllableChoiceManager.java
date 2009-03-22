package pcgen.core.chooser;

public interface ControllableChoiceManager<T>
{
	public void setController(ChooseController<T> cc);

	public int getPreChooserChoices();

	public int getChoicesPerUnitCost();
}
