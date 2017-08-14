import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by lifei on 2016/12/12.
 */

public class DimenTool {

    public static void main(String[] args){
        getDifDimen();

    }

    public static void getDifDimen() {

        //以此文件夹下的dimens.xml文件内容为初始值参照
        File file = new File("./app/src/main/res/values/dimens.xml");

        BufferedReader reader = null;
        StringBuilder w280 = new StringBuilder();
        StringBuilder w320 = new StringBuilder();
        StringBuilder w480 = new StringBuilder();
        StringBuilder w600 = new StringBuilder();
        StringBuilder w720 = new StringBuilder();
        StringBuilder w800 = new StringBuilder();
        StringBuilder w820 = new StringBuilder();

        //生成不同分辨率
        try {

            System.out.printf("生成不同分辨率");

            reader = new BufferedReader(new FileReader(file));

            String tempString;

            int line = 1;

            //一次读入一行，直到读入null为文件结束

            while ((tempString = reader.readLine()) != null){

                if (tempString.contains("</dimen>")){
                    String start = tempString.substring(0,tempString.indexOf(">")+1);
                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);

                    //截取 <dimen></dimen>标签中的内容，

                    Double num = Double.parseDouble(tempString.substring(tempString.indexOf(">")+1,tempString.indexOf("dp</dimen>")-2));

                    //根据不同的尺寸，计算新的值，拼接新的字符串，并且结尾处换行
                    w280.append(start).append(num * 1).append(end).append("\r\n");
                    w320.append(start).append(num).append(end).append("\r\n");
                    w480.append(start).append(num * 1.5).append(end).append("\r\n");
                    w600.append(start).append(num * 1.87).append(end).append("\r\n");
//                    w720.append(start).append(num * 2.25).append(end).append("\r\n");
//                    w800.append(start).append(num * 2.5).append(end).append("\r\n");
//                    w820.append(start).append(num * 2.56).append(end).append("\r\n");
                    w720.append(tempString).append("\n");
                    w800.append(tempString).append("\n");
                    w820.append(tempString).append("\n");

                }else{
                    w280.append(tempString).append("");
                    w480.append(tempString).append("");
                    w600.append(tempString).append("");
                    w720.append(tempString).append("");
                    w800.append(tempString).append("");
                    w820.append(tempString).append("");
                }

                line++;

            }

            reader.close();

            System.out.printf("<!-- w280 -->");
            System.out.println(w280);
            System.out.printf("<!-- w480 -->");
            System.out.println(w480);
            System.out.printf("<!-- w600 -->");
            System.out.println(w600);
            System.out.printf("<!-- w720 -->");
            System.out.println(w720);
            System.out.printf("<!-- w8000 -->");
            System.out.println(w800);
            System.out.printf("<!-- w820 -->");
            System.out.println(w820);


            String w280_file = "./app/src/main/res/values-w280dp/dimens.xml";
            String w480_file = "./app/src/main/res/values-w480dp/dimens.xml";
            String w600_file = "./app/src/main/res/values-w600dp/dimens.xml";
            String w720_file = "./app/src/main/res/values-w720dp/dimens.xml";
            String w800_file = "./app/src/main/res/values-w800dp/dimens.xml";
            String w820_file = "./app/src/main/res/values-w820dp/dimens.xml";

            //将新的内容,写入到指定的文件中去

            writeFile(w280_file,w280.toString());
            writeFile(w480_file,w480.toString());
            writeFile(w600_file,w600.toString());
            writeFile(w720_file,w720.toString());
            writeFile(w800_file,w800.toString());
            writeFile(w820_file,w820.toString());



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void writeFile(String file, String dimen) {

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            out.println(dimen);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
