package net.whispwriting.kep3.discord.util;

import org.json.JSONArray;
import org.json.JSONObject;

public class TickerBoardJsonBuilder {
    private JSONObject root = new JSONObject();
    private JSONArray performers = new JSONArray();
    private static TickerBoardJsonBuilder instance;

    public void setWelcome(String top, String bottom, int minutes, String[] ticker){
        JSONObject welcome = new JSONObject();
        welcome.append("top", top);
        welcome.append("bottom", bottom);
        welcome.append("displayOnPlatformAfterMinutes", minutes);
        welcome.append("ticker", ticker);
        root.remove("welcome");
        root.append("welcome", welcome);
    }

    public void setEvent(String top, String bottom, int minutes, String[] ticker){
        JSONObject event = new JSONObject();
        event.append("top", top);
        event.append("bottom", bottom);
        event.append("displayOnPlatformAfterMinutes", minutes);
        event.append("ticker", ticker);
        root.remove("event");
        root.append("event", event);
    }

    public void setInfo(String[] pages, int pageDisplayTime){
        JSONObject info = new JSONObject();
        info.append("pages", pages);
        info.append("pageDisplayTime", pageDisplayTime);
        root.remove("info");
        root.append("info", info);
    }

    public void setPerformer(int number, String name, long startTime, int delayMins, String genre, String[] ticker){
        JSONObject performer = new JSONObject();
        performer.append("name", name);
        performer.append("startTime", startTime);
        performer.append("delayMins", delayMins);
        performer.append("genre", genre);
        performer.append("ticker", ticker);
        performers.put(number, performer);
    }

    public void setSchedule(long startTime, long closeTime, String[] ticker){
        JSONObject schedule = new JSONObject();
        schedule.append("displayStartTime", schedule);
        schedule.append("performers", performers);
        schedule.append("closeTime", closeTime);
        schedule.append("ticker", ticker);
        root.append("schedule", schedule);
    }

    public String build(){
        return root.toString();
    }

    private TickerBoardJsonBuilder(){}

    public static TickerBoardJsonBuilder getInstance(){
        if (instance == null)
            return new TickerBoardJsonBuilder();
        return instance;
    }
}
