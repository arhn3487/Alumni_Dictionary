package com.company.alumniloginpage;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection
{
    // Singleton instance
    private static MongoDBConnection instance;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private static MongoCollection<Document> collection;

    public MongoDBConnection()
    {
        // Connect to MongoDB
        //mongoClient = MongoClients.create("mongodb://localhost:27017");
        mongoClient = MongoClients.create("mongodb+srv://arafatsakibisbat:Password123@alumni.meusu.mongodb.net/?retryWrites=true&w=majority&appName=alumni");
        //mongoClient = mongodb+srv://arafatsakibisbat:Password123@alumni.meusu.mongodb.net/?retryWrites=true&w=majority&appName=alumni
        database = mongoClient.getDatabase("alumni");
        collection = database.getCollection("info");
    }

    public static void insertinfo(Document doc)
    {
        collection.insertOne(doc);
        System.out.println("Document inserted");
    }

    public static MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void closeConnection() {
        mongoClient.close();
        System.out.println("Connection closed.");
    }
}