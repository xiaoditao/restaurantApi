
/**
 *
 * @author xiaoditao
 */


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * The ConnectDataBase class helps input data from android to MangoDB
 * and get each record info from MangoDB
 */
public class ConnectDataBase {
    MongoDatabase database = null;
    MongoCollection<Document> collection = null;
    static Map<String, Integer> map = null;
    /**
     *
     * Constructor, set up all the database settings
     */
    public ConnectDataBase() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb://xiaodit:Wangliyun1970@cluster0-shard-00-00-3wcku.mongodb.net:27017,cluster0-shard-00-01-3wcku.mongodb.net:27017,cluster0-shard-00-02-3wcku.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");
        MongoClient mongoClient = new MongoClient(uri);
        database = mongoClient.getDatabase("try1");
        collection = database.getCollection("try1");
        map = new HashMap<>();
    }
    
    public String getDB(String id) {
        MongoCursor<Document> cursor = collection.find().iterator();
        String res = "";
        String record = "";
        if (id.equals("none")){
            
            while (cursor.hasNext()) {
                try {
                    
                    JSONObject json = new JSONObject(cursor.next().toJson());
                    String temptID = (String)json.get("restaurantID");
                    String temptName = (String)json.get("name");
                    JSONObject inside = new JSONObject();
                    JSONArray menu = new JSONArray();
                    JSONObject outside = new JSONObject();
                    inside.put("id", temptID);
                    inside.put("name", temptName);
                    menu.put(inside);
                    outside.put("MenuSection", menu);
                    record = outside.toString();
                    res += record;
                } catch (Exception ex) {
                    Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return res;
        }
        Document myDoc = collection.find(eq("restaurantID", id)).first();
        if(myDoc == null) return "";
        JSONObject json = new JSONObject(myDoc);
        String temptID;
        try {
            temptID = (String)json.get("restaurantID");
            String temptName = (String)json.get("name");
            JSONObject inside = new JSONObject();
            JSONArray menu = new JSONArray();
            JSONObject outside = new JSONObject();
            inside.put("id", temptID);
            inside.put("name", temptName);
            menu.put(inside);
            outside.put("MenuSection", menu);
            res = outside.toString();
        } catch (JSONException ex) {
            Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public String postDB(String id, String name) {
        boolean flag = true;
        String res = "";
        try {
            Document myDoc = collection.find(eq("restaurantID", id)).first();
            if(myDoc != null) {
                flag = false;
                res = "error";
                return res;
            }
            Document doc = new Document("restaurantID", id)
                    .append("name", name);
            collection.insertOne(doc);
            JSONObject inside = new JSONObject();
            JSONArray menu = new JSONArray();
            JSONObject outside = new JSONObject();
            
            inside.put("id", id);
            inside.put("name", name);
            menu.put(inside);
            outside.put("success", flag);
            outside.put("MenuSection", menu);
            res = outside.toString();
            return res;
            
        } catch (JSONException ex) {
            Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public String putDB(String name,String id) {
        boolean flag = true;
        String res = "";
        try {
            Document myDoc = collection.find(eq("restaurantID", id)).first();
            if(myDoc == null) {
                flag = false;
                res = "error, the record doest not exist";
                return res;
            }
            collection.updateOne(eq("restaurantID", id), new Document("$set", new Document("restaurantID", id).append("name", name)));
            JSONObject inside = new JSONObject();
            JSONArray menu = new JSONArray();
            JSONObject outside = new JSONObject();
            
            inside.put("id", id);
            inside.put("name", name);
            menu.put(inside);
            outside.put("success", flag);
            outside.put("MenuSection", menu);
            res = outside.toString();
            return res;
            
        } catch (JSONException ex) {
            Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public String deleteDB(String id) {
        Boolean flag = true;
        JSONObject res = new JSONObject();
        String ans = "";
        try {
            collection.deleteOne(eq("restaurantID", id));
            ans = res.toString();
            res.put("success", flag);
        } catch (JSONException ex) {
            flag = false;
            return "error";
        }
        return ans;
    }
    
}

