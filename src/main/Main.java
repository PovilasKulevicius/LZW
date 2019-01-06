package main;


import main.models.Decode;
import main.models.Encode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException, URISyntaxException {
        Scanner in = new Scanner(System.in);
        int length = in.nextInt();
        if(length == 0)
            length = 63;

        //launch(args);
        Encode enc = new Encode("input.txt", length);
        enc.encode();
        Decode dec = new Decode();
        dec.decode("input.txt.Encoded","result.txt");
    }


}
