//package test;
//
//import main.models.Decode;
//import main.models.Encode;
//import main.models.LzwUtils;
//
//import java.io.*;
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class Test {
//
//    private final String sliosas = "-----------------------------------------------------------------------------------------";
//    private Encode encoder;
//    private Decode decoder;
//    private File file = new File("testFile.txt");
//    private File encodedFile = new File("testFile.txt.encoded");
//    private File decodedFile = new File("testFileDecoded.txt");
//    private final String path = "resources/";
//    private ArrayList<File> testfileArr;
//    public Test() {}
//
//    public static void main(String[] args) throws IOException, URISyntaxException {
//        Test test = new Test();
//        test.extendedTests();
//    }
//
//    public void compareFilesWithGivenContent(String fileContent) throws IOException, URISyntaxException {
//        FileOutputStream writer = new FileOutputStream(new File(file.getName()));
//
//        writer.write(fileContent.getBytes());
//        writer.close();
//
//        encoder.encode();
//        decoder.decode(encodedFile.toURL(), decodedFile.toURL());
//        byte[] f1 = Files.readAllBytes(file.toPath());
//        byte[] f2 = Files.readAllBytes(decodedFile.toPath());
//
//        if(!Arrays.equals(f1, f2))
//        {
//          System.out.println("failed");
//            System.exit(-1);
//        }
//        //assert (Arrays.equals(f1, f2));
//    }
//
//    public void runAllTests() {
//
//        for(LzwUtils.DictionaryMode dictMode : LzwUtils.DictionaryMode.values()){
//            for(int maxNumLen = 8; maxNumLen< 30; ++maxNumLen){
//                runTestCases(maxNumLen, dictMode);
//            }
//        }
//
//    }
//
//    private void runTestCases(int maxNumberLen, LzwUtils.DictionaryMode mode){
//        try {
//            encoder = new Encode(file.toURL(),maxNumberLen, mode);
//            decoder = new Decode();
//            compareFilesWithGivenContent("asd\0eeesf");
//            compareFilesWithGivenContent("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
//            compareFilesWithGivenContent("THIS IS TEST STRING agajgnadjkfnejwafgeg THIS IS TEST STRING agajgnadjkfnejwafgeg THIS IS TEST STRING agajgnadjkfnejwafgeg THIS IS TEST STRING agajgnadjkfnejwafgeg");
//            compareFilesWithGivenContent("a");
//            compareFilesWithGivenContent("csdbtnyjukjgtrfasdefgtry");
//            compareFilesWithGivenContent("add your test case here");
//            compareFilesWithGivenContent("ab");
//            compareFilesWithGivenContent("afsf");
//            compareFilesWithGivenContent("THIS IS TEST STRING THIS IS TEST STRING THIS IS TEST STRING THIS IS TEST STRING THIS IS TEST STRING ");
//            compareFilesWithGivenContent("TOBEORNOTTOBEORTOBEORNOT");
//
//            int len = 10000;
//            StringBuilder longString = new StringBuilder(len);
//            for(int i = 0; i < len; i++)
//                longString.append((char)i);
//
//            compareFilesWithGivenContent(longString.toString());
//            //get rid of that
//            file.delete();
//            encodedFile.delete();
//            decodedFile.delete();
//
//        }catch (IOException e)
//        {
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void extendedTests()
//    {
//        testfileArr = new ArrayList<>();
//        testfileArr.add(new File(path + "AltoriuSeseli.pdf"));
//        testfileArr.add(new File(path + "Dot.bmp"));
//        testfileArr.add(new File(path + "Dot.png"));
//        testfileArr.add(new File(path + "Dot.jpg"));
//        testfileArr.add(new File(path + "cardano"));
//        testfileArr.add(new File(path + "cardano.exe"));
//
//        System.out.println("----------------------------START--------------------------------");
//        testfileArr.forEach(f -> {
//            try {
//                System.out.println("File being compressed: " + f.getName());
//                System.out.println("Infinite size");
//                //Meassuring time
//                long startTime = System.nanoTime();
//                encoder = new Encode(f.toURI().toURL(), 0, LzwUtils.DictionaryMode.Infinite);
//                encoder.encode();
//                long endTime = System.nanoTime();
//
//                //Printing results
//                System.out.println("File size: " + encoder.getInputFileSize() + " Compressed size: " + encoder.getOutputFilesize()
//                + System.lineSeparator() + "Compression Coefficient: " + (float)encoder.getInputFileSize()/encoder.getOutputFilesize());
//
//                System.out.println("Duration : " + (endTime - startTime));
//                System.out.println(System.lineSeparator());
//                encoder.deleteOutputFile();
//
//
//                System.out.println("15 size with no clear");
//                startTime = System.nanoTime();
//                encoder = new Encode(f.toURI().toURL(), 15, LzwUtils.DictionaryMode.Continue);
//                encoder.encode();
//                endTime = System.nanoTime();
//
//                //Printing results
//                System.out.println("File size: " + encoder.getInputFileSize() + " Compressed size: " + encoder.getOutputFilesize()
//                        + System.lineSeparator() + "Compression Coefficient: " + (float)encoder.getInputFileSize()/encoder.getOutputFilesize());
//
//                System.out.println("Duration : " + (endTime - startTime));
//                System.out.println(System.lineSeparator());
//                encoder.deleteOutputFile();
//
//
//                System.out.println("15 size with clear");
//                startTime = System.nanoTime();
//                encoder = new Encode(f.toURI().toURL(), 15, LzwUtils.DictionaryMode.Clear);
//                encoder.encode();
//                endTime = System.nanoTime();
//
//                //Printing results
//                System.out.println("File size: " + encoder.getInputFileSize() + " Compressed size: " + encoder.getOutputFilesize()
//                        + System.lineSeparator() + "Compression Coefficient: " + (float)encoder.getInputFileSize()/encoder.getOutputFilesize());
//
//                System.out.println("Duration : " + (endTime - startTime));
//                encoder.deleteOutputFile();
//                System.out.println(sliosas);
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        });
//
//    }
//}
