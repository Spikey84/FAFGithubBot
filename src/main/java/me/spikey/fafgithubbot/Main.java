package me.spikey.fafgithubbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.kohsuke.github.GHPullRequest;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class Main {
    private final GitHubManager gitHubManager;
    private final DiscordManager discordManager;
    private HashMap configMap;


    public Main(String configPath) {
        loadConfig(configPath);

        this.discordManager = new DiscordManager((String) configMap.get("discordToken"), (String) configMap.get("guildID"), (String) configMap.get("forumChannelID"));
        this.gitHubManager = new GitHubManager((String) configMap.get("githubToken"));

        new LoopThread(this, (int) configMap.get("delayTime")).start();
    }

    public void makeUnmadePrs() {
        List<GHPullRequest> prsToLink = gitHubManager.getPullRequestsForDiscord();
        List<Integer> alreadyPosted = discordManager.getCurrentForumPosts();

        for (GHPullRequest pullRequest : prsToLink) {
            if (alreadyPosted.contains(pullRequest.getNumber())) continue;
            createForumPostBasedOnPR(pullRequest);
        }
    }

    private void createForumPostBasedOnPR(GHPullRequest pullRequest) {

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(pullRequest.getTitle() + " #" + pullRequest.getNumber())
                .setColor(Color.BLUE)
                .setDescription(pullRequest.getBody())
                .setUrl(pullRequest.getHtmlUrl().toString());
        try {
            embedBuilder.addField("Date", "Created: <t:%s>".formatted(pullRequest.getCreatedAt().getTime()/1000), true);
        } catch (IOException e) {
            embedBuilder.setFooter("Unknown");
        }

        discordManager.createForumPost(pullRequest.getTitle() + " #" + pullRequest.getNumber(), new MessageCreateBuilder().addEmbeds(embedBuilder.build()));
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter config path.");
        } else {
            Main main = new Main(args[0]);
        }
    }

    public GitHubManager getGitHubManager() {
        return gitHubManager;
    }

    public String getGuildID() {
        return (String) configMap.get("guildID");
    }

    public String getForumID() {
        return (String) configMap.get("forumChannelID");
    }

    private void loadConfig(String path) {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(path);
            configMap  = yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
