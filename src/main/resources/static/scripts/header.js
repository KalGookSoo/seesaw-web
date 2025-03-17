document.addEventListener('DOMContentLoaded', e => {
    const header = e.target.getElementById('header');
    e.target.addEventListener('scroll', e => {
        const methodName = window.scrollY > 0 ? 'add' : 'remove';
        header.classList[methodName]('scrolled');
    });
});