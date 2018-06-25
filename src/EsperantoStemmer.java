
/*
 *	Copyright (C) 2018 Declan Whitford Jones
 *
 *	Liscensed under GPL 3
 *
 *	Structure copied from Vuk Batanović's SCStemmer
 *	https://github.com/vukbatanovic/SCStemmers/tree/master/src/weka/core/stemmers
 *
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EsperantoStemmer {

	protected HashMap<String, String> stemmerRules;
	protected HashSet<String> pluralDirectChecks;
	protected HashSet<String> stemmerExceptions;
	protected HashSet<String> basicNumerals;

	protected int maxSuffixLength;
	protected int minStemLength = 2;

	private final Set<Character> vowels = new HashSet<Character>(
			Arrays.asList(new Character[] { 'a', 'e', 'i', 'o', 'u' }));

	public EsperantoStemmer() {
		initStemmerRules();
		initMaxSuffixLength();
		initPluralDirectChecks();
		initStemmerExceptions();
		initNumerals();
	}

	// Rules are sourced from https://en.wikipedia.org/wiki/Esperanto_grammar
	// Rules are of the form <Suffix, Removed Suffix>
	protected void initStemmerRules() {
		stemmerRules = new LinkedHashMap<String, String>();

		// Part of speech suffixes
		stemmerRules.put("o", "o");
		stemmerRules.put("a", "a");
		stemmerRules.put("e", "e");
		stemmerRules.put("i", "i");

		// Verb conjugations
		// Mood
		stemmerRules.put("u", "u");
		stemmerRules.put("us", "us");
		// Indicative
		stemmerRules.put("is", "is");
		stemmerRules.put("as", "as");
		stemmerRules.put("os", "os");
		// Voice
		stemmerRules.put("inta", "inta");
		stemmerRules.put("anta", "anta");
		stemmerRules.put("onta", "onta");
		stemmerRules.put("ita", "ita");
		stemmerRules.put("ata", "ata");
		stemmerRules.put("ota", "ota");
		// Compound Tense
		stemmerRules.put("intas", "intas");
		stemmerRules.put("antas", "antas");
		stemmerRules.put("ontas", "ontas");
		stemmerRules.put("itas", "itas");
		stemmerRules.put("atas", "atas");
		stemmerRules.put("otas", "otas");
		stemmerRules.put("intis", "intis");
		stemmerRules.put("antis", "antis");
		stemmerRules.put("ontis", "ontis");
		stemmerRules.put("itis", "itis");
		stemmerRules.put("atis", "atis");
		stemmerRules.put("otis", "otis");
		stemmerRules.put("intos", "intos");
		stemmerRules.put("antos", "antos");
		stemmerRules.put("ontos", "ontos");
		stemmerRules.put("itos", "itos");
		stemmerRules.put("atos", "atos");
		stemmerRules.put("otos", "otos");
		stemmerRules.put("intus", "intus");
		stemmerRules.put("antus", "antus");
		stemmerRules.put("ontus", "ontus");
		stemmerRules.put("itus", "itus");
		stemmerRules.put("atus", "atus");
		stemmerRules.put("otus", "otus");
		// Nominal participles
		stemmerRules.put("inte", "inte");
		stemmerRules.put("ante", "ante");
		stemmerRules.put("onte", "onte");
		stemmerRules.put("ite", "ite");
		stemmerRules.put("ate", "ate");
		stemmerRules.put("ote", "ote");
		stemmerRules.put("into", "into");
		stemmerRules.put("anto", "anto");
		stemmerRules.put("onto", "onto");
		stemmerRules.put("ito", "ito");
		stemmerRules.put("ato", "ato");
		stemmerRules.put("oto", "oto");
		// -o, -oj, -on, -ojn
		stemmerRules.put("-o", "-o");
		stemmerRules.put("-a", "-a");
		stemmerRules.put("-e", "-e");
		stemmerRules.put("-", "-");
	}

	protected void initMaxSuffixLength() {
		for (String suffix : stemmerRules.keySet()) {
			if (suffix.length() > maxSuffixLength)
				maxSuffixLength = suffix.length();
		}
	}

	protected void initPluralDirectChecks() {
		pluralDirectChecks = new HashSet<String>();

		pluralDirectChecks.add("ci");
		pluralDirectChecks.add("ĝi");
		pluralDirectChecks.add("gi");
		pluralDirectChecks.add("iŝi");
		pluralDirectChecks.add("li");
		pluralDirectChecks.add("mi");
		pluralDirectChecks.add("ni");
		pluralDirectChecks.add("ri");
		pluralDirectChecks.add("ŝi");
		pluralDirectChecks.add("si");
		pluralDirectChecks.add("ŝli");
		pluralDirectChecks.add("vi");

		pluralDirectChecks.add("ia");
		pluralDirectChecks.add("io");
		pluralDirectChecks.add("iu");
	}

	protected void initStemmerExceptions() {
		stemmerExceptions = new HashSet<String>();

		// The article
		stemmerExceptions.add("la");
		// Conjunctions
		stemmerExceptions.add("kaj");
		stemmerExceptions.add("ke");
		stemmerExceptions.add("kie");
		stemmerExceptions.add("minus");
		stemmerExceptions.add("plus");
		stemmerExceptions.add("se");
		// Interjections
		stemmerExceptions.add("aha");
		stemmerExceptions.add("bis");
		stemmerExceptions.add("damne");
		stemmerExceptions.add("dirlididi");
		stemmerExceptions.add("fi");
		stemmerExceptions.add("forfikiĝu");
		stemmerExceptions.add("ha");
		stemmerExceptions.add("ho");
		stemmerExceptions.add("hola");
		stemmerExceptions.add("hu");
		stemmerExceptions.add("hura");
		stemmerExceptions.add("muu");
		stemmerExceptions.add("nedankinde");
		stemmerExceptions.add("nu");
		stemmerExceptions.add("oho");
		stemmerExceptions.add("ve");
		// Pronouns
		stemmerExceptions.add("aliu");
		stemmerExceptions.add("ĉio");
		stemmerExceptions.add("ĉiu");
		stemmerExceptions.add("ili");
		stemmerExceptions.add("io");
		stemmerExceptions.add("iŝi");
		stemmerExceptions.add("iu");
		stemmerExceptions.add("kio");
		stemmerExceptions.add("kiu");
		stemmerExceptions.add("nenio");
		stemmerExceptions.add("neniu");
		stemmerExceptions.add("oni");
		stemmerExceptions.add("tio");
		stemmerExceptions.add("tiu");
		// Determiners
		stemmerExceptions.add("ĉies");
		stemmerExceptions.add("ia");
		stemmerExceptions.add("kelka");
		stemmerExceptions.add("kia");
		stemmerExceptions.add("nenia");
		stemmerExceptions.add("tia");
		stemmerExceptions.add("tie");
		// Prepositions
		stemmerExceptions.add("cis");
		stemmerExceptions.add("ĉe");
		stemmerExceptions.add("da");
		stemmerExceptions.add("de");
		stemmerExceptions.add("disde");
		stemmerExceptions.add("ekde");
		stemmerExceptions.add("en");
		stemmerExceptions.add("ĝis");
		stemmerExceptions.add("je");
		stemmerExceptions.add("kun");
		stemmerExceptions.add("na");
		stemmerExceptions.add("po");
		stemmerExceptions.add("pri");
		stemmerExceptions.add("pro");
		stemmerExceptions.add("sen");
		stemmerExceptions.add("tra");
		// Adverbs
		stemmerExceptions.add("malplej");
		stemmerExceptions.add("malpli");
		stemmerExceptions.add("plej");
		stemmerExceptions.add("pli");
		stemmerExceptions.add("plu");
		stemmerExceptions.add("tamen");
		// Particles
		stemmerExceptions.add("ajn");
		stemmerExceptions.add("ĉu");
		stemmerExceptions.add("ĉi");
		stemmerExceptions.add("jen");
		stemmerExceptions.add("ju");
		stemmerExceptions.add("ne");
		// Alternate Numerals in the range 11-20
		stemmerExceptions.add("dekdu");
		stemmerExceptions.add("dekkvin");
		stemmerExceptions.add("dektri");
		stemmerExceptions.add("dekunu");
		// Dates
		stemmerExceptions.add("a");
		stemmerExceptions.add("an");
	}

	protected void initNumerals() {
		basicNumerals = new HashSet<String>();

		basicNumerals.add("unu");
		basicNumerals.add("du");
		basicNumerals.add("tri");
		basicNumerals.add("kvar");
		basicNumerals.add("kvin");
		basicNumerals.add("ses");
		basicNumerals.add("sep");
		basicNumerals.add("ok");
		basicNumerals.add("naŭ");
	}

	private int firstVowelPos(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (vowels.contains(word.charAt(i))) {
				return i;
			}
		}

		return -1;
	}

	public String stemWord(String word) {
		word = word.toLowerCase();
		String suffix = word;

		int firstVowel = firstVowelPos(suffix);
		int localMinStemLength = (minStemLength > (firstVowel + 1)) ? minStemLength : (firstVowel + 1);

		int numeralIndex = -1;

		// Check if it is an exception to stemming
		if (stemmerExceptions.contains(suffix) || basicNumerals.contains(suffix)) {
			return word;
		} else if ((numeralIndex = suffix.indexOf("meg")) != -1 || (numeralIndex = suffix.indexOf("cent")) != -1
				|| (numeralIndex = suffix.indexOf("dek")) != -1) {
			// Filters constructed numerals on the order of thousands/hundreds/tens
			// Numbers of a million and larger are treated as nouns
			// https://en.wiktionary.org/wiki/miliono
			if (basicNumerals.contains(suffix.substring(0, numeralIndex))) {
				return word;
			}
		}

		int pluralDirectOffset = 0;

		// Direct object suffix
		if (suffix.endsWith("n")) {
			pluralDirectOffset += 1;
			suffix = suffix.substring(0, suffix.length() - 1);
		}

		// Pluralization suffix
		if (suffix.endsWith("j")) {
			pluralDirectOffset += 1;
			suffix = suffix.substring(0, suffix.length() - 1);
		}

		if (pluralDirectOffset > 0
				&& (pluralDirectChecks.contains(word.substring(0, word.length() - pluralDirectOffset))
						|| stemmerExceptions.contains(word.substring(0, word.length() - pluralDirectOffset)))) {
			return word.substring(0, word.length() - pluralDirectOffset);
		}

		// All others
		while (suffix.length() > maxSuffixLength || (!stemmerRules.containsKey(suffix) && !suffix.equals(""))
				|| (stemmerRules.containsKey(suffix)
						&& (word.length() - stemmerRules.get(suffix).length() - pluralDirectOffset) < localMinStemLength
						&& suffix.charAt(0) != '-')) {
			suffix = suffix.substring(1);
		}

		int stemLength = 0;

		if (!suffix.equals("")) {
			stemLength = word.length() - stemmerRules.get(suffix).length() - pluralDirectOffset;
			if (suffix.charAt(0) == '-' && suffix.length() + pluralDirectOffset < word.length()) {
				return word.substring(0, stemLength);
			}
		}

		if (stemLength < localMinStemLength) {
			return word;
		}

		return word.substring(0, stemLength);
	}

	public String stemLine(String line) {
		// Esperanto uses the Latin Alphabet + ĉ, ĝ, ĥ, ĵ, ŝ, ŭ but does not use q, w,
		// x, y
		// Sourced from https://en.wikipedia.org/wiki/Esperanto_grammar
		Matcher matcher = Pattern.compile("\\b[0-9a-zA-ZĉĝĥĵŝŭĈĜĤĴŜŬ[-]&&[^qwxyQWXY]]+\\b").matcher(line);
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

		sb.append(line.substring(startPos));

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
