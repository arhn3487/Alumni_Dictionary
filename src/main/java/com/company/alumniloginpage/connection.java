package com.company.alumniloginpage;

import  java.sql.DriverManager;
import  java.sql.Connection;

public class connection {
    public Connection databaselink;

    public Connection getConnection(){
        String databaseName="colege";
        String databaseUser="root";
        String databasePassword="arafathasan03";
        String url = "jdbc:mysql://localhost/"+databaseName;
        try{
            databaselink=DriverManager.getConnection(url,databaseName,databasePassword);
        }catch(Exception e){
            System.out.println(e);
        }
        return databaselink;
    }
}
