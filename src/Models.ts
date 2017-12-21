export interface Configuration {
    source: string;
    outputDir: string;
    templateDir: string;
    documentProps: DocumentProperties;
}

interface DocumentProperties {
    companyName: string;
    productName: string;
    documentName: string;
    lastUpdated: string;
}