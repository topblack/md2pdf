const marked = require('marked');
const fs = require('fs');
const path = require('path');
const toc = require('markdown-toc');
import { Configuration } from './Models'


/*
// Default options of marked
marked.setOptions({
    renderer: new marked.Renderer(),
    gfm: true,
    tables: true,
    breaks: false,
    pedantic: false,
    sanitize: false,
    smartLists: true,
    smartypants: false
  });
*/

interface TocItem {
    content: string;
    slug: string;
    lvl: number;
}

export class Md2HtmlConverter {
    templateFolderAbsPath: string;
    config: Configuration;

    constructor (config: Configuration) {
        this.templateFolderAbsPath = path.resolve(config.templateDir);
        this.config = config;
    }

    private getTemplateHtml = (name: string) => {
        let contentPath = path.join(this.templateFolderAbsPath, `${name}.html`);
        let contentHtml = fs.readFileSync(contentPath, 'utf-8');
        contentHtml = this.fixLinkUrls(contentHtml, this.templateFolderAbsPath);

        return contentHtml;
    }

    public convert = (source: string, target: string) => {
        let markdownString = fs.readFileSync(source, 'utf-8');
        let mdHtmlString = marked(markdownString);
        let mdParentDir = path.resolve(path.dirname(source));
        mdHtmlString = this.fixLinkUrls(mdHtmlString, mdParentDir);

        let tocItems = toc(markdownString, {stripHeadingTags: false}).json;
        let tocHtml = this.getTOCHtml(tocItems);
        let coverHtml = this.getTemplateHtml('cover');
        coverHtml = this.fillDocumentProps(coverHtml);
        let mainHtml = this.getTemplateHtml('main');

        let resultHtml = mainHtml.replace('$body', mdHtmlString)
                            .replace('$toc', tocHtml)
                            .replace('$cover', coverHtml);

        fs.writeFileSync(path.join(path.dirname(target), 'toc.json'), JSON.stringify(tocItems));
        fs.writeFileSync(target, resultHtml);
    }

    private fillDocumentProps = (content: string): string => {
        return content.replace('$documentName', this.config.documentProps.documentName)
            .replace('$companyName', this.config.documentProps.companyName)
            .replace('$lastUpdated', this.config.documentProps.lastUpdated)
            .replace('$productName', this.config.documentProps.productName);
    }

    private fixLinkUrls = (body: string, basePath: string) => {
        basePath = basePath.replace(/\\/g, '/');
        return body.replace(/img\ssrc=\"(?!http)/g, `img src=\"file:///${basePath}/`)
          .replace(/script\ssrc=\"(?!http)/g, `script src=\"file:///${basePath}/`)
          .replace(/stylesheet\"\shref=\"(?!http)/g, `stylesheet" href="file:///${basePath}/`);
    }

    private getTOCHtml = (tocItems: TocItem[]) => {
        let html = '<ul class="tocList">\r\n';
        for (let i = 0; i < tocItems.length; i++) {
            let item = tocItems[i] as TocItem;
            if (item.lvl > 3) {
                continue;
            }
            html += `<li class="tocItemL${item.lvl}">`;
            html += '<a href="#';
            let link = toc.linkify(item.content);
            console.info(`${item.content} -> ${link}`);
            html += link.replace(/[|&:;$%@"<>()+,\s\.]+/g, '-').toLowerCase();
            html += '">';
            html += item.content;
            html += '</a>'
            html += '</li>\r\n';
        }

        html += '</ul>\r\n';

        return html;
    }

    public test = () => {
    }
}

//new Md2HtmlConverter().test();

/*
const markdownString = '```js\n console.log("hello"); \n```';

// Async highlighting with pygmentize-bundled
marked.setOptions({
    highlight: function (code, lang, callback) {
        require('pygmentize-bundled')({ lang: lang, format: 'html' }, code, function (err, result) {
            callback(err, result.toString());
        });
    }
});

// Using async version of marked
marked(markdownString, function (err, content) {
    if (err) throw err;
    console.log(content);
});

// Synchronous highlighting with highlight.js
marked.setOptions({
    highlight: function (code) {
        return require('highlight.js').highlightAuto(code).value;
    }
});

console.log(marked(markdownString));
*/