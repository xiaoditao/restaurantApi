
package restaurantapiclient;

/**
 *
 * @author xiaoditao
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

// A simple class to wrap a result
class Result {
    String value;
    /**
    * Returns the string result
    * @return      the string result
    */
    public String getValue() {
        return value;
    }
    /**
    * Set the string result
    * @param  the string result
    */
    public void setValue(String value) {
        this.value = value;
    }
}

// The client class for the api
public class RestaurantApiClient {   
    /**
    * Main method that shows the menu and switch the user's input 
    * If user chooses 1 or 2, go to GET method
    * If user chooses 3, go to POST method 
    * If user chooses 4, go to PUT method 
    * If user chooses 5, go to DELETE method 
    * If user chooses 6, quit the client 
 */
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        System.out.println("Restaurant API Menu");
        System.out.println("1. Get all menu sections");
        System.out.println("2. Get a menu section by ID");
        System.out.println("3. Add a new menu section");
        System.out.println("4. Edit a menu section");
        System.out.println("5. Delete a menu section");
        System.out.println("6. Exit");
        // continously print the menu, until the user quits
        while(sc.hasNextLine()) {
            // if input 1, go to getAll which calls the doGetList method that
            // shows all the records in the database
            String input = sc.nextLine();
            if (input.equals("1")) {
                System.out.println("Response Body:");
                System.out.println(getAll());
            }
            // if input 2, use read method to get the result from doGet method
            if (input.equals("2")) {
                System.out.println("Please insert the ID");
                String id = sc.nextLine();
                System.out.println("Response Body:");
                System.out.println(read(id));
            }
            // if input 3, use assign method to get the result from doPost method
            if (input.equals("3")) {
                System.out.println("Please insert the ID");
                String id = sc.nextLine();
                System.out.println("Please insert the name");
                String name = sc.nextLine();
                Result r = new Result();
                Boolean flag = assign(name, id, r);
                // print the error info when the user tends to add new menue which has the 
                // id that already in the database
                if (flag == false) {
                    System.out.println("Response Body:");
                    System.out.println("fail to add, there already exists a record with the same id");
                }
                
            }
            // if input 3, use edit method to get the result from doPut method
            if (input.equals("4")) {
                System.out.println("Please insert the ID");
                String id = sc.nextLine();
                System.out.println("Please insert the name");
                String name = sc.nextLine(); 
                Boolean flag = edit(name, id);
                // print the error info when the user tends to edit  menue which has the 
                // id that not in the database
                if (flag == false) {
                    System.out.println("Response Body:");
                    System.out.println("fail to edit, there isn't a record with the id");
                }
            }
            // if input 3, use clear method to call doDelete method
            if (input.equals("5")) {
                System.out.println("Please insert the ID");
                String id = sc.nextLine();
                Boolean flag = clear(id);
                JSONObject json = new JSONObject();
                json.put("success", flag);
                System.out.println(json.toString());
            }
            // if input 6, quit
            if (input.equals("6")) {
                System.out.println("quitting the client...");
                break;
            }
            System.out.println();
            System.out.println("Restaurant API Menu");
            System.out.println("1. Get all menu sections");
            System.out.println("2. Get a menu section by ID");
            System.out.println("3. Add a new menu section");
            System.out.println("4. Edit a menu section");
            System.out.println("5. Delete a menu section");
            System.out.println("6. Exit");
        }
    }
    
    /**
     * Returns an String that contains all the menu info in the database
     * @return      the String that contains all the menu info in the database
     */
    public static String getAll() {
        Result r = new Result();
        int status = 0;
        if((status = doGetList(r)) != 200) return "Error from server "+ status;
        return r.getValue();
    }
    
    
    
    /**
     * let the user to create new menu
     * @param name the name of the section
     * @param id the id of the section
     * @param r the result
     * @return  whether the assignment is successful
     */
    public static boolean assign(String name, String id, Result r) {
        Boolean flag = true;
        if(doPost(name,id,r) == 200) {
            System.out.println("Response Body:");
            String res = "";
            String print = read(id);
            try {
                JSONObject json = new JSONObject(print);
                json.put("success", flag);
                res = json.toString();
            } catch (JSONException ex) {
                Logger.getLogger(RestaurantApiClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(res);
            return true;
        }
        flag = false;
        return false;
    }
     /**
     * let the user to edit new menu
     * @param name the name of the section
     * @param id the id of the section
     * @return  whether the edit is successful
     */
    public static boolean edit(String name, String id) {
        Boolean flag = true;
        if(doPut(name,id) == 200) {
            System.out.println("Response Body:");
            String res = "";
            String print = read(id);
            try {
                JSONObject json = new JSONObject(print);
                json.put("success", flag);
                res = json.toString();
            } catch (JSONException ex) {
                Logger.getLogger(RestaurantApiClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(res);
            return true;
        }
        
        return false;
    }
     /**
     * let the user to read menu
     * @param id the id of the section
     * @return  the string contains the info of the menu section
     */
    public static String read(String id) {
        Result r = new Result();
        int status = 0;
        if((status = doGet(id,r)) != 200) return "Error from server "+ status;
        return r.getValue();
    }
     /**
     * let the user to delete menu
     * @param id the id of the section
     * @return  boolean, whether the delete is successful
     */
    public static boolean clear(String id) {
        if(doDelete(id) == 200) return true;
        else return false;
    }
    
    // Low level routine to make an HTTP POST request
    // POST does not use the URL line for its message to the server
    public static int doPost(String name, String id, Result r) {
        r.setValue("");
        String response = "";
        int status = 0;
        String output ="";
        
        try {
            // Make call to a particular URL
            URL url = new URL("https://intense-lake-93564.herokuapp.com/menusection/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set request method to POST and send name value pair
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "text/plain");
            // write to POST data area
            JSONObject objJson = new JSONObject();
            JSONObject objJsonSend = new JSONObject();
            try {
                objJson.put("id", id);
                objJson.put("name", name);
                objJsonSend.put("name", name);
            } catch (JSONException ex) {
                Logger.getLogger(RestaurantApiClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            String ans = objJson.toString();
            System.out.println("Request Body:");
            System.out.println(objJsonSend.toString());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(ans);
            out.close();
            // get HTTP response code sent by server
            status = conn.getResponseCode();
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            output = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                response += output;  
            }
            conn.disconnect();
            //close the connection
            conn.disconnect();
        }
        // handle exceptions
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
        // return HTTP status
        r.setValue(response);
        return status;   
    }
     // Make an HTTP GET passing the name on the URL line
    public static int doGet(String id, Result r) {
        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;
        try {
            // pass the name on the URL line
            URL url = new URL("https://intense-lake-93564.herokuapp.com/menusection/" + "//"+id);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");
            // wait for response
            status = conn.getResponseCode();
            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                response += output;
            }
            conn.disconnect();  
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        }
        // return value from server
        // set the response object
        r.setValue(response);
        // return HTTP status to caller
        return status;
    }
    /**
     * get all the records
     * @param r
     * @return the status
     */
    public static int doGetList(Result r) {
        // Make an HTTP GET passing the name on the URL line
        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;
        try {
            
            URL url = new URL("https://intense-lake-93564.herokuapp.com/menusection/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");
            // wait for response
            status = conn.getResponseCode();
            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));    
            while ((output = br.readLine()) != null) {
                response += output;       
            }    
            conn.disconnect();
            
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        } 
        // return value from server
        // set the response object
        r.setValue(response);
        // return HTTP status to caller
        return status;
    }
    
    // Low level routine to make an HTTP PUT request
    // PUT does not use the URL line for its message to the server
    public static int doPut(String name, String id) {
        int status = 0;
        try {
            URL url = new URL("https://intense-lake-93564.herokuapp.com/menusection/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            // make the request info as an json message
            JSONObject objJson = new JSONObject();
            JSONObject objJsonSend = new JSONObject();
            try {
                objJson.put("id", id);
                objJson.put("name", name);
                objJsonSend.put("name", name);
            } catch (JSONException ex) {
                Logger.getLogger(RestaurantApiClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            String ans = objJson.toString();
            System.out.println("Request Body:");
            System.out.println(objJsonSend.toString());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(ans);
            out.close();
            // wait for response
            status = conn.getResponseCode();
            conn.disconnect();   
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
    
    // Send an HTTP DELETE to server along with name on the URL line
    public static int doDelete(String id) {
        int status = 0;
        try {
            // pass the id on the URL line
            URL url = new URL("https://intense-lake-93564.herokuapp.com/menusection/" + "//"+id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            status = conn.getResponseCode();
            String output = "";
            String response = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                response += output;   
            }
            conn.disconnect(); 
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }    
}
