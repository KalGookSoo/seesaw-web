/**
 * @typedef {Event & {
 *   prompt: () => Promise<void>,
 *   userChoice: Promise<{ outcome: 'accepted' | 'dismissed', platform: string }>
 * }} BeforeInstallPromptEvent
 */

document.addEventListener('DOMContentLoaded', () => {
  const siteIdMeta = /** @type {HTMLMetaElement} */ (document.querySelector('meta[name="seesaw-site-id"]'));

  const themeColorMeta = /** @type {HTMLMetaElement} */ (document.querySelector('meta[name="theme-color"]'));

  const currentNavigator = /** @type {Navigator & { standalone?: boolean }} */ (window.navigator);

  const siteId = siteIdMeta.content || 'default';
  const themeColor = themeColorMeta.content || '#000000';
  const storageKey = `seesaw:pwa:install-dismissed:${siteId}`;
  const isStandalone = window.matchMedia('(display-mode: standalone)').matches || currentNavigator.standalone === true;

  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/sw.js', { scope: '/' }).catch(error => {
      console.debug('Service worker registration failed.', error);
    });
  }

  if (isStandalone || localStorage.getItem(storageKey) === 'true') {
    return;
  }

  /** @type {BeforeInstallPromptEvent | null} */
  let deferredPrompt = null;

  const removeInstallPrompt = () => {
    document.getElementById('pwa-install-prompt')?.remove();
  };

  const createInstallPrompt = () => {
    if (document.getElementById('pwa-install-prompt')) {
      return;
    }

    /** @type {HTMLDivElement} */
    const prompt = document.createElement('div');
    prompt.id = 'pwa-install-prompt';
    prompt.setAttribute('role', 'status');
    prompt.style.cssText = [
      'position:fixed',
      'left:16px',
      'right:16px',
      'bottom:16px',
      'z-index:1080',
      'display:flex',
      'align-items:center',
      'justify-content:space-between',
      'gap:12px',
      'max-width:560px',
      'margin:0 auto',
      'padding:12px 14px',
      'border:1px solid rgba(15,23,42,0.12)',
      'border-radius:8px',
      'background:#ffffff',
      'box-shadow:0 12px 30px rgba(15,23,42,0.18)',
      'color:#0f172a',
      'font-size:14px',
      'line-height:1.5'
    ].join(';');

    /** @type {HTMLSpanElement} */
    const message = document.createElement('span');
    message.textContent = '홈 화면에 추가해 앱처럼 사용할 수 있습니다.';

    /** @type {HTMLSpanElement} */
    const actions = document.createElement('span');
    actions.style.cssText = 'display:flex;gap:8px;flex:0 0 auto';

    /** @type {HTMLButtonElement} */
    const installButton = document.createElement('button');
    installButton.type = 'button';
    installButton.textContent = '추가';
    installButton.style.cssText = [
      'border:0',
      'border-radius:6px',
      'padding:8px 12px',
      `background:${themeColor}`,
      'color:#ffffff',
      'font-weight:700'
    ].join(';');

    /** @type {HTMLButtonElement} */
    const closeButton = document.createElement('button');
    closeButton.type = 'button';
    closeButton.textContent = '닫기';
    closeButton.style.cssText = [
      'border:1px solid #cbd5e1',
      'border-radius:6px',
      'padding:8px 12px',
      'background:#ffffff',
      'color:#334155',
      'font-weight:700'
    ].join(';');

    installButton.addEventListener('click', async () => {
      if (!deferredPrompt) {
        return;
      }

      deferredPrompt.prompt();
      await deferredPrompt.userChoice;
      deferredPrompt = null;
      localStorage.setItem(storageKey, 'true');
      removeInstallPrompt();
    });

    closeButton.addEventListener('click', () => {
      localStorage.setItem(storageKey, 'true');
      deferredPrompt = null;
      removeInstallPrompt();
    });

    actions.append(installButton, closeButton);
    prompt.append(message, actions);
    document.body.append(prompt);
  };

  window.addEventListener('beforeinstallprompt', event => {
    event.preventDefault();
    deferredPrompt = /** @type {BeforeInstallPromptEvent} */ (event);
    createInstallPrompt();
  });

  window.addEventListener('appinstalled', () => {
    localStorage.setItem(storageKey, 'true');
    deferredPrompt = null;
    removeInstallPrompt();
  });
});
