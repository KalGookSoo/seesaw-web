import { formatFileSize } from './file-util.js';

/**
 * @class AttachmentManager
 */
export class AttachmentManager {
  /**
   * @param {Object} options
   * @param {HTMLElement|string} options.container - 렌더링될 컨테이너 요소 또는 셀렉터
   * @param {HTMLInputElement|string} options.input - file input 요소 또는 셀렉터
   * @param {string} [options.title] - 드롭존 타이틀
   * @param {string} [options.helperText] - 드롭존 안내 문구
   */
  constructor({ container, input, title = '파일을 드래그하거나 클릭하여 첨부하세요', helperText = '여러 파일을 한 번에 추가할 수 있습니다.' }) {
    this.container = typeof container === 'string' ? document.querySelector(container) : container;
    this.input = typeof input === 'string' ? document.querySelector(input) : input;

    if (!this.container) {
      throw new Error('Container element not found');
    }
    if (!this.input) {
      throw new Error('Input element not found');
    }

    this.title = title;
    this.helperText = helperText;
    this.files = [];
    this.events = {};

    this.render();
    this.bindEvents();
  }

  /**
   * 이벤트를 등록합니다.
   * @param {string} eventName
   * @param {Function} handler
   * @returns {AttachmentManager}
   */
  on(eventName, handler) {
    if (!this.events[eventName]) {
      this.events[eventName] = [];
    }
    this.events[eventName].push(handler);
    return this;
  }

  /**
   * 이벤트를 트리거합니다.
   * @param {string} eventName
   * @param {...any} args
   */
  trigger(eventName, ...args) {
    if (this.events[eventName]) {
      this.events[eventName].forEach((handler) => handler(...args));
    }
  }

  /**
   * 현재 첨부된 파일 목록을 반환합니다.
   * @returns {File[]}
   */
  getFiles() {
    return [...this.files];
  }

  /**
   * 현재 input의 FileList를 반환합니다.
   * @returns {FileList}
   */
  getFileList() {
    return this.input.files;
  }

  /**
   * 파일을 추가합니다.
   * @param {FileList|File[]} fileList
   */
  addFiles(fileList) {
    const incoming = Array.from(fileList || []);
    if (incoming.length === 0) return;

    this.files = [...this.files, ...incoming];
    this.syncInputFiles();
    this.renderList();
    this.trigger('change', this.getFiles());
  }

  /**
   * 파일을 제거합니다.
   * @param {number} index
   */
  removeFile(index) {
    if (index < 0 || index >= this.files.length) return;
    this.files.splice(index, 1);
    this.syncInputFiles();
    this.renderList();
    this.trigger('change', this.getFiles());
  }

  /**
   * FileList를 동기화합니다.
   */
  syncInputFiles() {
    const dt = new DataTransfer();
    this.files.forEach((file) => dt.items.add(file));
    this.input.files = dt.files;
  }

  /**
   * UI를 렌더링합니다.
   */
  render() {
    const inputWasInContainer = this.input.parentElement === this.container;

    this.container.innerHTML = '';
    if (inputWasInContainer) {
      this.container.appendChild(this.input);
    }
    this.input.classList.add('hidden');
    this.input.setAttribute('multiple', '');

    const wrapper = document.createElement('div');
    wrapper.className = 'attachment-manager space-y-2';
    wrapper.innerHTML = `
      <div class="attachment-dropzone" data-action="dropzone">
        <div class="mb-2">
          <i class="bi bi-cloud-upload fs-2 text-primary"></i>
        </div>
        <p class="mb-1 fw-bold">${this.title}</p>
        <p class="small text-muted mb-0">${this.helperText}</p>
      </div>
      <div class="mt-3">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="mb-0">첨부된 파일</h6>
          <span class="badge bg-secondary" data-role="count"></span>
        </div>
        <ul class="list-group attachment-list" data-role="list"></ul>
      </div>
    `;

    this.container.appendChild(wrapper);
    this.dropzone = this.container.querySelector('[data-action="dropzone"]');
    this.list = this.container.querySelector('[data-role="list"]');
    this.count = this.container.querySelector('[data-role="count"]');

    this.renderList();
  }

  /**
   * 리스트를 렌더링합니다.
   */
  renderList() {
    if (!this.list || !this.count) return;
    this.count.textContent = `${this.files.length}개`;

    if (this.files.length === 0) {
      this.list.innerHTML = `
        <li class="list-group-item text-center text-muted py-3">
          아직 첨부된 파일이 없습니다.
        </li>
      `;
      return;
    }

    this.list.innerHTML = this.files
      .map((file, index) => `
        <li class="list-group-item d-flex justify-content-between align-items-center">
          <div class="text-truncate" style="max-width: 80%;">
            <div class="fw-bold text-truncate">${file.name}</div>
            <small class="text-muted">${formatFileSize(file.size)}</small>
          </div>
          <button type="button" class="btn btn-sm btn-outline-danger" data-action="remove" data-index="${index}">
            <i class="bi bi-trash"></i>
          </button>
        </li>
      `)
      .join('');
  }

  /**
   * 이벤트를 바인딩합니다.
   */
  bindEvents() {
    if (!this.dropzone) return;

    this.dropzone.addEventListener('click', () => this.input.click());

    ['dragenter', 'dragover'].forEach((eventName) => {
      this.dropzone.addEventListener(eventName, (e) => {
        e.preventDefault();
        this.dropzone.classList.add('ring-2', 'ring-green-300', 'bg-green-100');
      });
    });

    ['dragleave', 'dragend', 'drop'].forEach((eventName) => {
      this.dropzone.addEventListener(eventName, (e) => {
        e.preventDefault();
        this.dropzone.classList.remove('ring-2', 'ring-green-300', 'bg-green-100');
      });
    });

    this.dropzone.addEventListener('drop', (e) => {
      const dt = /** @type {DragEvent} */ (e).dataTransfer;
      if (dt && dt.files) {
        this.addFiles(dt.files);
      }
    });

    this.input.addEventListener('change', () => {
      this.addFiles(this.input.files);
    });

    this.container.addEventListener('click', (e) => {
      const target = /** @type {HTMLElement} */ (e.target);
      if (target.dataset.action === 'remove') {
        const index = parseInt(target.dataset.index, 10);
        this.removeFile(index);
      }
    });
  }
}
