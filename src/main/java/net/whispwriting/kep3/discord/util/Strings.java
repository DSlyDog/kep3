package net.whispwriting.kep3.discord.util;

import net.whispwriting.kep3.Main;

import java.io.IOException;

public class Strings {

    public static String ADMIN_ROLE = "775440914578669570";
    public static String DJ_ROLE = "1194892753532895363";
    public static String DJ_APPLICATION_ROLE = "1269748432570941530";
    public static String DJ_APPLICATION_CATEGORY = "1234681055664144515";
    public static String DJ_SUBMISSION_CHANNEL = "1194827653279141938";
    public static String DJ_APP_ARCHIVE_CATEGORY = "1269770865520939118";

    public static void loadData(){
        JsonFile file = new JsonFile("config", "./");

        if (!JsonFile.exists("config", "")) {
            file.set("admin_role", ADMIN_ROLE);
            file.set("dj_role", DJ_ROLE);
            file.set("dj_application_role", DJ_APPLICATION_ROLE);
            file.set("dj_application_category", DJ_APPLICATION_CATEGORY);
            file.set("dj_submission_channel", DJ_SUBMISSION_CHANNEL);
            file.set("dj_app_archive_category", DJ_APPLICATION_CATEGORY);

            try {
                file.save();
            } catch (IOException e) {
                System.err.println("There was an error building the config.yml");
                e.printStackTrace();
            }
        }else {
            ADMIN_ROLE = file.getString("admin_role");
            DJ_ROLE = file.getString("dj_role");
            DJ_APPLICATION_ROLE = file.getString("dj_application_role");
            DJ_APPLICATION_CATEGORY = file.getString("dj_application_category");
            DJ_SUBMISSION_CHANNEL = file.getString("dj_submission_channel");
            DJ_APP_ARCHIVE_CATEGORY = file.getString("dj_app_archive_category");
        }

        Main.getLogger().info("config path: " + file.getFilePath());
    }
}
