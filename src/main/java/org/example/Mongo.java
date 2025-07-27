package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import io.github.cdimascio.dotenv.Dotenv;

public class Mongo {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CONNECTION_STRING = dotenv.get("MONGODB_CONNECTION_STRING");
    private static final String DATABASE_NAME = dotenv.get("MONGODB_DATABASE_NAME");
    private static final String COLLECTION_NAME = dotenv.get("MONGODB_COLLECTION_NAME");
    public static void insert(String id, String Taskname, String Score, String Statement) {
        MongoClient client = MongoClients.create(CONNECTION_STRING);

        MongoDatabase db = client.getDatabase(DATABASE_NAME);

        MongoCollection col = db.getCollection(COLLECTION_NAME);

        Document task = new Document("_id", id)
                .append("Taskname", Taskname)
                .append("Score", Score)
                .append("Statement", Statement);
        col.insertOne(task);

    }
}
