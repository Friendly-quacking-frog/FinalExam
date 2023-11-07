package org.example;

import java.io.FileInputStream;
import java.util.Properties;

public class Company {

    public boolean isActive;
    public String name;
    public String description;
    public Company(){
        Properties prop = new Properties();
        String fileName = "src/company.conf";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
            name = prop.getProperty("name");
            description = prop.getProperty("desc");
            isActive = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }
}
