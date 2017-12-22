import { Html2PdfConverter, ExportPdfOption, PaperSize } from './Html2PdfConverter'
import { Md2HtmlConverter } from './Md2HtmlConverter'
import { Configuration } from './Models'
import { PdfUtils } from './PdfUtils'
const fs = require('fs');
const path = require('path');

class Md2PdfConverter {
    private config: Configuration;

    constructor(configFile: string) {
        this.config = JSON.parse(fs.readFileSync(configFile, 'utf-8'));
    }

    private removeIntermediates = (parentFolder: string) => {
        let intermediates: string[] = fs.readdirSync(parentFolder);
        for (let i = 0; i < intermediates.length; i++) {
            fs.unlinkSync(path.join(parentFolder, intermediates[i]));
        }
        fs.rmdirSync(parentFolder);
    }

    private finalize = () => {
        let finalPdfPath = path.join(this.config.outputDir, 'final.pdf');
        let finalPdfName = `${this.config.documentProps.productName} ${this.config.documentProps.documentName}.pdf`;
        let originalOutputFolder = path.join(this.config.outputDir, '..', finalPdfName);
        fs.renameSync(finalPdfPath, originalOutputFolder);
        //this.removeIntermediates(this.config.outputDir);
    }

    public convert = async () => {
        let sourceMarkdown = this.config.source.replace('\\', path.sep);
        this.config.source = sourceMarkdown;
        let templateFolder = this.config.templateDir.replace('\\', path.sep);
        this.config.templateDir = templateFolder;
        let outputFolder = this.config.outputDir.replace('\\', path.sep);
        this.config.outputDir = outputFolder;
        if (!fs.existsSync(outputFolder)) {
            fs.mkdirSync(outputFolder);
        }

        let outputName = path.basename(sourceMarkdown, '.md');
        outputFolder = path.resolve(outputFolder);
        outputFolder = path.join(outputFolder, '.intermediates');
        console.info(outputFolder);
        this.config.outputDir = outputFolder;
        if (!fs.existsSync(outputFolder)) {
            fs.mkdirSync(outputFolder);
        }
        let intermediatesConfig: string = path.join(outputFolder, 'config.json');
        fs.writeFileSync(intermediatesConfig, JSON.stringify(this.config));

        templateFolder = path.resolve(templateFolder);
        let mainHtmlOutput = path.join(outputFolder, 'main.html')
        let mainPDFOutput = path.join(outputFolder, 'main.pdf');
        let coverPDFOutput = path.join(outputFolder, 'cover.pdf');
        let overlayPDFOutput = path.join(outputFolder, 'overlay.pdf');
        const html2PdfConverter = new Html2PdfConverter();

        let mainContentPdfOption: ExportPdfOption = new ExportPdfOption(mainPDFOutput);
        mainContentPdfOption.setMargin(1.7, 1, 1.6, 1);

        new Md2HtmlConverter(this.config).convert(sourceMarkdown, mainHtmlOutput);
        let htmlUrl = `file:///${mainHtmlOutput}`.replace('\\', '/');
        await html2PdfConverter.convert(htmlUrl, mainContentPdfOption, this.config);

        let htmlPath = path.join(templateFolder, 'overlay.html');
        htmlUrl = `file:///${htmlPath}`.replace('\\', '/');
        let overlayPdfOption: ExportPdfOption = new ExportPdfOption(overlayPDFOutput);
        overlayPdfOption.setMargin(0.8, 1, 0.8, 1);
        await html2PdfConverter.convert(htmlUrl, overlayPdfOption, this.config);

        let coverPdfOption: ExportPdfOption = new ExportPdfOption(coverPDFOutput);
        htmlPath = path.join(templateFolder, 'cover.html');
        htmlUrl = `file:///${htmlPath}`.replace('\\', '/');
        await html2PdfConverter.convert(htmlUrl, coverPdfOption, this.config);

        new PdfUtils().process(intermediatesConfig, this.finalize);
    };
}

new Md2PdfConverter(process.argv[2]).convert();