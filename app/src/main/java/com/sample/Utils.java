package com.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by timian on 2018/11/16.
 * <p>
 * 这是一个在 图片（PNG,JPG,JPEG,GIF）、
 * <p>
 * 算法思想是 把每个像素的RGB通道的最后一位 替换成 需要隐藏的文本被编码成二进制的顺序位
 * <p>
 * 能够隐藏多少个字符，完全取决于你的这张图片的尺寸
 * <p>
 * 理论上 100*100的图片，能够隐藏3750个英文字符，能够隐藏1250个中文字符
 */

public class Utils {
    private static final String TAG = "Utils";
    private static final String FLAG = "TAO";

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

    //二进制生成字符串
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

    //把int值转换成32位的二进制
    private static String transformIntTo32BitBinary(int value) {
        char[] chs = new char[Integer.SIZE];
        for (int i = 0; i < Integer.SIZE; i++) {
            chs[Integer.SIZE - 1 - i] = (char) (((value >> i) & 1) + '0');
        }
        return new String(chs);
    }

    //校验文件是否含有flag标志 否则 认为不是一个合格的图片
    private static boolean checkFlagValidate(int curPixelIndex, int flagSize, String binaryString) {
        //如果 不包含flag  则认为图片没有需要的信息，直接结束了，下同
        if (curPixelIndex == flagSize) {
            String flag = generateString(binaryString);
            return TextUtils.equals(flag, FLAG);
        } else {
            return true;
        }
    }

    //校验数据块二进制的长度的int值
    private static int checkChunkSizeValidate(int curPixelIndex, int minRequireSize, String binaryString) {
        if (curPixelIndex == minRequireSize) {
            try {
                Integer valueOf = Integer.valueOf(binaryString, 2);
                return valueOf;
            } catch (Exception e) {
                Log.e(TAG, "binarySafely2Int: " + e.getMessage());
                return -1;
            }
        } else {
            return 0;
        }
    }


    public static Bitmap insertMessage3(@NonNull Bitmap bitmap, @NonNull String text) {
        String dataBinaryString = str2Binary(text);
        if (TextUtils.isEmpty(dataBinaryString))
            return null;

        //组合成11100011 111100001111000 1110111011101110111011101100  识别的时候也是这样子
        String flagBinaryString = str2Binary(FLAG);
        String sizeBinaryString = transformIntTo32BitBinary(dataBinaryString.length());
        String actualBinaryString = flagBinaryString + sizeBinaryString + dataBinaryString;
        int actualBinLen = actualBinaryString.length();

        int bitWidth = bitmap.getWidth();
        int bitHeight = bitmap.getHeight();
        //最低位有效
        if (bitWidth * bitHeight * 3 < actualBinLen)
            return bitmap;

        Bitmap mutable = bitmap.copy(bitmap.getConfig(), true);
        //从原图中提取出仅需要的那几行 像素值
        long start = System.currentTimeMillis();
        int requireHeight;
        if (actualBinLen <= bitWidth) {
            requireHeight = 1;
        } else {
            requireHeight = actualBinLen / bitWidth + (actualBinLen % bitWidth == 0 ? 0 : 1);
        }
        int[] pixels = new int[bitWidth * requireHeight];
        bitmap.getPixels(pixels, 0, bitWidth, 0, 0, bitWidth, requireHeight);

        int requirePixel = actualBinLen;
        int curPixelIndex = -1;
        char[] chars = actualBinaryString.toCharArray();
        StringBuilder binarybuilder = new StringBuilder();
        StringBuilder charbuilder = new StringBuilder();
        for (int i = 0; i < requirePixel; i++) {
            int pixel = pixels[i];

            int alpha = (pixel >> 24) & 0xff;
            int redPixel = (pixel >> 16) & 0xff;
            int greenPixel = (pixel >> 8) & 0xff;
            int bluePixel = (pixel) & 0xff;

            curPixelIndex++;
            if (curPixelIndex >= requirePixel) {
                int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
                pixels[i] = color;
                binarybuilder.append(Integer.toBinaryString(redPixel) + "-->" + Integer.toBinaryString(greenPixel) + "-->" + Integer.toBinaryString(bluePixel));
                break;
            }

            char rChar = chars[curPixelIndex];
            if (rChar == 49) {
                redPixel |= (0b00000001);
            } else {
                redPixel &= (0b11111110);
            }
            charbuilder.append(rChar);
            curPixelIndex++;
            if (curPixelIndex >= requirePixel) {
                int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
                pixels[i] = color;
                binarybuilder.append(Integer.toBinaryString(redPixel) + "-->" + Integer.toBinaryString(greenPixel) + "-->" + Integer.toBinaryString(bluePixel));
                break;
            }

            char gChar = chars[curPixelIndex];
            if (gChar == 49) {
                greenPixel |= (0b00000001);
            } else {
                greenPixel &= (0b11111110);
            }
            charbuilder.append(gChar);
            curPixelIndex++;
            if (curPixelIndex >= requirePixel) {
                int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
                pixels[i] = color;
                binarybuilder.append(Integer.toBinaryString(redPixel) + "-->" + Integer.toBinaryString(greenPixel) + "-->" + Integer.toBinaryString(bluePixel));
                break;
            }

            char bChar = chars[curPixelIndex];
            if (bChar == 49) {
                bluePixel |= (0b00000001);
            } else {
                bluePixel &= (0b11111110);
            }
            int color = Color.argb(alpha, redPixel, greenPixel, bluePixel);
            pixels[i] = color;
            charbuilder.append(bChar);
            binarybuilder.append(Integer.toBinaryString(redPixel) + "-->" + Integer.toBinaryString(greenPixel) + "-->" + Integer.toBinaryString(bluePixel));

        }

        String checkString = charbuilder.toString();
        Log.e(TAG, "insertMessage3: " + TextUtils.equals(checkString, actualBinaryString));
        //用新像素生成新的位图
        Bitmap pixelBitmap = Bitmap.createBitmap(pixels, bitWidth, requireHeight, mutable.getConfig());
        //从原图截取出像素没动过的位图
        Bitmap clipBitmap = Bitmap.createBitmap(mutable, 0, requireHeight, bitWidth, bitHeight - requireHeight);
        //合成新的位图
        Bitmap resultBitmap = Bitmap.createBitmap(bitWidth, bitHeight, mutable.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(pixelBitmap, 0, 0, null);
        canvas.drawBitmap(clipBitmap, 0, requireHeight, null);

        long end = System.currentTimeMillis();
        Log.e(TAG, "insertMessage3---> 提取像素耗时：" + (end - start));
        int[] newPixels = new int[bitWidth];
        resultBitmap.getPixels(newPixels, 0, bitWidth, 0, 0, bitWidth, 1);
        mutable.recycle();
        pixelBitmap.recycle();
        clipBitmap.recycle();

        return resultBitmap;

    }

    public static String extractMessage2(@NonNull Bitmap bitmap) {

        int bitWidth = bitmap.getWidth();
        int bitHeight = bitmap.getHeight();

        int curPixelIndex = 0;//记录RGB通道累加 计数器
        int flagSize = str2Binary(FLAG).length();//flag位的长度
        int minRequireSize = Integer.SIZE + flagSize;//flag位+数据块int转换成二进制后的长度，也是要求的最小有效长度
        int maxValidateSize = minRequireSize;//flag位+数据块int值二进制长度+数据块二进制长度

        String flagBinaryString = "";
        String sizeBinaryString = "";
        String extractedBinaryString = "";

        StringBuilder sb = new StringBuilder();
        if (bitWidth * bitHeight * 3 < minRequireSize)
            return null;

        long start = System.currentTimeMillis();
        SKIP:
        for (int i = 0; i < bitHeight; i++) {
            for (int j = 0; j < bitWidth; j++) {
                int pixel = bitmap.getPixel(j, i);
                //ignore alpha channel

                //red channel  last bit
                curPixelIndex++;
                int rlBit = ((pixel >> 16) & 0xff) & 1;
                sb.append(rlBit);
                if (curPixelIndex <= flagSize) {
                    flagBinaryString += rlBit;
                    if (!checkFlagValidate(curPixelIndex, flagSize, flagBinaryString)) break SKIP;
                } else if (curPixelIndex <= minRequireSize) {
                    sizeBinaryString += rlBit;
                    maxValidateSize += checkChunkSizeValidate(curPixelIndex, minRequireSize, sizeBinaryString);
                    if (maxValidateSize < 0) break SKIP;
                } else if (curPixelIndex <= maxValidateSize) {
                    extractedBinaryString += rlBit;
                } else {
                    break SKIP;
                }

                //green channel  last bit
                curPixelIndex++;
                int glBit = ((pixel >> 8) & 0xff) & 1;
                sb.append(glBit);
                if (curPixelIndex <= flagSize) {
                    //如果 不包含flag  则认为图片没有需要的信息，直接结束了，下同
                    flagBinaryString += glBit;
                    if (!checkFlagValidate(curPixelIndex, flagSize, flagBinaryString)) break SKIP;
                } else if (curPixelIndex <= minRequireSize) {
                    sizeBinaryString += glBit;
                    maxValidateSize += checkChunkSizeValidate(curPixelIndex, minRequireSize, sizeBinaryString);
                    if (maxValidateSize < 0) break SKIP;
                } else if (curPixelIndex <= maxValidateSize) {
                    extractedBinaryString += glBit;
                } else {
                    break SKIP;
                }

                //blue channel  last bit
                curPixelIndex++;
                int blBit = ((pixel) & 0xff) & 1;
                sb.append(blBit);
                if (curPixelIndex <= flagSize) {
                    //如果 不包含flag  则认为图片没有需要的信息，直接结束了，下同
                    flagBinaryString += blBit;
                    if (!checkFlagValidate(curPixelIndex, flagSize, flagBinaryString)) break SKIP;
                } else if (curPixelIndex <= minRequireSize) {
                    sizeBinaryString += blBit;
                    maxValidateSize += checkChunkSizeValidate(curPixelIndex, minRequireSize, sizeBinaryString);
                    if (maxValidateSize < 0) break SKIP;
                } else if (curPixelIndex <= maxValidateSize) {
                    extractedBinaryString += blBit;
                } else {
                    break SKIP;
                }

            }

        }

        String extractedText = generateString(extractedBinaryString);
        long end = System.currentTimeMillis();
        Log.e(TAG, "extractMessage2: 解码耗时时间" + (end - start));
        return extractedText;
    }
}
