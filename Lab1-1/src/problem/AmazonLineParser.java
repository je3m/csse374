package problem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonLineParser implements ILineParser{

	@Override
	public String parse(String line) {
		Pattern p = Pattern.compile("(.*?) ttl (.*?), (.*?) ttl (.*)");
		Matcher m = p.matcher(line);

		if(m.find()){
			String s =  m.group(1) + " : " + m.group(2) + "\n" +
					m.group(3) + " : " + m.group(4);

			return s;
		}
		return null;
	}

}
