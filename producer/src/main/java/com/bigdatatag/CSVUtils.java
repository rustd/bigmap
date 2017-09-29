package com.bigdatatag;

/**
 * Created by safak on 6/8/17.
 */

import com.bigdatatag.producerEntity.Measurement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public static void getAndSendData(String filePath, String server, String topic) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        int lineCounter = 0;
        Scanner scanner = new Scanner(new File(filePath));
        while (scanner.hasNext()) {

            String scannedLine = scanner.nextLine();

            int count = StringUtils.countMatches(scannedLine, "\"");

            if (count % 2 != 0) {
                scannedLine += " " + scanner.nextLine();
            }

            List<String> line = parseLine(scannedLine);
            Measurement measurement = new Measurement();
            measurement.setCapturedTime(line.get(0));
            measurement.setLatitude(line.get(1));
            measurement.setLongitude(line.get(2));
            measurement.setValue(line.get(3));
            measurement.setUnit(line.get(4));
            measurement.setLocationName(line.get(5));
            measurement.setDeviceID(line.get(6));
            measurement.setMD5Sum(line.get(7));
            measurement.setHeight(line.get(8));
            measurement.setSurface(line.get(9));
            measurement.setRadiation(line.get(10));
            measurement.setUploadedTime(line.get(11));
            measurement.setLoaderID(line.get(12));

            lineCounter++;
            System.out.println(lineCounter);
            System.out.println(measurement.toString());

            if (!measurement.getCapturedTime().equals("Captured Time")) {
                String jsonInString = mapper.writeValueAsString(measurement);
                KafkaSender.Sender(server, topic, jsonInString);
            }

        }
        scanner.close();
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

}
