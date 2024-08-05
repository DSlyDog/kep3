package net.whispwriting.kep3.discord.util;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Application {

    private Profile profile;
    private TextChannel applicationChannel;

    public Application(Profile profile, TextChannel applicationChannel){
        this.profile = profile;
        this.applicationChannel = applicationChannel;
    }

    public Profile getProfile(){
        return profile;
    }

    public TextChannel getApplicationChannel(){
        return applicationChannel;
    }
}
