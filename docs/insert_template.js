// npm install uuid
const { v4: uuidv4 } = require('uuid');

const data = [];

const results = data.map(row => ({'id': uuidv4(), ...row}))
    .map(row => {
        const fields = Object.keys(row);

        const values = Object.values(row).map((value) => {
            // 값 변환 및 처리
            if (typeof value === 'string') {
                return `'${value.replace(/'/g, '\'\'')}'`; // 문자열일 경우 작은 따옴표 escape
            } else if (value === true || value === false) {
                return value;
            } else if (value === null) {
                return 'NULL';
            }
            return value;
        });

        return `INSERT INTO tb_article (${fields.join(', ')})
                VALUES (${values.join(', ')});`;
    });

results.forEach(result => {console.log(result)});