package il.ac.tau.cs.software1.bigram;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class BigramModel {
    public static final int MAX_VOCABULARY_SIZE = 14500;
    public static final String VOC_FILE_SUFFIX = ".voc";
    public static final String COUNTS_FILE_SUFFIX = ".counts";
    public static final String SOME_NUM = "some_num";
    public static final int ELEMENT_NOT_FOUND = -1;

    String[] mVocabulary;
    int[][] mBigramCounts;

    // DO NOT CHANGE THIS !!!
    public void initModel(String fileName) throws IOException {
        mVocabulary = buildVocabularyIndex(fileName);
        mBigramCounts = buildCountsArray(fileName, mVocabulary);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods - already implemented for you. Use them!
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Converts a single token (a whitespace-free piece of text from the file)
     * into the form in which it is stored in the vocabulary:
     *   - a token with at least one English letter  -> the token in lowercase
     *   - a token made only of digits (an integer)  -> SOME_NUM
     *   - anything else is not a legal word         -> null
     *
     * @pre: token != null
     */
    public static String normalizeWord(String token) {
        boolean hasLetter = false;
        boolean allDigits = !token.isEmpty();
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
            }
            if (c < '0' || c > '9') {
                allDigits = false;
            }
        }
        if (hasLetter) {
            return token.toLowerCase(Locale.ROOT);
        }
        if (allDigits) {
            return SOME_NUM;
        }
        return null;
    }

    /*
     * @pre: vocabulary != null
     * @post: $ret = the index of word in vocabulary, or ELEMENT_NOT_FOUND if
     * word is null or does not appear in it
     */
    public static int findIndex(String[] vocabulary, String word) {
        if (word == null) {
            return ELEMENT_NOT_FOUND;
        }
        for (int i = 0; i < vocabulary.length; i++) {
            if (word.equals(vocabulary[i])) {
                return i;
            }
        }
        return ELEMENT_NOT_FOUND;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Questions 1 - 5: implement these
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public String[] buildVocabularyIndex(String fileName) throws IOException { // Question 1
        String[] vocab = new String[MAX_VOCABULARY_SIZE];
        int vocab_index = 0;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line;
        String[] tokens;

        while ((line = bufferedReader.readLine()) != null && vocab_index < MAX_VOCABULARY_SIZE)
        {
            tokens = line.trim().split("\\s+");
            for (String token : tokens)
            {
                String normalizedToken = normalizeWord(token);
                if (normalizedToken != null && findIndex(vocab, normalizedToken) == ELEMENT_NOT_FOUND)
                {
                    if (vocab_index < MAX_VOCABULARY_SIZE)
                    {
                        vocab[vocab_index] = normalizedToken;
                        vocab_index += 1;
                    }
                }
            }
        }

        int i = 0;
        while (i < vocab.length && vocab[i] != null)
        {
            i++;
        }

        String[] finalVocab = new String[i];

        for (int j=0; j<i; j++)
        {
            finalVocab[j] = vocab[j];
        }

        bufferedReader.close();
        return finalVocab;
    }

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Question 2
        int[][] bigramCounts = new int[vocabulary.length][vocabulary.length];
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line;
        String[] tokens;

        while ((line = bufferedReader.readLine()) != null)
        {
            tokens = line.trim().split("\\s+");
            for (int i=0; i<tokens.length-1; i++)
            {
                String currentToken = tokens[i];
                String nextToken = tokens[i+1];
                String currentNormazliedToken = normalizeWord(currentToken);
                String nextNormazliedToken = normalizeWord(nextToken);
                int curIndex = findIndex(vocabulary, currentNormazliedToken);
                int nextIndex = findIndex(vocabulary, nextNormazliedToken);
                if (curIndex != ELEMENT_NOT_FOUND && nextIndex != ELEMENT_NOT_FOUND)
                {
                    bigramCounts[curIndex][nextIndex]++;
                }
            }
        }

        bufferedReader.close();
        return bigramCounts;
    }

    /*
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: fileName is a legal file path
     */
    public void saveModel(String fileName) throws IOException { // Question 3
        BufferedWriter vocBufferedWriter = new BufferedWriter(new FileWriter(fileName + VOC_FILE_SUFFIX));
        vocBufferedWriter.write(this.mVocabulary.length + " " + "words" + "\r\n");
        
        for (int i=0; i<this.mVocabulary.length; i++)
        {
            vocBufferedWriter.write(i + "," + this.mVocabulary[i] + "\r\n");
        }

        BufferedWriter countBufferedWriter = new BufferedWriter(new FileWriter(fileName + COUNTS_FILE_SUFFIX));

        for (int i=0; i<this.mBigramCounts.length; i++)
        {
            for (int j=0; j<this.mBigramCounts[0].length; j++)
            {
                if (this.mBigramCounts[i][j] != 0)
                {
                    countBufferedWriter.write(i + "," + j + ":" + this.mBigramCounts[i][j] + "\r\n");
                }
            }
        }

        vocBufferedWriter.close();
        countBufferedWriter.close();
    }

    /*
     * @pre: fileName is a legal file path
     */
    public void loadModel(String fileName) throws IOException { // Question 4
        BufferedReader vocBufferedReader = new BufferedReader(new FileReader(fileName + VOC_FILE_SUFFIX));
        String line;
        String[] tokens;

        tokens = vocBufferedReader.readLine().split(" ");
        int wordsCount = Integer.parseInt(tokens[0]);

        String[] vocabulary = new String[wordsCount];

        while ((line = vocBufferedReader.readLine()) != null)
        {
            tokens = line.split(",");
            vocabulary[Integer.parseInt(tokens[0])] = tokens[1];
        }

        BufferedReader bigramBufferedReader = new BufferedReader(new FileReader(fileName + COUNTS_FILE_SUFFIX));
        int[][] bigramCounts = new int[wordsCount][wordsCount];
        String[] indices;

        while ((line = bigramBufferedReader.readLine()) != null)
        {
            tokens = line.split(":");
            indices = tokens[0].split(",");
            bigramCounts[Integer.parseInt(indices[0])][Integer.parseInt(indices[1])] = Integer.parseInt(tokens[1]);
        }

        this.mVocabulary = vocabulary;
        this.mBigramCounts = bigramCounts;

        vocBufferedReader.close();
        bigramBufferedReader.close();
    }

    /*
     * @pre: sentence is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: each two words in the sentence are separated with a single space
     * @post: if sentence is probable, according to the model, $ret = true, else, $ret = false
     */
    public boolean isLegalSentence(String sentence) { // Question 5
        String[] tokens = sentence.split(" ");
        if (tokens.length == 0)
        {
            return true;
        }
        if (tokens.length == 1 && findIndex(this.mVocabulary, tokens[0]) != ELEMENT_NOT_FOUND)
        {
            return true;
        }
        
        for (int i=0; i<tokens.length-1; i++)
        {
            String currentToken = tokens[i];
            String nextToken = tokens[i+1];
            int currentTokenIndex = findIndex(this.mVocabulary, currentToken);
            int nextTokenIndex = findIndex(this.mVocabulary, nextToken);
            if (currentTokenIndex == ELEMENT_NOT_FOUND || nextTokenIndex == ELEMENT_NOT_FOUND)
            {
                return false;
            }
            if (this.mBigramCounts[currentTokenIndex][nextTokenIndex] == 0)
            {
                return false;
            }
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queries on the model - already implemented for you.
    // They start working once Questions 1 - 2 are done.
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @pre: word is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
     */
    public int getWordIndex(String word) {
        return findIndex(mVocabulary, word);
    }

    /*
     * @pre: word1, word2 are in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
     * exist in the vocabulary, $ret = 0
     */
    public int getBigramCount(String word1, String word2) {
        int index1 = getWordIndex(word1);
        int index2 = getWordIndex(word2);
        if (index1 == ELEMENT_NOT_FOUND || index2 == ELEMENT_NOT_FOUND) {
            return 0;
        }
        return mBigramCounts[index1][index2];
    }

    /*
     * @pre: word is in lowercase, and is in mVocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the word with the lowest vocabulary index that appears most frequently after word
     * (if a bigram starting with word was never seen, $ret will be null)
     */
    public String getMostFrequentProceeding(String word) {
        int[] row = mBigramCounts[getWordIndex(word)];
        int best = ELEMENT_NOT_FOUND;
        for (int i = 0; i < row.length; i++) {
            if (row[i] > 0 && (best == ELEMENT_NOT_FOUND || row[i] > row[best])) {
                best = i;
            }
        }
        return best == ELEMENT_NOT_FOUND ? null : mVocabulary[best];
    }

    /*
     * @pre: arr1.length = arr2.length
     * @post: if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calculates CosineSim
     */
    public static double calcCosineSim(int[] arr1, int[] arr2) {
        double dot = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            dot += (double) arr1[i] * arr2[i];
            norm1 += (double) arr1[i] * arr1[i];
            norm2 += (double) arr2[i] * arr2[i];
        }
        if (norm1 == 0 || norm2 == 0) {
            return -1;
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /*
     * @pre: word is in vocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w)
     * among all the other words in vocabulary (the lowest index wins a tie)
     */
    public String getClosestWord(String word) {
        int wordIndex = getWordIndex(word);
        int best = ELEMENT_NOT_FOUND;
        double bestSim = 0;
        for (int i = 0; i < mVocabulary.length; i++) {
            if (i == wordIndex) {
                continue;
            }
            double sim = calcCosineSim(mBigramCounts[wordIndex], mBigramCounts[i]);
            if (best == ELEMENT_NOT_FOUND || sim > bestSim) {
                best = i;
                bestSim = sim;
            }
        }
        // A vocabulary with a single word: the closest word is the word itself
        return best == ELEMENT_NOT_FOUND ? word : mVocabulary[best];
    }
}
