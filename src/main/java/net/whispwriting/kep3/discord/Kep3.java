package net.whispwriting.kep3.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.whispwriting.kep3.Main;
import net.whispwriting.kep3.discord.commands.Command;
import net.whispwriting.kep3.discord.commands.CommandHandler;
import net.whispwriting.kep3.discord.events.*;
import net.whispwriting.kep3.discord.util.Application;
import net.whispwriting.kep3.discord.util.JsonFile;
import net.whispwriting.kep3.discord.util.Profile;
import net.whispwriting.kep3.discord.util.Strings;
import org.json.simple.JSONObject;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kep3 {
    private JDA jda;
    private JsonFile announcers;
    private final CommandHandler handler = new CommandHandler();
    private Map<TextChannel, TextChannel> announcerChannels = new HashMap<>();
    private static Map<String, Application> applications = new HashMap<>();
    private Map<String, Profile> profiles = new HashMap<>();
    private String avatar;

    private String applicationMessageID = "";
    private boolean isAcceptingApplications;

    public enum SearchType{
        NAME,
        ID
    }
    private static Kep3 instance;
    private Kep3(){}
        public void init(String token) throws LoginException {
        Thread postLoad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    loadAnnouncers();
                    loadDJProfiles();
                    loadDJApplications();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Main.getLogger().info("Assets loaded");
            }
        });
        jda = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES, /*GatewayIntent.GUILD_BANS,*/
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
        jda.getPresence().setActivity(Activity.of(Activity.ActivityType.PLAYING, "VRChat"));
        announcers = new JsonFile("announcers", System.getProperty("user.dir"));
        postLoad.start();
        jda.addEventListener(handler);
        jda.addEventListener(new MessageEvent());
        jda.addEventListener(new ButtonPress());
        jda.addEventListener((new ApplyButton()));
        jda.addEventListener(new CreateProfileButton());
        jda.addEventListener(new UpdateProfileButton());
        avatar = jda.getSelfUser().getAvatarUrl();
    }

    private void loadAnnouncers(){
        if (!announcers.isEmpty()) {
            Guild guild = jda.getGuilds().get(0);
            for (Channel channel : guild.getChannels()) {
                String id = announcers.getString(channel.getId());
                if (id != null) {
                    Channel output = getChannel(id, SearchType.ID);
                    if (output.getType() == ChannelType.TEXT) {
                        announcerChannels.put((TextChannel) channel, (TextChannel) output);
                    }
                }
            }
        }
    }

    private void loadDJProfiles(){
        for (Member member : jda.getGuilds().get(0).getMembersWithRoles(jda.getRoleById(Strings.DJ_ROLE))){
            if (JsonFile.exists("dj_profiles", member.getId())){
                Profile profile = Profile.loadFromFile(member.getId());
                profiles.put(member.getId(), profile);
            }
        }
    }

    private void loadDJApplications(){
        Role role = getRole(Strings.DJ_APPLICATION_ROLE, SearchType.ID);
        List<Member> members = jda.getGuilds().get(0).getMembersWithRoles(role);
        JsonFile file = new JsonFile("applications", "./");
        for (Member member : members){
            Profile profile = Profile.loadFromApplicationFile(member.getId());
            JSONObject json = file.getJSONString(member.getId());
            String channelID = (String) json.get("app_channel");
            TextChannel channel = (TextChannel) getChannel(channelID, SearchType.ID);
            if (profile != null)
                applications.put(member.getId(), new Application(profile, channel));
        }

    }
    public JDA getJDA() {
        return jda;
    }
    public String getAvatar() {
        return avatar;
    }
    public Map<TextChannel, TextChannel> getAnnouncerChannels() {
        return announcerChannels;
    }

    public JsonFile getAnnouncerFile(){
        return announcers;
    }
    public void setPresence(Activity activity){
        jda.getPresence().setActivity(activity);
    }
    public void registerCommand(String label, Command command){
        handler.registerCommand(label, command);
    }
    public void sendMessage(String message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            channel.sendMessage(message).queue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(MessageCreateData message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            channel.sendMessage(message).queue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public Message sendMessageWithReturn(String message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            return channel.sendMessage(message).complete();
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    public Message sendMessageWithReturn(MessageCreateData message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            return channel.sendMessage(message).complete();
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    public Channel getChannel(String key, SearchType type){
        switch (type){
            case NAME:
                return getChannelByName(key);
            case ID:
                return getChannelByID(key);
            default:
                return null;
        }
    }

    private Channel getChannelByID(String id){
        Guild guild = jda.getGuilds().get(0);
        try {
            return guild.getChannelById(TextChannel.class, id);
        }catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    private Channel getChannelByName(String name){
        Guild guild = jda.getGuilds().get(0);
        return guild.getTextChannelsByName(name, true).get(0);
    }

    public Role getRole(String key, SearchType type) {
        switch (type){
            case NAME:
                return getRoleByName(key);
            case ID:
                return getRoleByID(key);
            default:
                return null;
        }
    }

    private Role getRoleByName(String name){
        Guild guild = jda.getGuilds().get(0);
        try {
            return guild.getRolesByName(name, true).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private Role getRoleByID(String id){
        Guild guild = jda.getGuilds().get(0);
        return guild.getRoleById(id);
    }

    public Profile getProfile(Member member){
        return profiles.get(member.getId());
    }

    public void addProfile(Member member, Profile profile){
        profiles.put(member.getId(), profile);
    }

    public List<TextChannel> getChannels(){
        return jda.getTextChannels();
    }

    public void setApplicationMessageID(String applicationMessageID){
        this.applicationMessageID = applicationMessageID;
    }

    public String getApplicationMessageID(){
        return this.applicationMessageID;
    }

    public void openDJApplications(){
        this.isAcceptingApplications = true;
    }

    public void closeDJApplicaations(){
        this.isAcceptingApplications = false;
    }

    public void stop(){
        jda.shutdown();
    }

    public static Kep3 getInstance() {
        if (instance == null)
            instance = new Kep3();
        return instance;
    }

    public class ApplicationManager {
        private static final TextChannel SUBMISSION_CHANNEL = Kep3.getInstance().jda.getTextChannelById(Strings.DJ_SUBMISSION_CHANNEL);
        public static boolean addApplication(Profile profile, String discordID, TextChannel appChannel){
            if (applications.containsKey(discordID))
                return false;

            applications.put(discordID, new Application(profile, appChannel));
            return true;
        }

        public static void sendApplicationMessage(User applicant, Profile profile){
            Button accept = Button.of(ButtonStyle.SUCCESS, "app_accept+" + applicant.getId(), "Accept");
            Button deny = Button.of(ButtonStyle.DANGER, "app_deny+" + applicant.getId(), "Reject");

            MessageCreateBuilder builder = new MessageCreateBuilder()
                    .addEmbeds(profile.getProfileEmbed())
                            .addActionRow(accept, deny);

            Kep3.getInstance().sendMessage(builder.build(), SUBMISSION_CHANNEL, 1000);
        }

        public static boolean acceptApplication(String discordID){
            Application application = applications.remove(discordID);
            Kep3.getInstance().profiles.put(discordID, application.getProfile());
            application.getApplicationChannel().getManager().setParent(Kep3.getInstance().getJDA().getCategoryById(Strings.DJ_APP_ARCHIVE_CATEGORY)).queue();
            return application.getProfile().newSave();
        }

        public static void denyApplication(String discordID){
            applications.get(discordID).getApplicationChannel().delete().queue();
            applications.remove(discordID);
        }
    }
}