(() => {
  const authenticated = document.querySelector('meta[name="seesaw-authenticated"]')?.content === 'true';
  const csrf = typeof CSRF === 'undefined' ? {} : CSRF;

  const render = (widget, summary) => {
    const button = widget.querySelector('[data-vote-toggle]');
    const icon = widget.querySelector('[data-vote-icon]');
    const count = widget.querySelector('[data-vote-count]');

    count.textContent = summary.count;
    button.classList.toggle('is-liked', summary.liked);
    button.setAttribute('aria-pressed', String(summary.liked));
    button.setAttribute('aria-label', summary.liked ? '좋아요 취소' : '좋아요');
    icon.classList.toggle('bi-heart-fill', summary.liked);
    icon.classList.toggle('bi-heart', !summary.liked);
    widget.dataset.voteState = 'ready';
    widget.querySelector('[data-vote-status]').textContent = '';
    widget.querySelector('[data-vote-status]').hidden = true;
  };

  const load = async widget => {
    const referenceId = widget.dataset.voteWidget;
    const endpoint = widget.dataset.voteEndpoint;
    const button = widget.querySelector('[data-vote-toggle]');
    const status = widget.querySelector('[data-vote-status]');
    widget.dataset.voteState = 'loading';
    button.disabled = true;
    button.setAttribute('aria-busy', 'true');
    try {
      const response = await fetch(`${endpoint}?referenceId=${encodeURIComponent(referenceId)}`);
      if (!response.ok) {
        throw new Error('좋아요 상태를 불러오지 못했습니다.');
      }
      render(widget, await response.json());
    } catch (error) {
      widget.dataset.voteState = 'error';
      status.textContent = error.message;
      status.hidden = false;
    } finally {
      button.disabled = false;
      button.removeAttribute('aria-busy');
    }
  };

  const toggle = async event => {
    const button = event.currentTarget;
    const widget = button.closest('[data-vote-widget]');
    const referenceId = widget.dataset.voteWidget;

    if (!authenticated) {
      window.location.href = widget.dataset.voteLoginUrl;
      return;
    }

    button.disabled = true;
    button.setAttribute('aria-busy', 'true');
    widget.dataset.voteState = 'loading';
    const method = button.getAttribute('aria-pressed') === 'true' ? 'DELETE' : 'PUT';
    try {
      const response = await fetch(`${widget.dataset.voteEndpoint}/${encodeURIComponent(referenceId)}`, {
        method,
        headers: {
          [csrf.headerName]: csrf.token
        }
      });
      if (response.ok) {
        render(widget, await response.json());
      } else {
        throw new Error('좋아요를 저장하지 못했습니다.');
      }
    } catch (error) {
      widget.dataset.voteState = 'error';
      const status = widget.querySelector('[data-vote-status]');
      status.textContent = error.message;
      status.hidden = false;
    } finally {
      button.disabled = false;
      button.removeAttribute('aria-busy');
    }
  };

  const initialize = widget => {
    if (widget.dataset.voteInitialized === 'true') {
      return;
    }
    widget.dataset.voteInitialized = 'true';
    widget.querySelector('[data-vote-toggle]').addEventListener('click', toggle);
    load(widget);
  };

  window.initializeVoteWidgets = (root = document, force = false) => {
    const widgets = [];
    if (root.matches?.('[data-vote-widget]')) {
      widgets.push(root);
    }
    widgets.push(...root.querySelectorAll('[data-vote-widget]'));
    if (force) {
      widgets.forEach(widget => delete widget.dataset.voteInitialized);
    }
    widgets.forEach(initialize);
  };

  window.initializeVoteWidgets();
})();
