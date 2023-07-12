package com.example.websitesurfingbot;

import java.io.IOException;
import java.util.Properties;

public class PropertiesInitialize {

    public static final Properties properties = new Properties();

    static{
        initProperties();
    }

    public static void initProperties(){
        try(var inputStream = BotStartingClass.class.getClassLoader().getResourceAsStream("application.properties")){
            properties.load(inputStream);
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
