package cn.smbms.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
@Component("dataConverter")
public class DateConverter implements Converter<String,Date>{
	private SimpleDateFormat[] dates = {new SimpleDateFormat("yyyy-MM-dd"),	  
				new SimpleDateFormat("MM/dd/yyyy"),
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
				new SimpleDateFormat("yyyy*MM*dd")
	};
	@Override
	public Date convert(String arg0) {
		for(SimpleDateFormat date1 : dates){
			try {
				return date1.parse(arg0);
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}
	
}
