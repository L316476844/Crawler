package org.jon.lv.crawlers;

/**
 * @Package org.jon.lv.crawlers.MutiPageNewsCrawler
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/6/7 16:44
 * version V1.0.0
 */

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import cn.wanghaomiao.xpath.model.JXDocument;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.net.www.content.image.png;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内分页文章整合抓取
 *
 * @since 2016/6/14.
 */
@Crawler(name = "building")
public class BuildingCrawler extends BaseSeimiCrawler {
    @Override
    public String[] startUrls() {
        return new String[]{"https://sh.fang.anjuke.com/"};
    }

    @Override
    public void start(Response response) {
        try {
            JXDocument document = response.document();
            List<Object> urls = document.sel("//div[@class='item-mod']/@data-link");

            for(Object urlStr : urls){
                String str = (String)urlStr;
                String id = str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf("."));
                System.out.println(id);

                String canshu = "https://sh.fang.anjuke.com/loupan/canshu-".concat(id).concat(".html");
                push(new Request(canshu, "getCanShu"));
                String huxing = "https://sh.fang.anjuke.com/loupan/huxing-".concat(id).concat(".html");
                push(new Request(huxing, "getHuxing"));
            }
//            String itemBody = StringUtils.join(document.sel("//div[@class='item-mod']/@data-link"), "$$");
//            System.out.println(itemBody);
            //拿到下一页的地址后缀
            String nextPage = null;
            nextPage = StringUtils.join(document.sel("//div[@class='pagination']/a[text()*='下一页']/@href"), "###");
            if (StringUtils.isNotEmpty(nextPage)) {
                //用这一个回调函数就够了
                Request req = Request.build(nextPage, "start");
                System.out.println("下页地址----" + req.getUrl());
                push(req);
            }
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    public void getCanShu(Response response){
        JXDocument doc = response.document();
        try {
            System.out.println("请求地址----" + response.getUrl());
            List<Object> lis = doc.sel("//div[@class='can-left']//div[@class='can-border']//li");
            for(Object obj : lis){
                Element li = (Element)obj;
                Elements names = li.select("div[class=name]");
                Elements values = li.select("div[class=des]");
                if(names.size() > 0 || values.size() > 0){
                    System.out.println(names.text() + "---" + values.text());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getHuxing(Response response){
        JXDocument doc = response.document();
        try {
            List<Object> lis = doc.sel("//ul[@class='hx-list g-clear']//li");
            for(Object obj : lis){
                Element li = (Element)obj;
                Elements srcs = li.select("img[imglazyload-src]");
                Elements values = li.select("div[class=type-name]");
                System.out.println(srcs);
                System.out.println(values.text());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}