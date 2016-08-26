package org.nk.search;

import info.monitorenter.cpdetector.io.ASCIIDetector;  
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;  
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;  
import info.monitorenter.cpdetector.io.JChardetFacade;  
import info.monitorenter.cpdetector.io.UnicodeDetector;  
  








import java.io.BufferedReader;
import java.io.File;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;  
import java.net.URI;  
import java.net.URISyntaxException;  
import java.net.URL;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  
  








import org.apache.http.HttpEntity;  
import org.apache.http.HttpHost;  
import org.apache.http.HttpResponse;  
import org.apache.http.client.ClientProtocolException;  
import org.apache.http.client.HttpClient;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.conn.routing.HttpRoute;  
import org.apache.http.conn.scheme.PlainSocketFactory;  
import org.apache.http.conn.scheme.Scheme;  
import org.apache.http.conn.scheme.SchemeRegistry;  
import org.apache.http.conn.ssl.SSLSocketFactory;  
import org.apache.http.impl.client.DecompressingHttpClient;  
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.impl.conn.PoolingClientConnectionManager;  
import org.apache.http.params.BasicHttpParams;  
import org.apache.http.params.CoreConnectionPNames;  
import org.apache.http.params.HttpParams;  
import org.apache.http.util.EntityUtils;  
import org.fnlp.nlp.cn.ner.stringPreHandlingModule;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.corpus.StopWords;
import org.fnlp.util.exception.LoadModelException;
import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;  
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 


import org.fnlp.app.keyword.AbstractExtractor;
import org.fnlp.app.keyword.WordExtract;
  
/** 
 * 需要commons-logging.jar和commons-lang.jar包才能运行 
 */  
  
  
  
  
  
  
  
  
public class CrawbaiduLink_test {  
      
    final private static String URL= "http://www.baidu.com/s?wd=";  
      
      
    /** 
     * HTTPCLIENT 连接管理 
     */  
    /** 
     * 最大连接数 
     */  
    public final static int MAX_TOTAL_CONNECTIONS = 800;  
    /** 
     * 获取连接的最大等待时间 
     */  
    public final static int WAIT_TIMEOUT = 60000;  
    /** 
     * 每个路由最大连接数 
     */  
    public final static int MAX_ROUTE_CONNECTIONS = 400;  
    /** 
     * 连接超时时间 
     */  
    public final static int CONNECT_TIMEOUT = 10000;  
    /** 
     * 读取超时时间 
     */  
    public final static int READ_TIMEOUT = 60000;  
      
    private static HttpClient httpClient;  
  
    private static DecompressingHttpClient decompressHttpClient;  
      
  
      
      
              
  
      
  
/** 
 * 初始化HTTPCLIENT 需要包httpclient-4.2.5.jar和httpcore-4.2.4.jar 
 */  
public static void  initHttpClient(){  
      
    HttpParams params = new BasicHttpParams();  
    SchemeRegistry schemeRegistry = new SchemeRegistry();  
    schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));  
    schemeRegistry.register(new Scheme("https",443,SSLSocketFactory.getSocketFactory()));  
    PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);  
    httpClient = new DefaultHttpClient(cm, params);  
    decompressHttpClient = new DecompressingHttpClient(httpClient);  
    cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);  
    cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);  
    HttpHost localhost = new HttpHost("locahost", 80);  
    cm.setMaxPerRoute(new HttpRoute(localhost), 50);  
    httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);  
    httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT);  
      
}  
  
/** 
 * 初始化消息头  
 * @param httpGet 
 * @param url 
 * @throws URISyntaxException 
 */  
public static void initHeader(HttpGet httpGet,String url) throws URISyntaxException{  
    httpGet.setURI(new URI(url));  
    httpGet.addHeader("Accept-Language", "en-us");  
//  httpGet.addHeader("Accept-Encoding", "gzip,deflate");  
}  
  
  
/** 
 * 爬取网页 上所有内容 
 * @param httpClient 
 * @param url 
 * @return 
 */  
public static String crawlPageContent(HttpClient httpClient, String url){  
    HttpGet httpGet = new HttpGet();  
    InputStream inputStream = null;  
    try {  
        initHeader(httpGet,url);  
        HttpResponse response = httpClient.execute(httpGet); 
        
        HttpEntity entity = response.getEntity();  
        String encode = getEncoding(url);  
         if(encode.equals("windows-1252")){  
             encode = "GBK";  
         }  
        if (entity != null) {  
            inputStream = entity.getContent();  
            String content = EntityUtils.toString(entity,encode);  
            return content;  
        }  
        return null;  
    } catch (ClientProtocolException e) {  
        e.printStackTrace();  
    } catch (IOException e) {  
        e.printStackTrace();  
    } catch (URISyntaxException e) {  
        e.printStackTrace();  
    } finally {  
        if (inputStream != null) {  
            try {  
                inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    return null;  
}  
  
  
/** 
 *分析页面编码 用到包cpdetector.jar,chardet.jar 
 */  
  
private static CodepageDetectorProxy detector;  
  
public static String getEncoding(File document) {  
      
    CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();  
    detector.add(new ByteOrderMarkDetector());  
    detector.add(ASCIIDetector.getInstance());  
    detector.add(UnicodeDetector.getInstance());  
    detector.add(JChardetFacade.getInstance());  
    java.nio.charset.Charset charset = null;  
    try {  
        charset = detector.detectCodepage(document.toURI().toURL());  
    } catch (MalformedURLException e1) {  
        e1.printStackTrace();  
    } catch (IOException e1) {  
        e1.printStackTrace();  
    }  
    return charset.toString();  
}  
  
public static String getEncoding(String url) {  
    java.nio.charset.Charset charset = null;  
    detector = CodepageDetectorProxy.getInstance();  
    detector.add(new ByteOrderMarkDetector());  
    detector.add(ASCIIDetector.getInstance());  
    detector.add(UnicodeDetector.getInstance());  
    detector.add(JChardetFacade.getInstance());  
    try {  
        charset = detector.detectCodepage(new URL(url));  
    } catch (MalformedURLException e1) {  
        e1.printStackTrace();  
    } catch (IOException e1) {  
        e1.printStackTrace();  
    }  
    if (charset == null) {  
        return "utf-8";  
    }  
    return charset.toString();  
}  
  
private final static Pattern regBaidu = Pattern  
.compile("(?:(?:site:([^']+))?'\\s*}\"\\s*href=\")(http://www\\.baidu\\.com/link\\?url=[^\"]+)");  
  
  
/** 
 * 解析百度搜索出的页面提取链接 
 *  
 * @param content 
 * @return 
 */  
public static List<Link> parseBaiduSearchLinks(String content) {  
    List<Link> rst = new ArrayList<Link>();  
    Matcher mt = regBaidu.matcher(content);  
    while (mt.find()) {  
        Link tlink = new Link();  
        tlink.setDepth(0);  
        tlink.setParent(initPrimiryLink("www.baidu.com"));  
        if (mt.group(1) != null) {  
            tlink.setSource(mt.group(1));  
        }  
        if (mt.group(2) != null) {  
            tlink.setUrl(mt.group(2));  
            rst.add(tlink);  
        }  
    }  
    return rst;  
}  
  
private static Link initPrimiryLink(String url){  
    Link link = new Link();  
    link.setDepth(0);  
    link.setParent(null);  
    link.setUrl(url);  
    return link;  
}  
/** 
 * 获取重定向地址 
 * @param path 
 * @return 
 * @throws Exception 
 */  
public String getRedirectUrl(String path) throws Exception {  
    HttpURLConnection conn = (HttpURLConnection) new URL(path)  
            .openConnection();  
    conn.setInstanceFollowRedirects(false);  
    conn.setConnectTimeout(5000);  
    return conn.getHeaderField("Location");  
}   
public static void main(String[] args) {  
     
    String keyword="如位于    Title    标签部分，百度会显示 Title 中后"; //要查找的关键字  
    BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));  
    System.out.print("请输入问题：");  
    try {
		keyword = strin.readLine();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}  
    String Title="";//要找的网页的title内容  
    String temp[] =null;
    if(keyword.length()>38){//关键词长度大于38个汉字，先提取前10个关键词
    	//提取关键词
    	StopWords sw= new StopWords("models/stopwords");
		CWSTagger seg;
		String result[] = null;
		try {
			seg = new CWSTagger("models/seg.m");
			AbstractExtractor key = new WordExtract(seg, sw);//sw的取值是是句子是否已经分好词
			//key.extract(s,n)表示取句子s中前n个
			String t= key.extract(keyword, 10, true).replaceAll(" ", "");
			result = t.substring(1, t.length()-1).split(",");
			System.out.print("提取的关键词：");
			temp = new String[result.length];//将关键词存入temp数组
			for(int i=0;i<result.length;i++){
				temp[i]= result[i].split("=")[0];
				System.out.print(temp[i]+"   ");
			}
			System.out.println();
		} catch (LoadModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }else{
    	temp = keyword.split("\\s+");//根据空格分隔搜索条件
    }
   
    String url = URL ; //整合URL 
    for (int i = 0; i < temp.length; i++) {//temp中的搜索条件连接起来
		url +=temp[i]+"+";
	}
    url = url.substring(0,url.length()-1);
     System.out.println("搜索的URL："+url); 
    initHttpClient();  
      
    String content =crawlPageContent(httpClient,url);  
//    System.out.println("content----------------->"+content); 
//    List<Link> links = parseBaiduSearchLinks(content);  
//      //System.out.println("links length -"+links.size());
//    for(Link l : links ){  
//        String pageContent = crawlPageContent(httpClient,l.getUrl());  
//        Document doc = Jsoup.parse(pageContent);  
//        String title = doc.title();  
//        System.out.println(l.getUrl() + "  " + title);  
//    }  
//      
    Document doc = Jsoup.parse(content);//解析返回的html页面
    Elements divResult = doc.select("div.result h3 a");
    String linkHref = "",linkText="";
    System.out.println("可以参考的网站：");
    for(int i=0;i<5;i++){
    	linkHref = divResult.get(i).attr("href");
    	linkText =divResult.get(i).text();
    	//文档中的链接是百度加密之后的链接，重定向之后获取没加密的链接
    	HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) new URL(linkHref)  
			.openConnection();
			conn.setInstanceFollowRedirects(false);  
			conn.setConnectTimeout(5000); 
			System.out.println((i+1)+". "+linkText+":  "+ conn.getHeaderField("Location"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

    	
    	
    }
    
    
    
}  
  
  
}  
