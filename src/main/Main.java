package main;


import main.models.Decode;
import main.models.Encode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException, URISyntaxException {
        Encode enc = new Encode("stakis.pdf", "output.txt", 16);
        enc.encode();
        Decode dec = new Decode();
        dec.decode("output.txt", "result.pdf");

//      String input, output,result;
//        while(true) {
//            System.out.println("c - encode");
//            System.out.println("d - decode");
//            System.out.println("e - exit");
//            Scanner in = new Scanner(System.in);
//            String choice = in.next();
//
//            switch (choice) {
//                case "c":
//                    System.out.println("dictionary_length input_file.txt output_file.txt");
//                    int length = in.nextInt();
//                    input = in.next();
//                    output = in.next();
//                    if (length == 0)
//                        length = 16;
//                    Encode enc = new Encode(input, output, length);
//                    enc.encode();
//                    break;
//                case "d":
//                    System.out.println("output_file.txt result_file.txt");
//                    output = in.next();
//                    result = in.next();
//                    Decode dec = new Decode();
//                    dec.decode(output, result);
//                    break;
//                case "e":
//                    System.exit(0);
//
//                default:
//                    System.out.println("wrong input");
//                    break;
//
//            }
//        }
    }


}
