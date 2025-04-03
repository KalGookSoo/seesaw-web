// npm install uuid
const { v4: uuidv4 } = require('uuid');

const data =                 [
        {
            "version": 0,
            "is_public": true,
            'article_id': 'dea264a3-e47b-4385-b145-9235b126dbed',
            'created_by': 'yasha_bread',
            'content': '관련 링크 !\nhttp://www.djbook.or.kr/main?command=main',
            'created_date': '2024-05-20 13:19:31'
        },
        {
            "version": 0,
            "is_public": true,
            'article_id': 'dea264a3-e47b-4385-b145-9235b126dbed',
            'created_by': 'yasha_bread',
            'content': '대전 포스트잇 이름으로 저 포함 총 7명 도서지원 및 음료지원 하였습니다.',
            'created_date': '2024-05-24 13:01:03'
        },
        {
            "version": 0,
            "is_public": true,
            'article_id': '0e0f5ee1-ea15-4a7a-8a89-8395f8d0456c',
            'created_by': 'yasha_bread',
            'content': '원문 링크\nhttps://www.daejeon.go.kr/drh/board/boardNormalView.do?boardId=normal_0096&menuSeq=1749&pageIndex=1&ntatcSeq=1457401777',
            'created_date': '2024-06-03 09:32:11'
        },
        {
            "version": 0,
            "is_public": true,
            'article_id': 'e49456c8-5d28-4a5f-84d5-3cd26ecb0e2b',
            'created_by': 'yasha_bread',
            'content': '회원가입 완료 !\n저는 독서지도사, 바리스타, 부동산자산관리전문가 도전해보고 싶네요 ㅎ.ㅎ',
            'created_date': '2024-12-04 17:11:57'
        },
        {
            "version": 0,
            "is_public": true,
            'article_id': 'e49456c8-5d28-4a5f-84d5-3cd26ecb0e2b',
            'created_by': '4643one',
            'content': '회원가입완료. 항목3개에 대해선 오늘까지 고민해 볼 예정입니다:)',
            'created_date': '2024-12-04 17:21:45'
        },
        {
            "version": 0,
            "is_public": true,
            'article_id': 'e49456c8-5d28-4a5f-84d5-3cd26ecb0e2b',
            'created_by': 'nuni8461',
            'content': '저는 부동산권리분석사에 도전해봐야겠어요 화이팅✌️',
            'created_date': '2024-12-06 10:00:11'
        },
        {
            "version": 0,
            "is_public": true,
            'article_id': '3f489202-82ed-4b5a-9cb0-3deeb6ce8145',
            'created_by': '4643one',
            'content': '와~~~~(박수)\n많관부!',
            'created_date': '2024-12-20 15:35:59'
        },
        {
            "version": 0,
            "is_public": true,
            'article_id': '3f489202-82ed-4b5a-9cb0-3deeb6ce8145',
            'created_by': 'yasha_bread',
            'content': '&#39;-&#39; 많관부!',
            'created_date': '2024-12-20 20:13:53'
        }
    ]
;

const results = data.map(row => ({'id': uuidv4(), ...row}))
    .map(row => {
        const fields = Object.keys(row);

        const values = Object.values(row).map((value) => {
            if (typeof value === 'string') {
                return `'${value.replace(/'/g, '\'\'')}'`;
            } else if (value === true || value === false) {
                return value;
            } else if (value === null) {
                return 'NULL';
            }
            return value;
        });

        return `INSERT INTO tb_reply (${fields.join(', ')})
                VALUES (${values.join(', ')});`;
    });

results.forEach(result => {console.log(result)});