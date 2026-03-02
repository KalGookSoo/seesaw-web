document.addEventListener('DOMContentLoaded', /** @param {Event} e */ e => {
  /** @type {Document} */
  const _document = e.target;

  /** @type {string[]} */
  const DATE_TYPES = ['datetime-local', 'date', 'time'];

  /**
   * input 이벤트를 통해 날짜/시간 입력 값을 동기화합니다.
   * target 요소가 minRef 또는 maxRef dataset 속성을 가지고 있다면
   * 해당 id를 가진 요소의 min 또는 max 속성을 target.value로 업데이트합니다.
   * 
   * @param {HTMLInputElement} target - 이벤트가 발생한 입력 요소
   */
  const syncMinMax = target => {
    const { minRef, maxRef } = target.dataset;
    const value = target.value;

    const updateRef = (refId, attr) => {
      const $el = document.getElementById(refId);
      $el && ($el[attr] = value);
      $el?.value && !$el.validity.valid && $el.reportValidity();
    };

    minRef && updateRef(minRef, 'max');
    maxRef && updateRef(maxRef, 'min');

    !target.validity.valid && target.reportValidity();
  };

  /**
   * 날짜, 시간 및 datetime-local 입력 요소의 입력 이벤트를 처리합니다.
   * 현재 값을 기반으로 참조된 입력의 최소 및 최대 값을 업데이트합니다.
   * 입력이 유효하지 않은 경우 유효성 검사 결과를 보고합니다.
   *
   * @param {InputEvent} e - 문서에서 발생한 입력 이벤트.
   */
  const handleDateInput = e => DATE_TYPES.includes(e.target.type) && syncMinMax(e.target);

  /**
   * 대상 요소 내의 모든 <details> 요소를 열거나 닫습니다.
   *
   * @param {string} targetId - 대상 요소의 ID.
   * @param {boolean} isOpen - 열기 여부.
   */
  const toggleDetails = (targetId, isOpen) => 
    Array.from(document.getElementById(targetId)?.getElementsByTagName('details') || [])
      .forEach(detail => detail.open = isOpen);

  /**
   * 확장 및 축소 버튼의 클릭 이벤트를 처리합니다.
   *
   * @param {MouseEvent} e - 문서에서 발생한 클릭 이벤트.
   */
  const handleDetailsClick = e => {
    const target = e.target;
    const $btn = target.closest('button[name="expand"], button[name="collapse"]');
    const { targetRef } = $btn?.dataset || {};
    
    $btn && targetRef && toggleDetails(targetRef, $btn.name === 'expand');
  };

  _document.addEventListener('input', e => handleDateInput(e));

  _document.addEventListener('change', e => handleDateInput(e));

  // 초기 로드 시 모든 날짜/시간 관련 입력 요소에 대해 동기화를 수행합니다.
  _document.querySelectorAll('input').forEach(input => DATE_TYPES.includes(input.type) && syncMinMax(input));

  _document.addEventListener('click', e => handleDetailsClick(e));
});
