import { Configuration } from "./Models";

const puppeteer = require('puppeteer');

export class PaperSize {
    width: number;
    height: number;

    /*
    All possible units are:
    px - pixel
    in - inch
    cm - centimeter
    mm - millimeter
    */
    unit: string;

    constructor(width: number, height: number, unit?: string) {
        this.width = width;
        this.height = height;

        if (!unit) {
            unit = 'in';
        }
        this.unit = unit;
    }

    public getWidth = (): string => {
        return `${this.width}${this.unit}`;
    }

    public getHeight = (): string => {
        return `${this.height}${this.unit}`;
    }

    /*
    Letter: 8.5in x 11in
    Legal: 8.5in x 14in
    Tabloid: 11in x 17in
    Ledger: 17in x 11in
    A0: 33.1in x 46.8in
    A1: 23.4in x 33.1in
    A2: 16.5in x 23.4in
    A3: 11.7in x 16.5in
    A4: 8.27in x 11.7in
    A5: 5.83in x 8.27in
    A6: 4.13in x 5.83in
    */
    static Letter: PaperSize = new PaperSize(8.5, 11);
    static A4: PaperSize = new PaperSize(8.27, 11.7);
}

export class ExportPdfOption {
    constructor(targetPath: string, size?: PaperSize) {
        this.path = targetPath;

        if (!size) {
            size = PaperSize.A4;
        }

        this.width = size.getWidth();
        this.height = size.getHeight();

        this.setMargin(0, 0, 0, 0);
    }

    public setMargin = (top: number, right: number, bottom: number, left: number, unit?: string, ) => {
        if (!unit) {
            unit = 'in';
        }

        this.margin = {
            top: `${top}${unit}`, left: `${left}${unit}`,
            right: `${right}${unit}`, bottom: `${bottom}${unit}`
        };
    }

    path: string;
    width: string;
    height: string;
    margin: {
        top: string;
        left: string;
        right: string;
        bottom: string;
    };
};

export class Html2PdfConverter {
    public convert = async (url: string, option: ExportPdfOption, config: Configuration) => {
        const browser = await puppeteer.launch();
        try {
            const page = await browser.newPage();
            await page.goto(url, { waitUntil: 'networkidle2' });
            await page.evaluate(updateDocumentPropertiesInHtml, config);
            await page.pdf(option);
        } catch (error) {
            console.error(error);
        } finally {
            browser.close();
        }
    };
};

function updateDocumentPropertiesInHtml(config: Configuration) {
    let updateProperty = (propName: string, propValue: string) => {
        let nodes = document.getElementsByClassName(propName);
        for (let i = 0; i < nodes.length; i++) {
            nodes[i].innerHTML = propValue;
        }
    }

    updateProperty('companyName', config.documentProps.companyName);
    updateProperty('productName', config.documentProps.productName);
    updateProperty('documentName', config.documentProps.documentName);
    updateProperty('lastUpdated', config.documentProps.lastUpdated);
}