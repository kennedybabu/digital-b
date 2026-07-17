package com.example.commonsobservability.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class CorrelationAndAccessLogFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(CorrelationAndAccessLogFilter.class);
    public static final String CID = "cid";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String cid = request.getHeader("X-Request-Id");
        if(cid == null || cid.trim().isEmpty()) {
            cid = UUID.randomUUID().toString();
        }

        long startNs = System.nanoTime();

        MDC.put(CID, cid);
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());

        //-----set header early so it always appears in the response
        response.setHeader("X-Request-Id", cid);

        try {
            LOG.info("ENTER controller cid={} method={} path={}", cid, MDC.get("method"), MDC.get("path"));
            filterChain.doFilter(request,response);
        } finally {
            long durMs = (System.nanoTime() - startNs) / 1_000_000L;

            //------Response side MDC-------------
            MDC.put("Status", Integer.toString(response.getStatus()));
            MDC.put("durMs", Long.toString(durMs));

            //-----------Echo header again (safety)-----------------
            response.setHeader("X-Request-Id", cid);


            //--------Single-line access using SLF4J placeholders---------------
            LOG.info(
                    "EXIT controller cid={} method={} path={} status={} durMs={} ",
                    cid,
                    MDC.get("method"),
                    MDC.get("path"),
                    MDC.get("status"),
                    MDC.get("durMs")
            );

            MDC.clear();
        }
    }
}
