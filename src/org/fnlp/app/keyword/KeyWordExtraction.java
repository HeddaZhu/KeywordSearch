package org.fnlp.app.keyword;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.fnlp.app.keyword.AbstractExtractor;
import org.fnlp.app.keyword.WordExtract;




//import NLPIR.Map;
//import NLPIR.n;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.corpus.StopWords;

/**
 * 关键词抽取使用示例
 * @author jyzhao,ltian
 *
 */
public class KeyWordExtraction {
	
	public static void main(String[] args) throws Exception {
		
		
		StopWords sw= new StopWords("models/stopwords");
		CWSTagger seg = new CWSTagger("models/seg.m");
		AbstractExtractor key = new WordExtract(seg, sw);//sw的取值是是句子是否已经分好词
		//key.extract(s,n)表示取句子s中前n个
		System.out.println(key.extract("甬温线特别重大铁路交通事故车辆经过近24小时的清理工作，26日深夜已经全部移出事故现场，之前埋下的D301次动车车头被挖出运走", 20, true));
		System.out.println("!!!");
		
//		String info = "",temp = null;
//		BufferedReader br = new BufferedReader(new InputStreamReader
//				(new FileInputStream("D:\\study\\sys\\data\\info1\\resource\\2015.10.27-qq-1.txt"),"gbk"));
//		while( (temp = br.readLine()) != null ){
//			info += temp;
//		}
//		br.close();
//		System.out.println(key.extract(info, 10, true));
//		System.out.println("Done");
//
//		
//		//处理已经分好词的句子
//		sw=null;
//		key = new WordExtract(seg,sw);
////		System.out.println(key.extract("甬温线 特别 重大 铁路交通事故车辆经过近24小时的清理工作，26日深夜已经全部移出事故现场，之前埋下的D301次动车车头被挖出运走", 20));
////		System.out.println(key.extract("赵嘉亿 是 好人 还是 坏人", 5));
////		Map<String,Integer> ans = key.extract(String str, Integer n);
//		System.out.println(key.extract("赵嘉亿 是 好人 还是 坏人", 10));
////		Map<String,Integer> ans = key.extract("赵佳怡 是 好人 还是 坏人", 5);//extract 输出的是结构体！
////		key = new WordExtract();
////		System.out.println(key.extract("", 5));
		
		
	}
}
