package me.br0mabs;

import com.iwebpp.crypto.TweetNaclFast;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;
import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.api.Permission.VOICE_MOVE_OTHERS;
import static net.dv8tion.jda.api.utils.MemberCachePolicy.ALL;
import static net.dv8tion.jda.api.utils.MemberCachePolicy.PENDING;

public class DiscordBot extends ListenerAdapter {

    public static Map<String,String> tileids = new HashMap<String,String>();
    public static List<String> WALL_TEMPLATE = new ArrayList<String>();
    public static List<String> WALL_TEMPLATE_NO_RED_FIVES = new ArrayList<String>();

    public static List<String> ONLY_MANZU = new ArrayList<String>();
    public static List<String> ONLY_PINZU = new ArrayList<String>();
    public static List<String> ONLY_SOUZU = new ArrayList<String>();
    public static Map<String,List<String>> WALLS = new HashMap<String,List<String>>();
    public static Map<String,List<String>> HANDS = new HashMap<String,List<String>>();
    public static List<List<Integer>> COMBINATION_INDICES = new ArrayList<>();

    public static HashMap<String,List<String>> USERS_USING_MAHJONG_HANDLE = new HashMap<>();
    public static HashMap<String,Integer> USER_GUESSES_REMAINING = new HashMap<>();

    // defense sim stuff
    public static HashMap<String,List<String>> OWN_HAND = new HashMap<>();
    public static HashMap<String,List<String>> RIICHI_HAND = new HashMap<>();
    public static HashMap<String,List<String>> PLAYER_ACROSS_HAND = new HashMap<>();
    public static HashMap<String,List<String>> PLAYER_RIGHT_HAND = new HashMap<>();

    public static HashMap<String,List<String>> OWN_DISCARD_PILE = new HashMap<>();
    public static HashMap<String,List<String>> RIICHI_DISCARD_PILE = new HashMap<>();
    public static HashMap<String,List<String>> PLAYER_ACROSS_DISCARD_PILE = new HashMap<>();
    public static HashMap<String,List<String>> PLAYER_RIGHT_DISCARD_PILE = new HashMap<>();

    public static HashMap<String,List<String>> DEFENSE_WALL = new HashMap<>();
    public static HashMap<String,List<String>> TENPAI_TILES = new HashMap<>();

    public static HashMap<String,List<String>> WAIT_TRAINER_USERS = new HashMap<>();

    public static long I_AM_DUMB_CHANNEL_ID = Long.parseLong("982820292881686528");

    public static void main(String[] args) throws LoginException, InterruptedException {

        tileids.put("0s", "994416283296731206");
        tileids.put("1s", "994416286798991471");
        tileids.put("2s", "994416290729054208");
        tileids.put("3s", "994416294814298233");
        tileids.put("4s", "994416298949877810");
        tileids.put("5s", "994416302787665960");
        tileids.put("6s", "994416306734510103");
        tileids.put("7s", "994416311792844942");
        tileids.put("8s", "994416317274783744");
        tileids.put("9s", "994416320647024670");

        tileids.put("0p", "994416281346383882");
        tileids.put("1p", "994416285553279016");
        tileids.put("2p", "994416289806291025");
        tileids.put("3p", "994416293539225671");
        tileids.put("4p", "994416298098430032");
        tileids.put("5p", "994416301802016788");
        tileids.put("6p", "994416305862082590");
        tileids.put("7p", "994416310769430618");
        tileids.put("8p", "994416316331065394");
        tileids.put("9p", "994416319690711041");

        tileids.put("0m", "994416279920312351");
        tileids.put("1m", "994416284139790438");
        tileids.put("2m", "994416288745136128");
        tileids.put("3m", "994416292947832852");
        tileids.put("4m", "994416296802390037");
        tileids.put("5m", "994416300543717430");
        tileids.put("6m", "994416304582836264");
        tileids.put("7m", "994416308999442452");
        tileids.put("8m", "994416314942763009");
        tileids.put("9m", "994416318688272475");

        tileids.put("1z", "994416287717544056");
        tileids.put("2z", "994416291970564226");
        tileids.put("3z", "994416295787364353");
        tileids.put("4z", "994416299927158864");
        tileids.put("5z", "994416303282585641");
        tileids.put("6z", "994416307678232646");
        tileids.put("7z", "994416314040987658");

        List<Integer> tmpIndices = new ArrayList<>();
        // empty combination (0 triples)
        COMBINATION_INDICES.add(tmpIndices);

        // 1 triple
        tmpIndices = new ArrayList<>();
        tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        // 2 triples
        tmpIndices = new ArrayList<>();
        tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(0); tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);

        // 3 triples
        tmpIndices = new ArrayList<>();
        tmpIndices.add(2);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(2); tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(2); tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(2); tmpIndices.add(1); tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        // 4 triples
        tmpIndices = new ArrayList<>();
        tmpIndices.add(3);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(3); tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(3); tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(3); tmpIndices.add(2);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(3); tmpIndices.add(2); tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(3); tmpIndices.add(2); tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(3); tmpIndices.add(1); tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices = new ArrayList<>();
        tmpIndices.add(3); tmpIndices.add(2); tmpIndices.add(1); tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        char[] suits = {'m', 'p', 's', 'z'};
        for (int i = 0; i < 4; ++i) {
            for (int j = 1; j <= 9; ++j) {
                if (i == 0) {
                    String tmp = "";
                    tmp += (char) (j + '0');
                    tmp += suits[i];
                    ONLY_MANZU.add(tmp);
                    ONLY_MANZU.add(tmp);
                    ONLY_MANZU.add(tmp);
                    ONLY_MANZU.add(tmp);
                } else if (i == 1) {
                    String tmp = "";
                    tmp += (char) (j + '0');
                    tmp += suits[i];
                    ONLY_PINZU.add(tmp);
                    ONLY_PINZU.add(tmp);
                    ONLY_PINZU.add(tmp);
                    ONLY_PINZU.add(tmp);
                } else if (i == 2) {
                    String tmp = "";
                    tmp += (char) (j + '0');
                    tmp += suits[i];
                    ONLY_SOUZU.add(tmp);
                    ONLY_SOUZU.add(tmp);
                    ONLY_SOUZU.add(tmp);
                    ONLY_SOUZU.add(tmp);
                }
                if (i == 3 && j == 8) break;
                for (int k = 1; k <= ((j == 5 && i != 3) ? 3 : 4); ++k) {
                    String tmp = "";
                    tmp += (char) (j + '0');
                    tmp += suits[i];
                    WALL_TEMPLATE.add(tmp);
                    WALL_TEMPLATE_NO_RED_FIVES.add(tmp);
                }
            }
        }
        WALL_TEMPLATE.add("0m");
        WALL_TEMPLATE.add("0p");
        WALL_TEMPLATE.add("0s");
        WALL_TEMPLATE_NO_RED_FIVES.add("5m");
        WALL_TEMPLATE_NO_RED_FIVES.add("5p");
        WALL_TEMPLATE_NO_RED_FIVES.add("5s");

        Config config = new Config();
        JDA bot = JDABuilder.createDefault(config.getToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.playing("mahgong sole"))
                .addEventListeners(new DiscordBot())
                .build();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String messageSent = event.getMessage().getContentRaw();

            String author = event.getAuthor().getId();
            Guild guild = event.getGuild();
            Member sender = guild.getMemberById(author);
            final TextChannel channel = event.getTextChannel();

            // this means that we are going to continue using the [trainer command for this individual
            if (WALLS.containsKey(author) && (messageSent.startsWith("[d") || messageSent.startsWith("[quit"))) {
                if (messageSent.equals("[quit")) {
                    event.getTextChannel().sendMessage("trainer quit sucessfully, <@" + author + ">!").queue();
                    WALLS.remove(author);
                    HANDS.remove(author);
                    return;
                }
                if (messageSent.length() < 4) {
                    event.getTextChannel().sendMessage("enter a valid tile to discard (`[d <tile>`), or type \"[quit\"!").queue();
                    return;
                }
                messageSent = messageSent.substring(3);
                List<String> wall = WALLS.get(author);
                List<String> hand = HANDS.get(author);
                if (!HANDS.get(author).contains(messageSent)) {
                    event.getTextChannel().sendMessage("enter a valid tile to discard (`[d <tile>`), or type \"[quit\"!").queue();
                    return;
                }
                hand.remove(messageSent);
                if (wall.size() == 14) {
                    event.getTextChannel().sendMessage("out of tiles, training has ended, <@" + author + ">!").queue();
                    WALLS.remove(author);
                    HANDS.remove(author);
                    return;
                }
                List<String> handtenpai = new ArrayList<>(hand);
                List<String> tenpaiTiles = checkTenpai(handtenpai);
                if (!tenpaiTiles.isEmpty()) {
                    tenpaiTiles.add("tmp");
                    tenpaiTiles = sortHand(tenpaiTiles);
                    tenpaiTiles.remove(tenpaiTiles.size() - 1);
                    event.getTextChannel().sendMessage("you are in tenpai, waiting on:").queue();
                    StringBuilder outputMsg = new StringBuilder();
                    for (int i = 0; i < tenpaiTiles.size(); ++i) {
                        outputMsg.append("<:" + tenpaiTiles.get(i) + ":" + tileids.get(tenpaiTiles.get(i)) + ">");
                    }
                    event.getTextChannel().sendMessage(outputMsg).queue();
                }
                String drawnTile = wall.get(wall.size() - 1);
                wall.remove(wall.size() - 1);
                hand.add(drawnTile);
                hand = sortHand(hand);
                StringBuilder outputMsg = convertToHand(hand);
                event.getTextChannel().sendMessage("current hand for " + "<@" + author + ">:").queue();
                event.getTextChannel().sendMessage(outputMsg).queue();
                List<String> handchecker = new ArrayList<>(hand);
                handchecker.add("tmp");
                handchecker = sortHand(handchecker);
                handchecker.remove(handchecker.size() - 1);
                if (checkWinningHandNormal(handchecker) || checkThirteenOrphans(handchecker) || checkSevenPairs(handchecker)) {
                    event.getTextChannel().sendMessage("tsumo nya! the hand is won (trainer quits automatically)").queue();
                    WALLS.remove(author);
                    HANDS.remove(author);
                } else {
                    event.getTextChannel().sendMessage("discard (`[d <tile>` or quit (`[quit`), " + (wall.size() - 14) + " tiles remaining.").queue();
                    WALLS.put(author, wall);
                    HANDS.put(author, hand);
                }
            }

            else if (USERS_USING_MAHJONG_HANDLE.containsKey(author) && messageSent.startsWith("[guess")) {

                List<String> actualHand = USERS_USING_MAHJONG_HANDLE.get(author);
                if (messageSent.length() < 8) {
                    event.getTextChannel().sendMessage("invalid guess").queue();
                    return;
                }
                messageSent = messageSent.substring(7);
                List<String> tiles = parseTiles(messageSent);
                if (tiles.get(tiles.size() - 1).equals("bad")) {
                    event.getTextChannel().sendMessage("invalid guess").queue();
                    return;
                } else if (tiles.size() != 14) {
                    event.getTextChannel().sendMessage("invalid guess").queue();
                    return;
                }
                List<String> tmp = new ArrayList<>(tiles);
                tmp = sortHand(tmp);
                if (!tmp.equals(tiles)) {
                    event.getTextChannel().sendMessage("sort the guess").queue();
                    return;
                }
                StringBuilder userHand = convertToHand(tiles);
                event.getTextChannel().sendMessage("<@" + author + ">:'s hand").queue();
                event.getTextChannel().sendMessage(userHand).queue();
                // stores the number of guesses of a tile
                Map<String,Integer> numGuesses = new HashMap<>();
                // will be true if user gets a yellow or a black
                boolean failed = false;
                // stores the emojis
                List<String> result = new ArrayList<>();
                for (int i = 0; i < tiles.size(); ++i) result.add("");
                // if the tile is in the correct position, give green, if this tile does not exist in the hand, give black
                for (int i = 0; i < tiles.size(); ++i) {
                    if (tiles.get(i).equals(actualHand.get(i))) {
                        result.set(i, ":green_square:");
                        if (!numGuesses.containsKey(tiles.get(i))) {
                            numGuesses.put(tiles.get(i), 1);
                        } else numGuesses.put(tiles.get(i), numGuesses.get(tiles.get(i)) + 1);
                    } else if (!actualHand.contains(tiles.get(i))) {
                        result.set(i, ":black_large_square:");
                        failed = true;
                    }
                }
                // process yellow
                for (int i = 0; i < tiles.size(); ++i) {
                    if (!tiles.get(i).equals(actualHand.get(i)) && actualHand.contains(tiles.get(i))) {
                        failed = true;
                        if (!numGuesses.containsKey(tiles.get(i))) {
                            numGuesses.put(tiles.get(i), 1);
                        } else numGuesses.put(tiles.get(i), numGuesses.get(tiles.get(i)) + 1);
                        if (Collections.frequency(actualHand, tiles.get(i)) < numGuesses.get(tiles.get(i))) {
                            result.set(i, ":black_large_square:");
                        } else result.set(i, ":yellow_square:");
                    }
                }
                StringBuilder resultMsg = new StringBuilder();
                for (int i = 0; i < result.size(); ++i) {
                    if (i == result.size() - 1) {
                        resultMsg.append("               ");
                    }
                    resultMsg.append(result.get(i));
                }
                event.getTextChannel().sendMessage(resultMsg).queue();
                if (!failed) {
                    event.getTextChannel().sendMessage("congratulations on guessing the hand nya!").queue();
                    USER_GUESSES_REMAINING.remove(author);
                    USERS_USING_MAHJONG_HANDLE.remove(author);
                } else {
                    // reduce number of guesses
                    USER_GUESSES_REMAINING.put(author, USER_GUESSES_REMAINING.get(author) - 1);
                    if (USER_GUESSES_REMAINING.get(author) == 0) {
                        event.getTextChannel().sendMessage("no more guesses nya").queue();
                        StringBuilder outputMsg = convertToHand(USERS_USING_MAHJONG_HANDLE.get(author));
                        event.getTextChannel().sendMessage("the hand was:").queue();
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        USER_GUESSES_REMAINING.remove(author);
                        USERS_USING_MAHJONG_HANDLE.remove(author);
                    } else {
                        event.getTextChannel().sendMessage("you have " + USER_GUESSES_REMAINING.get(author) + " guesses remaining nya").queue();
                    }
                }
            }

            // defense trainer continuation
            else if (OWN_HAND.containsKey(author) && ((messageSent.startsWith("[quit") || messageSent.startsWith("[d")) && !messageSent.startsWith("viewdicsards"))) {
                if (messageSent.startsWith("[quit")) {
                    event.getTextChannel().sendMessage("quit the defense simulator nya").queue();
                    clearDefenseSession(author);
                    return;
                } else {
                    if (messageSent.length() < 4) {
                        event.getTextChannel().sendMessage("enter a valid tile to discard (`[d <tile>`), or type \"[quit\"!").queue();
                        return;
                    }
                    messageSent = messageSent.substring(3);
                    if (!WALL_TEMPLATE_NO_RED_FIVES.contains(messageSent)) {
                        event.getTextChannel().sendMessage("enter a valid tile to discard (`[d <tile>`), or type \"[quit\"!").queue();
                        return;
                    }
                    if (!OWN_HAND.get(author).contains(messageSent)) {
                        event.getTextChannel().sendMessage("enter a valid tile to discard (`[d <tile>`), or type \"[quit\"!").queue();
                        return;
                    }
                    List<String> ownHand = OWN_HAND.get(author);
                    List<String> riichiHand = RIICHI_HAND.get(author);
                    List<String> playerRightHand = PLAYER_RIGHT_HAND.get(author);
                    List<String> playerAcrossHand = PLAYER_ACROSS_HAND.get(author);
                    List<String> ownDiscards = OWN_DISCARD_PILE.get(author);
                    List<String> riichiDiscards = RIICHI_DISCARD_PILE.get(author);
                    List<String> rightDiscards = PLAYER_RIGHT_DISCARD_PILE.get(author);
                    List<String> acrossDiscards = PLAYER_ACROSS_DISCARD_PILE.get(author);
                    List<String> wall = DEFENSE_WALL.get(author);
                    List<String> tenpaiTiles = TENPAI_TILES.get(author);
                    event.getTextChannel().sendMessage("you discard").queue();
                    event.getTextChannel().sendMessage("<:" + messageSent + ":" + tileids.get(messageSent) + ">").queue();
                    ownHand.remove(messageSent);
                    ownDiscards.add(messageSent);
                    OWN_DISCARD_PILE.put(author, ownDiscards);
                    // if your discard got you ronned
                    if (tenpaiTiles.contains(messageSent)) {
                        event.getTextChannel().sendMessage("you got ronned L bozo lmao nya").queue();
                        event.getTextChannel().sendMessage("riichi'ers hand:").queue();
                        StringBuilder outputMsg = convertToTileSequence(riichiHand);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically. better luck next time nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // sort your own hand
                    ownHand.add("tmp");
                    ownHand = sortHand(ownHand);
                    ownHand.remove(ownHand.size() - 1);
                    // check for empty wall
                    if (wall.isEmpty()) {
                        event.getTextChannel().sendMessage("no more tiles in wall, successful defense nya!\nriichi hand was:").queue();
                        StringBuilder outputMsg = convertToTileSequence(riichiHand);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("waiting on:").queue();
                        outputMsg = convertToTileSequence(tenpaiTiles);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // opponent on the right draws
                    playerRightHand.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                    // opponent on the right discards
                    String rightPlayerDiscard = getDefenseDiscard(author, playerRightHand);
                    event.getTextChannel().sendMessage("right player discards").queue();
                    event.getTextChannel().sendMessage("<:" + rightPlayerDiscard + ":" + tileids.get(rightPlayerDiscard) + ">").queue();
                    playerRightHand.remove(rightPlayerDiscard);
                    rightDiscards.add(rightPlayerDiscard);
                    PLAYER_RIGHT_DISCARD_PILE.put(author, rightDiscards);
                    // if the player gets ronned
                    if (tenpaiTiles.contains(rightPlayerDiscard)) {
                        event.getTextChannel().sendMessage("right player got ronned L bozo lmao nya").queue();
                        event.getTextChannel().sendMessage("riichi'ers hand:").queue();
                        StringBuilder outputMsg = convertToTileSequence(riichiHand);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically. troll nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // if the tile passes (sort the hand)
                    playerRightHand.add("tmp");
                    playerRightHand = sortHand(playerRightHand);
                    playerRightHand.remove(playerRightHand.size() - 1);
                    // check for empty wall
                    if (wall.isEmpty()) {
                        event.getTextChannel().sendMessage("no more tiles in wall, successful defense nya!\nriichi hand was:").queue();
                        StringBuilder outputMsg = convertToTileSequence(riichiHand);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("waiting on:").queue();
                        outputMsg = convertToTileSequence(tenpaiTiles);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // opponent across draws
                    playerAcrossHand.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                    // opponent across discards
                    String acrossPlayerDiscard = getDefenseDiscard(author, playerAcrossHand);
                    event.getTextChannel().sendMessage("across player discards").queue();
                    event.getTextChannel().sendMessage("<:" + acrossPlayerDiscard + ":" + tileids.get(acrossPlayerDiscard) + ">").queue();
                    playerAcrossHand.remove(acrossPlayerDiscard);
                    acrossDiscards.add(acrossPlayerDiscard);
                    PLAYER_ACROSS_DISCARD_PILE.put(author, acrossDiscards);
                    // if the player gets ronned
                    if (tenpaiTiles.contains(acrossPlayerDiscard)) {
                        event.getTextChannel().sendMessage("across player got ronned L bozo lmao nya").queue();
                        event.getTextChannel().sendMessage("riichi'ers hand:").queue();
                        StringBuilder outputMsg = convertToTileSequence(riichiHand);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically. troll nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // if the tile passes (sort the hand)
                    playerAcrossHand.add("tmp");
                    playerAcrossHand = sortHand(playerAcrossHand);
                    playerAcrossHand.remove(playerAcrossHand.size() - 1);
                    // check for empty wall
                    if (wall.isEmpty()) {
                        event.getTextChannel().sendMessage("no more tiles in wall, successful defense nya!\nriichi hand was:").queue();
                        StringBuilder outputMsg = convertToTileSequence(riichiHand);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("waiting on:").queue();
                        outputMsg = convertToTileSequence(tenpaiTiles);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // riichi'er draws
                    riichiHand.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                    // check if it wins
                    List<String> checkWinHand = new ArrayList<>(riichiHand);
                    checkWinHand.add("tmp");
                    checkWinHand = sortHand(checkWinHand);
                    checkWinHand.remove(checkWinHand.size() - 1);
                    // hand is generated to only be normal 4 sets and 1 pair
                    if (checkWinningHandNormal(checkWinHand)) {
                        event.getTextChannel().sendMessage("tsumo nya! riichi'er won, but you didn't deal in nya :sunglasses:").queue();
                        StringBuilder outputMsg = convertToHand(riichiHand);
                        event.getTextChannel().sendMessage("riichi'er's hand:").queue();
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // if no win, discard tile that was drew
                    event.getTextChannel().sendMessage("riichi (left) player discards").queue();
                    event.getTextChannel().sendMessage("<:" + riichiHand.get(riichiHand.size() - 1) + ":" + tileids.get(riichiHand.get(riichiHand.size() - 1)) + ">").queue();
                    riichiDiscards.add(riichiHand.get(riichiHand.size() - 1));
                    RIICHI_DISCARD_PILE.put(author, riichiDiscards);
                    riichiHand.remove(riichiHand.size() - 1);
                    // check for empty wall
                    if (wall.isEmpty()) {
                        event.getTextChannel().sendMessage("no more tiles in wall, successful defense nya!\nriichi hand was:").queue();
                        StringBuilder outputMsg = convertToTileSequence(riichiHand);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("waiting on:").queue();
                        outputMsg = convertToTileSequence(tenpaiTiles);
                        event.getTextChannel().sendMessage(outputMsg).queue();
                        event.getTextChannel().sendMessage("the simulator quits automatically nya").queue();
                        clearDefenseSession(author);
                        return;
                    }
                    // you draw once again
                    ownHand.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                    event.getTextChannel().sendMessage("your hand:").queue();
                    StringBuilder ownHandParsed = convertToHand(ownHand);
                    event.getTextChannel().sendMessage(ownHandParsed).queue();
                    event.getTextChannel().sendMessage("`[quit` to quit, `[d <tile>` to discard, or `[viewdiscards` to view relevant discards..\nThere are " + wall.size() + " tiles left in the live wall.").queue();

                    // update everything in the map
                    OWN_HAND.put(author, ownHand);
                    RIICHI_HAND.put(author, riichiHand);
                    PLAYER_ACROSS_HAND.put(author, playerAcrossHand);
                    PLAYER_RIGHT_HAND.put(author, playerRightHand);
                    OWN_DISCARD_PILE.put(author, ownDiscards);
                    RIICHI_DISCARD_PILE.put(author, riichiDiscards);
                    PLAYER_ACROSS_DISCARD_PILE.put(author, acrossDiscards);
                    PLAYER_RIGHT_DISCARD_PILE.put(author, rightDiscards);
                    DEFENSE_WALL.put(author, wall);
                    TENPAI_TILES.put(author, tenpaiTiles);
                }
            }
            // shows all discards after riichi
            else if (OWN_HAND.containsKey(author) && messageSent.startsWith("[viewdiscards")) {
                // you
                event.getTextChannel().sendMessage("your discard pile after riichi:").queue();
                List<String> tiles = new ArrayList<>();
                for (int i = 8; i < OWN_DISCARD_PILE.get(author).size(); ++i) {
                    tiles.add(OWN_DISCARD_PILE.get(author).get(i));
                }
                if (tiles.isEmpty()) {
                    event.getTextChannel().sendMessage("no discards after riichi").queue();
                } else {
                    event.getTextChannel().sendMessage(convertToTileSequence(tiles)).queue();
                }
                // right
                event.getTextChannel().sendMessage("player to the right discard pile after riichi:").queue();
                tiles.clear();
                for (int i = 8; i < PLAYER_RIGHT_DISCARD_PILE.get(author).size(); ++i) {
                    tiles.add(PLAYER_RIGHT_DISCARD_PILE.get(author).get(i));
                }
                if (tiles.isEmpty()) {
                    event.getTextChannel().sendMessage("no discards after riichi").queue();
                } else {
                    event.getTextChannel().sendMessage(convertToTileSequence(tiles)).queue();
                }
                // across
                event.getTextChannel().sendMessage("player across discard pile after riichi:").queue();
                tiles.clear();
                for (int i = 8; i < PLAYER_ACROSS_DISCARD_PILE.get(author).size(); ++i) {
                    tiles.add(PLAYER_ACROSS_DISCARD_PILE.get(author).get(i));
                }
                if (tiles.isEmpty()) {
                    event.getTextChannel().sendMessage("no discards after riichi").queue();
                } else {
                    event.getTextChannel().sendMessage(convertToTileSequence(tiles)).queue();
                }
                // riichi'er
                event.getTextChannel().sendMessage("player to the left (riichi'er) **full** discard pile").queue();
                tiles.clear();
                for (int i = 0; i < RIICHI_DISCARD_PILE.get(author).size(); ++i) {
                    tiles.add(RIICHI_DISCARD_PILE.get(author).get(i));
                }
                event.getTextChannel().sendMessage(convertToTileSequence(tiles)).queue();
                event.getTextChannel().sendMessage("`[quit` to quit, `[d <tile>` to discard, or `[viewdiscards` to view relevant discards..\nThere are " + DEFENSE_WALL.get(author).size() + " tiles left in the live wall.").queue();
            } else if (WAIT_TRAINER_USERS.containsKey(author) && messageSent.startsWith("[wait")) {
                if (messageSent.length() < 7) {
                    event.getTextChannel().sendMessage("enter valid tiles").queue();
                    return;
                }
                messageSent = messageSent.substring(6);
                List<String> tiles = parseTiles(messageSent);
                if (tiles.contains("bad")) {
                    event.getTextChannel().sendMessage("enter valid tiles").queue();
                    return;
                }
                tiles.add("b");
                tiles = sortHand(tiles);
                tiles.remove(tiles.size() - 1);
                StringBuilder seq = convertToTileSequence(tiles);
                event.getTextChannel().sendMessage("your inputted waits:").queue();
                event.getTextChannel().sendMessage(seq).queue();
                StringBuilder hand = convertToTileSequence(WAIT_TRAINER_USERS.get(author));
                event.getTextChannel().sendMessage("the actual waits: ").queue();
                event.getTextChannel().sendMessage(hand).queue();
                if (tiles.size() != WAIT_TRAINER_USERS.get(author).size()) {
                    event.getTextChannel().sendMessage("better luck next time nya!").queue();
                    WAIT_TRAINER_USERS.remove(author);
                } else {
                    for (int i = 0; i < tiles.size(); ++ i) {
                        if (!tiles.get(i).equals(WAIT_TRAINER_USERS.get(author).get(i))) {
                            event.getTextChannel().sendMessage("better luck next time nya!").queue();
                            WAIT_TRAINER_USERS.remove(author);
                            return;
                        }
                    }
                    event.getTextChannel().sendMessage("you chose the correct waits nya!").queue();
                    WAIT_TRAINER_USERS.remove(author);
                }
            }
            // [help command
            else if (messageSent.equals("[help")) {
                //event.getTextChannel().sendMessage("commands:\n`[tile <tile> (use 0 for red 5) displays images of tiles\nex:\n`[tiles 345m`\n`[tiles 567p47z`\n`[tiles 405s6z3p").queue();
                // Create the EmbedBuilder instance
                EmbedBuilder eb = new EmbedBuilder();

                eb.setTitle("Commands", null);


                eb.setColor(Color.red);
                eb.setColor(new Color(0xAA13CD));
                eb.setColor(new Color(0xAA13CD));

                eb.setDescription("bot made for fun and maybe something here will be of practical use idk");

                eb.addField("[help", "displays this message", false);

                eb.addField("[tiles", "displays assortment of tiles\nex: 40m5s6z3p2z", false);

                eb.addField("[hand", "enter 14 tiles, first 13 part of the hand, the last one is the one you drew, also checks if the hand is complete\nex: 19m19p19s12345671z", false);

                eb.addField("[sfx <sound>", "list of sounds: `ron` `tsumo` `riichi` `mangan` `haneman` `baiman` `sanbaiman` `yakuman`\nex: [sfx ron", false);

                eb.addField("[generate", "generates a random starting hand (autosorted)", false);

                eb.addField("[trainer", "generates a random starting hand (autosorted), and allows you to draw and discard", false);

                eb.addField("[tenpai", "enter a 13-tile hand and check to see if tenpai, and if so what tiles it is waiting on", false);

                eb.addField("[ronnya <@user>", "only works in vc and if u have perms :smiling_imp:", false);

                eb.addField("[tsumonya <@user>", "same thing as ronnya but tsumonya", false);

                eb.addField("[mahjonghandle", "wordle but for mahjong (note: all hands are menzentsumo because no yaku implemented yet, no chiitoitsu or thirteen orphans either (but you can guess them))", false);

                eb.addField("[defensesim", "defense simulator where you defend against one player in riichi", false);

                eb.addField("[roman", "displays roman funny moments", false);

                eb.addField("[waittrainer", "tests your skill at wait detection", false);

                eb.setFooter("tsumo nya");

                event.getTextChannel().sendMessage(" ").setEmbeds(eb.build()).complete();

            }

            // [tile command
            else if (messageSent.startsWith("[tiles")) {
                List<String> processedTiles = parseTiles(messageSent);
                if (processedTiles.get(processedTiles.size() - 1).equals("bad")) {
                    event.getTextChannel().sendMessage("tile(s) do(es) not exist").queue();
                    return;
                }
                StringBuilder outputMsg = new StringBuilder();
                for (int i = 0; i < processedTiles.size(); ++i) {
                    outputMsg.append("<:" + processedTiles.get(i) + ":" + tileids.get(processedTiles.get(i)) + ">");
                }
                event.getTextChannel().sendMessage(outputMsg).queue();
            }

            //[hand command
            else if (messageSent.startsWith("[hand")) {
                List<String> processedTiles = parseTiles(messageSent);
                if (processedTiles.get(processedTiles.size() - 1).equals("bad")) {
                    event.getTextChannel().sendMessage("tile(s) do(es) not exist").queue();
                } else if (processedTiles.size() != 14) {
                    event.getTextChannel().sendMessage("wrong number of tiles").queue();
                } else {
                    StringBuilder outputMsg = convertToHand(processedTiles);
                    event.getTextChannel().sendMessage(outputMsg).queue();
                    processedTiles.add("tmp");
                    processedTiles = sortHand(processedTiles);
                    processedTiles.remove(processedTiles.size() - 1);
                    if (checkWinningHandNormal((processedTiles)) || checkThirteenOrphans(processedTiles) || checkSevenPairs(processedTiles)) {
                        event.getTextChannel().sendMessage("this is a winning hand!").queue();
                    }
                    /*EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Generated Hand", null);
                    eb.addField("Name cannot be longer than 256 characters.", outputMsg.toString(), false);
                    eb.setFooter("tsumo nya");
                    event.getTextChannel().sendMessage(" ").setEmbeds(eb.build()).complete();*/
                }
            }

            // [sfx command
            else if (messageSent.startsWith("[sfx")) {
                if (messageSent.length() < 6) {
                    event.getTextChannel().sendMessage("enter a valid sound").queue();
                    return;
                }
                String sound = messageSent.substring(5);
                if (sound.equals("ron")) {
                    File file = new File("D:\\discordbot\\audio files\\ronnya.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("tsumo")) {
                    File file = new File("D:\\discordbot\\audio files\\tsumonya.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("riichi")) {
                    File file = new File("D:\\discordbot\\audio files\\riichinya.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("mangan")) {
                    File file = new File("D:\\discordbot\\audio files\\mangan.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("haneman")) {
                    File file = new File("D:\\discordbot\\audio files\\haneman.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("baiman")) {
                    File file = new File("D:\\discordbot\\audio files\\baiman.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("sanbaiman")) {
                    File file = new File("D:\\discordbot\\audio files\\sanbaiman.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("yakuman")) {
                    File file = new File("D:\\discordbot\\audio files\\yakuman.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else {
                    event.getTextChannel().sendMessage("enter a valid sound").queue();
                }
            }

            // [generate command
            else if (messageSent.startsWith("[generate")) {
                //event.getTextChannel().sendMessage("#tiles: " + wall.size()).queue();
                List<String> wall = WALL_TEMPLATE;
                Collections.shuffle(wall);
                StringBuilder hand = new StringBuilder();
                for (int i = 1; i <= 14; ++i) {
                    hand.append(wall.get(i - 1));
                }
                List<String> processedTiles = parseTiles(hand.toString());
                processedTiles = sortHand(processedTiles);
                StringBuilder outputMsg = convertToHand(processedTiles);
                event.getTextChannel().sendMessage(outputMsg).queue();
            }

            // [trainer command used for the first time
            else if (messageSent.startsWith("[trainer")) {
                List<String> tmpwall = new ArrayList<String>(WALL_TEMPLATE);
                Collections.shuffle(tmpwall);
                StringBuilder hand = new StringBuilder();
                for (int i = 1; i <= 14; ++i) {
                    hand.append(tmpwall.get(tmpwall.size() - 1));
                    tmpwall.remove(tmpwall.size() - 1);
                }
                List<String> tmphand = parseTiles(hand.toString());
                tmphand = sortHand(tmphand);
                event.getTextChannel().sendMessage("opening hand for <@" + author + ">:").queue();
                StringBuilder outputMsg = convertToHand(tmphand);
                event.getTextChannel().sendMessage(outputMsg).queue();
                List<String> tmphandchecker = new ArrayList<>(tmphand);
                tmphandchecker.add("tmp");
                tmphandchecker = sortHand(tmphandchecker);
                tmphandchecker.remove(tmphandchecker.size() - 1);
                WALLS.put(author, tmpwall);
                HANDS.put(author, tmphand);
                if (checkWinningHandNormal(tmphandchecker) || checkSevenPairs(tmphandchecker) || checkThirteenOrphans(tmphandchecker)) {
                    event.getTextChannel().sendMessage("tsumo nya! the hand is won (trainer quits automatically)").queue();
                    WALLS.remove(author);
                    HANDS.remove(author);
                } else
                    event.getTextChannel().sendMessage("discard (`[d <tile>`) or quit (`[quit`), " + (tmpwall.size() - 14) + " tiles remaining.").queue();
            } else if (messageSent.startsWith("[tenpai")) {
                if (messageSent.length() < 9) {
                    event.getTextChannel().sendMessage("invalid number of tiles").queue();
                    return;
                }
                messageSent = messageSent.substring(8);
                List<String> tiles = parseTiles(messageSent);
                if (tiles.size() != 13) {
                    event.getTextChannel().sendMessage("invalid number of tiles").queue();
                    return;
                }
                List<String> wait = checkTenpai(tiles);
                if (wait.isEmpty()) {
                    event.getTextChannel().sendMessage("not in tenpai nya").queue();
                } else {
                    wait.add("tmp");
                    wait = sortHand(wait);
                    wait.remove(wait.size() - 1);
                    event.getTextChannel().sendMessage("waiting on:").queue();
                    StringBuilder outputMsg = new StringBuilder();
                    for (int i = 0; i < wait.size(); ++i) {
                        outputMsg.append("<:" + wait.get(i) + ":" + tileids.get(wait.get(i)) + ">");
                    }
                    event.getTextChannel().sendMessage(outputMsg).queue();
                }
            }
            // used on user in vc, moves them to another vc (which the bot joins, and then plays ronnya sfx to them before returning them to call)
            else if (messageSent.startsWith("[ronnya") || messageSent.startsWith("[tsumonya")) {
                if (!sender.hasPermission(VOICE_MOVE_OTHERS)) {
                    event.getTextChannel().sendMessage("not enough perms nya").queue();
                    return;
                }
                boolean tsumo = messageSent.startsWith("[tsumonya");
                if (messageSent.length() < 9 && messageSent.startsWith("[ronnya")) {
                    event.getTextChannel().sendMessage("invalid user").queue();
                    return;
                } else if (messageSent.length() < 11 && messageSent.startsWith("[tsumonya")) {
                    event.getTextChannel().sendMessage("invalid user").queue();
                    return;
                }
                messageSent = messageSent.startsWith("[ronnya") ? messageSent.substring(8) : messageSent.substring(10);
                messageSent = messageSent.substring(2, messageSent.length() - 1);
                long id = 0;
                try {
                    id = parseLong(messageSent);
                } catch (Exception NumberFormatException) {
                    event.getTextChannel().sendMessage("invalid user").queue();
                    return;
                }
                Member member;
                try {
                    member = guild.getMemberById(id);
                } catch (Exception e) {
                    event.getTextChannel().sendMessage("invalid user").queue();
                    return;
                }

                if (messageSent.equals("993916134384476280")) {
                    event.getTextChannel().sendMessage("i am immune nya").queue();
                    return;
                }
                AudioChannel origin;
                try {
                    origin = member.getVoiceState().getChannel();
                } catch (Exception e) {
                    event.getTextChannel().sendMessage("user is not in vc").queue();
                    return;
                }
                VoiceChannel destination = guild.getVoiceChannelById(I_AM_DUMB_CHANNEL_ID);
                try {
                    guild.moveVoiceMember(member, destination).queue();
                } catch (Exception e) {
                    event.getTextChannel().sendMessage("user is not in vc").queue();
                    return;
                }
                AudioChannel connectedChannel = guild.getVoiceChannelById(I_AM_DUMB_CHANNEL_ID);
                AudioManager audioManager = event.getGuild().getAudioManager();

                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                audioManager.openAudioConnection(connectedChannel);
                if (tsumo) {
                    executorService.schedule(new Runnable() {
                        public void run() {
                            PlayerManager.getInstance()
                                    .loadAndPlay(channel, "D:\\discordbot\\audio files\\tsumonya.mp3");
                        }
                    }, 1000, TimeUnit.MILLISECONDS);
                } else {
                    executorService.schedule(new Runnable() {
                        public void run() {
                            PlayerManager.getInstance()
                                    .loadAndPlay(channel, "D:\\discordbot\\audio files\\ronnya.mp3");
                        }
                    }, 1000, TimeUnit.MILLISECONDS);
                }

                final GuildVoiceState memberVoiceState = member.getVoiceState();

                executorService.schedule(new Runnable() {
                    public void run() {
                        guild.moveVoiceMember(member, origin).queue();
                        event.getGuild().getAudioManager().closeAudioConnection();
                    }
                }, 2500, TimeUnit.MILLISECONDS);
            } else if (messageSent.startsWith("[mahjonghandle")) {
                List<String> generatedHand = generateWonHand();
                USERS_USING_MAHJONG_HANDLE.put(author, generatedHand);
                USER_GUESSES_REMAINING.put(author, 6);
                event.getTextChannel().sendMessage("enter your first guess using command `[guess` (sorted valid hand with last tile being winning tile)\nex. 13m345p567789s77z2m\nyou have 6 guesses").queue();
            }
            // simulator for defense setup
            // remember to consider red fives as normal fives when discarded
            else if (messageSent.startsWith("[defensesim")) {
                // generate riichi user's hand
                List<String> wonHand = generateWonHand();
                List<String> wall = new ArrayList<>(WALL_TEMPLATE_NO_RED_FIVES);
                Collections.shuffle(wall);
                wonHand.remove(wonHand.size() - 1);
                // now we have to remove all these tiles from the wall
                for (int i = 0; i < wonHand.size(); ++i) {
                    wall.remove(wonHand.get(i));
                }
                List<String> checkTenpaiHand = new ArrayList<>(wonHand);
                // calculate which tiles are tenpai
                List<String> tenpaiTiles = checkTenpai(checkTenpaiHand);
                // fill in opponent's hands
                List<String> playerAcrossHand = new ArrayList<>();
                for (int i = 0; i < 13; ++i) {
                    playerAcrossHand.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                }
                List<String> playerRightHand = new ArrayList<>();
                for (int i = 0; i < 13; ++i) {
                    playerRightHand.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                }
                // fill in your hand
                List<String> ownHand = new ArrayList<>();
                for (int i = 0; i < 13; ++i) {
                    ownHand.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                }
                // fill in discards for riichi'er
                List<String> furitenDiscards = new ArrayList<>();
                List<String> riichiDiscards = new ArrayList<>();
                while (riichiDiscards.size() < 8) {
                    String tile = wall.get(wall.size() - 1);
                    wall.remove(wall.size() - 1);
                    if (tenpaiTiles.contains(tile)) {
                        furitenDiscards.add(tile);
                    } else {
                        riichiDiscards.add(tile);
                    }
                }
                // add back any extra tiles that would have made them furiten
                for (String tile : furitenDiscards) {
                    wall.add(tile);
                }
                // shuffle the wall, to get rid of the order when putting the tiles back
                Collections.shuffle(wall);
                // discard tiles for opponents
                List<String> acrossDiscards = new ArrayList<>();
                List<String> rightDiscards = new ArrayList<>();
                for (int i = 0; i < 8; ++i) {
                    acrossDiscards.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                }
                for (int i = 0; i < 8; ++i) {
                    rightDiscards.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                }
                // discard tiles for yourself
                List<String> ownDiscards = new ArrayList<>();
                for (int i = 0; i < 8; ++i) {
                    ownDiscards.add(wall.get(wall.size() - 1));
                    wall.remove(wall.size() - 1);
                }
                // remove the dead wall
                for (int i = 0; i < 14; ++i) {
                    wall.remove(wall.get(wall.size() - 1));
                }
                // draw a tile
                ownHand.add(wall.get(wall.size() - 1));
                wall.remove(wall.size() - 1);
                ownHand = sortHand(ownHand);
                OWN_HAND.put(author, ownHand);
                RIICHI_HAND.put(author, wonHand);
                PLAYER_ACROSS_HAND.put(author, playerAcrossHand);
                PLAYER_RIGHT_HAND.put(author, playerRightHand);
                OWN_DISCARD_PILE.put(author, ownDiscards);
                RIICHI_DISCARD_PILE.put(author, riichiDiscards);
                PLAYER_ACROSS_DISCARD_PILE.put(author, acrossDiscards);
                PLAYER_RIGHT_DISCARD_PILE.put(author, rightDiscards);
                DEFENSE_WALL.put(author, wall);
                TENPAI_TILES.put(author, tenpaiTiles);
                event.getTextChannel().sendMessage("the player on your left declared riichi with: ").queue();
                List<String> discardTile = new ArrayList<>();
                discardTile.add(riichiDiscards.get(riichiDiscards.size() - 1));
                StringBuilder outputMsg = convertToTileSequence(discardTile);
                event.getTextChannel().sendMessage(outputMsg).queue();

                event.getTextChannel().sendMessage("your discard pile:").queue();
                outputMsg = convertToTileSequence(ownDiscards);
                event.getTextChannel().sendMessage(outputMsg).queue();

                event.getTextChannel().sendMessage("right player discard pile: ").queue();
                outputMsg = convertToTileSequence(rightDiscards);
                event.getTextChannel().sendMessage(outputMsg).queue();

                event.getTextChannel().sendMessage("across player discard pile: ").queue();
                outputMsg = convertToTileSequence(acrossDiscards);
                event.getTextChannel().sendMessage(outputMsg).queue();

                event.getTextChannel().sendMessage("left player's discard pile (declared riichi)").queue();
                outputMsg = convertToTileSequence(riichiDiscards);
                event.getTextChannel().sendMessage(outputMsg).queue();
                System.out.println(wonHand);
                event.getTextChannel().sendMessage("your hand:").queue();
                outputMsg = convertToHand(ownHand);
                event.getTextChannel().sendMessage(outputMsg).queue();
                event.getTextChannel().sendMessage("`[quit` to quit, `[d <tile>` to discard, `[viewdiscards` to view relevant discards.\nThere are " + wall.size() + " tiles left in the live wall.").queue();
            }
            else if (messageSent.startsWith("[roman")) {
                Random rd = new Random();
                File file = new File("D:\\discordbot\\pictures\\romanexposed" + (rd.nextInt(12) + 1) + ".png");
                event.getTextChannel().sendMessage(" ").addFile(file).queue();
                Config roman = new Config();
                //event.getTextChannel().sendMessage(roman.getRomanId()).queue();
            }
            else if (messageSent.startsWith("[waittrainer")) {
                List<String> tiles = generateTenpaiHandOneSuit();
                List<String> tmptiles = new ArrayList<>(tiles);
                List<String> waits = checkTenpai(tmptiles);
                waits.add("e");
                waits = sortHand(waits);
                waits.remove(waits.size() - 1);
                List<String> removals = new ArrayList<String>();
                // remove any tiles that are impossible to wait on
                for (String s : waits) {
                    if (Collections.frequency(tiles, s) == 4) {
                        removals.add(s);
                    }
                }
                for (String s : removals) {
                    waits.remove(s);
                }
                // now insert everything into hashmap
                WAIT_TRAINER_USERS.put(author, waits);
                StringBuilder hand = convertToTileSequence(tiles);
                event.getTextChannel().sendMessage("here is the hand nya").queue();
                event.getTextChannel().sendMessage(hand).queue();
                event.getTextChannel().sendMessage("enter `[wait <wait>` as a tile sequence to check").queue();
            }
        }
    }

    public static List<String> parseTiles(String messageSent) {
        List<String> processedTiles = new ArrayList<String>();
        int idx = messageSent.indexOf(" ");
        if (messageSent.length() < idx + 3) {
            processedTiles.add("bad");
            return processedTiles;
        }
        String tiles = messageSent.substring(idx + 1);
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < tiles.length(); ++i) {
            if (tiles.charAt(i) == 'm' || tiles.charAt(i) == 'p' || tiles.charAt(i) == 's' || tiles.charAt(i) == 'z') {
                if (tmp.length() == 0) {
                    processedTiles.add("bad");
                    return processedTiles;
                } else {
                    for (int j = 0; j < tmp.length(); ++j) {
                        if ((tmp.charAt(j) == '8' || tmp.charAt(j) == '9' || tmp.charAt(j) == '0') && tiles.charAt(i) == 'z') {
                            processedTiles.add("bad");
                            return processedTiles;
                        }
                        String tile = "";
                        tile += tmp.charAt(j);
                        tile += tiles.charAt(i);
                        processedTiles.add(tile);
                    }
                    tmp.setLength(0);
                }
            } else {
                if (!Character.isDigit(tiles.charAt(i))) {
                    processedTiles.add("bad");
                    return processedTiles;
                }
                tmp.append(tiles.charAt(i));
            }
        }
        if (tmp.length() != 0) {
            processedTiles.add("bad");
            return processedTiles;
        }
        return processedTiles;
    }

    public static StringBuilder convertToHand(List<String> processedTiles) {
        StringBuilder outputMsg = new StringBuilder();
        for (int i = 0; i < processedTiles.size() - 1; ++i) {
            outputMsg.append("<:" + processedTiles.get(i) + ":" + tileids.get(processedTiles.get(i)) + ">");
        }
        outputMsg.append("               ");
        outputMsg.append("<:" + processedTiles.get(processedTiles.size() - 1) + ":" + tileids.get(processedTiles.get(processedTiles.size() - 1)) + ">");
        return outputMsg;
    }

    // does not sort the last tile into the hand
    public static List<String> sortHand(List<String> processedTiles) {
        List<String> tmp = new ArrayList<String>();
        for (int i = 0; i < processedTiles.size() - 1; ++i) {
            String s = "";
            s += processedTiles.get(i).charAt(1);
            if (processedTiles.get(i).charAt(0) == '0') {
                s += "55";
            } else {
                s += processedTiles.get(i).charAt(0);
            }
            tmp.add(s);
        }
        Collections.sort(tmp);
        List<String> tmp2 = new ArrayList<String>();
        for (int i = 0; i < processedTiles.size() - 1; ++i) {
            String s = "";
            if (tmp.get(i).length() == 3) {
                s += '0';
                s += tmp.get(i).charAt(0);
            } else {
                s += tmp.get(i).charAt(1);
                s += tmp.get(i).charAt(0);
            }
            tmp2.add(s);
        }
        tmp2.add(processedTiles.get(processedTiles.size() - 1));
        return tmp2;
    }

    // ensure hand is sorted, this is for 4 sets and a pair
    // assumes no melding, which means no quads
    public static boolean checkWinningHandNormal(List<String> hand) {
        hand = convertRedFives(hand);
        // now the hand should be sorted
        // divide hand into suits by using a hashmap
        Map<Character,List<Integer>> suits = new HashMap<>();
        suits.put('m',  new ArrayList<Integer>());
        suits.put('p',  new ArrayList<Integer>());
        suits.put('s',  new ArrayList<Integer>());
        suits.put('z',  new ArrayList<Integer>());
        for (int i = 0; i < hand.size(); ++i) {
            suits.get(hand.get(i).charAt(1)).add(Character.getNumericValue(hand.get(i).charAt(0)));
        }
        // preliminary check -> at least one pair, additionally find all possible pairs
        boolean hasPair = false;
        List<String> pairs = new ArrayList<>();
        for (Character suit : suits.keySet()) {
            for (int i = 0; i < suits.get(suit).size(); ++i) {
                int freq = Collections.frequency(suits.get(suit), suits.get(suit).get(i));
                if (freq >= 2) {
                    // any triplet should not function as a pair, any quad is also discounted
                    if (freq > 2 && suit == 'z') continue;
                    hasPair = true;
                    // add pair in the format "[number][suit]"
                    String pair = "";
                    pair += suits.get(suit).get(i);
                    pair += suit;
                    if (!pairs.contains(pair)) pairs.add(pair);
                }
            }
        }
        if (!hasPair) return false;

        // preliminary check -> no isolated honour, no 4-of-a-kind (quads don't exist just yet)
        for (int i = 0; i < suits.get('z').size(); ++i) {
            int freq = Collections.frequency(suits.get('z'), suits.get('z').get(i));
            if (freq == 1) return false;
            else if (freq == 4) return false;
        }

        // check that there is at most one honour pair (if there is an honour pair, that will have to be the pair of the hand)
        int cnt = 0;
        String honourPair = "";
        for (int i = 0; i < pairs.size(); ++i) {
            if (pairs.get(i).charAt(1) == 'z') {
                if (cnt != 0) return false;
                ++cnt;
                honourPair = pairs.get(i);
            }
        }
        if (cnt != 0) {
            pairs.clear();
            pairs.add(honourPair);
        }
        // iterate through all possible pairs, process all suits
        // at this point the honours are valid
        for (int i = 0; i < pairs.size(); ++i) {
            // remove the pair from the respective suit
            String pair = pairs.get(i);
            List<Integer> manzu = new ArrayList<>();
            List<Integer> pinzu = new ArrayList<>();
            List<Integer> souzu = new ArrayList<>();
            for (int j = 0; j < suits.get('m').size(); ++j) {
                manzu.add(suits.get('m').get(j));
            }
            for (int j = 0; j < suits.get('p').size(); ++j) {
                pinzu.add(suits.get('p').get(j));
            }
            for (int j = 0; j < suits.get('s').size(); ++j) {
                souzu.add(suits.get('s').get(j));
            }
            Integer num = Character.getNumericValue(pair.charAt(0));
            if (pair.charAt(1) == 'm') {
                manzu.remove(num); manzu.remove(num);
            } else if (pair.charAt(1) == 'p') {
                pinzu.remove(num); pinzu.remove(num);
            } else if (pair.charAt(1) == 's') {
                souzu.remove(num); souzu.remove(num);
            }
            // all suits are good
            if (verifySuit(manzu) && verifySuit(pinzu) && verifySuit(souzu)) {
                return true;
            }
        }
        // otherwise return bad, nothing works
        return false;
    }

    public static boolean verifySuit(List<Integer> suit) {
        // test all possible combinations of triples
        List<Integer> triplets = new ArrayList<>();
        for (int i = 1; i <= 9; ++i) {
            if (Collections.frequency(suit, i) >= 3) {
                triplets.add(i);
            }
        }
        for (int i = 0; i < COMBINATION_INDICES.size(); ++i) {
            List<Integer> comb = COMBINATION_INDICES.get(i);
            List<Integer> tmpSuit = new ArrayList<>();
            for (int j = 0; j < suit.size(); ++j) {
                tmpSuit.add(suit.get(j));
            }
            for (int j = 0; j < comb.size(); ++j) {
                if (comb.get(j) >= triplets.size()) {
                    // at a point where if we haven't found yet it's too late
                    return false;
                }
                // remove 3 times for triplet
                tmpSuit.remove(tmpSuit.indexOf(triplets.get(comb.get(j))));
                tmpSuit.remove(tmpSuit.indexOf(triplets.get(comb.get(j))));
                tmpSuit.remove(tmpSuit.indexOf(triplets.get(comb.get(j))));
            }
            // check sequences now (only one has to satisfy for this to work)
            if (checkSequences(tmpSuit)) return true;
        }
        // shouldn't reach here but return false anyways
        return false;
    }

    // checks if we can make into sequences
    public static boolean checkSequences(List<Integer> tmpSuit) {
        // sequences are always groups of 3
        if (tmpSuit.size() % 3 != 0) return false;
        // iterate through
        while (!tmpSuit.isEmpty()) {
            Integer start = tmpSuit.get(0);
            if (!tmpSuit.contains(start + 1) || !tmpSuit.contains(start + 2)) return false;
            tmpSuit.remove(Integer.valueOf(start)); tmpSuit.remove(Integer.valueOf(start + 1)); tmpSuit.remove(Integer.valueOf(start + 2));
        }
        return true;
    }

    public static boolean checkThirteenOrphans(List<String> hand) {
        List<Integer> occurences = new ArrayList<>();
        occurences.add(Collections.frequency(hand, "1m"));
        occurences.add(Collections.frequency(hand, "9m"));
        occurences.add(Collections.frequency(hand, "1p"));
        occurences.add(Collections.frequency(hand, "9p"));
        occurences.add(Collections.frequency(hand, "1s"));
        occurences.add(Collections.frequency(hand, "9s"));
        occurences.add(Collections.frequency(hand, "1z"));
        occurences.add(Collections.frequency(hand, "2z"));
        occurences.add(Collections.frequency(hand, "3z"));
        occurences.add(Collections.frequency(hand, "4z"));
        occurences.add(Collections.frequency(hand, "5z"));
        occurences.add(Collections.frequency(hand, "6z"));
        occurences.add(Collections.frequency(hand, "7z"));
        // above must all be present
        if (occurences.contains(0)) {
            return false;
        }
        // extra tile must be one of the above
        if (occurences.contains(2)) {
            return true;
        }
        return false;
    }

    public static boolean checkSevenPairs(List<String> hand) {
        hand = convertRedFives(hand);
        Map<String,Integer> occurences = new HashMap<String,Integer>();
        for (int i = 0; i < hand.size(); ++i) {
            if (occurences.containsKey(hand.get(i))) {
                occurences.put(hand.get(i), occurences.get(hand.get(i)) + 1);
            } else occurences.put(hand.get(i), 1);
        }
        if (occurences.size() != 7) return false;
        for (String s : occurences.keySet()) {
            if (occurences.get(s) != 2) return false;
        }
        return true;
    }

    public static List<String> convertRedFives(List<String> hand) {
        // convert all red fives to 5p for our purposes
        for (int i = 0; i < hand.size(); ++i) {
            if (hand.get(i).charAt(0) == '0') {
                String tmp = "";
                tmp += '5';
                tmp += hand.get(i).charAt(1);
                hand.set(i, tmp);
            }
        }
        return hand;
    }

    // returns a list of tiles that the hand is waiting on (empty tenpai allowed)
    public static List<String> checkTenpai(List<String> hand) {
        List<String> tiles = new ArrayList<>();
        // go through every type of tile
        for (String s : tileids.keySet()) {
            hand.add(s);
            hand.add("tmp");
            hand = sortHand(hand);
            hand.remove(hand.size() - 1);
            if (checkWinningHandNormal(hand) || checkSevenPairs(hand) || checkThirteenOrphans(hand)) {
                if (s.charAt(0) == '0') {
                    String tmp = "";
                    tmp += "5";
                    tmp += s.charAt(1);
                    hand.remove(tmp);
                } else {
                    tiles.add(s);
                    hand.remove(s);
                }
            } else {
                if (s.charAt(0) == '0') {
                    String tmp = "";
                    tmp += "5";
                    tmp += s.charAt(1);
                    hand.remove(tmp);
                } else hand.remove(s);
            }
        }
        return tiles;
    }

    public static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> generateWonHand() {
        List<String> tiles = new ArrayList<>(WALL_TEMPLATE_NO_RED_FIVES);
        List<String> wonHand = new ArrayList<>();
        Random rand = new Random();
        int upperbound = 100;
        // attempt to generate a completed set
        for (int i = 1; i <= 4; ++i) {
            int int_random = rand.nextInt(upperbound);
            // try to make a triple
            if (int_random >= 85) {
                String tile = tiles.get(rand.nextInt(tiles.size()));
                while (Collections.frequency(tiles, tile) < 3) {
                    tile = tiles.get(rand.nextInt(tiles.size()));
                }
                // triple has been formed, now we delete
                tiles.remove(tile); tiles.remove(tile); tiles.remove(tile);
                wonHand.add(tile); wonHand.add(tile); wonHand.add(tile);
            }
            // now here we do sequences (note that we cannot use honour tiles, or 8 and 9)
            else {
                String tile = tiles.get(rand.nextInt(tiles.size()));
                // first part of while statement check checks for invalid sequence starters
                // second part (if passed), checks if it is possible given number of tiles remaining
                while ((tile.charAt(0) == '9' || tile.charAt(0) == '8' || tile.charAt(1) == 'z' || tile.charAt(0) == '0') || (!checkPossibleSequence(tile, tiles))) {
                    tile = tiles.get(rand.nextInt(tiles.size()));
                }
                tiles.remove(tile); wonHand.add(tile);
                String tmp = "";
                tmp += (char)((Character.getNumericValue(tile.charAt(0)) + 1) + '0');
                tmp += tile.charAt(1);
                tiles.remove(tmp); wonHand.add(tmp);
                tmp = "";
                tmp += (char)((Character.getNumericValue(tile.charAt(0)) + 2) + '0');
                tmp += tile.charAt(1);
                tiles.remove(tmp); wonHand.add(tmp);
            }
        }
        // now add a pair
        String tile = tiles.get(rand.nextInt(tiles.size()));
        while (Collections.frequency(tiles, tile) < 2) {
            tile = tiles.get(rand.nextInt(tiles.size()));
        }
        tiles.remove(tile); tiles.remove(tile);
        wonHand.add(tile); wonHand.add(tile);
        // now we should randomize the hand and then sort the hand
        Collections.shuffle(wonHand);
        wonHand = sortHand(wonHand);
        return wonHand;
    }

    public static List<String> generateTenpaiHandOneSuit() {
        Random rand1 = new Random();
        int typ = rand1.nextInt(3);
        List<String> tiles;
        if (typ == 0) tiles = new ArrayList<>(ONLY_MANZU);
        else if (typ == 1) tiles = new ArrayList<>(ONLY_PINZU);
        else tiles = new ArrayList<>(ONLY_SOUZU);
        List<String> wonHand = new ArrayList<>();
        Random rand = new Random();
        int upperbound = 100;
        // attempt to generate a completed set
        for (int i = 1; i <= 4; ++i) {
            int int_random = rand.nextInt(upperbound);
            // try to make a triple
            if (int_random >= 85) {
                String tile = tiles.get(rand.nextInt(tiles.size()));
                while (Collections.frequency(tiles, tile) < 3) {
                    tile = tiles.get(rand.nextInt(tiles.size()));
                }
                // triple has been formed, now we delete
                tiles.remove(tile); tiles.remove(tile); tiles.remove(tile);
                wonHand.add(tile); wonHand.add(tile); wonHand.add(tile);
            }
            // now here we do sequences (note that we cannot use honour tiles, or 8 and 9)
            else {
                String tile = tiles.get(rand.nextInt(tiles.size()));
                // first part of while statement check checks for invalid sequence starters
                // second part (if passed), checks if it is possible given number of tiles remaining
                while ((tile.charAt(0) == '9' || tile.charAt(0) == '8' || tile.charAt(1) == 'z' || tile.charAt(0) == '0') || (!checkPossibleSequence(tile, tiles))) {
                    tile = tiles.get(rand.nextInt(tiles.size()));
                }
                tiles.remove(tile); wonHand.add(tile);
                String tmp = "";
                tmp += (char)((Character.getNumericValue(tile.charAt(0)) + 1) + '0');
                tmp += tile.charAt(1);
                tiles.remove(tmp); wonHand.add(tmp);
                tmp = "";
                tmp += (char)((Character.getNumericValue(tile.charAt(0)) + 2) + '0');
                tmp += tile.charAt(1);
                tiles.remove(tmp); wonHand.add(tmp);
            }
        }
        // now add a pair
        String tile = tiles.get(rand.nextInt(tiles.size()));
        while (Collections.frequency(tiles, tile) < 2) {
            tile = tiles.get(rand.nextInt(tiles.size()));
        }
        tiles.remove(tile); tiles.remove(tile);
        wonHand.add(tile); wonHand.add(tile);
        // now we should randomize the hand, remove a tile, and then sort the hand
        Collections.shuffle(wonHand);
        wonHand.remove(wonHand.size() - 1);
        wonHand.add("b");
        wonHand = sortHand(wonHand);
        wonHand.remove(wonHand.size() - 1);
        return wonHand;
    }

    public static boolean checkPossibleSequence(String tile, List<String> tiles) {
        if (!tiles.contains(tile)) return false;
        String tmp = "";
        tmp += (char)((Character.getNumericValue(tile.charAt(0)) + 1) + '0');
        tmp += tile.charAt(1);
        if (!tiles.contains(tmp)) return false;
        tmp = "";
        tmp += (char)((Character.getNumericValue(tile.charAt(0)) + 2) + '0');
        tmp += tile.charAt(1);
        return (tiles.contains(tmp));
    }

    public static void clearDefenseSession(String author) {
        OWN_HAND.remove(author);
        RIICHI_HAND.remove(author);
        PLAYER_ACROSS_HAND.remove(author);
        PLAYER_RIGHT_HAND.remove(author);
        OWN_DISCARD_PILE.remove(author);
        RIICHI_DISCARD_PILE.remove(author);
        PLAYER_ACROSS_DISCARD_PILE.remove(author);
        PLAYER_RIGHT_DISCARD_PILE.remove(author);
        DEFENSE_WALL.remove(author);
        TENPAI_TILES.remove(author);
    }
    public static StringBuilder convertToTileSequence(List<String> processedTiles) {
        StringBuilder outputMsg = new StringBuilder();
        for (int i = 0; i < processedTiles.size(); ++i) {
            outputMsg.append("<:" + processedTiles.get(i) + ":" + tileids.get(processedTiles.get(i)) + ">");
        }
        return outputMsg;
    }

    // algorithm will discard any genbutsu if exists, otherwise will discard random tile in hand
    public String getDefenseDiscard(String author, List<String> hand) {
        List<String> genbutsu = new ArrayList<>();
        String discard = "";
        // starting from the eighth tile in riichi player's discards
        for (int i = 7; i < RIICHI_DISCARD_PILE.get(author).size(); ++i) {
            genbutsu.add(RIICHI_DISCARD_PILE.get(author).get(i));
        }
        // starting from the ninth tile in all other player's discards (including own)
        for (int i = 8; i < OWN_DISCARD_PILE.get(author).size(); ++i) {
            genbutsu.add(OWN_DISCARD_PILE.get(author).get(i));
        }
        for (int i = 8; i < PLAYER_RIGHT_DISCARD_PILE.get(author).size(); ++i) {
            genbutsu.add(PLAYER_RIGHT_DISCARD_PILE.get(author).get(i));
        }
        for (int i = 8; i < PLAYER_ACROSS_DISCARD_PILE.get(author).size(); ++i) {
            genbutsu.add(PLAYER_ACROSS_DISCARD_PILE.get(author).get(i));
        }
        // check hand to see if there is genbutsu
        for (int i = 0; i < hand.size(); ++i) {
            if (genbutsu.contains(hand.get(i))) {
                return hand.get(i);
            }
        }
        // if no genbutsu
        List<String> tmp = new ArrayList<>(hand);
        Collections.shuffle(tmp);
        return tmp.get(0);
    }
    // implement draw tile function? where we keep track of all the tiles and draw them sequentially
    // make 4 starting hands + dora doable?
    // download all pictures, map their string to downloads and then we can implement randomized hand dealing

    // next step is to get emotes into image thing, and then do hand dealing by sorting and such

    // make disp tile able to display more than 1 tile, and in whatever order
    // user input is standard, but they end each sequence of the same type of tile with either 'm', 'p', 's', 'z'
    // then we can know

    // also add hand paramater to separate last tile
}