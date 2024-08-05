package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.kep3.discord.Kep3;

public class MessageEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.isFromType(ChannelType.TEXT) && !event.getAuthor().isBot()){
            if (Kep3.getInstance().getAnnouncerChannels().containsKey(event.getChannel().asTextChannel())){
                Kep3.getInstance().getAnnouncerChannels().get(event.getChannel().asTextChannel())
                        .sendMessage(event.getMessage().getContentRaw()).queue();
                event.getMessage().reply("Your announcement has been sent.").queue();
            }
        }
    }
}
