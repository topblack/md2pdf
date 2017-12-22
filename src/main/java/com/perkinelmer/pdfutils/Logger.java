package com.perkinelmer.pdfutils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class Logger {

    private static FileWriter fw = null;

    private final static String LOG_FILE = "log.txt";

    static {
        new File(LOG_FILE).delete();
    }

    public static void debug(Object message) {
        if (System.getenv("MD2PDF_VERBOSE") != null) {
            log("Debug", message.toString());
        }
    }

    public static void error(Object message) {
        log("Error", message.toString());
    }

    public static void warn(Object message) {
        log("Warn", message.toString());
    }

	private static void log(String level, String message) {
		try {
            if (fw == null) {
                fw = new FileWriter(LOG_FILE);
            }

            String formattedMessage = "[" + level.toUpperCase() + "]\t" + message + "\r\n";
            fw.append(formattedMessage);
            System.out.print(formattedMessage);
        } catch(IOException ex) {
            System.err.println(ex.getMessage());
        }
	}
}
