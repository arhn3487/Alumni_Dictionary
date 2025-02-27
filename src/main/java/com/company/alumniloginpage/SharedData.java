package com.company.alumniloginpage;

public class SharedData
{
    private static SharedData instance;
    private String loggedInUserId;

    // Private constructor to prevent instantiation
    private SharedData() {}

    // Get the singleton instance
    public static SharedData getInstance()
    {
        if (instance == null)
        {
            instance = new SharedData();
        }
        return instance;
    }

    // Getter and setter for loggedInUserId
    public String getLoggedInUserId()
    {
        return loggedInUserId;
    }

    public void setLoggedInUserId(String loggedInUserId)
    {
        this.loggedInUserId = loggedInUserId;
    }
}