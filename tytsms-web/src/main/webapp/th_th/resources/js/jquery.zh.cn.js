/* Chinese initialisation for the jQuery UI date picker plugin. */
/* Written by Cloudream (cloudream@gmail.com). */
jQuery(function($){
    $.datepicker.regional['zh-CN'] = {
        closeText: 'ปิด',/*关闭*/
        prevText: 'เดือนที่แล้ว&#x3c;',/*上月*/
        nextText: 'เดือนหน้า&#x3e;',/*下月*/
        currentText: 'วันนี้',/*今天*/
        monthNames: ['วันนี้','กุมภาพันธ์','มีนาคม','เมษายน','พฤษภาคม','มิถุนายน',
        'กรกฎาคม','สิงหาคม','กันยายน','ตุลาคม','พฤศจิกายน','ธันวาคม'],/*一月二月三月四月五月六月七月八月九月十月十一月十二月*/
        monthNamesShort: ['หนึ่ง','สอง','สาม','สี่','ห้า','หก',
        'เจ็ด','แปด','เก้า','สิบ','สิบเอ็ด','สิบสอง'],/*一二三四五六七八九十 十一 十二*/
        dayNames: ['วันอาทิตย์','วันจันร์','วันอังคาร','วันพุธ','วันพฤหัสบดี','วันศุกร์','วันเสาร์'],/*星期日星期一星期二星期三星期四星期五星期六*/
        dayNamesShort: ['อาทิตย์','จันร์','อังคาร','พุธ','พฤหัสบดี','ศุกร์','เสาร์'],/*周日周一周二周三周四周五周六*/
        dayNamesMin: ['วันที่','หนึ่ง','สอง','สาม','สี่','ห้า','หก'],/*日一二三四五六*/
        dateFormat: 'yy-mm-dd', firstDay: 1,
        isRTL: false};
    $.datepicker.setDefaults($.datepicker.regional['zh-CN']);
});