window.onload = () => {
    url = new URL(document.location.href);
    msg = url.searchParams.get("msg");
    archive = url.searchParams.get("export");
    if (msg) {
        showToast(msg, false);
    }
    if (archive) {
        $('.archiveContainer').show();
        $('#archive').attr('href', `/static/exports/phishtale.zip`);
    }
}

const showToast = (msg, fixed=false) => {
    $('#globalToast').hide();
    $('#globalToast').slideDown();
    $('#globalToastMsg').text(msg);
    if (!fixed) {
        setTimeout(() => {
            $('#globalToast').slideUp();
        }, 2500);
    }
}