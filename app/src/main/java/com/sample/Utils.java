package com.sample;

import android.graphics.Bitmap;
import android.graphics.Color;
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
        int rgba = (255) | (red << 16) | (green << 8) | (blue);
        Log.e(TAG, "setBitmapPixel: " + rgba + "-->" + Integer.toBinaryString(red) + "--" + Integer.toBinaryString(green) + "--" + Integer.toBinaryString(blue));

        int pixel = bitmap.getPixel(i, j);
        bitmap.setPixel(i, j, rgba);
        int pixel2 = bitmap.getPixel(i, j);

        Log.e(TAG, "setBitmapPixel: " + pixel + "--->" + pixel2);
    }

    //把int值转换成32位的二进制
    private static String transformIntTo32BitBinary(int value) {
        char[] chs = new char[Integer.SIZE];
        for (int i = 0; i < Integer.SIZE; i++) {
            chs[Integer.SIZE - 1 - i] = (char) (((value >> i) & 1) + '0');
        }
        return new String(chs);
    }


    public static Bitmap insertMessage2(Bitmap bitmap, String text) {
        String binaryString = str2Binary(text);
        if (TextUtils.isEmpty(binaryString))
            return null;

        Bitmap mutable = bitmap.copy(bitmap.getConfig(), true);

        int bitWidth = mutable.getWidth();
        int bitHeight = mutable.getHeight();

        long l = System.currentTimeMillis();
        int[] pixels = new int[bitWidth * bitHeight];
        mutable.getPixels(pixels, 0, bitWidth, 0, 0, bitWidth, bitHeight);
        long l1 = System.currentTimeMillis();
        Log.e(TAG, "insertMessage:cost time :" + (l1 - l));

        String sizeBin = transformIntTo32BitBinary(binaryString.length());
        String actualBinaryString = sizeBin + binaryString;
        int actualBinLen = actualBinaryString.length();

        //最低位有效
        if (bitWidth * bitHeight * 3 < actualBinLen)
            return mutable;

        int requirePixel = actualBinLen;
        char[] chars = actualBinaryString.toCharArray();
        int curPixelIndex = -1;
        StringBuilder binarybuilder = new StringBuilder();
        StringBuilder charbuilder = new StringBuilder();
        for (int i = 0; i < requirePixel; i++) {
            int pixel = pixels[i];

            int alpha = (pixel >> 24) & 0xff;
            int redPixel = (pixel >> 16) & 0xff;
            int greenPixel = (pixel >> 8) & 0xff;
            int bluePixel = (pixel) & 0xff;

            String redBinary = Integer.toBinaryString(redPixel);
            String greenBinary = Integer.toBinaryString(greenPixel);
            String blueBinary = Integer.toBinaryString(bluePixel);

            curPixelIndex++;
            if (curPixelIndex >= requirePixel) {
                int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
                pixels[i] = color;
                binarybuilder.append(redBinary + "-->" + greenBinary + "-->" + blueBinary);
                break;
            }

            char rChar = chars[curPixelIndex];
            redBinary = redBinary.substring(0, redBinary.length() - 1) + rChar;
            redPixel = Integer.valueOf(redBinary, 2);
            charbuilder.append(rChar);
            curPixelIndex++;
            if (curPixelIndex >= requirePixel) {
                int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
                pixels[i] = color;
                binarybuilder.append(redBinary + "-->" + greenBinary + "-->" + blueBinary);
                break;
            }

            char gChar = chars[curPixelIndex];
            greenBinary = greenBinary.substring(0, greenBinary.length() - 1) + gChar;
            greenPixel = Integer.valueOf(greenBinary, 2);
            charbuilder.append(gChar);
            curPixelIndex++;
            if (curPixelIndex >= requirePixel) {
                int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
                pixels[i] = color;
                binarybuilder.append(redBinary + "-->" + greenBinary + "-->" + blueBinary);
                break;
            }

            char bChar = chars[curPixelIndex];
            blueBinary = blueBinary.substring(0, blueBinary.length() - 1) + bChar;
            bluePixel = Integer.valueOf(blueBinary, 2);
            int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
            pixels[i] = color;
            charbuilder.append(bChar);
            binarybuilder.append(redBinary + "-->" + greenBinary + "-->" + blueBinary + "-->");

        }

        String checkString = charbuilder.toString();
        Log.e(TAG, "insertMessage2: " + TextUtils.equals(checkString, actualBinaryString));

        Bitmap bitmap1 = Bitmap.createBitmap(pixels, bitWidth, bitHeight, mutable.getConfig());
//        for (int i = 0; i < bitHeight; i++) {
//            for (int j = 0; j < bitWidth; j++) {
//                int pix = bitmap1.getPixel(j, i);
//                Log.e(TAG, "insertMessage2: " + ((i + 1) * j) + "--" + pix + "---" + pixels[(i + 1) * j]);
//            }
//        }
        return bitmap1;
    }


    public static Bitmap insertMessage(Bitmap bitmap, String text) {
        String binaryString = str2Binary(text);
        if (TextUtils.isEmpty(binaryString))
            return null;

        Bitmap mutable = bitmap.copy(bitmap.getConfig(), true);

        int bitWidth = mutable.getWidth();
        int bitHeight = mutable.getHeight();

        long l = System.currentTimeMillis();
        int[] pixels = new int[bitWidth * bitHeight];
        mutable.getPixels(pixels, 0, bitWidth, 0, 0, bitWidth, bitHeight);
        long l1 = System.currentTimeMillis();
        Log.e(TAG, "insertMessage:cost time :" + (l1 - l));

        String actualBinaryString = transformIntTo32BitBinary(binaryString.length()) + binaryString;
        int actualBinLen = actualBinaryString.length();

        //最低位有效
        if (bitWidth * bitHeight * 3 < actualBinLen)
            return mutable;

        int requirePixel = actualBinLen;
        char[] chars = actualBinaryString.toCharArray();
        int curPixelIndex = 0;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bitHeight; i++) {
            for (int j = 0; j < bitWidth; j++) {
                int pixel = mutable.getPixel(j, i);
                int alpha = (pixel >> 24) & 0xff;
                int redPixel = (pixel >> 16) & 0xff;
                int greenPixel = (pixel >> 8) & 0xff;
                int bluePixel = (pixel) & 0xff;

                String redBinary = Integer.toBinaryString(redPixel);
                String greenBinary = Integer.toBinaryString(greenPixel);
                String blueBinary = Integer.toBinaryString(bluePixel);

                Log.e(TAG, "insertMessage: " + pixel + "--" + redBinary + "--" + greenBinary + "--" + blueBinary);
                int index = curPixelIndex == 0 ? 0 : curPixelIndex++;
                if (curPixelIndex >= requirePixel) {
                    setBitmapPixel(mutable, j, i, alpha, redPixel, greenPixel, bluePixel);
                    break;
                }

                char rchar = chars[index];
                char[] redChars = redBinary.toCharArray();
                redChars[redChars.length - 1] = rchar;
                redBinary = new String(redChars);
                redPixel = Integer.valueOf(redBinary, 2);

                curPixelIndex++;
                if (curPixelIndex >= requirePixel) {
                    setBitmapPixel(mutable, j, i, alpha, redPixel, greenPixel, bluePixel);
                    break;
                }
                char gchar = chars[curPixelIndex];
                char[] greenChars = greenBinary.toCharArray();
                greenChars[greenChars.length - 1] = gchar;
                greenBinary = new String(greenChars);
                greenPixel = Integer.valueOf(greenBinary, 2);

                curPixelIndex++;
                if (curPixelIndex >= requirePixel) {
                    setBitmapPixel(mutable, j, i, alpha, redPixel, greenPixel, bluePixel);
                    break;
                }
                char bchar = chars[curPixelIndex];
                sb.append(bchar);
                char[] blueChars = blueBinary.toCharArray();
                blueChars[blueChars.length - 1] = bchar;
                blueBinary = new String(blueChars);
                bluePixel = Integer.valueOf(blueBinary, 2);
                setBitmapPixel(mutable, j, i, alpha, redPixel, greenPixel, bluePixel);
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
        int requirePixel = Integer.SIZE;
        String requirePixelStr = "";
        String extractedBinStr = "";

        StringBuilder checkBuild = new StringBuilder();
        long l = System.currentTimeMillis();
        int[] pixels = new int[bitWidth * bitHeight];
        bi.getPixels(pixels, 0, bitWidth, 0, 0, bitWidth, bitHeight);
        long l1 = System.currentTimeMillis();
//
//        for (int i = 0; i < 30; i++) {
//            int pixel = pixels[i];
//
//            int alpha = (pixel >> 24) & 0xff;
//            int redPixel = (pixel >> 16) & 0xff;
//            int greenPixel = (pixel >> 8) & 0xff;
//            int bluePixel = (pixel) & 0xff;
//
//            String redBinary = Integer.toBinaryString(redPixel);
//            String greenBinary = Integer.toBinaryString(greenPixel);
//            String blueBinary = Integer.toBinaryString(bluePixel);
//
//            String r = redBinary.substring(redBinary.length() - 1, redBinary.length());
//            String g = greenBinary.substring(greenBinary.length() - 1, greenBinary.length());
//            String b = blueBinary.substring(blueBinary.length() - 1, blueBinary.length());
//
//            extractedBinStr += r + g + b;
//
//        }
        int index = 0;
        labelA:
        for (int i = 0; i < bitHeight; i++) {
            for (int j = 0; j < bitWidth; j++) {
                int pixel = bi.getPixel(j, i);
                Log.e(TAG, "extractMessage: " + pixel + "--" + pixels[index++]);

                int redPixel = (pixel >> 16) & 0xff;

                String redBinStr = Integer.toBinaryString(redPixel);
                String redLastBin = redBinStr.substring(redBinStr.length() - 1, redBinStr.length());
                checkBuild.append(redBinStr + "-->");
                curIndex++;
                if (curIndex <= Integer.SIZE) {
                    requirePixelStr += redLastBin;
                    if (curIndex == Integer.SIZE) {
                        requirePixel = Integer.valueOf(requirePixelStr, 2) + Integer.SIZE;
                    }
                } else {
                    extractedBinStr += redLastBin;
                    if (curIndex >= requirePixel) {
                        break labelA;
                    }
                }

                int greenPixel = (pixel >> 8) & 0xff;
                String greenBinStr = Integer.toBinaryString(greenPixel);
                String greenLastBin = greenBinStr.substring(greenBinStr.length() - 1, greenBinStr.length());
                checkBuild.append(greenBinStr + "-->");
                curIndex++;
                if (curIndex <= Integer.SIZE) {
                    requirePixelStr += greenLastBin;
                    if (curIndex == Integer.SIZE) {
                        requirePixel = Integer.valueOf(requirePixelStr, 2) + Integer.SIZE;
                    }
                } else {
                    extractedBinStr += greenLastBin;
                    if (curIndex >= requirePixel) {
                        break labelA;
                    }
                }

                int bluePixel = (pixel) & 0xff;
                String blueBinStr = Integer.toBinaryString(bluePixel);
                String blueLastBin = blueBinStr.substring(blueBinStr.length() - 1, blueBinStr.length());
                checkBuild.append(blueLastBin + "-->");
                curIndex++;
                if (curIndex <= Integer.SIZE) {
                    requirePixelStr += blueLastBin;
                    if (curIndex == Integer.SIZE) {
                        requirePixel = Integer.valueOf(requirePixelStr, 2) + Integer.SIZE;
                    }
                } else {
                    extractedBinStr += blueLastBin;
                    if (curIndex >= requirePixel) {
                        break labelA;
                    }
                }
            }

        }

        String extractedText = generateString(extractedBinStr);
        return extractedText;
    }
}
