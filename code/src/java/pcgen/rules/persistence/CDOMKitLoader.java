package pcgen.rules.persistence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.core.kit.BaseKit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class CDOMKitLoader implements CDOMLoader<Kit>
{
	private final Map<String, CDOMSubLineLoader<? extends BaseKit>> loadMap = new HashMap<String, CDOMSubLineLoader<? extends BaseKit>>();

	private final Class<Kit> targetClass = Kit.class;

	public void addLineLoader(CDOMSubLineLoader<? extends BaseKit> loader)
	{
		// TODO check null
		// TODO check duplicate!
		loadMap.put(loader.getPrefix(), loader);
	}

	public boolean parseSubLine(LoadContext context, Kit obj, String val,
			URI source)
	{
		int sepLoc = val.indexOf('\t');
		String firstToken = (sepLoc == -1) ? val : val.substring(0, sepLoc);
		int colonLoc = firstToken.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Unsure what to do with line without "
							+ "a colon in first token: " + val + " in file: "
							+ source);
			return false;
		}

		String prefix = firstToken.substring(0, colonLoc);
		CDOMSubLineLoader<? extends BaseKit> loader = loadMap.get(prefix);
		if (loader == null)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Unsure what to do with line with prefix: " + prefix
							+ ".  Line was: " + val + " in file: " + source);
			return false;
		}
		try
		{
			if (!subParse(context, obj, loader, val, source))
			{
				return false;
			}
		}
		catch (PersistenceLayerException ple)
		{
			// TODO Auto-generated catch block
			ple.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean parseLine(LoadContext context, Kit obj, String val,
			URI source) throws PersistenceLayerException
	{
		//TODO shell
		return false;
	}

	public void loadLstFiles(LoadContext context,
			Collection<CampaignSourceEntry> sources)
	{
		throw new IllegalStateException("Can't do this yet");
	}

	public void loadLstFile(LoadContext context, URI uri)
	{
		throw new IllegalStateException("Can't do this yet");
	}

	private <CC extends BaseKit> boolean subParse(LoadContext context, Kit kit,
			CDOMSubLineLoader<CC> loader, String line, URI uri)
			throws PersistenceLayerException
	{
		CC obj = loader.getCDOMObject(context);
		Class cl = obj.getClass();
		context.obj.addToList(kit, ListKey.KIT_TASKS, obj);
		return loader.parseLine(context, obj, line, uri);
	}

	protected Kit getCDOMObject(LoadContext context, String name)
	{
		Kit obj = context.ref.silentlyGetConstructedCDOMObject(targetClass,
				name);
		if (obj == null)
		{
			obj = context.ref.constructCDOMObject(targetClass, name);
		}
		return obj;
	}

	public Class<Kit> getTargetClass()
	{
		return targetClass;
	}

	public void unloadLstFiles(LoadContext lc,
			Collection<CampaignSourceEntry> files)
	{
		HashMapToList<Class<?>, CDOMSubLineLoader<?>> loaderMap = new HashMapToList<Class<?>, CDOMSubLineLoader<?>>();
		for (CDOMSubLineLoader<?> loader : loadMap.values())
		{
			loaderMap.addToListFor(loader.getLoadedClass(), loader);
		}
		for (CampaignSourceEntry cse : files)
		{
			lc.setExtractURI(cse.getURI());
			URI writeURI = cse.getWriteURI();
			File f = new File(writeURI);
			ensureCreated(f.getParentFile());
			try
			{
				TreeSet<String> set = new TreeSet<String>();
				for (Kit k : lc.ref.getConstructedCDOMObjects(Kit.class))
				{
					if (cse.getURI().equals(k.get(ObjectKey.SOURCE_URI)))
					{
						StringBuilder sb = new StringBuilder();
						String[] unparse = lc.unparse(k, "*KITTOKEN");
						sb.append("STARTPACK:");
						sb.append(k.getDisplayName());
						if (unparse != null)
						{
							sb.append("\t").append(
									StringUtil.join(unparse, "\t"));
						}
						sb.append("\n");

						Changes<BaseKit> changes = lc.getObjectContext()
								.getListChanges(k, ListKey.KIT_TASKS);
						Collection<BaseKit> tasks = changes.getAdded();
						if (tasks == null)
						{
							continue;
						}
						for (BaseKit kt : tasks)
						{
							List<CDOMSubLineLoader<?>> loaders = loaderMap
									.getListFor(kt.getClass());
							for (CDOMSubLineLoader loader : loaders)
							{
								processTask(lc, kt, loader, sb);
							}
						}
						sb.append("\n");
						set.add(sb.toString());
					}
				}
				PrintWriter pw = new PrintWriter(f);
				pw.println("#~PARAGRAPH");
				for (String s : set)
				{
					pw.print(s);
				}
				pw.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private <T extends BaseKit> void processTask(LoadContext lc, T kt,
			CDOMSubLineLoader<T> loader, StringBuilder pw)
	{
		loader.unloadObject(lc, kt, pw);
	}

	private boolean ensureCreated(File rec)
	{
		if (!rec.exists())
		{
			if (!ensureCreated(rec.getParentFile()))
			{
				return false;
			}
			return rec.mkdir();
		}
		return true;
	}
}
