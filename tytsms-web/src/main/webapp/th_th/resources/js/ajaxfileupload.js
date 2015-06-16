jQuery.extend({  
  //���� iframe Ԫ��,�����ύ����Ӧ  
  createUploadIframe: function(id, uri) {  
    //create frame  
    var frameId = 'jUploadFrame' + id;  
  
  
    if (window.ActiveXObject) {  
      //fix ie9 and ie 10-------------  
      if (jQuery.browser.version == "9.0" || jQuery.browser.version == "10.0") {  
        var io = document.createElement('iframe');  
        io.id = frameId;  
        io.name = frameId;  
      } else if (jQuery.browser.version == "6.0" || jQuery.browser.version == "7.0" || jQuery.browser.version == "8.0") {  
        var io = document.createElement('<iframe id="' + frameId + '" name="' + frameId + '" />');  
        if (typeof uri == 'boolean') {  
          io.src = 'javascript:false';  
        } else if (typeof uri == 'string') {  
          io.src = uri;  
        }  
      }  
    } else {  
      var io = document.createElement('iframe');  
      io.id = frameId;  
      io.name = frameId;  
    }  
    io.style.position = 'absolute';  
    io.style.top = '-1000px';  
    io.style.left = '-1000px';  
  
  
    document.body.appendChild(io);  
  
  
    return io;  
  },  
  //���� from Ԫ�أ������ύ�ı�  
  createUploadForm: function(id, fileElementId, postData) {  
    //create form<span style="white-space:pre">   </span>  
    var formId = 'jUploadForm' + id;  
    var fileId = 'jUploadFile' + id;  
    var form = $('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data"></form>');  
    var oldElement = $('#' + fileElementId);  
    var newElement = $(oldElement).clone();  
  
  
    $(oldElement).attr('id', fileId);  
    $(oldElement).before(newElement);  
    $(oldElement).appendTo(form);  
    //����Զ������  
    if (postData) {  
      //�ݹ����JSON���м�ֵ  
  
  
      function recurJson(json) {  
        for (var i in json) {  
          //alert(i+"="+json[i])  
          $("<input name='" + i + "' id='" + i + "' value='" + json[i] + "' />").appendTo(form);  
          if (typeof json[i] == "object") {  
            recurJson(json[i]);  
          }  
        }  
      }  
  
  
      recurJson(postData);  
    }  
    //set attributes  
    $(form).css('position', 'absolute');  
    $(form).css('top', '-1200px');  
    $(form).css('left', '-1200px');  
    $(form).appendTo('body');  
    return form;  
  },  
  //�ϴ��ļ�  
  //s ������json����  
  ajaxFileUpload: function(s) {  
    s = jQuery.extend({fileFilter:"",fileSize:0}, jQuery.ajaxSettings, s);  
    //�ļ�ɸѡ  
    var fielName = $('#' + s.fileElementId).val();  
    var extention = fielName.substring(fielName.lastIndexOf(".") + 1).toLowerCase();  
    if (s.fileFilter && s.fileFilter.indexOf(extention) < 0) {  
      alert("��֧�� (" + s.fileFilter + ") Ϊ��׺�����ļ�!");  
      return;  
    }  
    //�ļ���С����  
    if (s.fileSize > 0) {  
      var fs = 0;  
      try {  
        if (window.ActiveXObject) {  
          //IE�����  
          var image = new Image();  
          image.dynsrc = fielName;  
          fs = image.fileSize;  
        } else {  
          fs = $('#' + s.fileElementId)[0].files[0].size;  
        }  
      } catch(e) {  
      }  
      if (fs > s.fileSize) {  
        alert("��ǰ�ļ���С (" + fs + ") �������������ֵ (" + s.fileSize +")��");  
        return;  
      }  
    }  
    var id = new Date().getTime();  
    //���� form ��Ԫ��  
    var form = jQuery.createUploadForm(id, s.fileElementId, s.data);  
    //���� iframe ��Ԫ��  
    var io = jQuery.createUploadIframe(id, s.secureuri);  
    var frameId = 'jUploadFrame' + id;  
    var formId = 'jUploadForm' + id;  
    //����Ƿ����µ�����  
    if (s.global && !jQuery.active++) {  
      jQuery.event.trigger("ajaxStart"); //���� AJAX ����ʼʱִ�к�����Ajax �¼���  
    }  
    var requestDone = false;  
    //�����������  
    var xml = {};  
    if (s.global)  
      jQuery.event.trigger("ajaxSend", [xml, s]); //���� AJAX ������ǰ�¼�  
    //������ɵĻص�����  
    var uploadCallback = function(isTimeout) {  
      var io = document.getElementById(frameId);  
      try {  
        //���ڿ���ű��������⣬���������޷����ʡ���ʾ����Ҫ����Ӧ���м�һ�νſ飺<script ...>document.domain = 'xxx.com';</script>  
        if (io.contentWindow) { //���ݸ������������ȡ���Ӵ��ڵ� window ����  
          xml.responseText = io.contentWindow.document.body ? io.contentWindow.document.body.innerHTML : null;  
          xml.responseXML = io.contentWindow.document.XMLDocument ? io.contentWindow.document.XMLDocument : io.contentWindow.document;  
  
  
        } else if (io.contentDocument) { //contentDocument Firefox ֧�֣�> ie8 ��ie֧�֡���ȡ���Ӵ��ڵ� document ����  
          xml.responseText = io.contentDocument.document.body ? io.contentDocument.document.body.innerHTML : null;  
          xml.responseXML = io.contentDocument.document.XMLDocument ? io.contentDocument.document.XMLDocument : io.contentDocument.document;  
        }  
      } catch(e) {  
        jQuery.handleErrorExt(s, xml, null, e);  
      }  
      if (xml || isTimeout == "timeout") {  
        requestDone = true;  
        var status;  
        try {  
          status = isTimeout != "timeout" ? "success" : "error";  
          // Make sure that the request was successful or notmodified  
          if (status != "error") {  
            //�������ݣ�����XMLͨ��httpData���ܻص���  
            var data = jQuery.uploadHttpData(xml, s.dataType);  
            // If a local callback was specified, fire it and pass it the data  
            if (s.success)  
              s.success(data, status);  
  
  
            // Fire the global callback  
            if (s.global)  
              jQuery.event.trigger("ajaxSuccess", [xml, s]);  
          } else  
            jQuery.handleErrorExt(s, xml, status);  
        } catch(e) {  
          status = "error";  
          jQuery.handleErrorExt(s, xml, status, e);  
        }  
  
  
        // The request was completed  
        if (s.global)  
          jQuery.event.trigger("ajaxComplete", [xml, s]);  
  
  
        // Handle the global AJAX counter  
        if (s.global && !--jQuery.active)  
          jQuery.event.trigger("ajaxStop");  
  
  
        // Process result  
        if (s.complete)  
          s.complete(xml, status);  
  
  
        jQuery(io).unbind();  
  
  
        setTimeout(function() {  
          try {  
            $(io).remove();  
            $(form).remove();  
          } catch(e) {  
            jQuery.handleErrorExt(s, xml, null, e);  
          }  
  
  
        }, 100);  
  
  
        xml = null;  
  
  
      }  
    };  
    //��ʱ��飬s.timeout �������� uploadCallback �ص�������ʾ����ʱ  
    if (s.timeout > 0) {  
      setTimeout(function() {  
        // Check to see if the request is still happening  
        if (!requestDone) uploadCallback("timeout");  
      }, s.timeout);  
    }  
    try {  
      //���ö�̬ form �����ύ����  
      // var io = $('#' + frameId);  
      var form = $('#' + formId);  
      $(form).attr('action', s.url);  
      $(form).attr('method', 'POST');  
      $(form).attr('target', frameId);  
      if (form.encoding) {  
        form.encoding = 'multipart/form-data';  
      } else {  
        form.enctype = 'multipart/form-data';  
      }  
      $(form).submit();  
  
  
    } catch(e) {  
      jQuery.handleErrorExt(s, xml, null, e);  
    }  
    //��̬����ҳ������¼���ע��ص�����  
    if (window.attachEvent) {  
      document.getElementById(frameId).attachEvent('onload', uploadCallback);  
    } else {  
      document.getElementById(frameId).addEventListener('load', uploadCallback, false);  
    }  
    return {  
      abort: function() {  
      }  
    };  
  
  
  },  
  //�ϴ��ļ�  
  uploadHttpData: function(r, type) { 
   
    //alert("type=" + type + ";uploadHttpData" + JSON.stringify(r))  
    var data = !type;  
    data = type == "xml" || data ? r.responseXML : r.responseText;  
    // If the type is "script", eval it in global context  
    if (type == "script")  
      jQuery.globalEval(data);  
    // Get the JavaScript object, if JSON is used.  
    if (type == "json")  
      data = jQuery.parseJSON(jQuery(data).text());
    // evaluate scripts within html  
    if (type == "html")  
      jQuery("<div>").html(data).evalScripts();  
    //alert($('param', data).each(function(){alert($(this).attr('value'));}));  
    return data;  
  },  
  handleErrorExt: function(s, xhr, status, e) {  
    // If a local callback was specified, fire it  
    if (s.error) {  
      s.error.call(s.context || s, xhr, status, e);  
    }  
  
  
    // Fire the global callback  
    if (s.global) {  
      (s.context ? jQuery(s.context) : jQuery.event).trigger("ajaxError", [xhr, s, e]);  
    }  
  }  
});  