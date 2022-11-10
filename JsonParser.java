

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;


public class JsonParser {
	

	public static void main(String[] args) throws IOException {
	      
		
		 InputStream is = new FileInputStream("age.json");
         String jsonTxt = IOUtils.toString(is, "UTF-8");
         //System.out.println(jsonTxt);
        // JSONObject jo = new JSONObject(jsonTxt);
         JSONObject jo = new JSONObject(jsonTxt) {
        	    /**
        	     * changes the value of JSONObject.map to a LinkedHashMap in order to maintain
        	     * order of keys.
        	     */
        	    @Override
        	    public JSONObject put(String key, Object value) throws JSONException {
        	        try {
        	            Field map = JSONObject.class.getDeclaredField("map");
        	            map.setAccessible(true);
        	            Object mapValue = map.get(this);
        	            if (!(mapValue instanceof LinkedHashMap)) {
        	                map.set(this, new LinkedHashMap<>());
        	            }
        	        } catch (NoSuchFieldException | IllegalAccessException e) {
        	            throw new RuntimeException(e);
        	        }
        	        return super.put(key, value);
        	    }
        	};
         JSONArray empArray = jo.getJSONArray("employees");
         
         System.out.println("\njsonArray: " + empArray);
         
         int count = empArray.length(); // get totalCount of all jsonObjects
         for(int i=0 ; i< count; i++){   // iterate through jsonArray 
             JSONObject jsonObject = empArray.getJSONObject(i);  // get jsonObject @ i position 
             System.out.println("jsonObject " + i + ": " + jsonObject);
         }
         
         for(int i=0 ; i< count; i++){   // iterate through jsonArray 
             JSONObject jsonObject = empArray.getJSONObject(i);  // get jsonObject @ i position 
             if(jsonObject.getString("name").contains("John"))
             {
            	 jsonObject.put("age", 22);
             }           
         }
         
         for(int i=0 ; i< count; i++){   // iterate through jsonArray 
             JSONObject jsonObject = empArray.getJSONObject(i);  // get jsonObject @ i position 
             System.out.println("after jsonObject " + i + ": " + jsonObject);
         }
         
         
         jo.put("employees", empArray);
         System.out.println("final " + jo.toString());
        
  

	}

}
