package net.whispwriting.kep3.discord.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {

    private Map<String, Command> commandMap = new HashMap<>();

    public void registerCommand(String label, Command command){
        commandMap.put(label, command);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        String label = event.getName();
        if (commandMap.containsKey(label)) {
            List<OptionMapping> args = event.getInteraction().getOptions();
            User sender = event.getUser();
            TextChannel channel = event.getChannel().asTextChannel();

            commandMap.get(label).onCommand(event, sender, label, args, channel);
        }
    }
}
