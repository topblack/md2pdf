package com.perkinelmer.pdfutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.perkinelmer.pdfutils.model.OutlineItem;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;

public class Pager {

	private List<OutlineItem> outlineItems = new LinkedList<OutlineItem>();

	private List<PDOutlineItem> parentOutlineItems = new ArrayList<PDOutlineItem>();

	private PagerStyle style;

	public class PagerStyle {
		private PageMargin margin;

		private PageNumberStyle mainPageNumberStyle;

		private PageNumberStyle tocPageNumberStyle;

		public PageMargin getMargin() {
			return margin;
		}

		public void setMargin(PageMargin margin) {
			this.margin = margin;
		}

		public PageNumberStyle getMainPageNumberStyle() {
			return mainPageNumberStyle;
		}

		public void setMainPageNumberStyle(PageNumberStyle mainPageNumberStyle) {
			this.mainPageNumberStyle = mainPageNumberStyle;
		}

		public PageNumberStyle getTocPageNumberStyle() {
			return tocPageNumberStyle;
		}

		public void setTocPageNumberStyle(PageNumberStyle tocPageNumberStyle) {
			this.tocPageNumberStyle = tocPageNumberStyle;
		}
	}

	public class PageNumberStyle {
		private PDFont font;

		private float size;

		public PDFont getFont() {
			return font;
		}

		public void setFont(PDFont font) {
			this.font = font;
		}

		public float getSize() {
			return size;
		}

		public void setSize(float size) {
			this.size = size;
		}

		public PageNumberStyle(PDFont font, float size) {
			this.font = font;
			this.size = size;
		}
	}

	public class PageMargin {
		private float left;

		private float top;

		private float right;

		private float bottom;

		public PageMargin(float top, float right, float bottom, float left) {
			this.left = left;
			this.top = top;
			this.bottom = bottom;
			this.right = right;
		}

		public float getLeft() {
			return left;
		}

		public void setLeft(float left) {
			this.left = left;
		}

		public float getTop() {
			return top;
		}

		public void setTop(float top) {
			this.top = top;
		}

		public float getRight() {
			return right;
		}

		public void setRight(float right) {
			this.right = right;
		}

		public float getBottom() {
			return bottom;
		}

		public void setBottom(float bottom) {
			this.bottom = bottom;
		}

	}

	/**
	 * Constructor.
	 */
	public Pager(String tocfile, int maxLevel) throws IOException {
		super();

		this.style = new PagerStyle();
		this.style.setMargin(new PageMargin(0, 70f, 80f, 0));
		PageNumberStyle numberStyle = new PageNumberStyle(PDType1Font.HELVETICA, 10f);
		this.style.setMainPageNumberStyle(numberStyle);
		this.style.setTocPageNumberStyle(numberStyle);

		Path tocFilePath = FileSystems.getDefault().getPath(tocfile);
		String tocContent = new String(Files.readAllBytes(tocFilePath), "utf-8");
		Gson gson = new Gson();
		OutlineItem[] tocItems = gson.fromJson(tocContent, OutlineItem[].class);

		OutlineItem lastSeenOi = null;
		for (OutlineItem oi : tocItems) {
			int lastSeenOiLevel = lastSeenOi != null ? lastSeenOi.getLvl() : 0;
			if (oi.getLvl() - lastSeenOiLevel > 1) {
				int fixedLevel = lastSeenOiLevel + 1;
				Logger.warn("Unsupported level sequence found at " + oi.getContent() + ", the previous level is " + lastSeenOiLevel);
				Logger.warn("Set the level of " + oi.getContent() + " to " + fixedLevel);
				oi.setLvl(fixedLevel);
			}

			lastSeenOi = oi;
			if (oi.getLvl() <= maxLevel) {
				this.outlineItems.add(oi);
			}
		}
	}

	private String convertTocContentToDestinationString(String content) {
		return content.replaceAll("[-\\[\\]^/,'*:.!><~@#$%&;+=?|\"\\\\()\\s]+", "-").replaceAll("-+", "-");
	}

	private OutlineItem takeOutlineItemInfoByName(String name) {
		OutlineItem result = null;
		for (OutlineItem oi : this.outlineItems) {
			if (convertTocContentToDestinationString(oi.getContent()).equalsIgnoreCase(name)) {
				result = oi;
				break;
			}
		}

		if (result != null) {
			this.outlineItems.remove(result);
		}

		return result;
	}

	static String getText(File pdfFile) throws IOException {
		PDDocument doc = PDDocument.load(pdfFile);

		return new PDFTextStripper().getText(doc);
	}

	private String genDots(int number) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < number; i++) {
			sb.append(".");
		}

		return sb.toString();
	}

	/**
	 * create the second sample document from the PDF file format specification.
	 *
	 * @param file
	 *            The file to write the PDF to.
	 * @param message
	 *            The message to write in the file.
	 * @param outfile
	 *            The resulting PDF.
	 *
	 * @throws IOException
	 *             If there is an error writing the data.
	 */
	public void doIt(String file, String outfile, String tocfile) throws IOException {
		float dotWidth = this.style.tocPageNumberStyle.font.getStringWidth(".") * this.style.tocPageNumberStyle.size
				/ 1000f;

		try (PDDocument doc = PDDocument.load(new File(file))) {
			PDDocumentOutline outline = new PDDocumentOutline();
			doc.getDocumentCatalog().setDocumentOutline(outline);

			PDFont font = this.style.mainPageNumberStyle.font;
			float fontSize = this.style.mainPageNumberStyle.size;

			int totalPages = doc.getPages().getCount();
			int currentPage = 1;
			int lastOutlineLevelHandled = 0;

			for (PDPage page : doc.getPages()) {
				String message = currentPage + " of " + totalPages;
				currentPage++;
				PDRectangle pageSize = page.getMediaBox();
				float stringWidth = font.getStringWidth(message) * fontSize / 1000f;

				float pageWidth = pageSize.getWidth();
				float mainPageNumberX = pageWidth - stringWidth - this.style.margin.right;
				float mainPageNumberY = this.style.margin.bottom + this.style.mainPageNumberStyle.size;

				// Add Table of Content
				List<PDAnnotation> annotationList = page.getAnnotations();
				for (PDAnnotation annotation : annotationList) {
					if (this.outlineItems.size() == 0) {
						// All items on toc.json have been proceed.
						break;
					}

					if (annotation instanceof PDAnnotationLink) {
						PDAnnotationLink link = (PDAnnotationLink) annotation;
						PDAction action = link.getAction();
						if (action instanceof PDActionURI) {
							//PDActionURI uri = (PDActionURI) action;
						} else if (action != null) {
							Logger.warn("Action: " + action.getClass().getName());
						} else {
							PDDestination dest = link.getDestination();
							if (dest instanceof PDNamedDestination) {
								PDNamedDestination namedDest = (PDNamedDestination) dest;
								PDRectangle rect = annotation.getRectangle();

								PDPageDestination pageDest = doc.getDocumentCatalog()
										.findNamedDestinationPage(namedDest);

								String pageNumberString = Integer.toString(pageDest.findPageNumber());
								float tocPageNumberX = pageWidth
										- (font.getStringWidth(pageNumberString) * fontSize / 1000f)
										- this.style.margin.right;
								float tocPageNumberY = rect.getLowerLeftY() + fontSize / 2 - 2.5f;

								// append the content to the existing stream
								try (PDPageContentStream contentStream = new PDPageContentStream(doc, page,
										AppendMode.APPEND, true, true)) {
									contentStream.beginText();
									// set font and font size
									contentStream.setFont(font, fontSize);
									// set text color to red
									contentStream.setNonStrokingColor(0, 0, 0);
									contentStream
											.setTextMatrix(Matrix.getTranslateInstance(tocPageNumberX, tocPageNumberY));

									contentStream.showText(pageNumberString);
									contentStream.endText();
								}

								// append the content to the existing stream
								try (PDPageContentStream contentStream = new PDPageContentStream(doc, page,
										AppendMode.APPEND, true, true)) {
									float spaceWidthBetweenDestNameAndPage = tocPageNumberX - rect.getUpperRightX();

									int dotsNeeded = (int) (spaceWidthBetweenDestNameAndPage / dotWidth);
									String dotsString = this.genDots(dotsNeeded);

									contentStream.beginText();
									// set font and font size
									contentStream.setFont(font, fontSize);
									// set text color to red
									contentStream.setNonStrokingColor(0, 0, 0);

									float dotsX = rect.getUpperRightX();
									float dotsY = tocPageNumberY;

									contentStream.setTextMatrix(Matrix.getTranslateInstance(dotsX, dotsY));

									contentStream.showText(dotsString);
									contentStream.endText();
								}

								String destinationString = ((PDNamedDestination) dest).getNamedDestination();
								OutlineItem oi = this.takeOutlineItemInfoByName(destinationString);
								Logger.debug(destinationString);
								if (oi == null) {
									Logger.warn("No outline item found for " + destinationString);
								} else {
									PDOutlineItem bookmark = new PDOutlineItem();
									bookmark.setDestination(dest);
									String bookmarkTitle = oi.getContent();
									bookmark.setTitle(bookmarkTitle);
									if (oi.getLvl() <= lastOutlineLevelHandled) {
										int levelGap = lastOutlineLevelHandled - oi.getLvl();
										for (int i = 0; i <= levelGap; i++) {
											this.parentOutlineItems.remove(this.parentOutlineItems.size() - 1);
										}
									}
									if (this.parentOutlineItems.size() == 0) {
										outline.addLast(bookmark);
									} else {
										PDOutlineItem parentBookmark = this.parentOutlineItems
												.get(this.parentOutlineItems.size() - 1);
										parentBookmark.addLast(bookmark);
									}
									this.parentOutlineItems.add(bookmark);
									lastOutlineLevelHandled = oi.getLvl();
								}
							}
						}

					} else if (annotation != null) {
						System.out.println("Annotation: " + annotation.getClass().getName());
					} else {
						System.out.println("Null Annotation");
					}

				}

				// Add page numbers on each page
				try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
						true)) {
					contentStream.beginText();
					// set font and font size
					contentStream.setFont(font, fontSize);
					// set text color to red
					contentStream.setNonStrokingColor(0, 0, 0);

					contentStream.setTextMatrix(Matrix.getTranslateInstance(mainPageNumberX, mainPageNumberY));

					/*
					 * if (rotate) { // rotate the text according to the page
					 * rotation
					 * contentStream.setTextMatrix(Matrix.getRotateInstance(Math
					 * .PI / 2, centerX, centerY)); } else {
					 * contentStream.setTextMatrix(Matrix.getTranslateInstance(
					 * centerX, centerY)); }
					 */

					contentStream.showText(message);
					contentStream.endText();
				}
			}

			for (OutlineItem oi : this.outlineItems) {
				Logger.warn("Unmatched " + convertTocContentToDestinationString(oi.getContent()));
			}

			outline.openNode();
			doc.save(outfile);
		}
	}

	/**
	 * This will create a hello world PDF document. <br>
	 * see usage() for commandline
	 *
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) throws IOException {
		String inputFilePath = args[0];
		String outputFilePath = args[1];
		String tocFilePath = args[2];
		int maxLevel = Integer.parseInt(args[3]);

		Pager app = new Pager(tocFilePath, maxLevel);

		app.doIt(inputFilePath, outputFilePath, tocFilePath);
	}
}
