package com.github.happyzleaf.pixelmonplaceholders;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVsStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Stats;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.pokedex.Pokedex;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import me.rojo8399.placeholderapi.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Arrays;
import java.util.Optional;

import static com.github.happyzleaf.pixelmonplaceholders.utility.ParserUtility.*;

@Listening
public class Placeholders {
	@Placeholder(id = "trainer")
	public Object trainer(@Source Player player, @Token String token) throws NoValueException {
		Optional<PlayerStorage> optStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player);
		if (optStorage.isPresent()) {
			PlayerStorage storage = optStorage.get();
			String[] values = token.split("_");
			switch (values[0]) {
				case "dexcount":
					return storage.pokedex.countCaught();
				case "dexpercentage":
					String result1 = String.valueOf((double) storage.pokedex.countCaught() * 100 / EnumPokemon.values().length);
					if (result1.substring(result1.indexOf(".") + 1).length() == 1) {
						return result1.substring(0, result1.length() - 2);
					} else {
						return result1.substring(0, result1.indexOf(".") + 3);
					}
				case "seencount":
					return storage.pokedex.getSeenMap().size();
				case "wins":
					return storage.stats.getWins();
				case "losses":
					return storage.stats.getLosses();
				case "wlratio":
					int wins = storage.stats.getWins();
					int total = wins + storage.stats.getLosses();
					double result2;
					if (total <= 0) {
						result2 = 1;
					} else {
						result2 = (double) wins / total;
					}
					if (String.valueOf(result2).substring(String.valueOf(result2).indexOf(".") + 1).length() == 1) {
						return String.valueOf(result2).concat("0");
					} else {
						return String.valueOf(result2).substring(0, String.valueOf(result2).indexOf(".") + 3);
					}
				case "balance":
					return formatBigNumbers(storage.getCurrency());
				case "team":
					if (values.length > 1) {
						String[] pokeValues = Arrays.copyOfRange(values, 1, values.length);
						try {
							EntityPixelmon pokemon = storage.getPokemon(storage.getIDFromPosition(Integer.parseInt(pokeValues[0]) - 1), (World) player.getWorld());
							if (pokemon == null) {
								throw new NoValueException();
							}
							
							if (pokemon.isEgg && PPConfig.disableEggInfo) {
								return TextSerializers.FORMATTING_CODE.deserialize(PPConfig.disabledEggMessage);
							}
							
							if (pokeValues.length >= 2) {
								switch (pokeValues[1]) {
									case "nickname":
										return pokemon.hasNickname() ? pokemon.getNickname() : pokemon.getName();
									case "exp":
										return formatBigNumbers(pokemon.getLvl().getExp());
									case "level":
										return pokemon.getLvl().getLevel();
									case "exptolevelup":
										return formatBigNumbers(pokemon.getLvl().getExpForNextLevelClient());
									case "stats":
										if (pokeValues.length >= 3) {
											Stats stats = pokemon.stats;
											switch (pokeValues[2]) {
												case "hp":
													return stats.HP;
												case "atk":
													return stats.Attack;
												case "def":
													return stats.Defence;
												case "spa":
													return stats.SpecialAttack;
												case "spd":
													return stats.SpecialDefence;
												case "spe":
													return stats.Speed;
												case "ivs":
													if (pokeValues.length >= 4) {
														IVStore ivs = stats.IVs;
														switch (pokeValues[3]) {
															case "hp":
																return ivs.HP;
															case "atk":
																return ivs.Attack;
															case "def":
																return ivs.Defence;
															case "spa":
																return ivs.SpAtt;
															case "spd":
																return ivs.SpDef;
															case "spe":
																return ivs.Speed;
															case "total": //since 1.2.3
																return ivs.HP + ivs.Attack + ivs.Defence + ivs.SpAtt + ivs.SpDef + ivs.Speed;
															case "totalpercentage":
																String result3 = "" + (ivs.HP + ivs.Attack + ivs.Defence + ivs.SpAtt + ivs.SpDef + ivs.Speed) * 100 / 186;
																if (result3.substring(result3.indexOf(".") + 1).length() == 1) {
																	return result3.substring(0, result3.length() - 2);
																} else {
																	return result3.substring(0, result3.indexOf(".") + 3);
																}
														}
													}
													break;
												case "evs":
													if (pokeValues.length >= 4) {
														EVsStore evs = stats.EVs;
														switch (pokeValues[3]) {
															case "hp":
																return evs.HP;
															case "atk":
																return evs.Attack;
															case "def":
																return evs.Defence;
															case "spa":
																return evs.SpecialAttack;
															case "spd":
																return evs.SpecialDefence;
															case "spe":
																return evs.Speed;
															case "total": //since 1.2.3
																return evs.HP + evs.Attack + evs.Defence + evs.SpecialAttack + evs.SpecialDefence + evs.Speed;
															case "totalpercentage":
																String result4 = "" + (evs.HP + evs.Attack + evs.Defence + evs.SpecialAttack + evs.SpecialDefence + evs.Speed) / 510;
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
										return pokemon.heldItem == null ? "None" : pokemon.heldItem.getDisplayName();
									case "pos":
										if (pokeValues.length >= 3) {
											BlockPos pos = pokemon.getPosition();
											switch (pokeValues[2]) {
												case "x":
													return pos.getX();
												case "y":
													return pos.getY();
												case "z":
													return pos.getZ();
											}
										}
										break;
									case "moveset":
										Moveset moveset = pokemon.getMoveset();
										try {
											return moveset.get(Integer.parseInt(pokeValues[2]) - 1);
										} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
											return asReadableList(pokeValues, 2, moveset.attacks);
										}
									case "friendship":
										return formatBigNumbers(pokemon.friendship.getFriendship());
									case "ability":
										if (pokeValues.length == 2) { //why did i put this check here? UPDATE: Ohhh yeah so it is reflected over the generic placeholders
											return pokemon.getAbility().getName();
										}
										break;
									case "ball":
										return pokemon.caughtBall.name();
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
								}
							}
							
							return parsePokedexInfo(EnumPokemon.getFromNameAnyCase(pokemon.getPokemonName()), pokeValues);
						} catch (NumberFormatException ignored) {}
					}
					break;
			}
		}
		throw new NoValueException();
	}
	
	@Placeholder(id = "pixelmon")
	public Object pixelmon(@Source Player player, @Token String token) throws NoValueException {
		switch (token) {
			case "dexsize":
				return EnumPokemon.values().length;
			case "dexsizeall":
				return Pokedex.pokedexSize;
		}
		throw new NoValueException();
	}
	
	@Placeholder(id = "pokedex")
	public Object pokedex(@Source Player player, @Token String token) throws NoValueException {
		String[] values = token.split("_");
		if (values.length >= 1) {
			EnumPokemon pokemon = null;
			
			try {
				int nationalId = Integer.parseInt(values[0]);
				if (nationalId >= 0 && nationalId <= EnumPokemon.values().length) {
					pokemon = EnumPokemon.getFromNameAnyCase(Pokedex.fullPokedex.get(nationalId).name);
				}
			} catch (NumberFormatException e) {
				pokemon = EnumPokemon.getFromNameAnyCase(values[0]);
			}
			
			if (pokemon != null) {
				return parsePokedexInfo(pokemon, values);
			}
		}
		throw new NoValueException();
	}
}
