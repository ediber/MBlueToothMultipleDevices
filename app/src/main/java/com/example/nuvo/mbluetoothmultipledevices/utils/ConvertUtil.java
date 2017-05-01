package com.example.nuvo.mbluetoothmultipledevices.utils;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtil {

    private static ArrayList<String> mBinaries;

    public ConvertUtil() {
        generateTableOfAllBinaries();
    }

    public static Byte intToHexByte(int num) {
        String ans = Integer.toHexString(num);
        return Byte.parseByte(ans);
    }

    private static String intToBinaryString(int num) {
        String ans = Integer.toBinaryString(num);
        for (int i = ans.length(); i < 8; i++) {
            ans = "0" + ans;
        }
        return ans;
    }

    public static String decimalToHexString(List<Integer> lst) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[lst.size() * 3];
        for (int j = 0; j < lst.size(); j++) {
            int v = lst.get(j);
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public static byte[] ByteListToArray(ArrayList<Byte> list) {
        Byte[] objects = list.toArray(new Byte[list.size()]);
        byte[] bytes = new byte[objects.length];
        int i = 0;
        for (Byte b : objects) {
            bytes[i++] = b.byteValue();
        }
        return bytes;
    }


    private void generateTableOfAllBinaries() {
        mBinaries = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            String binary = intToBinaryString(i);
            mBinaries.add(binary);
        }
    }


    public static String decimalToBinary(int dec) {
        return mBinaries.get(dec);
    }


    public static List<Integer> bytesToPositive(byte[] bytes) {
        List<Integer> positive = new ArrayList<>();
        for (int i = 0; i < bytes.length - 1; i++) {
            positive.add(bytes[i] & 0xFF);
        }
        return positive;
    }
}
