import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ConvertToUTC {

	public static void main(String[] args) {
		String standardTS = "2017-08-01T14:30:45.111-04:00";
		
		String formattedTS = "2017/04/30 08:10:25";
		String format = "yyyy/MM/dd HH:mm:ss";
		
		System.out.println(convertTsToUTC(standardTS));
		System.out.println(convertTsToUTC(formattedTS,format));
	}
	
	public static String convertTsToUTC(String ts) {
		String utcTime=OffsetDateTime.parse(ts).withOffsetSameInstant(ZoneOffset.UTC).toString();
		return utcTime;
	}
	
	public static String convertTsToUTC(String ts,String format) {

		DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern(format);
		LocalDateTime ldt = LocalDateTime.parse(ts, sourceFormatter);
		
		ZoneId zoneId = ZoneId.of("America/New_York");
		String odt = ldt.atZone(zoneId).toOffsetDateTime().toString();
		
		String utcTime=OffsetDateTime.parse(odt).withOffsetSameInstant(ZoneOffset.UTC).toString();
		return utcTime;
	}

}
