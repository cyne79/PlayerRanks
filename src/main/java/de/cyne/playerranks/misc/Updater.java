package de.cyne.playerranks.misc;

import de.cyne.playerranks.PlayerRanks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private long resourceId;
    private String latestVersion;
    private String currentVersion;
    private UpdateResult updateResult;

    public Updater(long resourceId) {
        this.resourceId = resourceId;
        this.currentVersion = PlayerRanks.getInstance().getDescription().getVersion();
    }

    public enum UpdateResult {
        UPDATE_AVAILABLE, NO_UPDATE, CONNECTION_ERROR
    }

    public void checkLatestVersion() {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL("https://www.spigotmc.org/api/general.php")
                    .openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.getOutputStream().write(
                    ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + this.resourceId)
                            .getBytes("UTF-8"));
            this.latestVersion = new BufferedReader(new InputStreamReader(httpConnection.getInputStream())).readLine();
        } catch (IOException e) {
            this.setUpdateResult(UpdateResult.CONNECTION_ERROR);
        }
    }

    public void compareVersions() {
        long currentVersionCompact = Long.parseLong(currentVersion.replace(".", ""));
        long latestVersionCompact = Long.parseLong(latestVersion.replace(".", ""));

        if (currentVersionCompact == latestVersionCompact) {
            this.setUpdateResult(UpdateResult.NO_UPDATE);
            return;
        }
        this.setUpdateResult(UpdateResult.UPDATE_AVAILABLE);
    }

    public void run() {
        PlayerRanks.getInstance().getLogger().info("Searching for an update on 'spigotmc.org'..");

        this.checkLatestVersion();
        this.compareVersions();

        switch (this.updateResult) {
            case UPDATE_AVAILABLE:
                PlayerRanks.getInstance().getLogger().info("There was a new version found. It is recommended to update. (Visit spigotmc.org)");
                PlayerRanks.updateAvailable = true;
                break;

            case NO_UPDATE:
                PlayerRanks.getInstance().getLogger().info("The plugin is up to date.");
                PlayerRanks.updateAvailable = false;
                break;

            case CONNECTION_ERROR:
                PlayerRanks.getInstance().getLogger().warning("Could not connect to spigotmc.org. Retrying soon.");
                PlayerRanks.updateAvailable = false;
                break;

            default:
                PlayerRanks.getInstance().getLogger().warning("Could not connect to spigotmc.org. Retrying soon.");
                PlayerRanks.updateAvailable = false;
                break;
        }
    }

    private void setUpdateResult(UpdateResult updateResult) {
        this.updateResult = updateResult;
    }

}