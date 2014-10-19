package com.gsy.femstoria.restful;


import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

/*
   Instantiate one HttpClient instance to be use for the entire application
   This is because each additional instance takes time to create and uses more
   memory. A single instance allows HttpClient to pool and reuse connections
*/
public class CustomHttpClient {

    private static AbstractHttpClient customHttpClient;

    // Wait this many milliseconds max for the TCP connection to be established
    private static final int SOCKET_OPERATION_TIMEOUT = 60 * 1000;

    private CustomHttpClient() {
    }

    public synchronized static HttpClient get() {
        if (customHttpClient != null) {
            return customHttpClient;
        }
        
    	HttpParams params = new BasicHttpParams();
    	params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        customHttpClient = new DefaultHttpClient() {
            @Override
            protected ClientConnectionManager createClientConnectionManager() {
            	
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                 
                SSLSocketFactory httpsSocket = SSLSocketFactory.getSocketFactory();
                                
                // registry.register(new Scheme("https", getHttpsSocketFactory(), 443));
                registry.register(new Scheme("https", httpsSocket, 443));
                
                HttpParams params = getParams();                
                HttpConnectionParams.setConnectionTimeout(params, SOCKET_OPERATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, SOCKET_OPERATION_TIMEOUT);
                
                HttpProtocolParams.setUseExpectContinue(params, true);
                HttpProtocolParams.setUserAgent(params, HttpProtocolParams.getUserAgent(params));
                
                return new ThreadSafeClientConnManager(params, registry);
            }

            /** Gets an HTTPS socket factory with SSL Session Caching if such support is available, otherwise falls back to a non-caching factory
             * @return
             */
            // Lydia - Recommended to use but commenting out for now. This requires the Application info to be pass in
//            protected SocketFactory getHttpsSocketFactory() {
//                try {
//                    Class<?> sslSessionCacheClass = Class.forName("android.net.SSLSessionCache");
//                    Object sslSessionCache = sslSessionCacheClass.getConstructor(Context.class).newInstance(application);
//                    Method getHttpSocketFactory = Class.forName("android.net.SSLCertificateSocketFactory").getMethod("getHttpSocketFactory", new Class<?>[]{int.class, sslSessionCacheClass});
//                    return (SocketFactory) getHttpSocketFactory.invoke(null, CONNECTION_TIMEOUT, sslSessionCache);
//                } catch (Exception e) {
//                    Logger.e("CustomHttpClient", "Unable to use android.net.SSLCertificateSocketFactory to get a SSL session caching socket factory, falling back to a non-caching socket factory", e);
//                    return SSLSocketFactory.getSocketFactory();
//                }
//            }
        };
        
        customHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(
                    final HttpRequest request,
                    final HttpContext context
            ) throws HttpException, IOException {            
                if (!request.containsHeader("Accept-Encoding"))
                    request.addHeader("Accept-Encoding", "gzip");
            }
        });
        
        customHttpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(
                    final HttpResponse response,
                    final HttpContext context) throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
        
        return customHttpClient;
    }
    
    /**
     * Delete static member customHttpClient, so that the next time get() is called it will be recreated
     */
    public static void clearCustomHttpClient(){
    	customHttpClient = null;
    }

//    private static String getUserAgent(String defaultHttpClientUserAgent) {
//
//        String versionName = "1.7.3";
//        if(WebServiceConfig.getApplication().getVersion() != "")
//        {
//            versionName = WebServiceConfig.getApplication().getVersion();
//            if(WebServiceConfig.getApplication().isDebugMode())
//            {
//                versionName += "." + WebServiceConfig.getApplication().getBuild();
//            }
//        }
//
//        StringBuilder ret = new StringBuilder();
//        ret.append(WebServiceConfig.getApplication().getPackageName());
//        ret.append("/");
//        ret.append(versionName);
//        ret.append(" (");
//        ret.append("Linux; U; Android ");
//        ret.append(Build.VERSION.RELEASE);
//        ret.append("; ");
//        ret.append(Locale.getDefault());
//        ret.append("; ");
//        ret.append(Build.PRODUCT);
//        ret.append(")");
//        if (defaultHttpClientUserAgent != null) {
//            ret.append(" ");
//            ret.append(defaultHttpClientUserAgent);
//        }
//        return ret.toString();
//    }
    
    private static class GzipDecompressingEntity extends HttpEntityWrapper {
        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }
        @Override
        public InputStream getContent()
            throws IOException, IllegalStateException {
            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }
        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }
    }
}
