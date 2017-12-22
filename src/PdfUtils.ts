const path = require('path');
const { exec } = require('child_process');

export class PdfUtils {
    private pdfUtilsPath: string;

    constructor () {
        let thisScript = process.argv[1];
        let appHomeDir = path.dirname(path.resolve(thisScript));
        this.pdfUtilsPath = path.resolve(path.join(appHomeDir, 'pdfutils.jar'));
    }

    public process = (configFile: string, cb: any) => {
        exec(`java -jar ${this.pdfUtilsPath} ${configFile}`, (err, stdout, stderr) => {
            console.log(stdout);
            if (err) {
                console.error(err);
                return;
            }
            cb();
        });
    }
}