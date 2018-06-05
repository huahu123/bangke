package com.neuq.info.common.utils.wxPayUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neuq.info.config.WxPayConfig;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HttpUtil {

	// User-Agent
	public static final String USERAGENT_FIREFOX = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0";  
	public static final String USERAGENT_IE = "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko";  
	

	private BasicCookieStore cookieStore;


    public static StringBuffer httpsRequest(String requestUrl, String requestMethod, String output) throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod(requestMethod);
        if (null != output) {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(output.getBytes("UTF-8"));
            outputStream.close();
        }
        // 从输入流读取返回内容
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        connection.disconnect();
        return buffer;
    }

	
	public HttpResult doGet(String url, Map<String, String> headers, Map<String, String> params) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException {

		if (url == null|| url.equals("")) {
			return null;
		}

		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
		cookieStore = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.setSSLSocketFactory(sslsf).build();

		HttpResult result = null;
		try {

			url = url + "?" + parseParams(params);
			HttpGet httpget = new HttpGet(url);
			httpget.setHeaders(parseHeader(headers));

			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					result = new HttpResult();
					result.setCookies(cookieStore.getCookies());
					result.setStatusCode(response.getStatusLine().getStatusCode());
					result.setHeaders(response.getAllHeaders());
					result.setBody(EntityUtils.toString(entity));
				}

			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

		return result;
		
	}



	private String parseParams(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (String key : params.keySet()) {
			sb.append(key + "=" + params.get(key) + "&");
		}
		return sb.substring(0, sb.length() - 1);

	}

	private Header[] parseHeader(Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			return getDefaultHeaders();
		}

		Header[] retHeader = new BasicHeader[headers.size()];
		int i = 0;
		for (String str : headers.keySet()) {
			retHeader[i++] = new BasicHeader(str, headers.get(str));
		}
		return retHeader;
	}

	private Header[] getDefaultHeaders() {
		Header[] headers = new BasicHeader[3];
		headers[0] = new BasicHeader("User-Agent", USERAGENT_IE);
		headers[1] = new BasicHeader("Accept-Encoding", "gzip, deflate");
		headers[2] = new BasicHeader("Accept-Language", "en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3");
		return headers;
	}

	private void close(HttpEntity entity, CloseableHttpResponse response) {
		try {
			if (entity != null) {
				InputStream input = entity.getContent();
				input.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * 带证书请求
	 * @param url 请求地址
	 * @param xmlParam 请求参数
	 * */

	public static StringBuffer httpClientCustomSSL(String url, String xmlParam) {
		StringBuffer sb = new StringBuffer();
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			FileInputStream instream = new FileInputStream(new File(WxPayConfig.KEY_PATH));
			try {
				keyStore.load(instream, WxPayConfig.MCH_ID.toCharArray());
			} finally {
				instream.close();
			}

			// 证书
			SSLContext sslcontext = SSLContexts.custom()
					.loadKeyMaterial(keyStore, WxPayConfig.MCH_ID.toCharArray())
					.build();
			// 只允许TLSv1协议
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[]{"TLSv1"},
					null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			//创建基于证书的httpClient,后面要用到
			CloseableHttpClient client = HttpClients.custom()
					.setSSLSocketFactory(sslsf)
					.build();

			HttpPost httpPost = new HttpPost(url);//请求接口
			StringEntity reqEntity = new StringEntity(xmlParam);
			// 设置类型
			reqEntity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(reqEntity);
			CloseableHttpResponse response = client.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				System.out.println(response.getStatusLine());
				if (entity != null) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
					String text = "";
					while ((text = bufferedReader.readLine()) != null) {
						sb.append(text);
					}
				}
				EntityUtils.consume(entity);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 *
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			System.out.println(realUrl);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();

			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

}
