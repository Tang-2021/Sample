import org.apache.commons.io.IOUtils
import org.json.{JSONArray, JSONObject}

import java.io.{FileInputStream, IOException, InputStream}
import java.lang.reflect.Field

import java.util.LinkedHashMap


object Parser {

  def main(args: Array[String]): Unit = {
    val is: InputStream = new FileInputStream("age.json")
    val jsonTxt: String = IOUtils.toString(is, "UTF-8")
    //System.out.println(jsonTxt);
    // JSONObject jo = new JSONObject(jsonTxt);
    val jo: JSONObject = new JSONObject(jsonTxt) {
      /**
       * changes the value of JSONObject.map to a LinkedHashMap in order to maintain
       * order of keys.
       */

      override def put(key: String, value: Any): JSONObject = {

        val map: Field = classOf[JSONObject].getDeclaredField("map")
        map.setAccessible(true)
        val mapValue: Any = map.get(this)
        if (!mapValue.isInstanceOf[LinkedHashMap[_, _]]) map.set(this, new LinkedHashMap[AnyRef, AnyRef])

        super.put(key, value)
      }
    }
    val empArray: JSONArray = jo.getJSONArray("employees")
    System.out.println("\njsonArray: " + empArray)
    val count: Int = empArray.length // get totalCount of all jsonObjects
    for (i <- 0 until count) { // iterate through jsonArray
      val jsonObject: JSONObject = empArray.getJSONObject(i) // get jsonObject @ i position
      System.out.println("jsonObject " + i + ": " + jsonObject)
    }
    for (i <- 0 until count) {
      val jsonObject: JSONObject = empArray.getJSONObject(i)
      if (jsonObject.getString("name").contains("John")) jsonObject.put("age", 22)
    }
    for (i <- 0 until count) {
      val jsonObject: JSONObject = empArray.getJSONObject(i)
      System.out.println("after jsonObject " + i + ": " + jsonObject)
    }
    jo.put("employees", empArray)
    System.out.println("final " + jo.toString)
  }
}