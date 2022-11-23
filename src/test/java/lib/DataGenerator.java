package lib;

import java.text.SimpleDateFormat;

public class DataGenerator {
    public static String getRandomEmail(){
        // получаем уникальное число, состоящее из даты и ставим его перед доменом в емайл
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnqa" + timestamp + "@example.com";
    }
}
