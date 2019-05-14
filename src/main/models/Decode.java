package main.models;

import main.models.LzwUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

public class Decode {
    private HashMap<Integer, String> dictionary = new HashMap<>();
    private int maxNumberLen;
    private int maxDictSize;
    private String sequence = "";
    private String encodedString = "";
    private int currentNumberLen;
    private LzwUtils.DictionaryMode dictionaryMode;   // 0 - grow infinite, 1 - clear, 2 - full and keep using full
    private boolean fullAndDontAddIt = false;

    private File inputFile;
    private File outputFile;
    public Decode() {

    }

    public void resetDefaults(){
        dictionary = new HashMap<>();
        sequence = "";
        encodedString = "";
        fullAndDontAddIt = false;
    }

    public void decode(String fileToDecode, String outputFileUrl) throws URISyntaxException {
        this.inputFile = new File(fileToDecode);
        this.outputFile = new File(outputFileUrl);

        resetDefaults();
        initDictionary();
        try (FileInputStream fs = new FileInputStream(inputFile)) {
            FileOutputStream out = new FileOutputStream(outputFile);
            DataOutputStream outputStream = new DataOutputStream(out);

            int readByte = fs.read();
            getMetaDataFromFirstByte(readByte);

            while (-1 != (readByte = fs.read())) {
                encodedString += convertBitsToBitString((byte) readByte);
                //System.out.println("encodedString: " + encodedString);
                if (encodedString.length() < currentNumberLen)
                    continue;

                checkDictionary();
                doUnMagicWithDictionary (convertBitStringToValue(), outputStream);
            }
      outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initDictionary() {
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }
    }

    private void getMetaDataFromFirstByte (int firstByte){
        dictionaryMode = LzwUtils.DictionaryMode.fromDictionaryValue((firstByte & 0xC0)>>6);
        maxNumberLen = firstByte & 0x3F; // if infinite mode, it should be 63 written in
        currentNumberLen = maxNumberLen == 8 ? 8 : 9;
        maxDictSize =(int) Math.pow(2.0,(float) maxNumberLen);
    }

    private void checkDictionary() {
        if (dictionary.size() == maxDictSize - 1) {
//
//            if (dictionaryMode == LzwUtils.DictionaryMode.Infinite) {
//                //increase size and word len
//                maxNumberLen++;
//                maxDictSize =(int) Math.pow(2.0,(float) this.maxNumberLen);
//            } else if (dictionaryMode == LzwUtils.DictionaryMode.Clear) {
//                dictionary = new HashMap<>();
//                initDictionary();
//            } else if (dictionaryMode == LzwUtils.DictionaryMode.Continue) {
                fullAndDontAddIt = true;
            //}
        }
        //check whether we need to increase code word lenght
        if (dictionary.size() == Math.pow(2,currentNumberLen) -1 && currentNumberLen < maxNumberLen){
            currentNumberLen++;
        }
    }

    private void doUnMagicWithDictionary (Integer currentCode, DataOutputStream outputStream) throws IOException {
        // Decompress data, reconstructing dictionary.
        //System.out.println(currentCode);
        if(!fullAndDontAddIt && dictionary.size() < maxDictSize) {
            //System.out.println("dictionary.size(): " + dictionary.size());
            //System.out.println("before**********" + sequence);
            if (currentCode == dictionary.size() + 1) {
                //System.out.println("--------------");
                //System.out.println(sequence + sequence.substring(0, 1));
                //extra case handling
                dictionary.put(dictionary.size() + 1, sequence + sequence.substring(0, 1));
            } else if (sequence.length() != 0) {
                //System.out.println("***********************else" + dictionary.get(currentCode));
                dictionary.put(dictionary.size() + 1, sequence + dictionary.get(currentCode).substring(0, 1));
                //System.out.println(sequence + dictionary.get(currentCode).substring(0, 1));
            }
        }

        // Write the code for the last sequence that was present to output.
        outputStream.writeBytes(dictionary.get(currentCode));

        // Start the sequence afresh with the new byte string.
        sequence = dictionary.get(currentCode);
        //System.out.println("sequence: " + sequence);
        //System.out.println("");
    }

    private int convertBitStringToValue() {
        if (encodedString.length() < currentNumberLen) {
            return -1;
        }  // not perfect but will do, we don't expect big int numbers

        String temp = encodedString.substring(0, currentNumberLen);
        encodedString = encodedString.substring(currentNumberLen);
        //System.out.println("INT: "+Integer.parseInt(temp, 2));
        return Integer.parseInt(temp, 2);
    }

    private String convertBitsToBitString(byte byteBuffer) {
        return String.format("%8s", Integer.toBinaryString(byteBuffer & 0xFF)).replace(' ', '0');
    }
    public long getInputFileSize(){return inputFile.length();}
    public long getOutputFilesize(){return outputFile.length();}
}
