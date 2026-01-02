document.addEventListener('DOMContentLoaded', /** @param {Event} e */ e => {
  /** @type {Document} */
  const _document = e.target;

  /** @type {string[]} */
  const DATE_TYPES = ['datetime-local', 'date', 'time'];

  /**
   * 날짜, 시간 및 datetime-local 입력 요소의 입력 이벤트를 처리합니다.
   * 현재 값을 기반으로 참조된 입력의 최소 및 최대 값을 업데이트합니다.
   * 입력이 유효하지 않은 경우 유효성 검사 결과를 보고합니다.
   *
   * @param {InputEvent} e - 문서에서 발생한 입력 이벤트.
   */
  const handleDateInput = e => {
    /** @type {HTMLInputElement} */
    const target = e.target;
    if (!DATE_TYPES.includes(target.type)) {
      return;
    }

    const { minRef, maxRef } = target.dataset;

    if (minRef) {
      /** @type {HTMLInputElement | null} */
      const $start = document.getElementById(minRef);
      if ($start) $start.max = target.value;
    }

    if (maxRef) {
      /** @type {HTMLInputElement | null} */
      const $end = document.getElementById(maxRef);
      if ($end) $end.min = target.value;
    }

    if (!target.validity.valid) {
      target.reportValidity();
    }
  };

  /**
   * 대상 요소 내의 모든 <details> 요소를 열거나 닫습니다.
   *
   * @param {string} targetId - 대상 요소의 ID.
   * @param {boolean} isOpen - 열기 여부.
   */
  const toggleDetails = (targetId, isOpen) => {
    const $target = document.getElementById(targetId);
    if ($target) {
      /** @type {HTMLCollectionOf<HTMLDetailsElement>} */
      const $details = $target.getElementsByTagName('details');
      // getElementsByTagName은 Live Collection을 반환하므로 순회 시 주의가 필요하지만,
      // 여기서는 단순히 속성만 변경하므로 안전하게 사용할 수 있습니다.
      // Array.from()을 사용하여 정적 배열로 변환 후 순회합니다.
      Array.from($details).forEach(detail => detail.open = isOpen);
    }
  };

  /**
   * 확장 및 축소 버튼의 클릭 이벤트를 처리합니다.
   *
   * @param {MouseEvent} e - 문서에서 발생한 클릭 이벤트.
   */
  const handleDetailsClick = e => {
    /** @type {HTMLElement} */
    const target = e.target;

    /** @type {HTMLButtonElement | null} */
    const $expandButton = target.closest('button[name="expand"]');
    if ($expandButton && $expandButton.dataset.targetRef) {
      toggleDetails($expandButton.dataset.targetRef, true);
      return;
    }

    /** @type {HTMLButtonElement | null} */
    const $collapseButton = target.closest('button[name="collapse"]');
    if ($collapseButton && $collapseButton.dataset.targetRef) {
      toggleDetails($collapseButton.dataset.targetRef, false);
    }
  };

  _document.addEventListener('input', /** @param {InputEvent} e */ e => {
    handleDateInput(e);
  });

  _document.addEventListener('click', /** @param {MouseEvent} e */ e => {
    handleDetailsClick(e);
  });
});
