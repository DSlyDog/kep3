package net.whispwriting.kep3;

import net.whispwriting.kep3.discord.Kep3;
import net.whispwriting.kep3.discord.commands.CommandDelegate;
import net.whispwriting.kep3.discord.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {


    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static boolean listen = true;

    public static void main(String[] args){

        if (args.length < 1){
            LOGGER.error("You must provide the bot token");
            return;
        }

        Strings.loadData();

        Kep3 bot = Kep3.getInstance();
        try{
            bot.init(args[0]);
        }catch (LoginException e){
            LOGGER.error("Failed to connect to the Discord bot");
            e.printStackTrace();
            return;
        }

        registerCommandsAsync(bot);

        listen(bot);
    }

    public static void listen(Kep3 bot){
        Scanner sc = new Scanner(System.in);
        while (listen){
            String line = sc.nextLine();
            onCommandLineCmd(line, bot);
        }
    }

    public static void onCommandLineCmd(String cmd, Kep3 bot){
        switch (cmd){
            case "stop":
                LOGGER.info("Shutting down bot...");
                listen = false;
                bot.stop();
                break;
            case "time":
                LocalDateTime time = LocalDateTime.now();
                System.out.println(time.getHour());
                break;
            default:
                LOGGER.info("Command not found");
                break;
        }
    }

    public static void registerCommandsAsync(Kep3 bot){
        Thread cmdRegisterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    CommandDelegate.registerCommands(bot);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        cmdRegisterThread.start();
    }

    public static Logger getLogger(){
        return LOGGER;
    }
}
