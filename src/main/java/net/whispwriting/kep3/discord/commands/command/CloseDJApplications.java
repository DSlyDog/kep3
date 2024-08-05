package net.whispwriting.kep3.discord.commands.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.commands.Command;
import net.whispwriting.kep3.discord.util.Strings;

import java.util.List;

public class CloseDJApplications implements Command {
    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        if (sender.isBot()){
            return;
        }

        Member user = Kep3.getInstance().getJDA().getGuilds().get(0).getMemberById(sender.getIdLong());
        if (!user.getRoles().contains(Kep3.getInstance().getRole(Strings.ADMIN_ROLE, Kep3.SearchType.ID))){
            event.reply("You are not permitted to close DJ applications").setEphemeral(true).queue();
            return;
        }

        Kep3.getInstance().openDJApplications();
        event.reply("Applications successfully closed").setEphemeral(true).queue();
    }
}
