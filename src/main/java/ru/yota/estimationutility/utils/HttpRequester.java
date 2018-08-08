package ru.yota.estimationutility.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class HttpRequester {
    private String protocol = null;
    private String host = null;
    private int port;
    private String url = null;
    private String querystringparam = null;
    private String line = null;

    private StringBuilder httpresponse = null;
    private Logger log = LogManager.getLogger(HttpRequester.class);
    private BufferedReader reader = null;

    public HttpRequester(String protocol, String host, int port, String url, String querystringparam) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.url = url;
        this.querystringparam = querystringparam;
    }

    public String HTTPGetClassificator(String phrase) {
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(50000)
                    .setConnectTimeout(50000)
                    .setConnectionRequestTimeout(5000)
                    .setSocketTimeout(5000)
                    .build();
            CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
            URIBuilder builder = new URIBuilder();
            builder.setScheme(protocol).setHost(host).setPort(port).setPath(url)
                    .setParameter(querystringparam, phrase);
            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                if (entity != null) {
                    log.debug("get result for phrase: {}", phrase);
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    httpresponse = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        httpresponse.append(line);
                        httpresponse.append("\n");
                    }
                    log.debug("\n"
                            + "-----------------------------------------------------------------"
                            + "\n"
                            + "get success http response: {} ", response.getStatusLine()
                            + "\n"
                            + "http response: "
                            + httpresponse
                            + "-----------------------------------------------------------------");
                }
            }
            else{
            log.error("Faile connection: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                log.error("\n"
                        + "-----------------------------------------------------------------"
                        + "\n"
                        + "unsuccessful connection: {} ", response.getStatusLine()
                        + "\n"
                        + "http response: "
                        + httpresponse
                        + "-----------------------------------------------------------------");
        }
    } catch(
    Exception e)

    {
        log.error("Network Error ", e);
        System.exit(0);
    }
        return new

    String(httpresponse);
}
}
