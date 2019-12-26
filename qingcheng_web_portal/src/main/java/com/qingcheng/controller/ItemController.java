package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {
    @Reference
    private SpuService spuService;

    @Value("${PagePath}")
    private  String PagePath;

    @Reference
    private CategoryService categoryService;

    @Autowired
    private TemplateEngine templateEngine;


    @GetMapping("/createPage")
    public void createPage(String spuId){
        Goods goods = spuService.findGoodsbyId(spuId);
        Spu spu = goods.getSpu();
        List<Sku> skuList = goods.getSkuList();
        List<String> categorylist=new ArrayList<>();
        categorylist.add(categoryService.findById(spu.getCategory1Id()).getName());
        categorylist.add(categoryService.findById(spu.getCategory2Id()).getName());
        categorylist.add(categoryService.findById(spu.getCategory3Id()).getName());
        Map<String,String> urlMap=new HashMap<>();
        for(Sku sku:skuList)
        {
            String specJson = JSON.toJSONString(JSON.parseObject(sku.getSpec()), SerializerFeature.MapSortField);
            urlMap.put(specJson,sku.getId()+".html");
        }
        for(Sku sku:skuList)
        {
            Context context = new Context();
            Map<String,Object> o = new HashMap<>();
            o.put("spu",spu);
            o.put("sku",sku);
            o.put("categorylist",categorylist);
            o.put("skuImages",sku.getImages().split(","));
            o.put("spuImages",spu.getImages().split(","));
            Map<String,String> jsonObject = (Map)JSON.parseObject(spu.getParaItems());
            o.put("paraItems",jsonObject);
            Map spec = JSON.parseObject(sku.getSpec());
            o.put("specItems",spec);
            Map<String,List> specitem = (Map)JSON.parseObject(spu.getSpecItems());
            for(String key:specitem.keySet())
            {
                List<String> list = specitem.get(key);
                List<Map> mapList=new ArrayList<>();
                for(String value:list)
                {
                    HashMap hashMap = new HashMap();
                    hashMap.put("option",value);
                    if(spec.get(key).equals(value)) {
                        hashMap.put("checked", true);
                    }
                    else
                    {
                        hashMap.put("checked", false);
                    }

                    Map<String,String> specmap = (Map)JSON.parseObject(sku.getSpec());
                    specmap.put(key,value);
                    String s = JSON.toJSONString(specmap, SerializerFeature.MapSortField);
                    hashMap.put("url",urlMap.get(s));
                    mapList.add(hashMap);
                }
                specitem.put(key,mapList);
            }
            o.put("specMap",specitem);
            context.setVariables(o);

            File dir=new File(PagePath);
            if(!dir.exists())
            {
                dir.mkdirs();
            }
            File file=new File(dir,sku.getId()+".html");
            try {
                PrintWriter writer=new PrintWriter(file,"UTF-8");
                templateEngine.process("item",context,writer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }
}
