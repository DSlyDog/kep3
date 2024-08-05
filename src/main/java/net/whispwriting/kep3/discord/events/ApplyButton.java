package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.util.Strings;

import java.util.EnumSet;
import java.util.Random;

public class ApplyButton extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (!event.getButton().getId().equals("apply")) {
            return;
        }

        if (event.getMember().getRoles().contains(Kep3.getInstance().getRole(Strings.DJ_ROLE, Kep3.SearchType.ID))){
            event.reply("You're already a DJ, silly!").setEphemeral(true).queue();
            return;
        }

        Random random = new Random(System.currentTimeMillis());
        int ticketID = random.nextInt(100000, 1000000);
        TextChannel channel = Kep3.getInstance().getJDA().getGuilds().get(0).getCategoryById(Strings.DJ_APPLICATION_CATEGORY).createTextChannel("application-" + ticketID)
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(Kep3.getInstance().getJDA().getRoleById(Strings.ADMIN_ROLE), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getMember().getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).complete();
        ApplicationConversation conversation = new ApplicationConversation(event.getMember(), channel, ticketID);
        Kep3.getInstance().getJDA().addEventListener(conversation);
        conversation.start();
        event.reply("Application opened in <#" + channel.getId() + ">").setEphemeral(true).queue();
    }
}
