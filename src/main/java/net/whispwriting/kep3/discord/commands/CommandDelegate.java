package net.whispwriting.kep3.discord.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.whispwriting.kep3.Main;
import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.commands.command.*;

public class CommandDelegate {

    public static void registerCommands(Kep3 bot){
        Main.getLogger().info("Initializing commands...");
        bot.getJDA().getGuilds().get(0).updateCommands().addCommands(
                Commands.slash("announcer", "link an input channel to an announcement channel.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOption(OptionType.CHANNEL, "input", "input channel")
                        .addOption(OptionType.CHANNEL, "output", "output channel"),
                Commands.slash("open_dj_apps", "Open DJ applications"),
                Commands.slash("close_dj_apps", "Close DJ Applications"),
                Commands.slash("dj_lookup", "Look up information on DJs")
                        .addOption(OptionType.STRING, "type", "name, vrc name, logo, genres, socials, demo sets, or full profile")
                        .addOption(OptionType.USER, "dj", "DJ to find data for"),
                Commands.slash("tickerset_welcome", "Set the ticker board welcome section")
                        .addOption(OptionType.STRING, "top", "top section")
                        .addOption(OptionType.STRING, "bottom", "bottom section")
                        .addOption(OptionType.INTEGER, "display_after_minutes", "displayOnPlatformAfterMinutes")
                        .addOption(OptionType.STRING, "ticker", "ticker panel, coma separated list")
        ).queue();

        bot.registerCommand("announcer", new CreateAnnouncerChannel());
        bot.registerCommand("open_dj_apps", new OpenDJApplications());
        bot.registerCommand("close_dj_apps", new CloseDJApplications());
        bot.registerCommand("dj_lookup", new DJLookup());
        Main.getLogger().info("Commands initialized");
    }
}
