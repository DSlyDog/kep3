package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.util.Strings;

import java.util.EnumSet;
import java.util.Random;

public class UpdateProfileButton extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().equals("update_profile") && !event.isAcknowledged()){
            if (!event.getMember().getRoles().contains(Kep3.getInstance().getRole(Strings.DJ_ROLE, Kep3.SearchType.ID))){
                event.reply("Sorry, only DJs have profiles they can update.").setEphemeral(true).queue();
                return;
            }

            if (Kep3.getInstance().getProfile(event.getMember()) == null){
                event.reply("You do not have a profile. Please create a profile instead.").setEphemeral(true).queue();
                return;
            }

            Random random = new Random(System.currentTimeMillis());
            int ticketID = random.nextInt(100000, 1000000);
            TextChannel channel = Kep3.getInstance().getJDA().getGuilds().get(0).getCategoryById(Strings.DJ_APPLICATION_CATEGORY).createTextChannel("update-" + ticketID)
                    .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(Kep3.getInstance().getJDA().getRoleById(Strings.ADMIN_ROLE), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(event.getMember().getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).complete();
            ProfileUpdateConversation profileUpdate = new ProfileUpdateConversation(event.getMember(), channel, ticketID);
            Kep3.getInstance().getJDA().addEventListener(profileUpdate);
            profileUpdate.start();
            event.reply("Profile editor opened in <#" + channel.getId() + ">").setEphemeral(true).queue();
        }
    }
}
