package src;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * @Author peelsannaw
 * @create 16/12/2022 下午8:15
 */
public class testTess {
    public static void main(String[] args) throws TesseractException {
        //创建,设置路径
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("D:\\courseDesign\\hmtt-workspace");
        //设置语言
        tesseract.setLanguage("chi_sim");
        File file = new File("C:\\Users\\27365\\Pictures\\test.jpg");
        String s = tesseract.doOCR(file);
        s.replaceAll("\\r|\\n","");
        System.out.println(s);
    }
}
