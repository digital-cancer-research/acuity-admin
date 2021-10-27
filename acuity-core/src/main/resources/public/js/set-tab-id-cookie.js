$(document).mousemove(() => {
    setBrowserTabIdCookie()
})

$(document).click(() => {
    setBrowserTabIdCookie()
})

setBrowserTabIdCookie = () => {
    generateWindowID()
    let pageId = sessionStorage.getItem('TabId')
    if (!document.cookie.includes(pageId)) {
        ajaxModule.sendAjaxRequestSimpleParams('/setTabCookie', pageId,
            {showDialog: false},
            () => {
            })
    }
}

generateWindowID = () => {
    //first see if the name is already set, if not, set it.
    if (!window.name.includes("TabId")) {
        window.name = 'TabId' + (new Date()).getTime()
        sessionStorage.setItem('TabId', window.name)
    }
}
