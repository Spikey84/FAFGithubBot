package me.spikey.fafgithubbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.ArrayList;
import java.util.List;

public class DiscordManager {
    private final JDA jda;
    private String guildID;
    private String channelID;

    public DiscordManager(String token, String guild, String channel) {
        this.guildID = guild;
        this.channelID = channel;

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.listening("to FAForever/fa"));
        jda = builder.build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getCurrentForumPosts();
    }

    private ForumChannel getMainForum() {
        return getGuild().getForumChannelById(channelID);
    }

    private Guild getGuild() {
        return jda.getGuildById(guildID);
    }

    public void createForumPost(String title, MessageCreateBuilder messageCreateBuilder) {
        getMainForum().createForumPost(title, messageCreateBuilder.build()).queue();
    }

    public List<Integer> getCurrentForumPosts() {
        List<Integer> currentPRs = new ArrayList<>();
        for (ThreadChannel threadChannel : getMainForum().getThreadChannels()) {
            String idString = threadChannel.getName().replaceAll("([A-Za-z0-9]+( [A-Za-z0-9]+)+)", "").replace(" #", "");
            int id;
            try {
                id = Integer.parseInt(idString);
            } catch (final NumberFormatException e) {
                id = 0;
            }
            if (!currentPRs.contains(id)) currentPRs.add(id);
        }
        return currentPRs;
    }
}
