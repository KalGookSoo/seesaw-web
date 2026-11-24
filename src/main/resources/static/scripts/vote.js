(() => {
  const widgets = document.querySelectorAll('[data-vote-widget]');
  if (!widgets.length) {
    return;
  }

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
  };

  const load = async widget => {
    const referenceId = widget.dataset.voteWidget;
    const endpoint = widget.dataset.voteEndpoint;
    const response = await fetch(`${endpoint}?referenceId=${encodeURIComponent(referenceId)}`);
    if (!response.ok) {
      return;
    }
    render(widget, await response.json());
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
      }
    } finally {
      button.disabled = false;
    }
  };

  widgets.forEach(widget => {
    widget.querySelector('[data-vote-toggle]').addEventListener('click', toggle);
    load(widget);
  });
})();
