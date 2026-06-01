const fs = require('fs');
const path = require('path');
const svg2vectordrawable = require('svg2vectordrawable');

const svgDir = '../assets';
const destDir = '../teknisio_android/app/src/main/res/drawable';

fs.readdirSync(svgDir).forEach(file => {
    if (file.endsWith('.svg')) {
        const svgCode = fs.readFileSync(path.join(svgDir, file), 'utf8');
        svg2vectordrawable(svgCode).then(xmlCode => {
            let baseName = file.replace('.svg', '').toLowerCase();
            baseName = baseName.replace(/ /g, '_').replace(/-/g, '_');
            const destPath = path.join(destDir, `ic_cat_${baseName}.xml`);
            fs.writeFileSync(destPath, xmlCode);
            console.log(`Converted ${file} to ic_cat_${baseName}.xml`);
        }).catch(err => {
            console.error(`Error converting ${file}:`, err);
        });
    }
});
