/*
 * Rule.java
 *
 * Created on April 25, 2003, 1:51 PM
 */
package plugin.doomsdaybook.util;

import pcgen.util.Logging;

import java.util.ArrayList;

/**
 *
 * @author  devon
 */
public class Rule extends ArrayList implements DataElement
{
	ArrayList retList = new ArrayList();
	String id;
	String title;
	VariableHashMap allVars;
	int weight;

	/**
	 * Creates a new instance of Rule
	 * @param allVars
	 */
	public Rule(VariableHashMap allVars)
	{
		this(allVars, "", "", 1);
	}

	public Rule(VariableHashMap allVars, int weight)
	{
		this(allVars, "", "", weight);
	}

	public Rule(VariableHashMap allVars, String title, String id)
	{
		this(allVars, title, id, 1);
	}

	public Rule(VariableHashMap allVars, String title, String id, int weight)
	{
		this.allVars = allVars;
		this.title = title;
		this.id = id;
		this.weight = weight;
	}

	public ArrayList getData() throws Exception
	{
		retList.clear();

		for (int i = 0; i < this.size(); i++)
		{
			String key = (String) this.get(i);
			DataElement ele = allVars.getDataElement(key);
			retList.addAll(ele.getData());
		}

		return retList;
	}

	public ArrayList getData(int choice) throws Exception
	{
		return getData();
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public ArrayList getLastData() throws Exception
	{
		retList.clear();

		for (int i = 0; i < this.size(); i++)
		{
			String key = (String) this.get(i);
			DataElement ele = allVars.getDataElement(key);
			retList.addAll(ele.getLastData());
		}

		return retList;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	public int getWeight()
	{
		return weight;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < this.size(); i++)
		{
			String key = (String) this.get(i);

			try
			{
				DataElement ele = allVars.getDataElement(key);

				if (ele.getTitle() != null)
				{
					sb.append("[" + ele.getTitle() + "] ");
				}
			}
			catch (Exception e)
			{
				Logging.errorPrint(e.getMessage(), e);
			}
		}

		return sb.toString();
	}
}
