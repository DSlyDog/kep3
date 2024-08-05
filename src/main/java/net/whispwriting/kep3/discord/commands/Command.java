package net.whispwriting.kep3.discord.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public interface Command {

    void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel);
}
