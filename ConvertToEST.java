import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ConvertToEST {

	public static void main(String[] args) {
		
		String standardTS = "2017-08-01T14:30:45.111Z";
		
		String formattedTS = "2017/04/30 08:10:25";
		String format = "yyyy/MM/dd HH:mm:ss";
		
		System.out.println(convertTsToEST(standardTS));
		System.out.println(convertTsToEST(formattedTS,format));

	}
	
	public static String convertTsToEST(String ts) {
		String estTime=OffsetDateTime.parse(ts).atZoneSameInstant(ZoneId.of("America/New_York")).toString();
		return estTime.replace("[America/New_York]", "");
	}
	
	public static String convertTsToEST(String ts,String format) {

		DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern(format);
		LocalDateTime ldt = LocalDateTime.parse(ts, sourceFormatter);
		
		ZoneOffset offset = ZoneOffset.UTC;
		String odt = ldt.atOffset(offset).toString();
		
		String estTime=OffsetDateTime.parse(odt).atZoneSameInstant(ZoneId.of("America/New_York")).toString();
		return estTime.replace("[America/New_York]", "");
	}

}
