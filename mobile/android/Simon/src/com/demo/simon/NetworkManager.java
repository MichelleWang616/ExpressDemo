
package com.demo.simon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetworkManager {
    private final static String TAG = "NetworkManager";
    private final static String EXPRESS_ACTION_BASE_URL = "http://www.intalker.com/express/demo/api/?op=";
    private final static String ACTION_GET_ALL_COMPANY = "GetAllCompanyList";
    private final static String ACTION_GET_PROPER_SITES = "GetProperSiteList";
    private final static String ACTION_GET_PROPER_COURIERS = "GetProperCourierList";
    private final static String ACTION_UPDATE_REQUEST_INFO = "UpdateRequestInfo";
    private final static String ACTION_GET_HISTORY_ORDERS = "GetMyHistoryList";
    private final static String EXPRESS_COMPANY_LOG_URL_PREFIX = "http://www.intalker.com/express/demo/resource/icon/company/";

    private static DefaultHttpClient sHttpClient;

    // concurrent hash map to avoid multiple downloading to a local file at the same time
    private static ConcurrentHashMap<String, Boolean> downloadingMap = new ConcurrentHashMap<String, Boolean>();

    public static void initialize(Context context) {
    }

    public static String getAllCompanyUrl() {
        return EXPRESS_ACTION_BASE_URL + ACTION_GET_ALL_COMPANY;
    }

    public static String getExpressCompanyLogoUrl(String companyId) {
        return EXPRESS_COMPANY_LOG_URL_PREFIX + companyId + ".jpg";
    }

    public static String getProperSitesURL() {
        return EXPRESS_ACTION_BASE_URL + ACTION_GET_PROPER_SITES;
    }

    public static String getProperCouriersURL() {
        return EXPRESS_ACTION_BASE_URL + ACTION_GET_PROPER_COURIERS;
    }

    public static String getUpdateRequestInfoURL() {
        return EXPRESS_ACTION_BASE_URL + ACTION_UPDATE_REQUEST_INFO;
    }

    public static String getAllHistoryOrderURL() {
        return EXPRESS_ACTION_BASE_URL + ACTION_GET_HISTORY_ORDERS;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void openLink(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    private static DefaultHttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        // disable stale checking
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setSoTimeout(params, 60000);

        // disable redirect
        HttpClientParams.setRedirecting(params, true);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http",
                PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager =
                new ThreadSafeClientConnManager(params, schemeRegistry);

        return new DefaultHttpClient(manager, params);
    }

    public static String executeGetRequest(Context context, String url) {

        HttpGet method = new HttpGet(url);
        String resultStr = null;
        try {
            HttpResponse response = executeHttpRequest(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                verifyContentType(context, method, entity);
                resultStr = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            Log.w(TAG, "Download manifest: " + e.toString());
        } catch (IOException e) {
            Log.w(TAG, "Download manifest: " + e.toString());
        } catch (RuntimeException e) {
            Log.w(TAG, "Download manifest: " + e.toString());
        }
        return resultStr;
    }

    public static String executePostRequest(Context context, String url, List<NameValuePair> params) {
        HttpPost method = new HttpPost(url);
        String resultStr = null;
        try {
            method.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = executeHttpRequest(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                resultStr = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            Log.w(TAG, "Download manifest: " + e.toString());
        } catch (IOException e) {
            Log.w(TAG, "Download manifest: " + e.toString());
        } catch (RuntimeException e) {
            Log.w(TAG, "Download manifest: " + e.toString());
        }
        return resultStr;
    }

    /**
     * save files downloading from the server
     * 
     * @return return true only if the file is downloaded and saved.
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static boolean downloadToLocal(Context context, String url, File file)
            throws ClientProtocolException, IOException {
        boolean newDownloaded = false;
        String fileName = file.getAbsolutePath();
        // putIfAbsent returns the value previously associated with fileName, or null
        if (downloadingMap.putIfAbsent(fileName, Boolean.TRUE) == null) {
            try {
                HttpGet method = new HttpGet(url);
                InputStream in = null;
                FileOutputStream out = null;
                boolean isSucceedDownloaded = false;
                try {
                    // The following statement is useless. But we did try to encode effect name to fix some download
                    // issues before. And you can not encode the url by this way then use the encoded one to create
                    // HttpGet instance since using encoded string to create URI would fail to get scheme, host which
                    // are needed to execute a HttpRequest.
                    // url = URLEncoder.encode(url, HTTP.UTF_8).replace("+", "%20");

                    HttpResponse response = executeHttpRequest(method);
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();

                        long contentLength = entity.getContentLength();
                        // if the file exists and is as same size as the server one. we won't download it again.
                        if (contentLength > 0 && contentLength != file.length()) {
                            in = entity.getContent();
                            out = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int numRead = 0;
                            while ((numRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, numRead);
                            }

                            newDownloaded = true;
                        } else {
                            // Instead of consuming the content, abort the connection is quick.
                            method.abort();
                        }
                        isSucceedDownloaded = true;
                    }
                    else {
                        throw new IOException(response.getStatusLine().toString());
                    }
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    if (!isSucceedDownloaded) {
                        Log.w(TAG, "downloadToLocal failed: url = " + url);
                        file.delete();
                    }
                }
            } finally {
                downloadingMap.remove(fileName);
            }
        }

        return newDownloaded;
    }

    public static HttpResponse executeHttpRequest(HttpUriRequest httpRequest) throws IOException {
        HttpClient client = getHttpClient();
        client.getConnectionManager().closeExpiredConnections();
        return client.execute(httpRequest);
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, Header[] headers, RequestParams params,
            AsyncHttpResponseHandler responseHandler) {
        client.get(context, url, headers, params, responseHandler);
    }

    public static void delete(Context context, String url, Header[] headers, RequestParams params,
            AsyncHttpResponseHandler responseHandler) {
        client.delete(context, url, headers, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static void verifyContentType(Context context, HttpGet method, HttpEntity entity) throws IOException {
        if (entity == null) {
            throw new IOException("content get is invalid");
        } else {
            Header header = entity.getContentType();
            // Hack, if the type is html, we must have network issue like a network with browser log in.
            if (header != null && !header.getValue().startsWith("text/html")) {
                return;
            }
            method.abort();
            throw new IOException("content get is invalid");
        }
    }

    private synchronized static DefaultHttpClient getHttpClient() {
        if (sHttpClient == null) {
            sHttpClient = createHttpClient();
        }
        return sHttpClient;
    }
}
