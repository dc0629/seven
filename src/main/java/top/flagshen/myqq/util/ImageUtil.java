package top.flagshen.myqq.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class ImageUtil {

    public static void main(String[] args) {
        String backgroundPath = "D:\\test\\test.jpg";
        String message01 ="名字";
        String message02 = "QQ:123";
        String outPutPath="D:\\test\\end1.jpg";
        String content = "abcd";
        overlapImage(backgroundPath,message01,message02,outPutPath, content);
    }

    /**
     * 生成公民证
     * @param backgroundPath
     * @param message01
     * @param message02
     * @param outPutPath
     * @param content
     * @return
     */
    public static String overlapImage(String backgroundPath,String message01,String message02,String outPutPath, String content){
        try {
            //设置图片大小
            BufferedImage background = resizeImage(720,638, ImageIO.read(new File(backgroundPath)));
            Graphics2D g = background.createGraphics();
            g.setColor(new Color(64, 64, 64));
            g.setFont(new Font("黑体",Font.BOLD,20));
            g.drawString(message01,400 ,360);
            g.drawString(message02,30 ,600);
            g.drawImage(convertQrPic(content), 250, 550, 50, 50, null);
            g.dispose();
            ImageIO.write(background, "jpg", new File(outPutPath));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedImage resizeImage(int x, int y, BufferedImage bfi){
        BufferedImage bufferedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(
                bfi.getScaledInstance(x, y, Image.SCALE_SMOOTH), 0, 0, null);
        return bufferedImage;
    }

    /**
     * 根据指定内容生成二维码
     * @param content
     * @return
     */
    private static BufferedImage convertQrPic(String content) throws WriterException {
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 50, 50, hints);
        bitMatrix = deleteWhite(bitMatrix);
        return toBufferedImage(bitMatrix);
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

}
