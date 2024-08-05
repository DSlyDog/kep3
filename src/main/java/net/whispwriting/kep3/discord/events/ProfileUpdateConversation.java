package net.whispwriting.kep3.discord.events;

import net.dv8tion.jda.api.Permission;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileUpdateConversation extends ListenerAdapter {

    private Member member;
    private TextChannel channel;
    private int ticketID;
    private int step = 0;
    private int delay = 1000;

    private Profile profile;
    private Map<Integer, String> responses = new HashMap<>();
    private Map<Integer, String> questions = new HashMap<>();

    private List<Permission> allow = new ArrayList<>();
    private List<Permission> deny  = new ArrayList<>();

    private String djNameBtn, vrcNameBtn, genresBtn, socialsBtn, logoBtn, demosBtn, yesBtn, noBtn, doneBtn, restartBtn;
    private Button djName, vrcName, genres, socials, logo, demo;

    public ProfileUpdateConversation(Member member, TextChannel channel, int ticketID){
        this.member = member;
        this.channel = channel;
        this.ticketID = ticketID;

        allow.add(Permission.VIEW_CHANNEL);
        deny.add(Permission.MESSAGE_SEND);

        profile = Kep3.getInstance().getProfile(member);

        questions.put(0, "Okay, we're updating your DJ name. What is your new one?");
        questions.put(1, "Okay, we're updating your VRChat name. What is your new one?");
        questions.put(2, "Alright, we're updating your genres.");
        questions.put(3, "Alrighty, we're updating your socials");
        questions.put(4, "Alrighty, we're updating your logo. Please send it in a " +
                "message. If you don't have one, just say so.");
        questions.put(5, "Okay, we're updating your demo sets.");

        responses.put(0, "Okay, your new DJ name is: %%. Is this correct?");
        responses.put(1, "Alright, your new VRChat name is: %%. Is this correct?");
        responses.put(2, "For your genres, I've got: %%. Is this correct?");
        responses.put(3, "For your socials, I've got: %%. Is this correct?");
        responses.put(4, "Okay, is this the correct logo?");
        responses.put(5, "Okay, your demo link is: %%. Is this correct?");

        djName = Button.of(ButtonStyle.SUCCESS, "dj_name" + this.ticketID, "DJ Name");
        vrcName = Button.of(ButtonStyle.SUCCESS, "vrc_name" + this.ticketID, "VRChat Name");
        genres = Button.of(ButtonStyle.SUCCESS, "genres" + this.ticketID, "Genres");
        socials = Button.of(ButtonStyle.SUCCESS, "socials" + this.ticketID, "Socials");
        logo = Button.of(ButtonStyle.SUCCESS, "logo" + this.ticketID, "Logo");
        demo = Button.of(ButtonStyle.SUCCESS, "demo" + this.ticketID, "Demo");

        djNameBtn = "dj_name" + this.ticketID;
        vrcNameBtn = "vrc_name" + this.ticketID;
        genresBtn = "genres" + this.ticketID;
        socialsBtn = "socials" + this.ticketID;
        logoBtn = "logo" + this.ticketID;
        demosBtn = "demo" + this.ticketID;
        yesBtn = "yes" + this.ticketID;
        noBtn = "no" + this.ticketID;
        doneBtn = "done" + this.ticketID;
        restartBtn = "restart" + this.ticketID;
    }

    public void start(){
        step = 0;

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent("Hello, " + member.getAsMention() + "! \n\nLet's get your profile update. Click the button below " +
                "for the item you would like to update.");
        builder.addActionRow(djName, vrcName, genres);
        builder.addActionRow(socials, logo, demo);

        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (event.getMember().getId().equals(member.getId())){
            if (event.getButton().getId().equals(djNameBtn)){
                Kep3.getInstance().sendMessage(questions.get(0), channel, delay);
            }else if (event.getButton().getId().equals(vrcNameBtn)){
                Kep3.getInstance().sendMessage(questions.get(1), channel, delay);
                step = 1;
            }else if (event.getButton().getId().equals(genresBtn)){
                Kep3.getInstance().sendMessage(questions.get(2), channel, delay);
                step = 2;
                genresContext();
            }else if (event.getButton().getId().equals(socialsBtn)){
                Kep3.getInstance().sendMessage(questions.get(3), channel, delay);
                step = 3;
                socialsContext();
            }else if (event.getButton().getId().equals(logoBtn)){
                Kep3.getInstance().sendMessage(questions.get(4), channel, delay);
                step = 4;
            }else if (event.getButton().getId().equals(demosBtn)){
                Kep3.getInstance().sendMessage(questions.get(5), channel, delay);
                step = 5;
                demosContext();
            }else if (event.getButton().getId().equals(yesBtn)){
                sendResponseDoneButton("Here is your new profile!");
            }else if (event.getButton().getId().equals(noBtn)){
                Kep3.getInstance().sendMessage("Let's try that again then.", channel, delay);
                Kep3.getInstance().sendMessage(questions.get(step), channel, delay);
            }else if (event.getButton().getId().equals(restartBtn)){
                start();
            }
        }
        event.deferEdit().queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getMember().getId().equals(member.getId())){
            switch (step){
                case 0:
                    djNameContext(event);
                    break;
                case 1:
                    vrcNameContext(event);
                    break;
                case 4:
                    logoContext(event);
                    break;
            }
        }
    }

    private void djNameContext(MessageReceivedEvent event){
        profile.updateName(event.getMessage().getContentRaw());
        sendResponse(responses.get(step), profile.getName());
    }

    private void vrcNameContext(MessageReceivedEvent event){
        profile.updateName(event.getMessage().getContentRaw());
        sendResponse(responses.get(step), profile.getName());
    }

    private void genresContext(){
        ProfileListUpdate updater = new ProfileListUpdate(member, channel, ticketID, profile, "genres");
        updater.start();
        Kep3.getInstance().getJDA().addEventListener(updater);
    }

    private void socialsContext(){
        ProfileListUpdate updater = new ProfileListUpdate(member, channel, ticketID, profile, "socials");
        updater.start();
        Kep3.getInstance().getJDA().addEventListener(updater);
    }

    private void logoContext(MessageReceivedEvent event){
        if (event.getMessage().getAttachments().isEmpty()){
            profile.updateLogo("");
            sendResponse("You do not have a logo, correct?", "");
        }else {
            profile.updateLogo(event.getMessage().getAttachments().get(0).getUrl());
            sendResponse("Is this the correct logo?", "");
        }
    }

    private void demosContext(){
        ProfileListUpdate updater = new ProfileListUpdate(member, channel, ticketID, profile, "demos");
        updater.start();
        Kep3.getInstance().getJDA().addEventListener(updater);
    }

    private String listToString(List<String> lst){
        StringBuilder builder = new StringBuilder();
        for (String s : lst){
            builder.append(s).append(", ");
        }

        return builder.substring(0, builder.toString().length()-2);
    }

    private void sendResponse(String message, String data){
        Button yes = Button.of(ButtonStyle.SUCCESS, "yes" + this.ticketID, "Yes");
        Button no = Button.of(ButtonStyle.DANGER, "no" + this.ticketID, "No");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message.replace("%%", data));
        builder.addActionRow(yes, no);
        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
    }

    private void sendResponse(String message, List<String> data){
        Button yes = Button.of(ButtonStyle.SUCCESS, "yes" + this.ticketID, "Yes");
        Button no = Button.of(ButtonStyle.DANGER, "no" + this.ticketID, "No");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message.replace("%%", listToString(data)));
        builder.addActionRow(yes, no);
        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
    }

    private void sendResponseDoneButton(String message){
        Button done = Button.of(ButtonStyle.PRIMARY, "done" + this.ticketID, "Done");
        Button restart = Button.of(ButtonStyle.PRIMARY, "restart" + this.ticketID, "Update Another Item");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message);
        builder.addEmbeds(profile.getProfileEmbed());
        builder.addActionRow(done, restart);
        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
        channel.getPermissionContainer().getManager().putMemberPermissionOverride(Long.parseLong(member.getId()), allow, deny).queue();
    }

}
