package com.company.alumniloginpage;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection
{

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBConnection()
    {
        // Connect to MongoDB
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("alumni");
        collection = database.getCollection("info");
    }

    public void insertinfo(Document doc)
    {
        collection.insertOne(doc);
        System.out.println("Document inserted");
    }

    public void closeConnection() {
        mongoClient.close();
        System.out.println("Connection closed.");
    }
}
