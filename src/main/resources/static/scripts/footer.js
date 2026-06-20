/**
 * 쿠키에서 특정 키의 값을 읽어옵니다.
 * @param {string} name
 * @returns {string|null}
 */
function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
}

/**
 * 신고 메일을 전송합니다.
 * @param {string} siteId - 사이트 식별자
 * @param {string} title - 메일 제목
 * @param {string} content - 메일 본문
 * @returns {Promise<boolean>} 성공 여부
 */
export async function sendToReport(siteId, title, content) {
  const headers = {
    'Content-Type': 'application/json'
  };

  try {
    const response = await fetch('/api/mail/send-to-report', {
      method: 'POST',
      headers: headers,
      body: JSON.stringify({ siteId, title, content })
    });
    return response.ok;
  } catch (error) {
    console.error('헬프데스크 문의에 실패했습니다.', error);
    return false;
  }
}

/**
 * 헬프데스크 문의 메일을 전송합니다.
 * @param {string} title - 메일 제목
 * @param {string} content - 메일 본문
 * @returns {Promise<boolean>} 성공 여부
 */
export async function sendToHelpdesk(title, content) {
  const headers = {
    'Content-Type': 'application/json'
  };

  try {
    const response = await fetch('/api/mail/send-to-helpdesk', {
      method: 'POST',
      headers: headers,
      body: JSON.stringify({ title, content })
    });
    return response.ok;
  } catch (error) {
    console.error('헬프데스크 문의에 실패했습니다.', error);
    return false;
  }
}
