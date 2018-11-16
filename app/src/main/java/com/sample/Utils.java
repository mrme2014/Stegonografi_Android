package com.sample;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by timian on 2018/11/16.
 */

public class Utils {
    private static final String TAG = "Utils";

    //字符串转二进制
    private static String str2Binary(String text) {
        if (TextUtils.isEmpty(text))
            return null;
        StringBuilder builder = new StringBuilder();
        byte[] bytes = text.getBytes();
        for (byte b : bytes) {
            String binaryString = Integer.toBinaryString(b & 0xff);
            if (binaryString.length() < 8) {
                binaryString = "0" + binaryString;
            }
            builder.append(binaryString);
        }
        return builder.toString();
    }

    private static String generateString(String sb) {
        byte[] b = new byte[sb.length() / 8];
        int j = 0, k = 0;
        for (int i = 0; i < sb.length(); i += 8) {
            j = i + 8;
            if (j > sb.length())
                break;
            System.out.println(sb.substring(i, j));
            b[k++] = Integer.valueOf(sb.substring(i, j), 2).byteValue();
        }
        System.out.println(new String(b));
        return new String(b);
    }

    private static void setBitmapPixel(Bitmap bitmap, int i, int j, int alpha, int red, int green, int blue) {
        int rgba = (alpha << 24) | (red << 16) | (green << 8) | (blue);
        bitmap.setPixel(j, i, rgba);
    }

    //把int值转换成32位的二进制
    private static String transformIntTo32BitBinary(int value) {
        char[] chs = new char[Integer.SIZE];
        for (int i = 0; i < Integer.SIZE; i++) {
            chs[Integer.SIZE - 1 - i] = (char) (((value >> i) & 1) + '0');
        }
        return new String(chs);
    }

    public static Bitmap insertMessage(Bitmap bitmap, String text) {
        String binaryString = str2Binary(text);
        if (TextUtils.isEmpty(binaryString))
            return null;

        Bitmap mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        int bitWidth = mutable.getWidth();
        int bitHeight = mutable.getHeight();
        String actualBinaryString = transformIntTo32BitBinary(binaryString.length()) + binaryString;
        int actualBinLen = actualBinaryString.length();

        //最低位有效
        if (bitWidth * bitHeight * 3 < actualBinLen)
            return mutable;

        int requirePixel = actualBinLen;
        char[] chars = actualBinaryString.toCharArray();
        int curPixelIndex = 0;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bitWidth; i++) {
            for (int j = 0; j < bitHeight; j++) {
                int pixel = mutable.getPixel(i, j);
                int alpha = (pixel >> 24) & 0xff;
                int redPixel = (pixel >> 16) & 0xff;
                int greenPixel = (pixel >> 8) & 0xff;
                int bluePixel = (pixel) & 0xff;

                String redBinary = Integer.toBinaryString(redPixel);
                String greenBinary = Integer.toBinaryString(greenPixel);
                String blueBinary = Integer.toBinaryString(bluePixel);

                int index = curPixelIndex == 0 ? 0 : curPixelIndex++;
                if (curPixelIndex >= requirePixel) {
                    setBitmapPixel(mutable, i, j, alpha, redPixel, greenPixel, bluePixel);
                    break;
                }
                char rchar = chars[index];
                sb.append(rchar);
                char[] redChars = redBinary.toCharArray();
                redChars[redChars.length - 1] = rchar;
                redBinary = new String(redChars);
                redPixel = Integer.valueOf(redBinary, 2);


                curPixelIndex++;
                if (curPixelIndex >= requirePixel) {
                    setBitmapPixel(mutable, i, j, alpha, redPixel, greenPixel, bluePixel);
                    break;
                }
                char gchar = chars[curPixelIndex];
                sb.append(gchar);
                char[] greenChars = greenBinary.toCharArray();
                greenChars[greenChars.length - 1] = gchar;
                greenBinary = new String(greenChars);
                greenPixel = Integer.valueOf(greenBinary, 2);


                curPixelIndex++;
                if (curPixelIndex >= requirePixel) {
                    setBitmapPixel(mutable, i, j, alpha, redPixel, greenPixel, bluePixel);
                    break;
                }
                char bchar = chars[curPixelIndex];
                sb.append(bchar);
                char[] blueChars = blueBinary.toCharArray();
                blueChars[blueChars.length - 1] = bchar;
                blueBinary = new String(blueChars);
                bluePixel = Integer.valueOf(blueBinary, 2);
                setBitmapPixel(mutable, i, j, alpha, redPixel, greenPixel, bluePixel);
            }
        }

        Log.e(TAG, "insertMessage: " + sb.toString());
        return mutable;
    }


    public static String extractMessage(Bitmap bi) {

        int bitWidth = bi.getWidth();
        int bitHeight = bi.getHeight();

        if (bitWidth * bitHeight * 3 < Integer.SIZE)
            return null;

        int curIndex = 0;
        int requirePixel = 0;
        String requirePixelStr = "";
        String extractedBinStr = "";
        for (int i = 0; i < bitWidth; i++) {
            for (int j = 0; j < bitHeight; j++) {
                int pixel = bi.getPixel(j, i);

                int redPixel = (pixel >> 16) & 0xff;
                String redBinStr = Integer.toBinaryString(redPixel);
                String redLastBin = redBinStr.substring(redBinStr.length() - 1, redBinStr.length());
                curIndex++;
                if (curIndex <= Integer.SIZE) {
                    requirePixelStr += redLastBin;
                    if (curIndex == Integer.SIZE) {
                        requirePixel = Integer.valueOf(requirePixelStr, 2);
                    }
                } else {
                    extractedBinStr += redLastBin;
                    if (curIndex > requirePixel) {
                        break;
                    }
                }

                int greenPixel = (pixel >> 8) & 0xff;
                String greenBinStr = Integer.toBinaryString(greenPixel);
                String greenLastBin = greenBinStr.substring(greenBinStr.length() - 1, greenBinStr.length());
                curIndex++;
                if (curIndex <= Integer.SIZE) {
                    requirePixelStr += greenLastBin;
                    if (curIndex == Integer.SIZE) {
                        requirePixel = Integer.valueOf(requirePixelStr, 2);
                    }
                } else {
                    extractedBinStr += redLastBin;
                    if (curIndex > requirePixel) {
                        break;
                    }
                }

                int bluePixel = (pixel) & 0xff;
                String blueBinStr = Integer.toBinaryString(bluePixel);
                String blueLastBin = blueBinStr.substring(blueBinStr.length() - 1, blueBinStr.length());
                curIndex++;
                if (curIndex <= Integer.SIZE) {
                    requirePixelStr += blueLastBin;
                    if (curIndex == Integer.SIZE) {
                        requirePixel = Integer.valueOf(requirePixelStr, 2);
                    }
                } else {
                    extractedBinStr += redLastBin;
                    if (curIndex > requirePixel) {
                        break;
                    }
                }
            }
        }

        String extractedText = generateString(extractedBinStr);
        return extractedText;
    }
}
