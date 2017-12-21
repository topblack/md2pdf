package com.perkinelmer.pdfutils;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

/**
 *
 * This example demonstrates the use of the new methods
 * {@link PDFMergerUtility#setDestinationDocumentInformation(org.apache.pdfbox.pdmodel.PDDocumentInformation) }
 * and
 * {@link PDFMergerUtility#setDestinationMetadata(org.apache.pdfbox.pdmodel.common.PDMetadata) }
 * that were added in April 2016. These allow to control the meta data in a
 * merge without having to reopen the result file.
 *
 * @author Alexander Kriegisch
 */
public class Merger {
	private static final Log LOG = LogFactory.getLog(Merger.class);

	/**
	 * Infamous main method.
	 *
	 * @param args
	 *            Command line arguments, should be at least 3.
	 *
	 * @throws IOException
	 *             If there is an error parsing the document.
	 */
	public static void main(String[] args) throws IOException {
		// suppress the Dock icon on OS X
		System.setProperty("apple.awt.UIElement", "true");

		Merger merge = new Merger();
		merge.merge(args);
	}

	private void merge(String[] args) throws IOException {
		int firstFileArgPos = 0;

		if (args.length - firstFileArgPos < 3) {
			usage();
		}

		PDFMergerUtility merger = new PDFMergerUtility();
		for (int i = firstFileArgPos; i < args.length - 1; i++) {
			String sourceFileName = args[i];
			merger.addSource(sourceFileName);
		}

		String destinationFileName = args[args.length - 1];
		merger.setDestinationFileName(destinationFileName);
		merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
	}

	/**
	 * This will print the usage requirements and exit.
	 */
	private static void usage() {
		String message = "Usage: java -jar pdfbox-app-x.y.z.jar PDFMerger " + "<inputfiles 2..n> <outputfile>\n"
				+ "\nOptions:\n" + "  <inputfiles 2..n> : 2 or more source PDF documents to merge\n"
				+ "  <outputfile>      : The PDF document to save the merged documents to\n";

		System.err.println(message);
		System.exit(1);
	}
}