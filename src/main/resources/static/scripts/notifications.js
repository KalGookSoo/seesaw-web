(() => {
  const siteId = document.querySelector('meta[name="seesaw-site-id"]')?.content;
  const csrf = typeof CSRF === 'undefined' ? {} : CSRF;
  const typeLabels = {
    ARTICLE_CREATED: '새 게시글',
    REPLY_CREATED: '새 댓글',
    REPORT_CREATED: '새 신고',
    INQUIRY_CREATED: '새 문의',
    ARTICLE_UPDATED: '게시글 변경',
    ARTICLE_DELETED: '게시글 삭제',
    SCHEDULE_UPDATED: '일정 변경',
    SCHEDULE_REMINDER: '일정 알림'
  };
  const typeIcons = {
    ARTICLE_CREATED: 'bi-file-earmark-text',
    REPLY_CREATED: 'bi-chat-square-text',
    REPORT_CREATED: 'bi-flag',
    INQUIRY_CREATED: 'bi-question-circle',
    ARTICLE_UPDATED: 'bi-pencil-square',
    ARTICLE_DELETED: 'bi-file-earmark-x',
    SCHEDULE_UPDATED: 'bi-calendar-event',
    SCHEDULE_REMINDER: 'bi-alarm'
  };

  if (!siteId) {
    return;
  }

  const request = async (url, options = {}) => {
    const response = await fetch(url, {
      ...options,
      headers: {
        ...(options.body ? {'Content-Type': 'application/json'} : {}),
        ...(options.method && options.method !== 'GET' ? {[csrf.headerName]: csrf.token} : {}),
        ...(options.headers || {})
      }
    });
    if (!response.ok) {
      throw new Error('알림 정보를 불러오지 못했습니다.');
    }
    return response.status === 204 ? null : response.json();
  };

  const readItems = data => data?._embedded?.notificationResponseList || data?._embedded?.notifications || data?.content || [];

  const formatDate = value => {
    if (!value) {
      return '';
    }
    return new Intl.DateTimeFormat('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
    }).format(new Date(value));
  };

  const createItem = notification => {
    const item = document.createElement('button');
    item.type = 'button';
    item.className = `notification-item${notification.read ? '' : ' is-unread'}`;
    item.dataset.notificationId = notification.id;
    item.dataset.notificationUrl = notification.targetUrl || '';
    item.innerHTML = `
      <span class="notification-item__icon"><i class="bi ${typeIcons[notification.type] || 'bi-bell'}" aria-hidden="true"></i></span>
      <span class="notification-item__body">
        <span class="notification-item__meta">${typeLabels[notification.type] || '활동'} · ${formatDate(notification.createdDate)}</span>
        <strong class="notification-item__title"></strong>
        <span class="notification-item__message"></span>
      </span>
      <span class="notification-item__state">${notification.read ? '' : '새 알림'}</span>
    `;
    item.querySelector('.notification-item__title').textContent = notification.title;
    item.querySelector('.notification-item__message').textContent = notification.message;
    item.addEventListener('click', () => openNotification(item));
    return item;
  };

  const openNotification = async item => {
    if (item.classList.contains('is-unread')) {
      try {
        await request(`/api/notifications/${item.dataset.notificationId}/read?siteId=${encodeURIComponent(siteId)}`, {method: 'PATCH'});
        item.classList.remove('is-unread');
        item.querySelector('.notification-item__state').textContent = '';
        await refreshUnreadCount();
      } catch (error) {
        // 이동은 알림 읽음 처리 실패와 무관하게 허용합니다.
      }
    }
    const targetUrl = item.dataset.notificationUrl || '/notifications';
    window.location.href = targetUrl;
  };

  const renderList = (container, items) => {
    container.innerHTML = '';
    if (!items.length) {
      const empty = document.createElement('p');
      empty.className = 'notification-empty';
      empty.textContent = '새로운 알림이 없습니다.';
      container.appendChild(empty);
      return;
    }
    items.forEach(notification => container.appendChild(createItem(notification)));
  };

  const refreshUnreadCount = async () => {
    const count = await request(`/api/notifications/unread-count?siteId=${encodeURIComponent(siteId)}`);
    document.querySelectorAll('[data-notification-count]').forEach(element => {
      element.textContent = count;
      element.hidden = count < 1;
    });
    document.querySelectorAll('[data-notification-page-count]').forEach(element => {
      element.textContent = count > 0 ? `읽지 않은 알림 ${count}개` : '읽지 않은 알림이 없습니다.';
    });
  };

  const loadRecent = async () => {
    const containers = document.querySelectorAll('[data-notification-list]');
    if (!containers.length) {
      return;
    }
    try {
      const data = await request(`/api/notifications?siteId=${encodeURIComponent(siteId)}&page=0&size=10`);
      const items = readItems(data);
      containers.forEach(container => renderList(container, items));
      await refreshUnreadCount();
    } catch (error) {
      containers.forEach(container => {
        container.innerHTML = '<p class="notification-empty notification-empty--error">알림을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.</p>';
      });
    }
  };

  const markAllAsRead = async () => {
    await request(`/api/notifications/read-all?siteId=${encodeURIComponent(siteId)}`, {method: 'POST'});
    document.querySelectorAll('.notification-item.is-unread').forEach(item => {
      item.classList.remove('is-unread');
      item.querySelector('.notification-item__state').textContent = '';
    });
    await refreshUnreadCount();
  };

  const initializeMenu = () => {
    document.querySelectorAll('[data-notification-toggle]').forEach(toggle => {
      const popover = document.getElementById(toggle.getAttribute('aria-controls'));
      toggle.addEventListener('click', async event => {
        event.stopPropagation();
        const isHidden = popover.hidden;
        popover.hidden = !isHidden;
        toggle.setAttribute('aria-expanded', String(isHidden));
        if (isHidden) {
          await loadRecent();
        }
      });
    });
    document.querySelectorAll('[data-notification-read-all]').forEach(button => {
      button.addEventListener('click', async () => {
        try {
          await markAllAsRead();
        } catch (error) {
          button.textContent = '다시 시도';
        }
      });
    });
    document.addEventListener('click', event => {
      document.querySelectorAll('[data-notification-popover]').forEach(popover => {
        const menu = popover.closest('.notification-menu');
        if (!menu.contains(event.target)) {
          popover.hidden = true;
          menu.querySelector('[data-notification-toggle]')?.setAttribute('aria-expanded', 'false');
        }
      });
    });
  };

  const initializePage = () => {
    document.querySelectorAll('[data-notification-page-read-all]').forEach(button => {
      button.addEventListener('click', async () => {
        try {
          await markAllAsRead();
        } catch (error) {
          button.textContent = '다시 시도';
        }
      });
    });
    loadRecent();
  };

  const initializeSettings = () => {
    document.querySelectorAll('[data-notification-preference]').forEach(card => {
      const type = card.dataset.notificationPreference;
      card.querySelector('[data-notification-type-label]').textContent = typeLabels[type] || '활동';
      card.querySelector('[data-notification-type-title]').textContent = `${typeLabels[type] || '활동'} 알림`;
      const webPush = card.querySelector('[data-notification-channel="webPush"]');
      const email = card.querySelector('[data-notification-channel="email"]');
      const status = card.querySelector('[data-notification-preference-status]');

      request(`/api/notification-preferences?siteId=${encodeURIComponent(siteId)}&type=${type}`)
        .then(preference => {
          webPush.checked = preference.webPushEnabled;
          email.checked = preference.emailEnabled;
        })
        .catch(() => status.textContent = '설정을 불러오지 못했습니다.');

      const update = async () => {
        const previous = {webPush: webPush.checked, email: email.checked};
        webPush.disabled = true;
        email.disabled = true;
        status.textContent = '저장 중입니다.';
        try {
          const preference = await request(`/api/notification-preferences?siteId=${encodeURIComponent(siteId)}`, {
            method: 'PUT',
            body: JSON.stringify({type, webPushEnabled: webPush.checked, emailEnabled: email.checked})
          });
          webPush.checked = preference.webPushEnabled;
          email.checked = preference.emailEnabled;
          status.textContent = '저장되었습니다.';
        } catch (error) {
          webPush.checked = previous.webPush;
          email.checked = previous.email;
          status.textContent = '저장하지 못했습니다. 다시 시도해주세요.';
        } finally {
          webPush.disabled = false;
          email.disabled = false;
        }
      };
      webPush.addEventListener('change', update);
      email.addEventListener('change', update);
    });
  };

  document.addEventListener('DOMContentLoaded', () => {
    initializeMenu();
    initializePage();
    initializeSettings();
    refreshUnreadCount().catch(() => {});
  });
})();
