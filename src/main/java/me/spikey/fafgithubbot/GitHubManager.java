package me.spikey.fafgithubbot;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GitHubManager {
    private GitHub gitHub;
    public GitHubManager(String token) {
        try {
            gitHub = new GitHubBuilder().withOAuthToken(token).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private GHRepository getRepo() {
        try {
            return gitHub.getRepository("FAForever/fa");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GHPullRequest getPullRequest(int id) {
        try {
            return Objects.requireNonNull(getRepo()).getPullRequest(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<GHPullRequest> getOpenPRs() {
        try {
            return Objects.requireNonNull(getRepo()).getPullRequests(GHIssueState.OPEN);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private GHLabel getLabel(String string) {
        try {
            return Objects.requireNonNull(getRepo()).getLabel(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<GHPullRequest> filterPRs(List<GHPullRequest> in, String label) {
        List<GHPullRequest> out = new ArrayList<>();

        GHLabel ghLabel = getLabel(label);
        for (GHPullRequest pr : in) {
            if (!pr.getLabels().contains(ghLabel)) continue;
            if (!pr.getBase().getRef().equals("deploy/fafdevelop") && !pr.getBase().getRef().equals("deploy/fafbeta")) continue;
            out.add(pr);
        }
        return out;
    }



    public List<GHPullRequest> getPullRequestsForDiscord() {
        return filterPRs(Objects.requireNonNull(getOpenPRs()), "DiscordLink");
    }

    public void refreshCache() {
        gitHub.refreshCache();
    }
}
