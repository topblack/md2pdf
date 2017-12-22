package com.perkinelmer.pdfutils.model;

public class Configuration {
	private String source;
	
	private String outputDir;
	
	private String templateDir;
	
	private Document documentProps;

	private int tocMaxLevel;

	public int getTocMaxLevel() {
		return this.tocMaxLevel;
	}

	public void setTocMaxLevel(int maxLevel) {
		this.tocMaxLevel = maxLevel;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getTemplateDir() {
		return templateDir;
	}

	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}

	public Document getDocumentProps() {
		return documentProps;
	}

	public void setDocumentProps(Document documentProps) {
		this.documentProps = documentProps;
	}

	public class Document {
		private String companyName;
		
		private String productName;
		
		private String documentName;
		
		private String lastUpdated;

		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public String getDocumentName() {
			return documentName;
		}

		public void setDocumentName(String documentName) {
			this.documentName = documentName;
		}

		public String getLastUpdated() {
			return lastUpdated;
		}

		public void setLastUpdated(String lastUpdated) {
			this.lastUpdated = lastUpdated;
		}
	}
}