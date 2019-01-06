package main.models;

import java.nio.ByteBuffer;

public class LzwUtils {


    public static String convertBitsToBitString(byte[] byteBuffer, int size) {
        StringBuilder bitBufferStr = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            bitBufferStr.append(convertByteToBitString(byteBuffer[i]));
        }
        return bitBufferStr.toString();
    }

    public static String convertByteToBitString(byte b){
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static String convertBitsToBitString(byte byteBuffer) {
        return String.format("%8s", Integer.toBinaryString(byteBuffer & 0xFF)).replace(' ', '0');
    }

    public static int convertBitStringToValue(String encodedString, int currentNumberLen) {
        if (encodedString.length() < currentNumberLen) {
            return -1;
        }  // not perfect but will do, we don't expect big int numbers

        String temp = encodedString.substring(0, currentNumberLen);
        encodedString = encodedString.substring(currentNumberLen);
        return Integer.parseInt(temp, 2);
    }

    public static String getEncodedBits(int val, int dictionaryCurrentSize) {
        byte[] byteInt = ByteBuffer.allocate(4).putInt(val).array();
        String bitString = LzwUtils.convertBitsToBitString(byteInt, byteInt.length);
        return bitString.substring(bitString.length() - dictionaryCurrentSize);
    }

    /**
     * Enum class for diffenret dictionary modes
     */
    public enum DictionaryMode
    {
        Infinite(0),
        Clear(1),
        Continue(2);

        int value;
        DictionaryMode(int value)
        {
            this.value = value;
        }

        public short getValue(){return (short)value;}

        public static DictionaryMode fromDictionaryValue(int value)
        {
            for (DictionaryMode mode :
                    DictionaryMode.values()) {
                if (mode.getValue() == (short) value)
                    return mode;
            }
            return null;
        }
    }
}
