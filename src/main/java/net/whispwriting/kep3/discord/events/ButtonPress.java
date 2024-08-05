package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.util.JsonFile;
import net.whispwriting.kep3.discord.util.Strings;

import java.io.IOException;

public class ButtonPress extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        String id = event.getButton().getId();
        if (id.contains("app_accept")){
            String discordID = id.substring(id.indexOf("+")+1);
            System.out.println(discordID);
            Kep3.ApplicationManager.acceptApplication(discordID);
            Kep3.getInstance().getJDA().getUserById(discordID).openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage("Congratulations! Your DJ application for Bass Station has been accepted!")).queue();
            Kep3.getInstance().getJDA().getGuilds().get(0).addRoleToMember(Kep3.getInstance().getJDA().getUserById(discordID),
                    Kep3.getInstance().getRole(Strings.DJ_ROLE, Kep3.SearchType.ID)).queue();
            Kep3.getInstance().getJDA().getGuilds().get(0).removeRoleFromMember(Kep3.getInstance().getJDA().getUserById(discordID),
                    Kep3.getInstance().getRole(Strings.DJ_APPLICATION_ROLE, Kep3.SearchType.ID)).queue();
            event.reply("This application has been accepted").setEphemeral(true).queue();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.getMessage().delete().queue();
        }

        if (id.contains("app_deny")){
            String discordID = id.substring(id.indexOf("+")+1);
            Kep3.ApplicationManager.denyApplication(discordID);
            Kep3.getInstance().getJDA().getUserById(discordID).openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage("Our sincerest apologies, unfortunately your DJ " +
                            "application for Bass Station has not been accepted at this time.")).queue();
            Kep3.getInstance().getJDA().getGuilds().get(0).removeRoleFromMember(Kep3.getInstance().getJDA().getUserById(discordID),
                    Kep3.getInstance().getRole(Strings.DJ_APPLICATION_ROLE, Kep3.SearchType.ID)).queue();
            JsonFile file = new JsonFile("applications", "./");
            file.set(discordID, null);
            try {
                file.save();
            } catch (IOException e) {
                System.err.println("Failed to delete application from applications.yml");
                e.printStackTrace();
            }
            event.reply("Application denied").setEphemeral(true).queue();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.getMessage().delete().queue();
        }
    }
}
