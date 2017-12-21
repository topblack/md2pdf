package com.perkinelmer.pdfutils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

public class MetadataUtils {
	public static void print(String file) throws InvalidPasswordException, IOException {
		try (PDDocument document = PDDocument.load(new File(file))) {
			PDDocumentInformation info = document.getDocumentInformation();
			System.out.println("Page Count=" + document.getNumberOfPages());
			System.out.println("Title=" + info.getTitle());
			System.out.println("Author=" + info.getAuthor());
			System.out.println("Subject=" + info.getSubject());
			System.out.println("Keywords=" + info.getKeywords());
			System.out.println("Creator=" + info.getCreator());
			System.out.println("Producer=" + info.getProducer());
			System.out.println("Creation Date=" + info.getCreationDate());
			System.out.println("Modification Date=" + info.getModificationDate());
			System.out.println("Trapped=" + info.getTrapped());
		}
	}
	
	public static void write(String file) throws IOException {
		PDDocument document = PDDocument.load(new File(file));
		PDDocumentInformation info = document.getDocumentInformation();
		info.setTitle("ChemDraw Direct 2.0 Developer's Guide");
		info.setAuthor("PerkinElmer Informatics");
		info.setCreator("PKIMkDocs");
		info.setProducer("PKIMkDocs");
		info.setCreationDate(Calendar.getInstance());
		info.setModificationDate(Calendar.getInstance());
		document.save(file);
	}
}
