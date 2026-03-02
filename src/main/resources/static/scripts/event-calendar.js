/**
 * @typedef {Object} CalendarRange
 * @property {Date} start 시작일
 * @property {Date} end 종료일
 * @property {string} viewName 뷰 이름 (month, week, day)
 */

/**
 * 현재 캘린더 상태를 기반으로 렌더링할 범위 텍스트를 계산합니다.
 * 
 * @param {CalendarRange} range 캘린더 범위 정보
 * @returns {string} 포맷팅된 날짜 범위 문자열
 */
export const calculateRenderRange = ({ start, end, viewName, viewDate }) => {
  const current = viewDate || start;
  const currentYear = current.getFullYear();
  const currentMonth = current.getMonth() + 1;
  
  if (viewName === 'month') {
    return `${currentYear}.${currentMonth}`;
  }
  
  const startYear = start.getFullYear();
  const startMonth = start.getMonth() + 1;
  const startDate = start.getDate();
  
  const endYear = end.getFullYear();
  const endMonth = end.getMonth() + 1;
  const endDate = end.getDate();
  
  return `${startYear}.${startMonth}.${startDate} ~ ${endYear}.${endMonth}.${endDate}`;
};

/**
 * 캘린더 네비게이션 액션을 생성합니다.
 * 
 * @param {import('@toast-ui/calendar').default} calendar TUI 캘린더 인스턴스
 * @param {Function} onUpdate 상태 변경 시 호출될 콜백 함수
 * @returns {Object} 네비게이션 액션 객체
 */
export const calendarActions = (calendar, onUpdate) => {
  const update = () => {
    if (onUpdate && typeof onUpdate === 'function') {
      onUpdate();
    }
  };

  return {
    /** 이전 범위로 이동 */
    movePrev: () => {
      calendar.prev();
      update();
    },
    /** 다음 범위로 이동 */
    moveNext: () => {
      calendar.next();
      update();
    },
    /** 오늘 날짜로 이동 */
    moveToday: () => {
      calendar.today();
      update();
    },
    /** 뷰 모드 변경 (월/주/일) */
    changeView: (viewName) => {
      calendar.changeView(viewName);
      update();
    }
  };
};

/**
 * API로부터 일정 데이터를 가져와 캘린더에 표시합니다.
 * 
 * @param {import('@toast-ui/calendar').default} calendar TUI 캘린더 인스턴스
 * @param {string} categoryId 카테고리 식별자
 * @returns {Promise<void>}
 */
export const fetchEvents = async (calendar, categoryId) => {
  const rangeStart = calendar.getDateRangeStart().toDate().toISOString();
  const rangeEnd = calendar.getDateRangeEnd().toDate().toISOString();

  try {
    const response = await fetch(`/api/events?categoryId=${categoryId}&start=${rangeStart}&end=${rangeEnd}`);
    const data = await response.json();
    
    calendar.clear();
    const events = data.map(item => ({
      id: item.articleId,
      calendarId: item.id,
      title: item.title,
      start: item.dtStart,
      end: item.dtEnd || item.dtStart,
      category: 'time',
      raw: item
    }));
    calendar.createEvents(events);
  } catch (error) {
    console.error('Failed to fetch events:', error);
  }
};
