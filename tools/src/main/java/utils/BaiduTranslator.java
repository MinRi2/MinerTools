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
    private static final int maxByteSize = (int)(6000 * 0.9);
    private static final String baiduTranslationApi = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    String appId;
    String appKey;

    public BaiduTranslator(String appId, String appKey){
        this.appId = appId;
        this.appKey = appKey;
    }

    public StringMap translate(Seq<String> seq, String fromTag, String toTag){
        StringMap result = new StringMap();

        for(String string : divide(seq, "\n")){
            result.putAll(translate(string, fromTag, toTag));
        }

        return result;
    }

    public StringMap translate(String source, String fromTag, String toTag){
        String url = getTranslateUrl(source, fromTag, toTag);

        HttpRequest request = Http.get(url);

        StringMap map = new StringMap();

        // 阻塞获取
        request.block(response -> {
            String translateResult = response.getResultAsString();

            JsonMap object = Jval.read(translateResult).asObject();
            if(!object.containsKey("trans_result") || !object.get("trans_result").isArray()){
                Log.warn("Fail to translate '@' from @ to @", source, fromTag, toTag);
                Log.info("Result: @", translateResult);
                return;
            }

            Seq<Jval> resultSeq = object.get("trans_result").asArray();

            for(Jval child : resultSeq){
                JsonMap result = child.asObject();
                map.put(result.get("src").asString(), result.get("dst").asString());
            }
        });

        return map;
    }

    private Seq<String> divide(Seq<String> seq, String separator){
        Seq<String> result = new Seq<>();
        StringBuilder builder = new StringBuilder();

        int byteSize = 0;
        for(int i = 0; i < seq.size; i++){
            String string = seq.get(i);

            if(byteSize + string.getBytes().length < maxByteSize){
                byteSize += string.getBytes().length;
                builder.append(string);

                if(i < seq.size - 1){
                    builder.append(separator);
                }
            }else{
                byteSize = 0;
                result.add(builder.toString());
                builder.setLength(0);
            }
        }

        if(!builder.isEmpty()){
            result.add(builder.toString());
        }

        return result;
    }

    private String getTranslateUrl(String text, String languageFrom, String languageTo){
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

    private String getParams(ObjectMap<String, ?> map){
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
