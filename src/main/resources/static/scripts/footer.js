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
 * POST 요청
 * @param requestUri
 * @param payload
 * @returns {Promise<boolean>}
 */
export async function requestApi(requestUri, payload) {
  const headers = {
    'Content-Type': 'application/json'
  };

  try {
    const response = await fetch(requestUri, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(payload)
    });
    return response.ok;
  } catch (error) {
    console.error('통신에 실패했습니다.', error);
    return false;
  }
}
