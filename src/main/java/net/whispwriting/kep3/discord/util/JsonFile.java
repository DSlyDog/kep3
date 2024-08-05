package net.whispwriting.kep3.discord.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonFile {

    private FileWriter writer;
    private FileReader reader;
    private File file;
    private JSONParser parser = new JSONParser();
    private JSONObject out = new JSONObject();
    private String name;

    public JsonFile(String name, String path) {
        this.name = name;
        File fPath = new File(path);
        if (!fPath.exists())
            fPath.mkdirs();
        file = new File(fPath, name + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create file");
                e.printStackTrace();
            }
        }
    }

    public void set(String path, Object object){
        out.put(path, object);
    }

    public boolean isEmpty(){
        return file.length() == 0;
    }
    public String getString(String path){
        try {
            reader = new FileReader(file);
            JSONObject object = (JSONObject) parser.parse(reader);
            String string = (String) object.get(path);
            reader.close();
            return string;
        }catch (IOException e){
            System.err.println("File read error");
            e.printStackTrace();
            return "";
        }catch(ParseException e){
            System.err.println("parse failed for " + file.getPath());
            e.printStackTrace();
            return "";
        }
    }

    public JSONObject getJSONString(String path){
        try{
            reader = new FileReader(file);
            JSONObject json = (JSONObject) parser.parse(reader);
            return (JSONObject) json.get(path);
        }catch (IOException e){
            System.err.println("File read error");
            e.printStackTrace();
            return null;
        }catch(ParseException e){
            System.err.println("parse failed for " + file.getPath());
            e.printStackTrace();
            return null;
        }
    }

    public int getInt(String path){
        try {
            reader = new FileReader(file);
            JSONObject object = (JSONObject) parser.parse(reader);
            int num = Integer.parseInt(object.get(path) + "");
            reader.close();
            return num;
        }catch (Exception e){
            System.err.println("File read error");
            e.printStackTrace();
            return 0;
        }
    }

    public long getLong(String path){
        try {
            reader = new FileReader(file);
            JSONObject object = (JSONObject) parser.parse(reader);
            long num = (long) object.get(path);
            reader.close();
            return num;
        }catch (Exception e){
            System.err.println("File read error");
            e.printStackTrace();
            return 0;
        }
    }

    public double getDouble(String path){
        try {
            reader = new FileReader(file);
            JSONObject object = (JSONObject) parser.parse(reader);
            double num = (double) object.get(path);
            reader.close();
            return num;
        }catch (Exception e){
            System.err.println("File read error");
            e.printStackTrace();
            return 0;
        }
    }

    public List<String> getStringList(String path){
        try {
            if (file.length() != 0) {
                reader = new FileReader(file);
                JSONObject object = (JSONObject) parser.parse(reader);
                JSONArray list = (JSONArray) object.get(path);
                List<String> strings = new ArrayList<>();
                for (Object obj : list) {
                    String str = (String) obj;
                    strings.add(str);
                }
                reader.close();
                return strings;
            }else{
                return null;
            }
        }catch(NullPointerException e){
            return null;
        } catch (Exception e){
            System.err.println("File read error");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean exists(String name, String path){
        return new File(path, name + ".json").exists();
    }

    public void save() throws IOException{
        writer = new FileWriter(file);
        writer.write(out.toString());
        writer.flush();
        writer.close();
    }

    public String getFilePath(){
        return file.getAbsolutePath();
    }

}
