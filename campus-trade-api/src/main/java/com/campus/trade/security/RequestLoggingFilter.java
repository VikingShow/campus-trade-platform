package com.campus.trade.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final static Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 我们需要包装请求，以便能够多次读取请求体（虽然这里只为日志读取一次）
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        // 只有当请求是我们的登录接口时，才打印详细日志
        if ("/users/authenticate".equals(request.getRequestURI())) {
            logRequestDetails(requestWrapper);
        }

        // 将包装后的请求传递给过滤器链的下一个环节
        filterChain.doFilter(requestWrapper, response);
    }

    private void logRequestDetails(ContentCachingRequestWrapper request) {
        log.info("================= INCOMING REQUEST (Diagnostic Filter) ==================");
        log.info("URI         : {}", request.getRequestURI());
        log.info("Method      : {}", request.getMethod());

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.info("Header      : {} = {}", headerName, request.getHeader(headerName));
            }
        }

        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            try {
                String contentString = new String(content, request.getCharacterEncoding());
                log.info(">>>>>> Request Body: {}", contentString.replaceAll("[\r\n\t]+", ""));
            } catch (UnsupportedEncodingException e) {
                log.warn("无法使用指定的编码读取请求体", e);
            }
        } else {
            log.warn(">>>>>> Request Body is EMPTY. 这就是问题的根源！");
        }
        log.info("=======================================================================");
    }
}