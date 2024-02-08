package utils;

import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import arc.util.Http.*;
import arc.util.serialization.*;
import arc.util.serialization.Jval.*;

import java.net.*;
import java.nio.charset.*;

/**
 * @author minri2
 * Create by 2024/2/8
 */
public class BaiduTranslator{
    static String baiduTranslationApi = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    String appId;
    String appKey;

    public BaiduTranslator(String appId, String appKey){
        this.appId = appId;
        this.appKey = appKey;
    }

    public String translate(String text, String languageTo){
        return translate(text, "auto", languageTo);
    }

    public String translate(String text, String languageFrom, String languageTo){
        return translate(Seq.with(text), languageFrom, languageTo).get(text);
    }

    public StringMap translate(Seq<String> seq, String languageFrom, String languageTo){
        String url = getTranslateUrl(seq.toString("\n"), languageFrom, languageTo);

        HttpRequest request = Http.get(url);

        StringMap map = new StringMap();

        // 阻塞获取
        request.block(response -> {
            String translateResult = response.getResultAsString();

            JsonMap object = Jval.read(translateResult).asObject();
            if(!object.containsKey("trans_result") || !object.get("trans_result").isArray()){
                Log.warn("Fail to translate '@' from @ to @", seq, languageFrom, languageTo);
                Log.info("Result: @", translateResult);
            }

            Seq<Jval> resultSeq = object.get("trans_result").asArray();

            for(Jval child : resultSeq){
                JsonMap result = child.asObject();
                map.put(result.get("src").asString(), result.get("dst").asString());
            }
        });

        return map;
    }

    String getTranslateUrl(String text, String languageFrom, String languageTo){
        long salt = System.currentTimeMillis();
        String sign = MD5.md5(appId + text + salt + appKey);
        return baiduTranslationApi + "?" + getParams(ObjectMap.of(
        "q", text,
        "from", languageFrom,
        "to", languageTo,
        "appid", appId,
        "salt", salt,
        "sign", sign
        ));
    }

    String getParams(ObjectMap<String, ?> map){
        StringBuilder builder = new StringBuilder();

        Entries<String, ?> iterator = map.iterator();
        while(true){
            Entry<String, ?> entry = iterator.next();

            String key = entry.key;
            Object value = entry.value;

            String encoded = URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);

            builder.append(key).append("=");
            builder.append(encoded);
            if(iterator.hasNext()){
                builder.append("&");
            }else{
                break;
            }
        }

        return builder.toString();
    }
}
