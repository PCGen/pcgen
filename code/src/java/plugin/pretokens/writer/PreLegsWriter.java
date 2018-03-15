package plugin.pretokens.writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

import java.io.IOException;
import java.io.Writer;

public class PreLegsWriter implements PrerequisiteWriterInterface
{

    @Override
	public String kindHandled()
	{
		return "legs";
	}

    @Override
	public PrerequisiteOperator[] operatorsHandled()
	{
		return null;
	}

    @Override
	public void write(Writer writer, Prerequisite prereq)
		throws PersistenceLayerException
	{
		try
		{
			writer.write("PRELEGS" + (prereq.isOverrideQualify() ? "Q:":""));
			writer.write(prereq.getOperator().toString().toUpperCase());
			writer.write(':');
			writer.write(prereq.getOperand());
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e.getMessage());
		}
	}

}
