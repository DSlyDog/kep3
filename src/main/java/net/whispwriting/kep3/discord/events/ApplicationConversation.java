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
import net.whispwriting.kep3.discord.util.Strings;

import java.util.*;

public class ApplicationConversation extends ListenerAdapter {

    private Member member;
    private TextChannel channel;
    private int ticketID;
    private int step = 0;
    private int delay = 1000;

    private Profile profile;
    private Map<Integer, String> questions = new HashMap<>();
    private Map<Integer, String> responses = new HashMap<>();
    private String vrcName;
    private String name;
    private String logo;
    private List<String> genres = new ArrayList<>();
    private List<String> demos = new ArrayList<>();
    private List<String> socials = new ArrayList<>();
    private boolean isApplicationContext = false;

    List<Permission> allow = new ArrayList<>();
    List<Permission> deny  = new ArrayList<>();

    public ApplicationConversation(Member member, TextChannel channel, int ticketID){
        this.member = member;
        this.channel = channel;
        this.ticketID = ticketID;

        questions.put(0, "What is your DJ name?");
        questions.put(1, "What is your VRChat name?");
        questions.put(2, "What genres do you play? Please add each in its own message. When you are done, press \"Done\"");
        questions.put(3, "What are your socials? Please add each in its own message. When you are done, press \"Done\"");
        questions.put(4, "What's your logo? Please attach it to your message. If you don't have one, simply say so.");
        questions.put(5, "Please provide a link to a demo set. It should be no longer than 60 minutes");
        questions.put(6, "This is your profile. If everything looks good, I'll send it off");

        responses.put(0, "Okay, your DJ name is: %%. Is this correct?");
        responses.put(1, "Alright, your VRChat name is: %%. Is this correct?");
        responses.put(2, "For your genres, I've got: %%. Is this correct?");
        responses.put(3, "For your socials, I've got: %%. Is this correct?");
        responses.put(5, "Okay, your demo link is: %%. Is this correct?");

        allow.add(Permission.VIEW_CHANNEL);
        deny.add(Permission.MESSAGE_SEND);
    }

    public void start(){
        Kep3.getInstance().sendMessage("Hello, " + member.getAsMention() + "! \n\nThank you for your interest in " +
                "DJing at our club. Please answer the following questions and I'll get your application off to our head DJ asap.\n\n-\n\n", channel, delay);
        Kep3.getInstance().sendMessage("First, what is your DJ name?", channel, delay);
    }

    public void startProfile(){
        Kep3.getInstance().sendMessage("Hello, " + member.getAsMention() + "! \n\nLet's get your profile built.\n\n-\n\n", channel, delay);
        Kep3.getInstance().sendMessage("First, what is your DJ name?", channel, delay);
        this.isApplicationContext = true;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (!event.getMember().getId().equals(member.getId()) || !event.getMessage().getChannelId().equals(channel.getId())) {
            return;
        }

        switch (step){
            case 0:
                this.name = event.getMessage().getContentRaw();
                sendResponse(responses.get(step), this.name);
                break;
            case 1:
                this.vrcName = event.getMessage().getContentRaw();
                sendResponse(responses.get(step), this.vrcName);
                break;
            case 2:
                genres.add(event.getMessage().getContentRaw());
                break;
            case 3:
                socials.add(event.getMessage().getContentRaw());
                break;
            case 4:
                if (event.getMessage().getAttachments().size() == 0) {
                    this.logo = "";
                    sendResponse("You do not have a logo, correct?", this.logo);
                }else {
                    this.logo = event.getMessage().getAttachments().get(0).getUrl();
                    sendResponse("Is the image you provided correct?", this.logo);
                }
                break; case 5: demos.add(event.getMessage().getContentRaw());
                sendResponse(responses.get(step), demos);
                break;
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (event.getMember().getId().equals(member.getId())){
            MessageCreateBuilder builder = new MessageCreateBuilder();

            if (!event.getButton().getId().contains(this.ticketID + "")){
                return;
            }

            event.getMessage().editMessageComponents().setComponents().queue();

            event.deferEdit().queue();

            if (event.getButton().getId().contains("yes")){
                switch (step){
                    case 0:
                        step++;
                        Kep3.getInstance().sendMessage("Wonderful! " + questions.get(step), channel, delay);
                        break;
                    case 1:
                        step++;
                        sendResponseDoneButton("Great! " + questions.get(step));
                        break;
                    case 2:
                        step++;
                        sendResponseDoneButton("Awesome! " + questions.get(step));
                        break;
                    case 3:
                        step++;
                        builder.addContent("Fantastic! " + questions.get(step));
                        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
                        break;
                    case 4:
                        step++;
                        builder.addContent("Excellent! " + questions.get(step));
                        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
                        break;
                    case 5:
                        step++;

                        Kep3.getInstance().sendMessage("Perfect!", channel, delay);

                        Button good = Button.of(ButtonStyle.SUCCESS, "good" + this.ticketID, "Looks good, send it!");
                        Button bad = Button.of(ButtonStyle.DANGER, "bad" + this.ticketID, "That's not right, let me start over");
                        Button cancel = Button.of(ButtonStyle.PRIMARY, "cancel" + this.ticketID, "Nevermind, cancel my application");
                        if (!isApplicationContext)
                            cancel = Button.of(ButtonStyle.PRIMARY, "cancel" + this.ticketID, "Nevermind, cancel my submission.");
                        profile = Profile.buildFromApplication(member.getId(), vrcName, name, logo, genres, socials, demos);
                        builder.addContent(questions.get(step));
                        builder.addActionRow(good, bad, cancel);
                        builder.addEmbeds(profile.getProfileEmbed());
                        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
                        break;
                }
            }else if (event.getButton().getId().contains("no")){
                switch (step) {
                    case 2:
                        genres.clear();
                        sendResponseDoneButton("Okay, let's try that again. " + questions.get(step));
                        break;
                    case 3:
                        socials.clear();
                        sendResponseDoneButton("Okay, let's try that again. " + questions.get(step));
                        break;
                    default:
                        Kep3.getInstance().sendMessage("Okay, let's try that again. " + questions.get(step), channel, delay);
                }
            }else if (event.getButton().getId().contains("done")){
                switch (step){
                    case 2:
                        sendResponse(responses.get(step), genres);
                        break;
                    case 3:
                        sendResponse(responses.get(step), socials);
                        break;
                }
            }else {
                if (isApplicationContext){
                    profileFinalization(event);
                }else{
                    applicationFinalization(event);
                }
            }
        }
    }

    private void applicationFinalization(ButtonInteractionEvent event){
        if (event.getButton().getId().equals("good" + this.ticketID)){
            Kep3.ApplicationManager.addApplication(profile, member.getId(), channel);
            Kep3.ApplicationManager.sendApplicationMessage(member.getUser(), profile);
            profile.saveAsApplication(channel);
            Kep3.getInstance().sendMessage("Okay! I've sent your application to our staff for review. I'll let you know the results asap!", channel, delay);
            Kep3.getInstance().getJDA().removeEventListener(this);
            channel.getPermissionContainer().getManager().putMemberPermissionOverride(Long.parseLong(event.getMember().getId()), allow, deny).queue();
            Kep3.getInstance().getJDA().getGuilds().get(0).addRoleToMember(Kep3.getInstance().getJDA().getUserById(member.getId()),
                    Kep3.getInstance().getRole(Strings.DJ_APPLICATION_ROLE, Kep3.SearchType.ID)).queue();
        }else if (event.getButton().getId().equals("bad" + this.ticketID)){
            step = 0;
            genres.clear();
            socials.clear();
            demos.clear();
            Kep3.getInstance().sendMessage("My apologies, let's start over. " + questions.get(step), channel, delay);
        }else if (event.getButton().getId().equals("cancel" + this.ticketID)){
            Kep3.getInstance().sendMessage("I'm sorry things didn't quite work out. If you'd like to apply in the future, please open a new application. " +
                    "Thank you, and have a great day", channel, 1000);
            Kep3.getInstance().getJDA().removeEventListener(this);
            channel.getPermissionContainer().getManager().putMemberPermissionOverride(Long.parseLong(event.getMember().getId()), allow, deny).queue();
        }
    }

    private void profileFinalization(ButtonInteractionEvent event){
        if (event.getButton().getId().equals("good" + this.ticketID)){
            Kep3.getInstance().addProfile(member, profile);
            profile.newSave();
            Kep3.getInstance().sendMessage("Okay! Your profile is in our database! Thank you.", channel, delay);
            Kep3.getInstance().getJDA().removeEventListener(this);
            channel.getPermissionContainer().getManager().putMemberPermissionOverride(Long.parseLong(event.getMember().getId()), allow, deny).queue();
        }else if (event.getButton().getId().equals("bad" + this.ticketID)){
            step = 0;
            genres.clear();
            socials.clear();
            demos.clear();
            Kep3.getInstance().sendMessage("My apologies, let's start over. " + questions.get(step), channel, delay);
        }else if (event.getButton().getId().equals("cancel" + this.ticketID)){
            Kep3.getInstance().sendMessage("I'm sorry things didn't quite work out. Thank you, and have a great day", channel, delay);
            Kep3.getInstance().getJDA().removeEventListener(this);
            channel.getPermissionContainer().getManager().putMemberPermissionOverride(Long.parseLong(event.getMember().getId()), allow, deny).queue();
        }
    }

    private String listToString(List<String> lst) throws StringIndexOutOfBoundsException{
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

        try {
            MessageCreateBuilder builder = new MessageCreateBuilder();
            builder.addContent(message.replace("%%", listToString(data)));
            builder.addActionRow(yes, no);
            Kep3.getInstance().sendMessage(builder.build(), channel, delay);
        }catch(StringIndexOutOfBoundsException e){
            Kep3.getInstance().sendMessage("You must provide at least one entry before pressing \"Done.\"", channel, delay);
        }
    }

    private void sendResponseDoneButton(String message){
        Button done = Button.of(ButtonStyle.PRIMARY, "done" + this.ticketID, "Done");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message);
        builder.addActionRow(done);
        Kep3.getInstance().sendMessage(builder.build(), channel, delay);
    }
}
