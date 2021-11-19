package inno;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.Integer.parseInt;


public class Main {
    private static final String INPUT_FILE = "src/inno/input.txt";
    private static final String OUTPUT_FILE = "src/inno/output.txt";
    final static String  SPACE_SYMBOL = " ";

    public static void main(String[] args) throws Exception {
        FileReader fileReader = new FileReader(INPUT_FILE);
        FileWriter fileWriter = new FileWriter(OUTPUT_FILE);

        String[] inputLines = fileReader.readLines();

        String textToSplit = inputLines[0];


        if (inputLines.length == 1){
            FileWriter.write("Exception, intended line width is not specified!");
            System.exit(0);
        }
        if (textToSplit.length() > 300){
            FileWriter.write("Exception, input exceeds text max size!");
            System.exit(0);
        }

        int maxLength = parseInt(inputLines[1]);

        if (maxLength <= 0){
            FileWriter.write("Exception, line width cannot be negative or zero!");
            System.exit(0);
        }

        ArrayList<String> words = new ArrayList<>();

        Collections.addAll(words, textToSplit.split(SPACE_SYMBOL));
        ArrayList<String> line = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            int lineWordsLength = 0;

            for (String w : line) {
                lineWordsLength += w.length(); //sum of lengths of the words in line
            }

            if (word.length() == 0) {
                FileWriter.write("Exception, input contains an empty word!");
                System.exit(0);
            }

            for (int i = 0; i < word.length(); i++){
                if(word.charAt(i) >= 'a' && word.charAt(i) <= 'z' || word.charAt(i) >= 'A' && word.charAt(i) <= 'Z' || word.charAt(i) == '.' || word.charAt(i) == ',' || word.charAt(i) == '!' || word.charAt(i) == '?' || word.charAt(i) == ':' || word.charAt(i) == ';' || word.charAt(i) == '-' || word.charAt(i) == '(' || word.charAt(i) == ')' || word.charAt(i) == '"' || word.charAt(i) == 39){
                    continue;
                } else {
                    FileWriter.write("Exception, input contains forbidden symbol '" + word.charAt(i) + "'!");
                    System.exit(0);
                }
            }

            try {
                if (word.length() > maxLength) {
                    throw new ExceedsMaxLengthException();
                }
            } catch (ExceedsMaxLengthException e) {
                FileWriter.write("Exception, '" + word + "' exceeds " + maxLength + " symbols!");
            }

            try {
                if (word.length() > 20) {
                    throw new ExceedsTwentySymException();
                }
            } catch (ExceedsTwentySymException e) {
                FileWriter.write("Exception, '" + word + "' exceeds the limit of 20 symbols!");
            }

            if (word.length() < (maxLength - lineWordsLength - line.size())) {
                line.add(word); //there's enough space for the word
            } else { //not enough space, so we begin to count spaces b/w previous ones
                int difference = maxLength - lineWordsLength;
                int numberOfSpaces = line.size() == 1 ? 0 : line.size() - 1;
                double buf = difference / numberOfSpaces;
                if (buf == 1){
                    buf = difference;
                }
                int spacesBetweenEachWord = numberOfSpaces == 0 ? 0 : (int) Math.ceil(buf);

                /* we entered else, that means, that current word couldn't be placed in the line
                and if the numberOfSpaces equals to zero -> we have only one word and should add /n */

                int j = 0;
                if (spacesBetweenEachWord == 0){
                    while (j != line.size() - 1){
                        j ++;
                    }
                    result.append(line.get(j)).append(String.join("", Collections.nCopies(difference, SPACE_SYMBOL)));
                    result.append("\n");
                    line = new ArrayList<>();
                }
                if (line.size() != 1){ //there's an algorithm for adding extra spaces b/w words (priority for the left side)


                    for (int i = 0; i < line.size(); i++) {
                        if (i == line.size() - 2) {
                            if (difference % 2 == 0){
                                result.append(line.get(i)).append(String.join("", Collections.nCopies(spacesBetweenEachWord, SPACE_SYMBOL)));
                            } else{
                                result.append(line.get(i)).append(String.join("", Collections.nCopies(spacesBetweenEachWord - 1, SPACE_SYMBOL)));
                            }
                        } else if (i == line.size() - 1){
                            result.append(line.get(i)).append("\n");
                        }
                        else if (i != line.size() - 2){
                            result.append(line.get(i)).append(String.join("", Collections.nCopies(spacesBetweenEachWord, SPACE_SYMBOL)));
                        }
                    }

                    line = new ArrayList<>();
                    line.add(word);
                } else if (line.size() == 1){
                    line.add(word);
                }
            }
        }
        int j = 0; //the last words in the line, that are not added to result yet (number of spaces b/w each word = 1)
        if (line.size() != 0) {
            while (j != line.size()){
                result.append(line.get(j));
                if (j != line.size() - 1){
                    result.append(" ");
                }
                j ++;
            }
        }

        fileWriter.write(result.toString());
    }
}

class FileReader {
    private final ArrayList<String> lines;
    private final String fileName;

    public FileReader(String fileName) {
        this.fileName = fileName;
        this.lines = new ArrayList<>();
    }

    public String[] readLines() {

        try{
            if (lines.isEmpty()){
                throw new IsFileEmptyException();
            }
        } catch (IsFileEmptyException e) {
            FileWriter.write("Exception, file is empty!");
        }

        try {
            File file = new File(this.fileName);
            Scanner sc;
            sc = new Scanner(file);
            while (sc.hasNextLine())
                this.lines.add(sc.nextLine());
        } catch (FileNotFoundException e) {
            FileWriter.write("Exception, file not found!");
        }

        return this.lines.toArray(new String[0]);
    }
}

class FileWriter {
    static String fileName;

    public FileWriter(String fileName) {
        this.fileName = fileName;
    }

    public static void write(String line) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(fileName, String.valueOf(StandardCharsets.UTF_8));

            writer.write(line);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class IsFileEmptyException extends Exception {
    final static String errorMessage = "Exception, file is empty!";
    public IsFileEmptyException() {
        super(errorMessage);
    }
}

class ExceedsMaxLengthException extends Exception {
    final static String errorMessage = "Exception, input exceeds text max size!";
    public ExceedsMaxLengthException() {
        super(errorMessage);
    }
}

class ExceedsTwentySymException extends Exception {
    final static String errorMessage = "Exception, input exceeds the limit of 20 symbols!";
    public ExceedsTwentySymException() {
        super(errorMessage);
    }
}