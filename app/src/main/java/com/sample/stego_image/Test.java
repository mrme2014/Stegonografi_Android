package com.sample.stego_image;

/**
 * Created by timian on 2018/11/15.
 */

public class Test {
    public static void main(String[] args) {

//        String s = "message消息中文字符串二进制流弊不";
//        //用StringBuffer来存储一长串的字符串转换成的比特流
//        StringBuffer sb = new StringBuffer();
//        String s1 = "";
//        //获得字符串的字节码数组
//        byte[] b = s.getBytes();
//        int j = 1;
//
//        for (int i = 0; i < b.length; i++) {
//            //将字节数组转换成二进制
//            s1 = Integer.toBinaryString(b[i] & 0xff);
//            if (s1.length() < 8) {
//                s1 = "0" + s1;
//            }
//            System.out.println(j + "-----" + s1 + "--LEN:" + s1.length());
//            sb.append(s1);
//            j++;
//        }
//        System.out.println("1000的二进制"+Integer.toBinaryString(10));
//        System.out.println(sb);
//        getString(sb);
//        System.out.println(0 & 0);
//        System.out.println(0 & 1);
//        System.out.println(Integer.toBinaryString(101 & 0b111));
//        System.out.println(Integer.toBinaryString(100000 & 0xff));
//        String bitBinary = transformIntTo32BitBinary(88);
//        getString(new StringBuffer(bitBinary));
        getString(null);
    }

    //用存储二进制字符的StringBuffer长生字符串
    public static void getString(StringBuffer sb) {
        //获得一串二进制字符
        //00000000000000000000000000111000
        //01101101011001010111001101110011011000010110011101100101
        //0110110101100101011100110111001101100001011001110110010
        StringBuffer buffer = new StringBuffer("0110110101100101011100110111001101100001011001110110010");
        //构造的字节数组长度仅为二进制字符流长度的1/8，否则空的字节数组部分会长生不必要的字符
        byte[] b = new byte[buffer.length() / 8];
        int j = 0, k = 0;
        //将二进制字符流七个一组地进行分割
        for (int i = 0; i < buffer.length(); i += 8) {
            j = i + 8;
            if (j > buffer.length())
                break;

            //用8个二进制字符构造一个字节并保存到字节数组中
            b[k++] = Integer.valueOf(buffer.substring(i, j), 2).byteValue();
            System.out.println(buffer.substring(i, j) + "--" + k);
        }
        //构造字符串并进行输出。
        System.out.println(new String(b));
    }


    private static String transformIntTo32BitBinary(int value) {
        char[] chs = new char[Integer.SIZE];
        for (int i = 0; i < Integer.SIZE; i++) {
            chs[Integer.SIZE - 1 - i] = (char) (((value >> i) & 1) + '0');
        }
        String s = new String(chs);
        System.out.println(s);
        System.out.println(Integer.toBinaryString(value));
        return s;
    }
}
