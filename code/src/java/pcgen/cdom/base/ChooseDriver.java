package pcgen.cdom.base;

import java.util.List;

import pcgen.base.formula.Formula;

public interface ChooseDriver
{

	public ChooseInformation<?> getChooseInfo();

	public Formula getSelectFormula();

	public List<ChooseSelectionActor<?>> getActors();

	public String getFormulaSource();

	public Formula getNumChoices();

	public String getDisplayName();

}
