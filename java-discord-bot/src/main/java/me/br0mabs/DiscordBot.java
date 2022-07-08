package me.br0mabs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class DiscordBot extends ListenerAdapter {

    public static Map<String,String> tileids = new HashMap<String,String>();
    public static List<String> WALL_TEMPLATE = new ArrayList<String>();
    public static Map<String,List<String>> WALLS = new HashMap<String,List<String>>();
    public static Map<String,List<String>> HANDS = new HashMap<String,List<String>>();

    public static List<List<Integer>> COMBINATION_INDICES = new ArrayList<>();

    public static void main(String[] args) throws LoginException {

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
        tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.clear();

        // 2 triples
        tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.clear();

        tmpIndices.add(0); tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.clear();

        // 3 triples
        tmpIndices.add(2);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.clear();

        tmpIndices.add(2); tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.remove(tmpIndices.size() - 1);

        tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.clear();

        // 4 triples
        tmpIndices.add(3);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.remove(tmpIndices.size() - 1);

        tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.remove(tmpIndices.size() - 1);

        tmpIndices.add(2);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.remove(tmpIndices.size() - 1);

        tmpIndices.add(1);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices.add(0);
        COMBINATION_INDICES.add(tmpIndices);

        tmpIndices.remove(tmpIndices.indexOf(2));
        COMBINATION_INDICES.add(tmpIndices);
        tmpIndices.clear();



        char[] suits = {'m', 'p', 's', 'z'};
        for (int i = 0; i < 4; ++i) {
            for (int j = 1; j <= 9; ++j) {
                if (i == 3 && j == 8) break;
                for (int k = 1; k <= ((j == 5 && i != 3) ? 3 : 4); ++k) {
                    String tmp = "";
                    tmp += (char) (j + '0');
                    tmp += suits[i];
                    WALL_TEMPLATE.add(tmp);
                }
            }
        }
        WALL_TEMPLATE.add("0m");
        WALL_TEMPLATE.add("0p");
        WALL_TEMPLATE.add("0s");

        Config config = new Config();
        JDA bot = JDABuilder.createDefault(config.getToken())
                .setActivity(Activity.playing("mahgong sole"))
                .addEventListeners(new DiscordBot())
                .build();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String messageSent = event.getMessage().getContentRaw();

            String author = event.getAuthor().getId();

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
                String drawnTile = wall.get(wall.size() - 1);
                wall.remove(wall.size() - 1);
                hand.add(drawnTile);
                hand = sortHand(hand);
                StringBuilder outputMsg = convertToHand(hand);
                event.getTextChannel().sendMessage("current hand for " + "<@" + author + ">:").queue();
                event.getTextChannel().sendMessage(outputMsg).queue();
                event.getTextChannel().sendMessage("discard (`[d <tile>` or quit (`[quit`), " + (wall.size() - 14) + " tiles remaining.").queue();
                WALLS.put(author, wall);
                HANDS.put(author, hand);
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

                eb.addField("[hand", "enter 14 tiles, first 13 part of the hand, the last one is the one you drew\nex: 19m19p19s12345671z", false);

                eb.addField("[sfx <sound>", "list of sounds: `ron` `tsumo`\nex: [sfx ron", false);

                eb.addField("[generate", "generates a random starting hand (autosorted)", false);

                eb.addField("[trainer", "generates a random starting hand (autosorted), and allows you to draw and discard", false);

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
                    if (checkWinningHandNormal((processedTiles))) {
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
                }
                String sound = messageSent.substring(5);
                if (sound.equals("ron")) {
                    File file = new File("D:\\discordbot\\audio files\\ronnya.mp3");
                    event.getTextChannel().sendMessage(" ").addFile(file).queue();
                } else if (sound.equals("tsumo")) {
                    File file = new File("D:\\discordbot\\audio files\\tsumonya.mp3");
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
                WALLS.put(author,tmpwall);
                HANDS.put(author,tmphand);
                event.getTextChannel().sendMessage("opening hand for <@" + author + ">:").queue();
                StringBuilder outputMsg = convertToHand(tmphand);
                event.getTextChannel().sendMessage(outputMsg).queue();
                event.getTextChannel().sendMessage("discard (`[d <tile>`) or quit (`[quit`), " + (tmpwall.size() - 14) + " tiles remaining.").queue();
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
        // convert all red fives to 5p for our purposes
        for (int i = 0; i < hand.size(); ++i) {
            if (hand.get(i).charAt(0) == '0') {
                String tmp = "";
                tmp += '5';
                tmp += hand.get(i).charAt(1);
                hand.set(i, tmp);
            }
        }
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

        // preliminary check -> no isolated terminals, no 4-of-a-kind (quads don't exist just yet)
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


    // implement draw tile function? where we keep track of all the tiles and draw them sequentially
    // make 4 starting hands + dora doable?
    // download all pictures, map their string to downloads and then we can implement randomized hand dealing

    // next step is to get emotes into image thing, and then do hand dealing by sorting and such

    // make disp tile able to display more than 1 tile, and in whatever order
    // user input is standard, but they end each sequence of the same type of tile with either 'm', 'p', 's', 'z'
    // then we can know

    // also add hand paramater to separate last tile
}