const cid = '4h2he818';
const comments = [];

const fetchComments = async () => {
    const promises = [];

    for (let i = 1; i <= 43; i++) {
        promises.push(
            fetch('https://www.modoo.at/apps/board/getCommentList.json', {
                headers: {
                    accept: 'application/json, text/javascript, */*; q=0.01',
                    'accept-language': 'ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7',
                    'content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    priority: 'u=1, i',
                    'sec-ch-ua': '"Chromium";v="134", "Not:A-Brand";v="24", "Google Chrome";v="134"',
                    'sec-ch-ua-mobile': '?0',
                    'sec-ch-ua-platform': '"macOS"',
                    'sec-fetch-dest': 'empty',
                    'sec-fetch-mode': 'cors',
                    'sec-fetch-site': 'same-origin',
                    'x-requested-with': 'XMLHttpRequest'
                },
                referrer: 'https://www.modoo.at/management',
                referrerPolicy: 'unsafe-url',
                body: `cid=${cid}&messageNo=${i}`,
                method: 'POST',
                mode: 'cors',
                credentials: 'include'
            })
                .then(async res => {
                    const responseBody = await res.text();
                    const data = JSON.parse(responseBody);
                    return data.commentList;
                })
        );
    }


    const results = await Promise.all(promises);
    results.forEach(commentList => comments.push(...commentList));
    console.log(comments.sort((a, b) => a.messageNo > b.messageNo));
};


fetchComments();