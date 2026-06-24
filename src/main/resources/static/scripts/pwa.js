/**
 * @typedef {Event & {
 *   prompt: () => Promise<void>,
 *   userChoice: Promise<{ outcome: 'accepted' | 'dismissed', platform: string }>
 * }} BeforeInstallPromptEvent
 */

/**
 * @typedef {{
 *   publicKey: string
 * }} VapidPublicKeyResponse
 */

/**
 * @typedef {{
 *   endpoint: string,
 *   keys: {
 *     p256dh: string,
 *     auth: string
 *   }
 * }} PushSubscriptionJson
 */

document.addEventListener('DOMContentLoaded', async () => {
  const themeColorMeta = /** @type {HTMLMetaElement} */ (document.querySelector('meta[name="theme-color"]'));
  const siteIdMeta = /** @type {HTMLMetaElement} */ (document.querySelector('meta[name="seesaw-site-id"]'));

  const currentNavigator = /** @type {Navigator & { standalone?: boolean }} */ (window.navigator);

  const themeColor = themeColorMeta.content || '#000000';
  const siteId = siteIdMeta.content;
  const isStandalone = window.matchMedia('(display-mode: standalone)').matches || currentNavigator.standalone === true;

  /** @type {BeforeInstallPromptEvent | null} */
  let deferredPrompt = null;

  /**
   * @returns {Promise<ServiceWorkerRegistration | null>}
   */
  const registerServiceWorker = async () => {
    if (!('serviceWorker' in navigator)) {
      return null;
    }

    return navigator.serviceWorker.register('/sw.js', { scope: '/' });
  };

  const getVapidPublicKey = async () => {
    const response = await fetch('/api/push/vapid-public-key', {
      headers: {
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`VAPID 공개 키를 조회할 수 없습니다. status=${response.status}`);
    }

    const data = /** @type {VapidPublicKeyResponse} */ (await response.json());
    return data.publicKey;
  };

  /**
   * @param {PushSubscription} subscription
   * @returns {Promise<void>}
   */
  const savePushSubscription = async subscription => {
    const subscriptionJson = /** @type {PushSubscriptionJson} */ (subscription.toJSON());
    const response = await fetch('/api/push/subscriptions', {
      method: 'POST',
      credentials: 'same-origin',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        siteId,
        endpoint: subscriptionJson.endpoint,
        keys: {
          p256dh: subscriptionJson.keys.p256dh,
          auth: subscriptionJson.keys.auth
        }
      })
    });

    if (!response.ok) {
      throw new Error(`푸시 구독 정보를 저장할 수 없습니다. status=${response.status}`);
    }
  };

  /**
   * Base64 URL-safe 문자열을 PushManager가 요구하는 Uint8Array로 변환합니다.
   *
   * @param {string} base64UrlString
   * @returns {Uint8Array}
   */
  const urlBase64ToUint8Array = base64UrlString => {
    const padding = '='.repeat((4 - base64UrlString.length % 4) % 4);
    const base64 = (base64UrlString + padding)
      .replace(/-/g, '+')
      .replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);

    for (let i = 0; i < rawData.length; i += 1) {
      outputArray[i] = rawData.charCodeAt(i);
    }

    return outputArray;
  };

  /**
   * @param {ServiceWorkerRegistration} registration
   * @returns {Promise<PushSubscription>}
   */
  const subscribePushNotification = async registration => {
    if (!('Notification' in window) || !('PushManager' in window) || !('serviceWorker' in navigator)) {
      throw new Error('이 브라우저는 Web Push를 지원하지 않습니다.');
    }

    const permission = await Notification.requestPermission();
    if (permission !== 'granted') {
      throw new Error(`알림 권한이 허용되지 않았습니다. permission=${permission}`);
    }

    const currentSubscription = await registration.pushManager.getSubscription();
    if (currentSubscription) {
      await savePushSubscription(currentSubscription);
      window.dispatchEvent(new CustomEvent('seesaw:push-subscribed', { detail: currentSubscription.toJSON() }));
      return currentSubscription;
    }

    const publicKey = await getVapidPublicKey();
    const subscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlBase64ToUint8Array(publicKey)
    });

    await savePushSubscription(subscription);
    window.dispatchEvent(new CustomEvent('seesaw:push-subscribed', { detail: subscription.toJSON() }));
    return subscription;
  };

  const createButton = (text, variant = 'primary') => {
    /** @type {HTMLButtonElement} */
    const button = document.createElement('button');
    button.type = 'button';
    button.textContent = text;
    button.style.cssText = [
      `border:${variant === 'primary' ? '0' : '1px solid #cbd5e1'}`,
      'border-radius:6px',
      'padding:8px 12px',
      `background:${variant === 'primary' ? themeColor : '#ffffff'}`,
      `color:${variant === 'primary' ? '#ffffff' : '#334155'}`,
      'font-weight:700'
    ].join(';');
    return button;
  };

  const createPrompt = id => {
    /** @type {HTMLDivElement} */
    const prompt = document.createElement('div');
    prompt.id = id;
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
    return prompt;
  };

  const removeInstallPrompt = () => {
    document.getElementById('pwa-install-prompt')?.remove();
  };

  const createInstallPrompt = () => {
    if (document.getElementById('pwa-install-prompt')) {
      return;
    }

    const prompt = createPrompt('pwa-install-prompt');

    /** @type {HTMLSpanElement} */
    const message = document.createElement('span');
    message.textContent = '홈 화면에 추가해 앱처럼 사용할 수 있습니다.';

    /** @type {HTMLSpanElement} */
    const actions = document.createElement('span');
    actions.style.cssText = 'display:flex;gap:8px;flex:0 0 auto';

    const installButton = createButton('추가');
    const closeButton = createButton('닫기', 'secondary');

    installButton.addEventListener('click', async () => {
      if (!deferredPrompt) {
        return;
      }

      deferredPrompt.prompt();
      await deferredPrompt.userChoice;
      deferredPrompt = null;
      removeInstallPrompt();

      const registration = await navigator.serviceWorker.ready;
      try {
        await subscribePushNotification(registration);
      } catch (error) {
        console.debug('Push subscription failed.', error);
      }
    });

    closeButton.addEventListener('click', () => {
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
    if (isStandalone) {
      return;
    }
    createInstallPrompt();
  });

  window.addEventListener('appinstalled', () => {
    deferredPrompt = null;
    removeInstallPrompt();
  });

  try {
    const registration = await registerServiceWorker();
    if (registration && 'Notification' in window && Notification.permission !== 'denied') {
      await subscribePushNotification(registration);
    }
  } catch (error) {
    console.debug('Push setup failed.', error);
  }
});
