package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.util.Strings;

public class VerificationButton extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getButton().getId().equals("Verify")){
            return;
        }

        if (event.getMember().getRoles().contains(Kep3.getInstance().getRole(Strings.MEMBER_ROLE, Kep3.SearchType.ID))){
            event.reply("You've already confirmed, silly!").setEphemeral(true).queue();
            return;
        }

        Kep3.getInstance().getJDA().getGuilds().get(0).addRoleToMember(Kep3.getInstance().getJDA().getUserById(event.getMember().getId()),
                Kep3.getInstance().getRole(Strings.DJ_ROLE, Kep3.SearchType.ID)).queue();
    }
}
