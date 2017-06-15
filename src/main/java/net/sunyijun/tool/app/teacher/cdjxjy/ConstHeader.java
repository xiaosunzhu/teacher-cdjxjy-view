package net.sunyijun.tool.app.teacher.cdjxjy;


import com.google.common.collect.ImmutableMap;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Map;

public class ConstHeader {

    static final String AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    static final String LANGUAGE = "zh-CN,zh;q=0.8";
    static final String INSECURE = "1";
    static final String ORIGIN = "http://www.cdjxjy.com";
    static final String HOST = "www.cdjxjy.com";
    static final String CACHE = "no-cache";

    static final String CONTENT_TYPE_NORMAL = "application/x-www-form-urlencoded; charset=UTF-8";
    static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    static final String EXTEND_AJAX = "Delta=true";
    static final String EXTEND_REQUEST = "XMLHttpRequest";

    static Map<String, String> getNormalHeaders() {
        return ImmutableMap.<String, String>builder().put("User-Agent", AGENT)
                .put("Accept-Language", LANGUAGE)
                .put("Upgrade-Insecure-Requests", INSECURE)
                .put("Origin", ORIGIN)
                .put("Host", HOST)
                .put("Content-Type", CONTENT_TYPE_NORMAL).build();
    }

    static void setNormalHeaders(HttpRequestBase request, String referer, boolean insecure) {
        request.addHeader("User-Agent", AGENT);
        request.addHeader("Accept-Language", LANGUAGE);
        if (insecure) {
            request.addHeader("Upgrade-Insecure-Requests", INSECURE);
        }
        request.addHeader("Origin", ORIGIN);
        request.addHeader("Host", HOST);
        request.addHeader("Content-Type", CONTENT_TYPE_NORMAL);
        request.addHeader("Cache-Control", CACHE);
        request.addHeader("Accept", "*/*");
        if (referer != null) {
            request.addHeader("Referer", referer);
        }
    }

    static void setJsonHeaders(HttpRequestBase request, String referer, boolean insecure) {
        setExtendHeaders(request, referer, insecure);
        request.setHeader("Content-Type", CONTENT_TYPE_JSON);
    }

    static void setExtendHeaders(HttpRequestBase request, String referer, boolean insecure) {
        setNormalHeaders(request, referer, insecure);
        request.addHeader("X-MicrosoftAjax", EXTEND_AJAX);
        request.addHeader("X-Requested-With", EXTEND_REQUEST);
    }

}
