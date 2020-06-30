package pcgen.output.model;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CodeControl;
import pcgen.output.base.SimpleWrapperLibrary;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A CodeControlModel is a TemplateHashModel that wraps a CodeControl setting.
 */
public class CodeControlModel implements TemplateHashModel
{

	private final CodeControl control;

	/**
	 * Constructs a new CodeControlModel with the given underlying CodeControl.
	 * 
	 * @param controller
	 *            The CodeControl underlying this CodeControlModel
	 */
	public CodeControlModel(CodeControl controller)
	{
		control = controller;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		String result = control.get(ObjectKey.getKeyFor(String.class, "*" + key));
		if (result == null)
		{
			return null;
		}
		return SimpleWrapperLibrary.wrap(result);
	}

	@Override
	public boolean isEmpty()
    {
		return false;
	}

}
