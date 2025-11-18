/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui2.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCCheck;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.character.CompanionMod;
import pcgen.core.spell.Spell;
import pcgen.gui2.converter.loader.AbilityLoader;
import pcgen.gui2.converter.loader.BasicLoader;
import pcgen.gui2.converter.loader.PCClassLoader;
import pcgen.gui2.converter.loader.CopyLoader;
import pcgen.gui2.converter.loader.EquipmentLoader;
import pcgen.gui2.converter.loader.SelfCopyLoader;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.persistence.lst.AbilityCategoryLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.persistence.CDOMControlLoader;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class LSTConverter extends Observable
{
	private final AbilityCategoryLoader catLoader = new AbilityCategoryLoader();
	private final GenericLoader<SizeAdjustment> sizeLoader = new GenericLoader<>(SizeAdjustment.class);
	private final GenericLoader<PCCheck> savesLoader = new GenericLoader<>(PCCheck.class);
	private final GenericLoader<PCAlignment> alignmentLoader = new GenericLoader<>(PCAlignment.class);
	private final GenericLoader<PCStat> statLoader = new GenericLoader<>(PCStat.class);
	private final CDOMControlLoader dataControlLoader = new CDOMControlLoader();
	private final EditorLoadContext context;
	private final List<Loader> loaders;
	private final Set<URI> written = new HashSet<>();
	private final String outDir;
	private final File rootDir;
	private final DoubleKeyMapToList<Loader, URI, CDOMObject> injected = new DoubleKeyMapToList<>();
	private final ConversionDecider decider;
	private final Writer changeLogWriter;

	public LSTConverter(EditorLoadContext lc, File root, String outputDir, ConversionDecider cd, Writer changeLogWriter)
	{
		context = lc;
		rootDir = root;
		outDir = outputDir;
		decider = cd;

		this.changeLogWriter = changeLogWriter;
		loaders = setupLoaders(context, changeLogWriter);
	}

	/**
	 * Return the number of files referred to by the campaign
	 * @param campaign The campaign to be tallied.
	 * @return The number of lst files used.
	 */
	public int getNumFilesInCampaign(Campaign campaign)
	{
		int numFiles = 0;

		for (final Loader loader : loaders)
		{
			List<CampaignSourceEntry> files = loader.getFiles(campaign);
			numFiles += files.size();
		}
		return numFiles;
	}

	/**
	 * Initialise the list of campaigns. This will load the ability
	 * categories in advance of the conversion.
	 * @param campaigns The campaigns or sources to be converted.
	 */
	public void initCampaigns(List<Campaign> campaigns)
	{
		List<CampaignSourceEntry> dataDefFileList = new ArrayList<>();
		for (Campaign campaign : campaigns)
		{
			// load ability categories first as they used to only be at the game
			// mode
			try
			{
				catLoader.loadLstFiles(context, campaign.getSafeListFor(ListKey.FILE_ABILITY_CATEGORY));
				sizeLoader.loadLstFiles(context, campaign.getSafeListFor(ListKey.FILE_SIZE));
				statLoader.loadLstFiles(context, campaign.getSafeListFor(ListKey.FILE_STAT));
				savesLoader.loadLstFiles(context, campaign.getSafeListFor(ListKey.FILE_SAVE));
				alignmentLoader.loadLstFiles(context, campaign.getSafeListFor(ListKey.FILE_ALIGNMENT));
				alignmentLoader.loadLstFiles(Globals.getContext(), campaign.getSafeListFor(ListKey.FILE_ALIGNMENT));

			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint(e.getMessage(), e);
			}
			dataDefFileList.addAll(campaign.getSafeListFor(ListKey.FILE_DATACTRL));

		}

		// Load using the new LstFileLoaders
		try
		{
			SourceFileLoader.addDefaultDataControlIfNeeded(dataDefFileList);
			dataControlLoader.loadLstFiles(context, dataDefFileList);
			SourceFileLoader.processFactDefinitions(context);
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("LSTConverter.initCampaigns failed", e);
		}

	}

	public void processCampaign(Campaign campaign)
	{
		startItem(campaign);
	}

	private void startItem(final Campaign campaign)
	{
		for (final Loader loader : loaders)
		{
			List<CampaignSourceEntry> files = loader.getFiles(campaign);
			for (final CampaignSourceEntry cse : files)
			{
				final URI uri = cse.getURI();
				setChanged();
				notifyObservers(uri);
				if (!"file".equalsIgnoreCase(uri.getScheme()))
				{
					Logging.log(Logging.WARNING, "Skipping campaign " + uri + " from " + campaign.getSourceURI()
						+ " as it is not a local file.");
					continue;
				}
				File in = new File(uri);
				// Use canonical name to stop reruns for the same file referred to using ..
				URI canonicalUri;
				try
				{
					canonicalUri = in.getCanonicalFile().toURI();
				}
				catch (IOException e1)
				{
					Logging.log(Logging.WARNING, "Skipping campaign " + uri + " from " + campaign.getSourceURI()
						+ " as it could not be made canonical. " + e1.getMessage());
					continue;
				}
				if (written.contains(canonicalUri))
				{
					continue;
				}
				written.add(canonicalUri);
				File base = findSubRoot(rootDir, in);
				if (base == null)
				{
					Logging.log(Logging.WARNING, "Skipping campaign " + uri + " from " + campaign.getSourceURI()
						+ " as it is not in the selected source directory.");
					continue;
				}
				String relative = in.toString().substring(base.toString().length() + 1);
				if (!in.exists())
				{
					Logging.log(Logging.WARNING, "Skipping campaign " + uri + " from " + campaign.getSourceURI()
						+ " as it does not exist. Campaign is " + cse.getCampaign().getSourceURI());
					continue;
				}
				File outFile = new File(outDir, File.separator + relative);
				if (outFile.exists())
				{
					Logging.log(Logging.WARNING, "Won't overwrite: " + outFile);
					continue;
				}
				ensureParents(outFile.getParentFile());
				try
				{
					changeLogWriter.append("\nProcessing ").append(String.valueOf(in)).append("\n");
					load(uri, loader)
							.ifPresent((String result) -> {
								try (Writer out = new BufferedWriter(new OutputStreamWriter(
										new FileOutputStream(outFile),
										StandardCharsets.UTF_8)))
								{
									out.write(result);
								} catch (IOException e)
								{
									Logging.errorPrint(e.getLocalizedMessage(), e);
								}
							});
				}
				catch (PersistenceLayerException | IOException e)
				{
					Logging.errorPrint(e.getLocalizedMessage(), e);
				}
			}
		}
	}

	private List<Loader> setupLoaders(EditorLoadContext context, Writer changeLogWriter)
	{
		List<Loader> loaderList = new ArrayList<>();
		loaderList.add(new BasicLoader<>(context, WeaponProf.class, ListKey.FILE_WEAPON_PROF, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, ArmorProf.class, ListKey.FILE_ARMOR_PROF, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, ShieldProf.class, ListKey.FILE_SHIELD_PROF, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, Skill.class, ListKey.FILE_SKILL, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, Language.class, ListKey.FILE_LANGUAGE, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, Ability.class, ListKey.FILE_FEAT, changeLogWriter));
		loaderList.add(new AbilityLoader(context, Ability.class, ListKey.FILE_ABILITY, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, Race.class, ListKey.FILE_RACE, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, Domain.class, ListKey.FILE_DOMAIN, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, Spell.class, ListKey.FILE_SPELL, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, Deity.class, ListKey.FILE_DEITY, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, PCTemplate.class, ListKey.FILE_TEMPLATE, changeLogWriter));
		loaderList.add(new EquipmentLoader(context, ListKey.FILE_EQUIP, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, EquipmentModifier.class, ListKey.FILE_EQUIP_MOD, changeLogWriter));
		loaderList.add(new BasicLoader<>(context, CompanionMod.class, ListKey.FILE_COMPANION_MOD, changeLogWriter));
		loaderList.add(new PCClassLoader(context, changeLogWriter));
		loaderList.add(new CopyLoader(ListKey.FILE_ABILITY_CATEGORY));
		loaderList.add(new CopyLoader(ListKey.LICENSE_FILE));
		loaderList.add(new CopyLoader(ListKey.FILE_KIT));
		loaderList.add(new CopyLoader(ListKey.FILE_BIO_SET));
		loaderList.add(new CopyLoader(ListKey.FILE_DATACTRL));
		loaderList.add(new CopyLoader(ListKey.FILE_STAT));
		loaderList.add(new CopyLoader(ListKey.FILE_SAVE));
		loaderList.add(new CopyLoader(ListKey.FILE_SIZE));
		loaderList.add(new CopyLoader(ListKey.FILE_ALIGNMENT));
		loaderList.add(new CopyLoader(ListKey.FILE_PCC));
		loaderList.add(new SelfCopyLoader());
		return loaderList;
	}

	private void ensureParents(File parentFile)
	{
		if (!parentFile.exists())
		{
			ensureParents(parentFile.getParentFile());
			parentFile.mkdir();
		}
	}

	private File findSubRoot(File root, File in)
	{
		File parent = in.getParentFile();
		if (parent == null)
		{
			return null;
		}
		if (parent.getAbsolutePath().equals(root.getAbsolutePath()))
		{
			return parent;
		}
		return findSubRoot(root, parent);
	}

	private Optional<String> load(URI uri, Loader loader) throws PersistenceLayerException
	{
		context.setSourceURI(uri);
		context.setExtractURI(uri);
		try
		{
			return LstFileLoader.readFromURI(uri)
				.map((String dataBuffer) -> {
					StringBuilder resultBuffer = new StringBuilder(dataBuffer.length());

					String[] fileLines = dataBuffer.split(LstFileLoader.LINE_SEPARATOR_REGEXP);
					for (int line = 0; line < fileLines.length; line++)
					{
						String lineString = fileLines[line];
						if ((lineString.isEmpty()) || (lineString.charAt(0) == LstFileLoader.LINE_COMMENT_CHAR)
								|| lineString.startsWith("SOURCE"))
						{
							resultBuffer.append(lineString);
						}
						else
						{
							try
							{
								List<CDOMObject> newObj = loader.process(resultBuffer, line, lineString, decider);
								if (newObj != null)
								{
									for (CDOMObject cdo : newObj)
									{
										injected.addToListFor(loader, uri, cdo);
									}
								}
							}
							catch (PersistenceLayerException | InterruptedException e)
							{
								String message = LanguageBundle.getFormattedString("Errors.LstFileLoader.LoadError", //$NON-NLS-1$
										uri, e.getMessage());
								Logging.errorPrint(message, e);
								return null;
							}
						}
						resultBuffer.append("\n");
					}
					return resultBuffer.toString();
				});
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint(LanguageBundle.getFormattedString("Errors.LstFileLoader.LoadError", //$NON-NLS-1$
					uri, ple.getMessage()));
			return Optional.empty();
		}
	}

	public Collection<Loader> getInjectedLoaders()
	{
		return injected.getKeySet();
	}

	public Collection<URI> getInjectedURIs(Loader l)
	{
		return injected.getSecondaryKeySet(l);
	}

	public Collection<CDOMObject> getInjectedObjects(Loader l, URI uri)
	{
		return injected.getListFor(l, uri);
	}
}
