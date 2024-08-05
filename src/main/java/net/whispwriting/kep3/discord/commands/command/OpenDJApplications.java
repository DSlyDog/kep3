package net.whispwriting.kep3.discord.commands.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.commands.Command;
import net.whispwriting.kep3.discord.util.Strings;

import java.util.List;

public class OpenDJApplications implements Command {
    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        if (sender.isBot()){
            return;
        }

        Member user = Kep3.getInstance().getJDA().getGuilds().get(0).getMemberById(sender.getIdLong());
        if (!user.getRoles().contains(Kep3.getInstance().getRole(Strings.ADMIN_ROLE, Kep3.SearchType.ID))){
            event.reply("You are not permitted to open DJ applications").setEphemeral(true).queue();
            return;
        }

        if (Kep3.getInstance().getApplicationMessageID().equals("")) {
            Button apply = Button.of(ButtonStyle.SUCCESS, "apply", "Apply");
            Button profile = Button.of(ButtonStyle.SUCCESS, "create_profile", "Create Profile");
            Button update = Button.of(ButtonStyle.SUCCESS, "update_profile", "Update Profile");
            MessageCreateBuilder builder = new MessageCreateBuilder();
            builder.addContent("Greetings! \n\nClick the \"Apply\" button below to submit a DJ application. If you " +
                    "are already a DJ and don't have a profile, click the \"Create Profile\" button. If you are a DJ looking " +
                    "to update your profile, click \"Update Profile\"");
            builder.addActionRow(apply, profile, update);
            Message message = Kep3.getInstance().sendMessageWithReturn(builder.build(), channel, 1000);
            Kep3.getInstance().setApplicationMessageID(message.getId());
            event.reply("Applications successfully opened!").setEphemeral(true).queue();
        }

        Kep3.getInstance().openDJApplications();
        event.reply("Applications successfully opened").setEphemeral(true).queue();
    }
}
