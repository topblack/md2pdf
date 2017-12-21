package com.perkinelmer.pdfutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.perkinelmer.pdfutils.model.Configuration;

public class Decorator {

	public static void main(String[] args) throws IOException {
		String configString = new String(Files.readAllBytes(FileSystems.getDefault().getPath(args[0])), "utf-8");
		Configuration config = new Gson().fromJson(configString, Configuration.class);
		String workingDir = config.getOutputDir();
		String pathToPDFToBeOverlayed =  workingDir + File.separator + "main.pdf";
		String pathToOverlayPDF =  workingDir + File.separator + "overlay.pdf";
		String pathToCoverPDF = workingDir + File.separator + "cover.pdf";
		String pathToOverlayedPDF = workingDir + File.separator + "overlayed.pdf";
		String pathToTocJson = workingDir + File.separator + "toc.json";
		String pathToPagedPDF = workingDir + File.separator + "paged.pdf";
		String finalPDF = workingDir + File.separator + "final.pdf";
		Overlayor.main(new String[] {pathToPDFToBeOverlayed, "-useAllPages", pathToOverlayPDF, pathToOverlayedPDF});
		Pager.main(new String[] {pathToOverlayedPDF, pathToPagedPDF, pathToTocJson});
		Merger.main(new String[] {pathToCoverPDF, pathToPagedPDF, finalPDF});
		
		MetadataUtils.write(finalPDF);
	}
}
