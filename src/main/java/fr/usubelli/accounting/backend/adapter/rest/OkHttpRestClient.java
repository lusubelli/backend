package fr.usubelli.accounting.backend.adapter.rest;

import okhttp3.*;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;

public class OkHttpRestClient {

    private final OkHttpClient client;
    private final Request.Builder builder;

    private OkHttpRestClient(Request.Builder builder) {
        this.builder = builder;
        this.client = getSSLClient();
    }

    private static OkHttpClient getSSLClient() {
        OkHttpClient client = null;

        try {

        SSLContext sslContext;
        SSLSocketFactory sslSocketFactory;
        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory;
        X509TrustManager trustManager;

        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(KeyStore.getInstance(new File("D:\\workspace\\compta\\user\\ssl\\localhost.p12"), "password".toCharArray()));
        trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }

        trustManager = (X509TrustManager) trustManagers[0];

        sslContext = SSLContext.getInstance("TLS");

        sslContext.init(null, new TrustManager[]{trustManager}, null);

        sslSocketFactory = sslContext.getSocketFactory();

        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    public static OkHttpRestClient url(String url) {
        return new OkHttpRestClient(new Request.Builder().url(url));
    }

    public OkHttpRestClient basicAuth(String username, String password) {
        this.builder.addHeader("Authorization",
                Credentials.basic(username, password));
        return this;
    }

    public OkHttpRestClient post(String json) {
        this.builder.post(RequestBody.create(json,
                MediaType.parse("application/json")));
        return this;
    }

    public OkHttpRestClient put(String json) {
        this.builder.put(RequestBody.create(json,
                MediaType.parse("application/json")));
        return this;
    }

    public OkHttpRestClient get() {
        this.builder.get();
        return this;
    }

    public RestResponse send() throws IOException {
        final Response response = client.newCall(this.builder.build()).execute();
        return new RestResponse(response.code(), response.body().string());
    }

}
