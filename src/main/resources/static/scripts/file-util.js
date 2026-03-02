/**
 * 파일 사이즈를 사람이 읽기 쉬운 형식으로 변환합니다.
 * @param {number} size
 * @returns {string}
 */
export const formatFileSize = (size) => {
  if (size < 1024) return `${size} B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
  return `${(size / (1024 * 1024)).toFixed(1)} MB`;
};
