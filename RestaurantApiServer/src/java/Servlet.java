
/**
 *
 * @author xiaoditao
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;


@WebServlet(name = "menusection", urlPatterns = {"/menusection/*"})
public class Servlet extends HttpServlet {
    private ConnectDataBase connectDataBase = new ConnectDataBase();
    // returns the GET response form the server to client
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        System.out.println("Console: doGET visited");
        String result = "";
        String id = "";
        // The name is on the path /name so skip over the '/'
        String idinput = (request.getPathInfo()).substring(1);
        // if the user doesn't specify the id, go get all the records
        if(idinput.equals("")) idinput = "none";
        result = connectDataBase.getDB(idinput);
        // return 401 if result not found
        if(result.equals("")) {
            response.setStatus(401);
            return;    
        }
        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("text/plain;charset=UTF-8");
        // return the value from a GET request      
        PrintWriter out = response.getWriter();
        out.println(result);  
        out.flush();
        }
               
    
    // Delete an existing variable from memory. If no such variable then return a 401
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Console: doDelete visited");
        String result = "";
        // The name is on the path /name so skip over the '/'
        String id = (request.getPathInfo()).substring(1); 
        if(id.equals("")) {
            // no value, return 401
            response.setStatus(401);
            return;      
        }
         result = connectDataBase.deleteDB(id);
         if(result.equals("error")) {
            response.setStatus(401);
            return;  
         }
        // Set HTTP response code to 200 OK
        response.setStatus(200);
        response.setContentType("text/plain;charset=UTF-8");
        // return the value from a GET request      
        PrintWriter out = response.getWriter();
        out.println(result);  
        out.flush();                
    }

    // POST is used to create a new record
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Console: doPost visited");
        String json = "";
        String output ="";
        String data = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        while ((output = br.readLine()) != null) {
			data += output;
		}
        try {
            // set the json object that serves as the response
            JSONObject dataJson = new JSONObject(data);
            String name = dataJson.getString("name");
            String id = dataJson.getString("id");
            if(id.equals("")) {
            // no variable name return 401
            response.setStatus(401);
            return;      
        }
            json = connectDataBase.postDB(id, name);
        } catch (JSONException ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        // If the variable is already in memory, let's return an error
        if(json.equals("error")) {
            response.setStatus(409);
            return;
        }
        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("text/plain;charset=UTF-8");
        // return the value from a POST request
        PrintWriter out = response.getWriter();
        out.println(json);  
        out.flush();
    } 
    
    // PUT is used to edit a record
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {  
        System.out.println("Console: doPut visited");
        String output ="";
        String data = "";
        String json = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        while ((output = br.readLine()) != null) {
			data += output;
		}
        try {
            JSONObject dataJson = new JSONObject(data);
            String name = dataJson.getString("name");
            String id = dataJson.getString("id");
            if(id.equals("")) {
            // no variable name return 401
            response.setStatus(401);
            return;      
        }
            json = connectDataBase.putDB(name,id);
        } catch (JSONException ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        }
       if(json.equals("error, the record doest not exist")) {
            response.setStatus(409);
            return;
        }
        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("text/plain;charset=UTF-8");
        // return the value from a PUT request 
        PrintWriter out = response.getWriter();
        out.println(json);  
        out.flush();    
        }       
}