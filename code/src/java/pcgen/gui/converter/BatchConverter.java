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
package pcgen.gui.converter;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.character.CompanionMod;
import pcgen.core.spell.Spell;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbilityCategoryLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.StatsAndChecksLoader;
import pcgen.rules.context.LoadContext;

public class BatchConverter
{
	private final StatsAndChecksLoader statCheckLoader = new StatsAndChecksLoader();
	private final AbilityCategoryLoader abilityCategoryLoader = new AbilityCategoryLoader();
	private final String outDir;
	private final File rootDir;

	public BatchConverter(String outputDirectory, String rootDirectory)
	{
		outDir = outputDirectory;
		rootDir = new File(rootDirectory);
	}

	public void process(List<Campaign> list, LoadContext context)
			throws PersistenceLayerException
	{
		// The first thing we need to do is load the
		// correct statsandchecks.lst file for this gameMode
		GameMode gamemode = SettingsHandler.getGame();
		if (gamemode == null)
		{
			// Autoload campaigns is set but there
			// is no current gameMode, so just return
			return;
		}
		List<AbstractLoader> loaders = setupLoaders(context);
		File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(),
				"gameModes");
		File specificGameModeDir = new File(gameModeDir, gamemode
				.getFolderName());
		File statsAndChecks = new File(specificGameModeDir,
				"statsandchecks.lst");
		statCheckLoader.loadLstFile(context, statsAndChecks.toURI());

		for (PCAlignment al : gamemode.getUnmodifiableAlignmentList())
		{
			context.ref.registerAbbreviation(al, al.getKeyName());
		}
		for (PCStat st : gamemode.getUnmodifiableStatList())
		{
			context.ref.registerAbbreviation(st, st.getAbb());
		}
		for (SizeAdjustment sz : gamemode.getUnmodifiableSizeAdjustmentList())
		{
			context.ref.registerAbbreviation(sz, sz.getAbbreviation());
		}

		for (Campaign c : list)
		{
			processCampaign(context, loaders, c);
			// Add all sub-files to the main campaign, regardless of exclusions
			for (CampaignSourceEntry fName : c.getSafeListFor(ListKey.FILE_PCC))
			{
				URI uri = fName.getURI();
				if (PCGFile.isPCGenCampaignFile(uri))
				{
					processCampaign(context, loaders, Globals.getCampaignByURI(
							uri, false));
				}
			}
		}
	}

	private List<AbstractLoader> setupLoaders(LoadContext context)
	{
		List<AbstractLoader> loaders = new ArrayList<AbstractLoader>();
		loaders.add(new BasicLoader(context, WeaponProf.class,
				ListKey.FILE_WEAPON_PROF));
		loaders.add(new BasicLoader(context, ArmorProf.class,
				ListKey.FILE_ARMOR_PROF));
		loaders.add(new BasicLoader(context, ShieldProf.class,
				ListKey.FILE_SHIELD_PROF));
		loaders.add(new BasicLoader(context, Skill.class, ListKey.FILE_SKILL));
		loaders.add(new BasicLoader(context, Language.class,
				ListKey.FILE_LANGUAGE));
		loaders.add(new BasicLoader(context, Ability.class, ListKey.FILE_FEAT));
		loaders.add(new BasicLoader(context, Ability.class,
				ListKey.FILE_ABILITY));
		loaders.add(new BasicLoader(context, Race.class, ListKey.FILE_RACE));
		loaders
				.add(new BasicLoader(context, Domain.class, ListKey.FILE_DOMAIN));
		loaders.add(new BasicLoader(context, Spell.class, ListKey.FILE_SPELL));
		loaders.add(new BasicLoader(context, Deity.class, ListKey.FILE_DEITY));
		loaders.add(new BasicLoader(context, PCTemplate.class,
				ListKey.FILE_TEMPLATE));
		loaders.add(new BasicLoader(context, Equipment.class,
				ListKey.FILE_EQUIP));
		loaders.add(new BasicLoader(context, EquipmentModifier.class,
				ListKey.FILE_EQUIP_MOD));
		loaders.add(new BasicLoader(context, CompanionMod.class,
				ListKey.FILE_COMPANION_MOD));
		loaders.add(new ClassLoader(context));
		loaders.add(new CopyLoader(context, ListKey.FILE_ABILITY_CATEGORY));
		loaders.add(new CopyLoader(context, ListKey.LICENSE_FILE));
		loaders.add(new CopyLoader(context, ListKey.FILE_KIT));
		loaders.add(new CopyLoader(context, ListKey.FILE_BIO_SET));
		loaders.add(new CopyLoader(context, ListKey.FILE_PCC));
		loaders.add(new SelfCopyLoader(context));
		return loaders;
	}

	private void processCampaign(LoadContext context,
			List<AbstractLoader> loaders, Campaign c)
			throws PersistenceLayerException
	{
		// load ability categories first as they used to only be at the game
		// mode
		abilityCategoryLoader.loadLstFiles(context, c
				.getSafeListFor(ListKey.FILE_ABILITY_CATEGORY));

		for (AbstractLoader loader : loaders)
		{
			loader.load(rootDir, outDir, c);
		}
	}

}
