package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.util.Profile;

import java.util.List;

public class ProfileListUpdate extends ListenerAdapter {

    private Member member;
    private TextChannel channel;
    private int ticketID;
    private String profileSection;
    private Profile profile;

    Button add, remove,done;

    private boolean isAdding = false;

    private int delay = 1000;

    public ProfileListUpdate(Member member, TextChannel channel, int ticketID, Profile profile, String profileSection){
        this.member = member;
        this.channel = channel;
        this.ticketID = ticketID;
        this.profileSection = profileSection;
        this.profile = profile;

        add = Button.of(ButtonStyle.SUCCESS, "add" + this.ticketID, "Add");
        remove = Button.of(ButtonStyle.DANGER, "remove" + this.ticketID, "Remove");
        done = Button.of(ButtonStyle.PRIMARY, "done" + this.ticketID, "Done");
    }

    public void start(){
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent("Okay, are we adding or removing an item?");
        builder.addActionRow(add, remove);
        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getMember().getId().equals(member.getId())) {
            if (event.getButton().getId().equals("add" + this.ticketID)) {
                MessageCreateBuilder builder = new MessageCreateBuilder();
                builder.addContent("Alrighty, please state each new item in its own message. Press \"Done\" when you are done.");
                builder.addActionRow(done);
                Kep3.getInstance().sendMessage(builder.build(), channel, delay);
                isAdding = true;
            }
            if (event.getButton().getId().equals("remove" + this.ticketID)) {
                MessageCreateBuilder builder = new MessageCreateBuilder();
                builder.addContent("Okay, please state the number associated with each item you would like to remove in its own message." +
                        "Press \"Done\" when you are done.");
                builder.addContent(listBuilder());
                builder.addActionRow(done);
                Kep3.getInstance().sendMessage(builder.build(), channel, delay);
            }if (event.getButton().getId().equals("done" + this.ticketID)){
                if (profileSection.equals("genres")){
                    sendResponse("For your genres, I've got: %%. Is this correct?", profile.getGenres());
                }else if (profileSection.equals("socials")){
                    sendResponse("For your socials, I've got: %%. Is this correct?", profile.getSocials());
                }else if (profileSection.equals("demos")){
                    sendResponse("Okay, your demo link is: %%. Is this correct?", profile.getDemoSets());
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getMember().getId().equals(member.getId())){
            try {
                switch (profileSection) {
                    case "genres":
                        if (isAdding)
                            profile.addGenre(event.getMessage().getContentRaw());
                        else
                            profile.removeGenre(Integer.parseInt(event.getMessage().getContentRaw()) - 1);
                        break;
                    case "socials":
                        if (isAdding)
                            profile.addSocial(event.getMessage().getContentRaw());
                        else
                            profile.removeGenre(Integer.parseInt(event.getMessage().getContentRaw()) - 1);
                        break;
                    case "demos":
                        if (isAdding)
                            profile.addDemoSet(event.getMessage().getContentRaw());
                        else
                            profile.removeDemoSet(Integer.parseInt(event.getMessage().getContentRaw()) - 1);
                        break;
                }
            }catch (NumberFormatException e) {
                Kep3.getInstance().sendMessage("Please only enter whole numbers.", channel, delay);
            } catch (IndexOutOfBoundsException e) {
                Kep3.getInstance().sendMessage("Sorry, that number is not an option.", channel, delay);
            }
        }
    }

    private String listBuilder(){
        return switch (profileSection) {
            case "genres" -> listBuilder(profile.getGenresList());
            case "socials" -> listBuilder(profile.getSocialsList());
            case "demos" -> listBuilder(profile.getDemoSetsList());
            default -> null;
        };
    }

    private String listBuilder(List<String> items){
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<items.size(); i++){
            builder.append(i+1).append(" - ").append(items.get(i));
        }
        return builder.toString();
    }

    private void sendResponse(String message, String data){
        Button yes = Button.of(ButtonStyle.SUCCESS, "yes" + this.ticketID, "Yes");
        Button no = Button.of(ButtonStyle.DANGER, "no" + this.ticketID, "No");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message.replace("%%", data));
        builder.addActionRow(yes, no);
        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
        Kep3.getInstance().getJDA().removeEventListener(this);
    }
}
