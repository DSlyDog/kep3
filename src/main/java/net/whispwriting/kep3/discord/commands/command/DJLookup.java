package net.whispwriting.kep3.discord.commands.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.commands.Command;
import net.whispwriting.kep3.discord.util.Profile;

import java.util.List;

public class DJLookup implements Command {
    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        if (sender.isBot()){
            return;
        }

        if (args.size() < 2){
            event.reply("You must provide both fields for a lookup.").setEphemeral(true).queue();
            return;
        }

        MessageCreateBuilder builder = new MessageCreateBuilder();
        Member member = args.get(1).getAsMember();
        Profile profile = Kep3.getInstance().getProfile(member);

        if (profile == null){
            event.reply("I could not find any information on that DJ").setEphemeral(true).queue();
            return;
        }

        switch (args.get(0).getAsString().toLowerCase()){
            case "name":
                sendResponse(event, builder, "I found this DJ name: " + profile.getName());
                break;
            case "vrc name":
                sendResponse(event, builder, "I found this VRC name: " + profile.getVRCName());
                break;
            case "logo":
                sendResponse(event, builder, "I found this logo: " + profile.getLogo());
                break;
            case "genres":
                sendResponse(event, builder, "Here's what I found: " + profile.getGenres());
                break;
            case "socials":
                sendResponse(event, builder, "Here's what I found: " + profile.getSocials());
                break;
            case "demo sets":
                sendResponse(event, builder, "Here's what I found: " + profile.getDemoSets());
                break;
            case "full profile":
                builder.addEmbeds(profile.getProfileEmbed());
                sendResponse(event, builder, "I found this profile: ");
                break;
            default:
                sendResponse(event, builder, "I don't recognize that type.");
                break;
        }
    }

    public void sendResponse(SlashCommandInteractionEvent event, MessageCreateBuilder builder, String message){
        builder.addContent(message);
        event.reply(builder.build()).setEphemeral(true).queue();
    }
}
