package org.example;

import java.io.FileInputStream;
import java.util.Properties;

public class Employee {
    /***
     * Class that was made for single purpose:
     * to be converted to JSON
     *
     * It contains some default data
     */

    int id = 0;
    String firstName = "Test";
    String lastName = "Testovich";
    String middleName = "Testov";
    int companyId;
    String email = "FQF@Email.com";
    String url = "fqf.com";
    String phone = "string";
    String birthdate = "2023-08-15T12:25:25.165Z";
    boolean isActive = true;

    public Employee(int companyId){
        this.companyId = companyId;
        Properties prop = new Properties();
        String fileName = "src/employee.conf";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
            url = prop.getProperty("url");
            firstName = prop.getProperty("firstName");
            lastName = prop.getProperty("lastName");
            middleName = prop.getProperty("middleName");
            email = prop.getProperty("email");
            url = prop.getProperty("url");
            phone = prop.getProperty("phone");
            birthdate = prop.getProperty("birthdate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId(){return id;}

    public int getCompanyId() {return companyId;}

    public String getEmail() {return email;}

    public String getUrl() {return url;}
}
