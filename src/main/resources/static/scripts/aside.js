document.addEventListener('DOMContentLoaded', e => {
    document.addEventListener('click', e => {
        const selector = Object.keys(clickHandlers).find(selector => e.target.closest(selector));
        clickHandlers[selector]?.(e);
    });

    const clickHandlers = {
        '.open-aside': e => {
            const asideDialog = document.getElementById('aside-dialog');
            asideDialog.showModal();
        },
        '#aside button[name="close"]': e => {
            const asideDialog = document.getElementById('aside-dialog');
            asideDialog.close();
        }
    };
});