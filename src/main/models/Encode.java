package main.models;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Encode {

    private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
    private int maxNumberLen;
    private int maxDictSize;
    private int currentNumberLen;
    private String sequence = "";
    private String encodedString = "";
    //private LzwUtils.DictionaryMode dictionaryMode = ;   // 0 - grow infinite, 1 - clear, 2 - full and keep using full
    private boolean fullAndDontAddIt = false;
    //Variables for UI
    private File inputFile;
    private File outputFile;


    public Encode(String file, int maxNumberLen){
        this.inputFile = new File(file);
        this.outputFile = new File(inputFile.getName() + ".Encoded");
        //this.dictionaryMode = dictionaryMode;
            this.maxNumberLen = maxNumberLen;

        maxDictSize = (int) Math.pow(2.0, (float) this.maxNumberLen);
        currentNumberLen = maxNumberLen == 8 ? 8 : 9;
    }


    public void resetDefaults() {
        dictionary = new HashMap<>();
        sequence = "";
        fullAndDontAddIt = false;
        encodedString = "";
        maxDictSize = (int) Math.pow(2.0, (float) maxNumberLen);
        currentNumberLen = maxNumberLen == 8 ? 8 : 9;
    }

    public void encode() {
        resetDefaults();
        initDictionary();
        try (FileInputStream fs = new FileInputStream(inputFile)) {
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile));
            //save dictionary mode since we will need it while decoding

            byte metaData = (byte)(/*dictionaryMode.getValue()*/2<<6);
            metaData |= maxNumberLen;
            outputStream.writeByte(metaData);

            int readByte = 0;
            while (-1 != (readByte = fs.read())) {
                String currentByte = String.valueOf((char) readByte);
                checkDictionary();
                doMagicWithDictionary(currentByte);
                writeDataToOutput(outputStream);
            }

            byte[] byteInt = intToBytes(dictionary.get(sequence));
            String temp = convertBitsToBitString(byteInt, byteInt.length);
            encodedString += temp.substring(temp.length() - currentNumberLen);
            writeDataToOutput(outputStream);

            if (!encodedString.isEmpty()) {
                //fill with zeros, decoder will ignore it since it is a remainder
                while (encodedString.length() != 8)
                    encodedString += "0";
                writeDataToOutput(outputStream);
            }
            outputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDictionary() {
        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }
    }

    private void checkDictionary() {
        //check whether max size reached
        if (dictionary.size() == maxDictSize - 1) {

//            if (dictionaryMode == LzwUtils.DictionaryMode.Infinite) {
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
        if (dictionary.size() == Math.pow(2,currentNumberLen)&& currentNumberLen < maxNumberLen){
            currentNumberLen++;
        }

    }

    private void doMagicWithDictionary(String currentByte) {

        sequence += currentByte;

        // If this sequence is not in the dictionary.
        if (!dictionary.containsKey(sequence)) {
            // Add it.
            if(!fullAndDontAddIt&& dictionary.size() < maxDictSize)
                dictionary.put(sequence, dictionary.size() + 1);

            // Remove the last byte from the sequence.
            sequence = sequence.substring(0, sequence.length() - 1);

            // convert value of dictionary to compressed bits and add it to result String
            byte[] byteInt = intToBytes(dictionary.get(sequence));
            String temp = convertBitsToBitString(byteInt, byteInt.length);
            encodedString += temp.substring(temp.length() - currentNumberLen);

            // Start the sequence afresh with the new byte string.
            sequence = currentByte;
        }
    }

    private void writeDataToOutput(DataOutputStream outputStream) throws IOException {
        // convert encoded string symbols to bytes and write them to stream
        for (int i = 0; i < encodedString.length(); i += 8) {
            if (encodedString.length() < i + 8) {
                encodedString = encodedString.substring(i);
                return;
            }
            String bitString = encodedString.substring(i, i + 8);
            byte b = (byte) Integer.parseInt(bitString, 2);
            outputStream.writeByte(b);
        }
        encodedString = "";
    }

    private byte[] intToBytes(int val) {
        return ByteBuffer.allocate(4).putInt(val).array();
    }

    private String convertBitsToBitString(byte[] byteBuffer, int size) {
        String bitBufferStr = "";
        for (int i = 0; i < size; ++i) {
            bitBufferStr += String.format("%8s", Integer.toBinaryString(byteBuffer[i] & 0xFF)).replace(' ', '0');
        }
        return bitBufferStr;
    }

    public long getInputFileSize(){return inputFile.length();}
    public long getOutputFilesize(){return outputFile.length();}
    public void deleteOutputFile() {outputFile.delete();}

}
