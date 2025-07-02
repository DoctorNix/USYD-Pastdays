package Edstemus.Scroll;

import Edstemus.database.ScrollData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDate;

public class FileConvertor {

    public static void convertTxtToBin(byte[] content, String outputFilePath) {
        try {
            //byte[] fileContent = Files.readAllBytes(Paths.get(inputFilePath));
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                fos.write(content);
                System.out.println("Binary file saved as: " + outputFilePath);
            }
        } catch (IOException e) {
            System.out.println("Error during file conversion: " + e.getMessage());
        }
    }

    public static Scroll saveContentToScroll(String filePath, int ownerID, String scrollName, int downloads){
        try{
            File file = new File(filePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            LocalDate uploadDate = LocalDate.now();
            return new Scroll (ownerID, scrollName, fileContent, downloads, uploadDate);
        }catch (IOException e) {
            //e.printStackTrace();
            System.out.println("No such file");
            return null;
        }
    }

    public static String displayScrollContent(Scroll scroll){
        if (scroll == null || scroll.getScrollData() == null) {
            System.out.println("No content available in this scroll.");
            return null;
        }

        byte[] content = scroll.getScrollData();


        String textContent = new String(content);

        if (textContent.isEmpty()){
            return null;
        }

        //System.out.println("Scroll Content (as text):");
        //System.out.println(textContent);
        return textContent;

    }
}
