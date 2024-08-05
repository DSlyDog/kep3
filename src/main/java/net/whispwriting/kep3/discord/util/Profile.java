package net.whispwriting.kep3.discord.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.whispwriting.kep3.discord.Kep3;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile {

    private String discordID;
    private String name;

    private String vrcName;
    private String logo;
    private List<String> genres = new ArrayList<>();
    private List<String> socials = new ArrayList<>();
    private List<String> demoSets = new ArrayList<>();

    private JsonFile file;


    private Profile(String discordID, String vrcName, String name, String logo, String genre, String demo, JsonFile file){
        this.discordID = discordID;
        this.name = name;
        this.vrcName = vrcName;
        this.logo = logo;
        this.file = file;
        genres.add(genre);
        demoSets.add(demo);
    }

    private Profile(String discordID, String vrcName, String name, String logo, List<String> genres, List<String> socials, List<String> demoSets, JsonFile file){
        this.discordID = discordID;
        this.name = name;
        this.vrcName = vrcName;
        this.logo = logo;
        this.genres = genres;
        this.socials = socials;
        this.demoSets = demoSets;
        this.file = file;
    }

    public boolean updateName(String name){
        this.name = name;
        file.set("name", name);
        return saveFile();
    }

    public String getName(){
        return name;
    }

    public boolean updateVRCName(String vrcName){
        this.vrcName = vrcName;
        file.set("vrcName", vrcName);
        return saveFile();
    }

    public String getVRCName(){
        return vrcName;
    }

    public boolean updateLogo(String logo){
        this.logo = logo;
        file.set("logo", logo);
        return saveFile();
    }

    public String getLogo(){
        return logo;
    }

    public boolean addGenre(String genre){
        genres.add(genre);
        file.set("genres", genres);
        return saveFile();
    }

    public boolean removeGenre(int index){
        genres.remove(index);
        file.set("genres", genres);
        return saveFile();
    }

    public String getGenres(){
        return listToString(genres);
    }

    public List<String> getGenresList(){
        return genres;
    }

    public boolean addSocial(String social){
        socials.add(social);
        file.set("socials", socials);
        return saveFile();
    }

    public boolean removeSocial(int index){
        socials.remove(index);
        file.set("socials", socials);
        return saveFile();
    }

    public String getSocials(){
        return listToString(socials);
    }

    public List<String> getSocialsList(){
        return socials;
    }

    public boolean addDemoSet(String demoSet){
        demoSets.add(demoSet);
        file.set("demoSets", demoSets);
        return saveFile();
    }

    public boolean removeDemoSet(int index){
        demoSets.remove(index);
        file.set("demoSets", demoSets);
        return saveFile();
    }

    public String getDemoSets(){
        return listToString(demoSets);
    }

    public List<String> getDemoSetsList(){
        return demoSets;
    }

    private boolean saveFile(){
        try {
            file.save();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save DJ Profile file");
            e.printStackTrace();
            return false;
        }
    }

    private String listToString(List<String> lst){
        StringBuilder builder = new StringBuilder();
        for (String s : lst){
            builder.append(s).append(", ");
        }
        return builder.substring(0, builder.toString().length());
    }

    public boolean newSave(){
        file.set("name", name);
        file.set("vrcName", vrcName);
        file.set("logo", logo);
        file.set("genres", genres);
        file.set("socials", socials);
        file.set("demoSets", demoSets);

        try {
            file.save();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save DJ Profile file");
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAsApplication(TextChannel channel){
        JsonFile file = new JsonFile("applications", "./");
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("vrcName", vrcName);
        json.put("logo", logo);
        json.put("genres", genres);
        json.put("socials", socials);
        json.put("demoSets", demoSets);
        json.put("app_channel", channel.getId());

        file.set(discordID, json);

        try{
            file.save();
            return true;
        }catch(IOException e){
            System.err.println("Failed to save application file");
            e.printStackTrace();
            return false;
        }
    }

    public MessageEmbed getProfileEmbed(){
        try {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setColor(Color.yellow);

            builder.addField("DJ Name", name, false);

            builder.addField("Discord Name", Kep3.getInstance().getJDA().getUserById(discordID).getEffectiveName(), false);

            builder.addField("VRChat Name", vrcName, false);

            String genreString = listFieldBuilder(genres);
            genreString = String.format(genreString, "genres");
            builder.addField("Genres", genreString, false);

            String socialsString = listFieldBuilder(socials);
            socialsString = String.format(socialsString, "socials");
            builder.addField("Socials", socialsString, false);

            String demoSetString = listFieldBuilder(demoSets);
            demoSetString = String.format(demoSetString, "demo sets");
            builder.addField("Demo Sets", demoSetString, false);

            if (logo.length() > 0)
                builder.setThumbnail(logo);

            return builder.build();
        }catch(IllegalArgumentException e){
            System.err.println("A profile field is null");
            e.printStackTrace();
            return new EmbedBuilder().addField("Error", "A profile was found, but there is a blank field. Please notify a staff member of this issue.", false).build();
        }
    }

    private String listFieldBuilder(List<String> lst){
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : lst){
            stringBuilder.append(item).append(", ");
        }
        return stringBuilder.toString().length() > 0 ? stringBuilder.toString().substring(0, stringBuilder.toString().length()-2) : "There are no %s listed.";
    }

    public static Profile buildFromApplication(String discordID, String vrcName, String name, String logo, String genre, String demo){
        JsonFile file = new JsonFile(discordID, "dj_profiles");

        return new Profile(discordID, vrcName, name, logo, genre, demo, file);
    }

    public static Profile buildFromApplication(String discordID, String vrcName, String name, String logo, List<String> genres, List<String> socials, List<String> demoSets){
        JsonFile file = new JsonFile(discordID, "dj_profiles");

        return new Profile(discordID, vrcName, name, logo, genres, socials, demoSets, file);
    }

    public static Profile loadFromFile(String discordID){
        JsonFile file = new JsonFile(discordID, "dj_profiles");
        String name = file.getString("name");
        String vrcName = file.getString("vrcName");
        String logo = file.getString("logo");
        List<String> genres = file.getStringList("genres");
        List<String> socials = file.getStringList("socials");
        List<String> demoSets = file.getStringList("demoSets");

        return new Profile(discordID, vrcName, name, logo, genres, socials, demoSets, file);
    }

    public static Profile loadFromApplicationFile(String discordID) {
        JsonFile file = new JsonFile("applications", "./");

        return new Profile(discordID, file.getString(discordID + ".vrcName"), file.getString(discordID + ".name"), file.getString(discordID + ".logo"),
                file.getStringList(discordID + ".genres"), file.getStringList(discordID + ".socials"),
                file.getStringList(discordID + "demoSets"), new JsonFile(discordID, "dj_profiles"));
    }
}
