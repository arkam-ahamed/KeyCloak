package com.example.KeyCloak;



import com.c4_soft.springaddons.security.oauth2.test.mockmvc.ServletUnitTestingSupport;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.keycloak.ServletKeycloakAuthUnitTestingSupport;
import com.example.KeyCloak.controller.Employee;
import com.example.KeyCloak.controller.EmployeeController;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;




@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
@Import({
        ServletKeycloakAuthUnitTestingSupport.UnitTestConfig.class})
@ActiveProfiles("keycloak")
@ContextConfiguration(classes = Employee.class)
@ComponentScan(basePackageClasses = { KeycloakSecurityComponents.class, KeycloakSpringBootConfigResolver.class })
public class EmployeeControllerTests extends ServletUnitTestingSupport {

    public static final String POST_URL_TEMPLATE = "http://localhost:8080/auth/realms/Demo-Realm/protocol/openid-connect/token";
    public static final String URL_TEMPLATE = "http://localhost:8000/api";

    public static final String PUT_URL_TEMPLATE= "/update/619e32712b93cb67e18e06ab";
    public static final String DELETE_URL_TEMPLATE= "/delete/619e32712b93cb67e18e06ab";


    private static CloseableHttpClient client;


    private String accessToken = "";

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustAllStrategy())
                .build();
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslcontext)
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
        client = HttpClients.custom()
                .setConnectionManager(cm)
                .evictExpiredConnections()
                .build();
    }


    class HttpResponse {
        public Integer code;
        public String payload;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }

    }

    @Before
    public void sendHttpPost() throws JSONException {
        Integer responseStatus;
        String responsePayload;

        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpPost httpPost = new HttpPost(POST_URL_TEMPLATE);


            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setHeader("Accept", "application/x-www-form-urlencoded");
            StringEntity params = new StringEntity("grant_type=password&client_id=springboot-microservice&client_secret=01b3286b-f1af-410d-8805-3df9845ca28a&password=1234&username=employee3", ContentType.APPLICATION_FORM_URLENCODED);
            httpPost.setEntity(params);

            CloseableHttpResponse res = client.execute(httpPost);

            responseStatus = res.getCode();
            httpResponse.code = responseStatus;


            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            responsePayload = sb.toString();
            httpResponse.payload = responsePayload;


            JSONObject array = new JSONObject(httpResponse.payload);
            accessToken = array.getString("access_token");


        } catch (Throwable e) {
            System.out.println("error =>" + e);
        }


    }


    public String getAccessToken() {
        return accessToken;
    }


    @Test
    public void testReadEmployee() throws Exception {
        Integer responseStatus;
        String responsePayload;

        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpGet httpGet = new HttpGet(URL_TEMPLATE + "/get");


            httpGet.setHeader("Authorization", "bearer " + getAccessToken());

            CloseableHttpResponse res = client.execute(httpGet);

            responseStatus = res.getCode();
            httpResponse.code = responseStatus;


            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            responsePayload = sb.toString();
            httpResponse.payload = responsePayload;


            JSONArray array = new JSONArray(httpResponse.payload);
            System.out.println(array);

        } catch (Throwable e) {
            System.out.println("error =>" + e);
        }

    }



    @Test
    public void testCreateEmployee() throws Exception{
        String newString =
                "{" +
                        "\"name\":\"arkam\"," +
                        "\"age\" : \"32\" " +"}";

        Integer responseStatus;
        String responsePayload;

        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpPost httpPost = new HttpPost(URL_TEMPLATE +"/add");


            httpPost.setHeader("Authorization", "bearer " + getAccessToken());
            httpPost.setHeader("Accept", "application/x-www-form-urlencoded");
            StringEntity params = new StringEntity(newString, ContentType.APPLICATION_JSON);
            httpPost.setEntity(params);

            CloseableHttpResponse res = client.execute(httpPost);

            responseStatus = res.getCode();
            httpResponse.code = responseStatus;


            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            responsePayload = sb.toString();
            httpResponse.payload = responsePayload;


            JSONObject array = new JSONObject(httpResponse.payload);
            System.out.println(array);


        } catch (Throwable e) {
            System.out.println("error =>" + e);
        }
    }

    @Test
    public void testUpdateEmployee() throws Exception{
        String newString =
                "{" +
                        "\"name\":\"arkam\"," +
                        "\"age\" : \"32\" " +"}";

        Integer responseStatus;
        String responsePayload;

        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpPut httpPut = new HttpPut( URL_TEMPLATE+ PUT_URL_TEMPLATE);


            httpPut.setHeader("Authorization", "bearer " + getAccessToken());
            StringEntity params = new StringEntity(newString, ContentType.APPLICATION_JSON);
            httpPut.setEntity(params);

            CloseableHttpResponse res = client.execute(httpPut);

            responseStatus = res.getCode();
            httpResponse.code = responseStatus;


            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            responsePayload = sb.toString();
            httpResponse.payload = responsePayload;


            JSONObject array = new JSONObject(httpResponse.payload);
            System.out.println(array);


        } catch (Throwable e) {
            System.out.println("error =>" + e);
        }
    }

    @Test
    public void testDeleteEmployee() throws Exception{
        Integer responseStatus;
        String responsePayload;

        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpDelete httpDelete = new HttpDelete( URL_TEMPLATE+ DELETE_URL_TEMPLATE);


            httpDelete.setHeader("Authorization", "bearer " + getAccessToken());

            CloseableHttpResponse res = client.execute(httpDelete);

            responseStatus = res.getCode();
            httpResponse.code = responseStatus;


            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            responsePayload = sb.toString();
            httpResponse.payload = responsePayload;


            JSONObject array = new JSONObject(httpResponse.payload);
            System.out.println(array);


        } catch (Throwable e) {
            System.out.println("error =>" + e);
        }
    }


    @Test
    public void postBadRequest() throws Exception {
        String newString =  "{" +
                "\"username\":\"arkam\"," +
                "\"age\" : \"32\" " +
                "\"day\" : \"32\" "+"}";
        Integer responseStatus;
        String responsePayload;

        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpPost httpPost = new HttpPost(URL_TEMPLATE + "/add");


            httpPost.setHeader("Authorization", "bearer " + getAccessToken());
            httpPost.setHeader("Accept", "application/x-www-form-urlencoded");
            StringEntity params = new StringEntity(newString, ContentType.APPLICATION_JSON);
            httpPost.setEntity(params);

            CloseableHttpResponse res = client.execute(httpPost);

            responseStatus = res.getCode();
            httpResponse.code = responseStatus;


            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            responsePayload = sb.toString();
            httpResponse.payload = responsePayload;


            JSONObject array = new JSONObject(String.valueOf(httpResponse.code));
            System.out.println(array);


        } catch (Throwable e) {
            System.out.println("error =>" + e.getLocalizedMessage());
        }

    }

    @Test
    public void deleteNonExisting() throws Exception {
        Integer responseStatus;
        String responsePayload;

        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpDelete httpDelete = new HttpDelete( URL_TEMPLATE+ "/delete/61af5e9b479871762d246fb8");


            httpDelete.setHeader("Authorization", "bearer " + getAccessToken());

            CloseableHttpResponse res = client.execute(httpDelete);

            responseStatus = res.getCode();
            httpResponse.code = responseStatus;


            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            responsePayload = sb.toString();
            httpResponse.payload = responsePayload;


            JSONObject array = new JSONObject(String.valueOf(httpResponse.code));
            System.out.println(array);


        } catch (Throwable e) {
            System.out.println("error =>" + e);
        }
    }
    }



