package com.github.happyzleaf.pixelmonplaceholders.utility;

import com.github.happyzleaf.pixelmonplaceholders.PPConfig;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.api.world.WeatherType;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.HiddenPower;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.PokemonDropInformation;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity3HasStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVsStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.Evolution;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.BiomeCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.ChanceCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.EvoCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.EvoRockCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.FriendshipCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.GenderCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.HeldItemCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.HighAltitudeCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.LevelCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.MoveCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.MoveTypeCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.PartyCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.TimeCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.WeatherCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.LevelingEvolution;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.enums.EnumType;
import com.pixelmonmod.pixelmon.items.heldItems.HeldItem;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import de.randombyte.entityparticles.data.EntityParticlesKeys;
import me.rojo8399.placeholderapi.NoValueException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

/***************************************
 * PixelmonPlaceholders
 * Created on 07/06/2017.
 * @author Vincenzo Montanari
 *
 * Copyright (c). All rights reserved.
 ***************************************/
public class ParserUtility {
	private static HashMap<EnumPokemon, PokemonDropInformation> pokemonDrops;
	private static Field mainDrop, rareDrop, optDrop1, optDrop2;
	private static Field friendship, level, type, weather;

	static {
		try {
			Field pokemonDropsField = DropItemRegistry.class.getDeclaredField("pokemonDrops");
			pokemonDropsField.setAccessible(true);
			pokemonDrops = (HashMap<EnumPokemon, PokemonDropInformation>) pokemonDropsField.get(null);
			mainDrop = PokemonDropInformation.class.getDeclaredField("mainDrop");
			mainDrop.setAccessible(true);
			rareDrop = PokemonDropInformation.class.getDeclaredField("rareDrop");
			rareDrop.setAccessible(true);
			optDrop1 = PokemonDropInformation.class.getDeclaredField("optDrop1");
			optDrop1.setAccessible(true);
			optDrop2 = PokemonDropInformation.class.getDeclaredField("optDrop2");
			optDrop2.setAccessible(true);

			friendship = FriendshipCondition.class.getDeclaredField("friendship");
			friendship.setAccessible(true);
			level = LevelCondition.class.getDeclaredField("level");
			level.setAccessible(true);
			type = MoveTypeCondition.class.getDeclaredField("type");
			type.setAccessible(true);
			weather = WeatherCondition.class.getDeclaredField("weather");
			weather.setAccessible(true);

		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static Object parsePokedexInfo(EnumPokemon pokemon, String[] values) throws NoValueException {
		if (values.length == 1) {
			return pokemon.name;
		}

		BaseStats stats = Entity3HasStats.getBaseStats(pokemon).orElse(null);
		if (stats == null) {
			throw new RuntimeException("Could not find BaseStats for " + pokemon.name + ".");
		}

		switch (values[1]) {
			case "name":
				return pokemon.name;
			case "catchrate":
				return stats.catchRate;
			case "nationalid":
				return stats.nationalPokedexNumber;
			case "rarity":
				if (values.length == 3) {
					int rarity;
					switch (values[2]) {
						case "day":
							throw new NoValueException(); //TODO fix
							//rarity = stats.rarity.day;
							//break;
						case "night":
							throw new NoValueException(); //TODO fix
							//rarity = stats.rarity.night;
							//break;
						case "dawndusk":
							throw new NoValueException(); //TODO fix
							//rarity = stats.rarity.dawndusk;
							//break;
						default:
							throw new NoValueException();
					}
					//TODO uncomment when fixed
					// return rarity <= 0 ? EnumPokemon.legendaries.contains(pokemon.name) ? 0 : 1 : rarity;
				}
				break;case "preevolutions":
				return asReadableList(values, 2, stats.preEvolutions);
				case "ability":
				if (values.length == 3) {
					String value1 = values[2];
					int ability = value1.equals("1") ? 0 : value1.equals("2") ? 1 : value1.equalsIgnoreCase("h") ? 2 : -1;
					if (ability != -1) {
						String result = stats.abilities[ability];
						return result == null ? PPConfig.noneText : result;
					}
				}
				break;
			case "abilities":
				return asReadableList(values, 2, stats.abilities);
			//Since 1.2.0
			case "biomes": //TODO fix
				throw new NoValueException();
				//return asReadableList(values, 2, Arrays.stream(stats.biomeIDs).map(id -> Biome.getBiome(id).getBiomeName()).toArray());
			case "spawnlocations":
				return asReadableList(values, 2, stats.spawnLocations);
			case "type":
				return asReadableList(values, 2, stats.getTypeList().toArray());
			case "basestats":
				if (values.length >= 3) {
					switch (values[2]) {
						case "hp":
							return stats.get(StatsType.HP);
						case "atk":
							return stats.get(StatsType.Attack);
						case "def":
							return stats.get(StatsType.Defence);
						case "spa":
							return stats.get(StatsType.SpecialAttack);
						case "spd":
							return stats.get(StatsType.SpecialDefence);
						case "spe":
							return stats.get(StatsType.Speed);
					}
				}
				break;
			case "drops":
				if (values.length >= 3) {
					PokemonDropInformation drops = pokemonDrops.get(pokemon);
					if (drops == null) {
						return PPConfig.noneText;
					} else {
						try {
							switch (values[2]) {
								case "main":
									return getItemStackInfo((ItemStack) mainDrop.get(drops));
								case "rare":
									return getItemStackInfo((ItemStack) rareDrop.get(drops));
								case "optional1":
									return getItemStackInfo((ItemStack) optDrop1.get(drops));
								case "optional2":
									return getItemStackInfo((ItemStack) optDrop2.get(drops));
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case "egggroups":
				return asReadableList(values, 2, stats.eggGroups);
			case "texturelocation": //Since 1.2.3
				return "pixelmon:sprites/pokemon/" + String.format("%03d", stats.nationalPokedexNumber);
			case "move": //Since 1.3.0 TODO test
				if (values.length >= 3) {
					try {
						Object[] attacks = getAllAttackNames(stats);
						int attack = Integer.parseInt(values[2]) - 1;
						if (attack >= 0 && attack < attacks.length) {
							return attacks[attack];
						} else {
							return PPConfig.moveNotAvailableText;
						}
					} catch (NumberFormatException ignored) {}
				}
				break;
			case "moves": //TODO test
				return asReadableList(values, 2, getAllAttackNames(stats));
		}
		throw new NoValueException();
	}

	public static Object[] getAllAttackNames(BaseStats stats) {
		return stats.abilities;
	}

	public static Object parsePokemonInfo(net.minecraft.entity.Entity owner, PlayerStorage storage, int[] id, String[] values) throws NoValueException {
		if (id.length == 2 && id[0] == -1 && id[1] == -1) {
			return PPConfig.teamMemberNotAvailableText;
		}

		boolean isSentOut = true; //The nbt "IsInBall" might be incorrect, so we save the real value here, before we load the pixelmon.
		//TODO ^ this is stupid pls fix

		EntityPixelmon pokemon = storage.getAlreadyExists(id, owner.world).orElse(null);
		if (pokemon == null) {
			pokemon = storage.sendOut(id, owner.world);
			isSentOut = false;
		}

		if (pokemon == null) {
			return PPConfig.entityNotFoundText;
		}

		if (pokemon.isEgg && PPConfig.disableEggInfo) {
			return PPConfig.disabledEggText;
		}
		//everything till this since 1.3.1

		if (values.length >= 2) {
			switch (values[1]) {
				case "nickname":
					return pokemon.hasNickname() ? pokemon.getNickname() : pokemon.getName();
				case "exp":
					return formatBigNumbers(pokemon.getLvl().getExp());
				case "level":
					return pokemon.getLvl().getLevel();
				case "exptolevelup":
					return formatBigNumbers(pokemon.getLvl().getExpForNextLevelClient());
				case "stats":
					if (values.length >= 3) {
						switch (values[2]) {
							case "hp":
								return pokemon.stats.HP;
							case "atk":
								return pokemon.stats.Attack;
							case "def":
								return pokemon.stats.Defence;
							case "spa":
								return pokemon.stats.SpecialAttack;
							case "spd":
								return pokemon.stats.SpecialDefence;
							case "spe":
								return pokemon.stats.Speed;
							case "ivs":
								if (values.length >= 4) {
									switch (values[3]) {
										case "hp":
											return pokemon.stats.IVs.HP;
										case "atk":
											return pokemon.stats.IVs.Attack;
										case "def":
											return pokemon.stats.IVs.Defence;
										case "spa":
											return pokemon.stats.IVs.SpAtt;
										case "spd":
											return pokemon.stats.IVs.SpDef;
										case "spe":
											return pokemon.stats.IVs.Speed;
										case "total": //since 1.2.3
											return pokemon.stats.IVs.HP
													+ pokemon.stats.IVs.Attack
													+ pokemon.stats.IVs.Defence
													+ pokemon.stats.IVs.SpAtt
													+ pokemon.stats.IVs.SpDef
													+ pokemon.stats.IVs.Speed;
										case "totalpercentage":
											String result3 = "" + (pokemon.stats.IVs.HP
													+ pokemon.stats.IVs.Attack
													+ pokemon.stats.IVs.Defence
													+ pokemon.stats.IVs.SpAtt
													+ pokemon.stats.IVs.SpDef
													+ pokemon.stats.IVs.Speed) * 100 / 186;
											if (result3.substring(result3.indexOf(".") + 1).length() == 1) {
												return result3.substring(0, result3.length() - 2);
											} else {
												return result3.substring(0, result3.indexOf(".") + 3);
											}
									}
								}
								break;
							case "evs":
								if (values.length >= 4) {
									switch (values[3]) {
										case "hp":
											return pokemon.stats.EVs.HP;
										case "atk":
											return pokemon.stats.EVs.Attack;
										case "def":
											return pokemon.stats.EVs.Defence;
										case "spa":
											return pokemon.stats.EVs.SpecialAttack;
										case "spd":
											return pokemon.stats.EVs.SpecialDefence;
										case "spe":
											return pokemon.stats.EVs.Speed;
										case "total": //since 1.2.3
											return pokemon.stats.EVs.HP
													+ pokemon.stats.EVs.Attack
													+ pokemon.stats.EVs.Defence
													+ pokemon.stats.EVs.SpecialAttack
													+ pokemon.stats.EVs.SpecialDefence
													+ pokemon.stats.EVs.Speed;
										case "totalpercentage":
											String result4 = "" + (pokemon.stats.EVs.HP
													+ pokemon.stats.EVs.Attack
													+ pokemon.stats.EVs.Defence
													+ pokemon.stats.EVs.SpecialAttack
													+ pokemon.stats.EVs.SpecialDefence
													+ pokemon.stats.EVs.Speed) / 510;
											if (result4.substring(result4.indexOf(".") + 1).length() == 1) {
												return result4.substring(0, result4.length() - 2);
											} else {
												return result4.substring(0, result4.indexOf(".") + 3);
											}
									}
								}
								break;
						}
					}
					break;
				case "helditem":
					return pokemon.heldItem == null ? PPConfig.noneText : pokemon.heldItem.getDisplayName();
				case "pos": //Before 1.3.0: last known position. After: exact pokémon position or player's position if carried in a ball
					if (values.length >= 3) {
						//Vector3d pos = isSentOut ? new Vector3d(pokemon.getPosition().getX(), pokemon.getPosition().getY(), pokemon.getPosition().getZ()) : player.getPosition();
						switch (values[2]) {
							case "x":
								//return pos.getX(); <-- this is way more elegant but i'd like to avoid creating a new vector on every placeholder request
								return formatDouble(isSentOut ? pokemon.getPosition().getX() : owner.getPosition().getX()); //NOTE: This method won't work with 7.0.0!
							case "y":
								return formatDouble(isSentOut ? pokemon.getPosition().getY() : owner.getPosition().getY());
							case "z":
								return formatDouble(isSentOut ? pokemon.getPosition().getZ() : owner.getPosition().getZ());
						}
					}
					break;
				case "moveset":
					Moveset moveset = pokemon.getMoveset();
					try {
						return moveset.get(Integer.parseInt(values[2]) - 1);
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
						return asReadableList(values, 2, moveset.attacks);
					}
				case "friendship":
					return formatBigNumbers(pokemon.friendship.getFriendship());
				case "ability":
					if (values.length == 2) { //why did i put this check here? UPDATE: Ohhh yeah so it is reflected over the generic placeholders
						return pokemon.getAbility().getName();
					}
					break;
				case "ball": //Updated 1.3.0: uses localized names
					return pokemon.caughtBall.getItem().getLocalizedName();
				//Since 1.2.0
				//case "possibledrops":
				//	return asReadableList(pokeValues, 2, DropItemRegistry.getDropsForPokemon(pokemon).stream().map(ParserUtility::getItemStackInfo).toArray());
				case "nature":
					return pokemon.getNature();
				case "gender": //since 1.2.3
					return pokemon.getGender().name();
				case "growth":
					return pokemon.getGrowth().name();
				case "shiny": //Since 1.3.0
					return pokemon.getIsShiny();
				case "hiddenpower":
					return HiddenPower.getHiddenPowerType(pokemon.stats.IVs);
				case "ivtotalsum":
					return sumIvs(pokemon.stats.IVs);
				case "evtotalsum":
					return sumEvs(pokemon.stats.EVs);
				case "ivtotalpercent":
					return String.format("%.0f%%", ((float) sumIvs(pokemon.stats.IVs) / (float) getMaxIvs()) * 100);
				case "evtotalpercent":
					return String.format("%.0f%%", ((float) sumEvs(pokemon.stats.EVs) / (float) getMaxEvs()) * 100);
				case "texturelocation":
					return getSprite(pokemon);
				case "customtexture":
				    return getCustomTexture(pokemon);
				case "form":
					return pokemon.getForm();
                case "aura":
                    return getAuraID(pokemon);
			}
		}
		return parsePokedexInfo(EnumPokemon.getFromNameAnyCase(pokemon.getPokemonName()), values);
	}

	private static String getCustomTexture(EntityPixelmon pokemon) {
		String custom =  pokemon.getDataManager().get(EntityPixelmon.dwCustomTexture);
		if (custom == null || custom.isEmpty()) {
			return "N/A";
		}
		return custom;
	}

	private static String getAuraID(EntityPixelmon pokemon) {
		if (!Sponge.getPluginManager().isLoaded("entity-particles")) {
			return "N/A";
		}
		return ((Entity) pokemon).get(EntityParticlesKeys.PARTICLE_ID).orElse("N/A");
	}

	private static String getSprite(EntityPixelmon pokemon) {
		EnumPokemon enumPokemon = EnumPokemon.getFromNameAnyCase(pokemon.getPokemonName());

		return getSpriteFromID(enumPokemon.getNationalPokedexInteger(), pokemon.getPokemonName(), pokemon.getIsShiny(), pokemon.isEgg,
					pokemon.eggCycles, pokemon.getForm());
	}

	// Based on com.pixelmonmod.pixelmon.client.render.tileEntities.RenderTileEntityTradingMachine#getSpriteFromID
	private static String getSpriteFromID(int nationalPokedexNumber, String pokemonName, boolean isShiny, boolean isEgg, int eggCycles, int variant) {
		String basePath = "pixelmon:sprites/pokemon/";
		if (isShiny) {
			basePath = "pixelmon:sprites/shinypokemon/";
		} else if (isEgg) {
			basePath = "pixelmon:sprites/eggs/";
		}

		if (isEgg) {
			String eggType = "egg";
			if (nationalPokedexNumber == 175) {
				eggType = "togepi";
			} else if (nationalPokedexNumber == 490) {
				eggType = "manaphy";
			}

			if (eggCycles > 10) {
				return basePath + eggType + "1";
			} else {
				return eggCycles > 5 ? basePath + eggType + "2" : basePath + eggType + "3";
			}
		} else {
			Optional<EnumPokemon> optional = EnumPokemon.getFromName(pokemonName);
			if (optional.isPresent()) {
				EnumPokemon pokemon = (EnumPokemon)optional.get();
				return basePath + pokemon.getNationalPokedexNumber() + pokemon.getFormEnum(variant).getSpriteSuffix();
			} else {
				return basePath + String.format("%03d", nationalPokedexNumber);
			}
		}
	}

	private static int sumIvs(IVStore ivStore) {
		return Arrays.stream(ivStore.getArray()).sum();
	}

	private static int sumEvs(EVsStore eVsStore) {
        return  + (eVsStore.HP
                + eVsStore.Attack
                + eVsStore.Defence
                + eVsStore.SpecialAttack
                + eVsStore.SpecialDefence
                + eVsStore.Speed);
	}

	private static int getMaxIvs() {
		IVStore store = new IVStore();
		store.maximizeIVs();
		return sumIvs(store);
	}

	private static int getMaxEvs() {
		return EVsStore.MAX_TOTAL_EVS;
	}

	/**
	 * @param values
	 * @param index  The index in the array values where the method should start
	 * @param data
	 * @return
	 */
	public static Object asReadableList(String[] values, int index, Object[] data) {
		String separator = ", ";
		if (values.length == index + 1) {
			separator = values[index].replaceAll("--", " ");
		}
		String list = "";
		for (Object s : data) {
			if (s != null) {
				if (s.equals(data[0])) {
					list = s.toString();
				} else {
					list = list.concat(separator + s);
				}
			}
		}
		return list.isEmpty() ? PPConfig.noneText : list;
	}

	public static String formatBigNumbers(int number) {
		if (number < 1000) {
			return String.valueOf(number);
		} else if (number < 1000000) {
			return String.valueOf((double) Math.round(number / 100) / 10) + "k";
		} else if (number < 1000000000) {
			return String.valueOf((double) Math.round(number / 100000) / 10) + "m";
		} else {
			return String.valueOf((double) Math.round(number / 100000000) / 10) + "b";
		}
	}

	private static DecimalFormat formatter = new DecimalFormat();

	static {
		formatter.setMaximumFractionDigits(PPConfig.maxFractionDigits);
		formatter.setMinimumFractionDigits(PPConfig.minFractionDigits);
	}

	public static String formatDouble(double number) {
		return formatter.format(number);
	}

	public static Object getItemStackInfo(@Nullable ItemStack is) {
		return is == null || is.getCount() == 0 ? PPConfig.noneText : is.getCount() + " " + is.getDisplayName();
	}

	/*
		TODO normalize function
		takes a string and returns the human-readable version of it
		MossyRock => Mossy Rock
		CLEAR => Clear
	 */

	//Please stop yelling at me Sandy! not in front of our children!
	private static Map<String, EvoParser> evoParsers = new HashMap<>();

	static {
		evoParsers.put("chance", new EvoParser<ChanceCondition>(ChanceCondition.class) {
			@Override
			public Object parse(ChanceCondition condition, String[] values, int index) {
				return formatDouble(condition.chance);
			}
		});
		evoParsers.put("friendship", new EvoParser<FriendshipCondition>(FriendshipCondition.class) {
			@Override
			public Object parse(FriendshipCondition condition, String[] values, int index) throws IllegalAccessException {
				int f = friendship.getInt(condition);
				return f == -1 ? 220 : f;
			}
		});
		evoParsers.put("gender", new EvoParser<GenderCondition>(GenderCondition.class) {
			@Override
			public Object parse(GenderCondition condition, String[] values, int index) {
				return asReadableList(values, index, condition.genders/*.stream().filter(gender -> gender != Gender.None)*/.toArray());
			}
		});
		evoParsers.put("altitude", new EvoParser<HighAltitudeCondition>(HighAltitudeCondition.class) {
			@Override
			public Object parse(HighAltitudeCondition condition, String[] values, int index) {
				return formatDouble(condition.minAltitude);
			}
		});
		evoParsers.put("level", new EvoParser<LevelCondition>(LevelCondition.class) {
			@Override
			public Object parse(LevelCondition condition, String[] values, int index) throws IllegalAccessException {
				return level.getInt(condition);
			}
		});
		evoParsers.put("movetype", new EvoParser<MoveTypeCondition>(MoveTypeCondition.class) {
			@Override
			public Object parse(MoveTypeCondition condition, String[] values, int index) throws IllegalAccessException {
				return ((EnumType) type.get(condition)).getLocalizedName();
			}
		});
		evoParsers.put("party", new EvoParser<PartyCondition>(PartyCondition.class) {
			@Override
			public Object parse(PartyCondition condition, String[] values, int index) throws NoValueException {
				if (values.length > index) {
					if (values[index].equals("pokemon")) {
						return asReadableList(values, index + 1, condition.withPokemon.toArray());
					} else if (values[index].equals("type")) {
						return asReadableList(values, index + 1, condition.withTypes.toArray());
					}
				}
				throw new NoValueException();
			}
		});
		//TODO StatRatioCondition
		evoParsers.put("time", new EvoParser<TimeCondition>(TimeCondition.class) { //TODO fix
			@Override
			public Object parse(TimeCondition condition, String[] values, int index) throws NoValueException {
				throw new NoValueException();
				//return condition.day ? "Day" : "Night";
			}
		});
		evoParsers.put("weather", new EvoParser<WeatherCondition>(WeatherCondition.class) {
			@Override
			public Object parse(WeatherCondition condition, String[] values, int index) throws IllegalAccessException {
				String w = ((WeatherType) weather.get(condition)).name();
				return w.substring(1) + w.substring(1, w.length() - 1);
			}
		});
	}

	public static abstract class EvoParser<T extends EvoCondition> {
		public Class<T> clazz;

		public EvoParser(Class<T> clazz) {
			this.clazz = clazz;
		}

		public abstract Object parse(T condition, String[] values, int index) throws NoValueException, IllegalAccessException;
	}
}
