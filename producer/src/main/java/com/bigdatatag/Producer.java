package com.bigdatatag;

/**
 * Created by safak on 6/8/17.
 */
public class Producer {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Args are wrong");
            System.exit(1);
        }


        //endless loop
        while (true) {
            CSVUtils.getAndSendData(args[0], args[1], args[2]);
        }
    }
}
