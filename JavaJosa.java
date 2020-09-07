import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 자바 한글 조사 처리. Replace Korean josa appropriately.
 * @author sangheon_lee (pod3275)
 * @see <a href="https://github.com/myevan/pyjosa">PyJosa</a>
 */
public class JavaJosa {
	HashMap<String, String[]> JOSA_PARID = new HashMap<String, String[]>();
	String JOSA_REGEX = "\\(이\\)가|\\(와\\)과|\\(을\\)를|\\(은\\)는|\\(아\\)야|\\(이\\)여|\\(으\\)로|\\(이\\)라|\\(이\\)에요";
	String JOSA_WITHSPACE = "\\s\\(이\\)가|\\s\\(와\\)과|\\s\\(을\\)를|\\s\\(은\\)는|\\s\\(아\\)야|\\s\\(이\\)여|\\s\\(으\\)로|\\s\\(이\\)라|\\s\\(이\\)에요";
	String JOSA_WITHNUM = "\\d\\(이\\)가|\\d\\(와\\)과|\\d\\(을\\)를|\\d\\(은\\)는|\\d\\(아\\)야|\\d\\(이\\)여|\\d\\(으\\)로|\\d\\(이\\)라|\\d\\(이\\)에요";
	String[][] NUM_PAIRED = new String[][] {{"0", "▥영"}, {"1", "▥일"}, {"2", "▥이"}, {"3", "▥삼"}, {"4", "▥사"}, {"5", "▥오"}, {"6", "▥육"}, {"7", "▥칠"}, {"8", "▥팔"}, {"9", "▥구"}};

	public void initJOSA_PARID(){
		this.JOSA_PARID.put("(이)가", new String[] {"이", "가"});
		this.JOSA_PARID.put("(와)과", new String[] {"과", "와"});
		this.JOSA_PARID.put("(을)를", new String[] {"을", "를"});
		this.JOSA_PARID.put("(은)는", new String[] {"은", "는"});
		this.JOSA_PARID.put("(으)로", new String[] {"으로", "로"});
		this.JOSA_PARID.put("(아)야", new String[] {"아", "야"});
		this.JOSA_PARID.put("(이)여", new String[] {"이여", "여"});
		this.JOSA_PARID.put("(이)라", new String[] {"이라", "라"});
		this.JOSA_PARID.put("(이)에요", new String[] {"이에요", "에요"});
		this.JOSA_PARID.put("(이)야", new String[] {"이야", "야"});
	}
	
	public String chooseJosa(char prev_char, String josa_key, String[] josa_pair) {
		int char_code = (int) prev_char;
		
		if(char_code < 0xac00 || char_code > 0xD7A3)
			return josa_pair[1];
		
		int local_code = char_code - 0xac00;
		int jong_code = local_code % 28;
		
		if (jong_code == 0)
			return josa_pair[1];
		
		if (josa_key == "(으)로") {
			if (jong_code == 8)
				return josa_pair[1];
		}
		
		return josa_pair[0];
	}
	
	public String removeSpace(String src) {
		Pattern pat = Pattern.compile(this.JOSA_WITHSPACE);
		Matcher match = pat.matcher(src);
		List<String> space_list = new ArrayList<String>();
		
		while(match.find()) {
			space_list.add(match.group());
		}
		
		if(space_list.size() == 0)
			return src;
		else {
			for(String word:space_list)
				src = src.replace(word, word.trim());
			return src;
		}
	}
	
	public String replaceNum2Kor(String src) {
		Pattern pat = Pattern.compile(this.JOSA_WITHNUM);
		Matcher match = pat.matcher(src);
		List<String> num_list = new ArrayList<String>();
		
		while(match.find()) {
			num_list.add(match.group());
		}
		
		if(num_list.size() == 0)
			return src;
		else {
			for(String word:num_list) {
				String replaced_word = word.replace(Character.toString(word.charAt(0)), this.NUM_PAIRED[Integer.parseInt(Character.toString(word.charAt(0)))][1]);
				src = src.replace(word, replaced_word);
			}
			return src;
		}
	}
	
	public String replaceKor2Num(String src) {
		for(String[] NumKor:this.NUM_PAIRED) {
			src = src.replace(NumKor[1], NumKor[0]);
		}
		return src;
	}
	
	public String replaceJosa(String src) {
		List<String> tokens = new ArrayList<String>();
		int base_idx = 0;
		
		Pattern pat = Pattern.compile(this.JOSA_REGEX);
		Matcher match = pat.matcher(src);
		
		while(match.find()) {
			String prev_token = src.substring(base_idx, match.start());
			char prev_char = prev_token.charAt(prev_token.length()-1);
			tokens.add(prev_token);
			
			String josa_key = match.group();
			tokens.add(this.chooseJosa(prev_char, josa_key, this.JOSA_PARID.get(josa_key)));
			
			base_idx = match.end();
		}
		tokens.add(src.substring(base_idx));
		
		String output = String.join("", tokens);
		return output;
	}
	
	public String getPostWord(String script){
		String output = script;
		this.initJOSA_PARID();
		output = this.removeSpace(output);
		output = this.replaceNum2Kor(output);
		output = this.replaceJosa(output);
		output = this.replaceKor2Num(output);
		
		return output;
	}
	
	public static void main(String[] args) {
		JavaJosa cls = new JavaJosa();
		String example = "5 (이)가 어떨까? 4(이)라능~ 수녀 (을)를 존경했어. 남자들 (을)를 입히다. 버튼(을)를 만지지 마.";
		System.out.println(cls.getPostWord(example));
	}
}
