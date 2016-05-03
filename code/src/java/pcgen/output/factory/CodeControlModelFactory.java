package pcgen.output.factory;

import pcgen.cdom.inst.CodeControl;
import pcgen.core.GameMode;
import pcgen.output.base.ModeModelFactory;
import pcgen.output.model.CodeControlModel;
import freemarker.template.TemplateModel;

public class CodeControlModelFactory implements ModeModelFactory
{

	@Override
	public TemplateModel generate(GameMode mode)
	{
		CodeControl controller =
				mode.getModeContext()
					.getReferenceContext()
					.silentlyGetConstructedCDOMObject(CodeControl.class,
						"Controller");
		if (controller == null)
		{
			return null;
		}
		return new CodeControlModel(controller);
	}

}
