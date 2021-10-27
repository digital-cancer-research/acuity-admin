$(document).ready(() =>
    $(document).mousemove(() => {
        setBrowserTabIdCookie()
    }))

$(document).ready(() =>
    $(document).click(() => {
        setBrowserTabIdCookie()
    }))

setBrowserTabIdCookie = () => {
    generateWindowID()
    let pageId = sessionStorage.getItem('TabId')
    if (!document.cookie.includes(pageId)) {
        ajaxModule.sendAjaxRequest('/setTabCookie', pageId,
            {showDialog: false},
            () => {
            }, error => {
                console.log("Error setting tab id", error)
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
