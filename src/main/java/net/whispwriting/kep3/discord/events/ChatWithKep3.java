package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.util.Strings;

import java.net.http.HttpClient;

public class ChatWithKep3 extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        if (!event.getChannel().getId().equals(Strings.AI_CHAT_CHANNEL))
            return;

        event.getChannel().sendTyping().queue();
        Kep3.getInstance().sendAIMessage(event.getChannel().asTextChannel(), event.getMessage().getContentRaw());
    }
}
