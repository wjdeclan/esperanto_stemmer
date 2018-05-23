import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EsperantoStemmer {

	protected HashMap<String, String> stemmerRules;

	protected int maxSuffixLength;

	public EsperantoStemmer() {
		initStemmerRules();
		initMaxSuffixLength();
	}

	// Rules are sourced from https://en.wikipedia.org/wiki/Esperanto_grammar
	// Rules are of the form <Suffix, Removed Suffix>
	protected void initStemmerRules() {
		stemmerRules = new LinkedHashMap<String, String>();

		// Plural direct object suffixes
		stemmerRules.put("ojn", "ojn");
		stemmerRules.put("ajn", "ajn");

		// Direct object suffixes
		stemmerRules.put("on", "on");
		stemmerRules.put("an", "an");

		// Plural noun/adjective suffixes
		stemmerRules.put("oj", "oj");
		stemmerRules.put("aj", "aj");

		// Part of speech suffixes
		stemmerRules.put("o", "o");
		stemmerRules.put("a", "a");
		stemmerRules.put("e", "e");
		stemmerRules.put("i", "i");
	}

	protected void initMaxSuffixLength() {
		for (String suffix : stemmerRules.keySet()) {
			if (suffix.length() > maxSuffixLength)
				maxSuffixLength = suffix.length();
		}
	}

	public String stemWord(String word) {
		String suffix = word.toLowerCase();

		while (suffix.length() > maxSuffixLength || (!stemmerRules.containsKey(suffix) && !suffix.equals(""))) {
			suffix = suffix.substring(1);
		}

		if (suffix.equals("")) {
			return word;
		}

		return word.substring(0, word.length() - stemmerRules.get(suffix).length());
	}

	public String stemLine(String line) {
		// Esperanto uses the Latin Alphabet + ĉ, ĝ, ĥ, ĵ, ŝ, ŭ but does not use q, w,
		// x, y
		// Sourced from https://en.wikipedia.org/wiki/Esperanto_grammar
		Matcher matcher = Pattern.compile("\\b[a-zA-Zĉĝĥĵŝŭ&&[^qwxyQWXY]]\\b").matcher(line);
		StringBuffer sb = new StringBuffer();

		boolean hasWords = matcher.find();
		int startPos = 0;

		while (hasWords) {
			// The text from the last match to the current one
			sb.append(line.substring(startPos, matcher.start()));
			// The stemmed word
			sb.append(stemWord(line.substring(matcher.start(), matcher.end())));

			startPos = matcher.end();
			hasWords = matcher.find();
		}

		return sb.toString();
	}

	public String stemText(String text) {
		String[] lines = text.split("\n");
		StringBuffer sb = new StringBuffer();

		for (String line : lines) {
			sb.append(stemLine(line)).append("\n");
		}

		return sb.toString().trim();
	}

	public void stemFile(String inputFile, String outputFile) throws IOException {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
		PrintWriter outputWriter = new PrintWriter(outputFile, "UTF-8");

		String line = null;

		while ((line = inputReader.readLine()) != null) {
			String stemmedLine = stemLine(line);
			outputWriter.println(stemmedLine);
		}

		inputReader.close();

		outputWriter.flush();
		outputWriter.close();
	}

	private static void printUsage() {
		System.out.println("Correct usage:");
		System.out.println("java -jar EsperantoStemmer.jar inputFile outputFile");
		System.out.println("inputFile - path to the UTF-8 TXT file to be stemmed.");
		System.out.println("outputFile - path to the file to store the output in.");
		System.exit(0);
	}

	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				throw new Exception();
			}

			EsperantoStemmer stemmer = new EsperantoStemmer();

			stemmer.stemFile(args[0], args[1]);

			System.out.println("Stemming complete.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			printUsage();
		}
	}

}
