package utils;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class DbConnectionManager {

    public static String CONNECTION_URL = "mongodb+srv://test:test@cluster0.saqt1.mongodb.net/pillMinderDB?retryWrites=true&w=majority";
    public static String DATABASE_PILL_MINDER = "pillMinderDB";

    public static MongoDatabase getMongoDatabase() {
        ConnectionString connectionString = new ConnectionString(CONNECTION_URL);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_PILL_MINDER);
        return database;
    }
}
