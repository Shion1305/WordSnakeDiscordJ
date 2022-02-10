package com.shion1305.discord.wordsnake;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.InteractionCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.EmbedData;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.util.Color;

import java.util.ArrayList;
import java.util.List;

import static com.shion1305.discord.wordsnake.WordCheckResult.*;

public class WordSnakeDiscord {
    List<Integer> wordHistory;
    GatewayDiscordClient client;
    RestChannel channel;
    String channelId;
    int mode = 0;
    public static final int STATE_STANDBY = 0;
    public static final int STATE_RUNNING = 1;
    char head;

    public WordSnakeDiscord(String token, String channel) {
        channelId = channel;
        wordHistory = new ArrayList<>();
        client = DiscordClient.create(token).gateway().setEnabledIntents(IntentSet.all()).login().block();
        //configure target channel
        this.channel = client.getChannelById(Snowflake.of(channel)).block().getRestChannel();
        //configure target
        client = DiscordClient.builder(token).build().login().block();
        this.channel.createMessage(MessageCreateSpec.builder().content("しりとりボットがやってきた!!!").addComponent(ActionRow.of(Button.success("!START", "開始する"))).build().asRequest()).block();
        client.on(MessageCreateEvent.class)
                .filter(messageCreateEvent -> !messageCreateEvent.getMember().get().isBot())
                .filter(messageCreateEvent -> messageCreateEvent.getMessage().getChannelId().asString().equals(channelId))
                .subscribe(messageCreateEvent -> {
                    String content = messageCreateEvent.getMessage().getData().content();
                    messageCreateEvent.getMessage().delete().block();
                    switch (mode) {
                        case STATE_RUNNING:
                            WordCheckResult re = WordSnakeChecker.checkWord(content, head);
                            switch (re.code) {
                                case WORD_NOT_FOUND:
                                    sendNOTFOUND(content, messageCreateEvent.getMember().get());
                                    break;
                                case WORD_INVALID:
                                    sendINVALID(content, messageCreateEvent.getMember().get());
                                    break;
                                case WORD_CONVENTION_REQUIRED:
                                    sendCONVENTION_REQUIRED(content, messageCreateEvent.getMember().get());
                                    break;
                                case WORD_NOT_SATISFIED:
                                    sendNOTSATISFIED(content, messageCreateEvent.getMember().get());
                                    break;
                                case WORD_OK:
                                    if (wordHistory.contains(re.data.id)) {

                                        //GAME OVER
                                    } else {
                                        re.banList.forEach(wordData -> wordHistory.add(wordData.id));
                                        nextHeadDeterminer(re.data.kana);
                                        sendOKNext(messageCreateEvent.getMember().get());
                                    }
                                    break;
                                case WORD_OUT:
                                    //GAME OVER
                                    break;
                            }
                            break;
                        case STATE_STANDBY:
                            if (content.equals("!START")) {
                                sendStart();
                                mode = STATE_RUNNING;
                                head = 'リ';
                            }
                    }
                });
        client.on(ButtonInteractionEvent.class)
                .filter(buttonInteractionEvent -> buttonInteractionEvent.getInteraction().getChannelId().asString().equals(channelId))
                .subscribe(buttonInteractionEvent -> {
                    if (buttonInteractionEvent.getCustomId().equals("!START")) {
                        sendStart();
                        mode = STATE_RUNNING;
                        head = 'リ';
                    }
                });
    }

    private void nextHeadDeterminer(String kana) {
        head = kana.charAt(kana.length() - 1);
        switch (head) {
            case 'ッ':
                head = 'ツ';
                break;
            case 'ャ':
                head = 'ヤ';
                break;
            case 'ュ':
                head = 'ユ';
                break;
            case 'ョ':
                head = 'ヨ';
                break;
            case 'ー':
                nextHeadDeterminer(kana.substring(0, kana.length() - 1));
                break;
            case 'ヮ':
                head = 'ワ';
                break;
            case 'ァ':
                head = 'ア';
                break;
            case 'ィ':
                head = 'イ';
                break;
            case 'ゥ':
                head = 'ウ';
                break;
            case 'ェ':
                head = 'エ';
                break;
            case 'ォ':
                head = 'オ';
                break;
        }
    }

    private void sendStart() {
        channel.createMessage("開始します。しりとりの「り」から始めてください。").block();
    }


    private void sendNOTFOUND(String keyword, User sender) {
        channel.createMessage(EmbedCreateSpec.builder()
                .author(EmbedCreateFields.Author.of(sender.getUsername(), null, sender.getAvatarUrl()))
                .title("「" + keyword + "」は辞書で見つかりませんでした。" + "「" + head + "」から始まる他の言葉を入力してください。")
                .color(Color.RED)
                .build().asRequest()).block();
    }

    private void sendINVALID(String keyword, User sender) {
        channel.createMessage(EmbedCreateSpec.builder()
                .author(EmbedCreateFields.Author.of(sender.getUsername(), null, sender.getAvatarUrl()))
                .title("「" + keyword + "」は無効な回答です。")
                .color(Color.RED)
                .build().asRequest()).block();
    }

    private void sendCONVENTION_REQUIRED(String keyword, User sender) {
        channel.createMessage(EmbedCreateSpec.builder()
                .author(EmbedCreateFields.Author.of(sender.getUsername(), null, sender.getAvatarUrl()))
                .title("辞書で「" + keyword + "」は複数見つかりました。漢字・カタカナ等に正しく変換してください。")
                .color(Color.RED)
                .build().asRequest()).block();
    }

    private void sendNOTSATISFIED(String keyword, User sender) {
        channel.createMessage(EmbedCreateSpec.builder()
                .author(EmbedCreateFields.Author.of(sender.getUsername(), null, sender.getAvatarUrl()))
                .title("「" + keyword + "」は「" + head + "」" + "から始まりません。")
                .color(Color.RED)
                .build().asRequest()).block();
    }

    private void sendOKNext(User sender) {
        StringBuilder title = new StringBuilder();
        for (int i = 1; i < 4 && wordHistory.size() - i > -1; i++) {
            title.append(" > ");
            title.append(WordSnakeChecker.getWordFromID(wordHistory.get(wordHistory.size() - i)).word);
        }
        channel.createMessage(EmbedCreateSpec.builder()
                .author(EmbedCreateFields.Author.of(sender.getUsername(), null, sender.getAvatarUrl()))
                .color(Color.DEEP_SEA)
                .title(title.toString())
                .description("正解です。次は「" + head + "」から始まる言葉を入力してください。")
                .build()
                .asRequest()).block();
    }
}
