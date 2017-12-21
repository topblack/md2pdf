import { Html2PdfConverter, ExportPdfOption, PaperSize } from './Html2PdfConverter'
import { Md2HtmlConverter } from './Md2HtmlConverter'
import { Configuration } from './Models'
const fs = require('fs');
const path = require('path');
const { exec } = require('child_process');


let configFile = process.argv[2];
let config: Configuration = JSON.parse(fs.readFileSync(configFile, 'utf-8'));

let createPDFs = async (config) => {
    let sourceMarkdown = config.source;
    let templateFolder = config.templateDir;
    let outputFolder = config.outputDir;
    if (!fs.existsSync(outputFolder)) {
        fs.mkdirSync(outputFolder);
    }

    let outputName = path.basename(sourceMarkdown, '.md');
    outputFolder = path.resolve(outputFolder);
    templateFolder = path.resolve(templateFolder);

    let mainHtmlOutput = path.join(outputFolder, `main.html`)
    let mainPDFOutput = path.join(outputFolder, 'main.pdf');
    let coverPDFOutput = path.join(outputFolder, 'cover.pdf');
    let overlayPDFOutput = path.join(outputFolder, 'overlay.pdf');

    const html2PdfConverter = new Html2PdfConverter();

    let mainContentPdfOption: ExportPdfOption = new ExportPdfOption(mainPDFOutput);
    mainContentPdfOption.setMargin(1.7, 1, 1.6, 1);

    new Md2HtmlConverter(config).convert(sourceMarkdown, mainHtmlOutput);
    let htmlUrl = `file:///${mainHtmlOutput}`.replace('\\', '/');
    await html2PdfConverter.convert(htmlUrl, mainContentPdfOption, config);

    let htmlPath = path.join(templateFolder, 'overlay.html');
    htmlUrl = `file:///${htmlPath}`.replace('\\', '/');
    let overlayPdfOption: ExportPdfOption = new ExportPdfOption(overlayPDFOutput);
    overlayPdfOption.setMargin(0.8, 1, 0.8, 1);
    await html2PdfConverter.convert(htmlUrl, overlayPdfOption, config);

    let coverPdfOption: ExportPdfOption = new ExportPdfOption(coverPDFOutput);
    htmlPath = path.join(templateFolder, 'cover.html');
    htmlUrl = `file:///${htmlPath}`.replace('\\', '/');
    await html2PdfConverter.convert(htmlUrl, coverPdfOption, config);

    exec(`java -jar tools\\pdfutils.jar ${configFile}`, (err, stdout, stderr) => {
        if (err) {
            console.error(err);
            return;
        }
        console.log(stdout);
    });
};

createPDFs(config);