package bundle;

import arc.files.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import arc.util.io.*;
import utils.*;

import java.util.regex.*;

/**
 * @author minri2
 * Create by 2024/2/6
 */
public class SyncBundle{
    private static final Pattern pattern = Pattern.compile("bundle_([a-z]{2})");
    private static final String placeHolder = "\\[NewLine]";

    private static final OrderedMap<String, String> main = new OrderedMap<>();
    private static final String mainBundle = "bundle_zh_CN.properties";
    private static final String mainLanguageTag = getLanguageTag(mainBundle);
    private static BaiduTranslator translator;

    public static void main(String[] args){
        createTranslator();

        PropertiesUtils.load(main, Fi.get(mainBundle).reader());

        Log.info("Updating bundles...");

        Fi.get("./").walk(SyncBundle::syncBundle);
    }

    // 基于anuke的... 能用就行!
    static void syncBundle(Fi bundleFi){
        if(bundleFi.name().equals(mainBundle)) return;

        Log.info("| Syncing @", bundleFi.nameWithoutExtension());

        Seq<String> mainKeys = main.orderedKeys();

        OrderedMap<String, String> other = new OrderedMap<>();

        // find the last known comment of each line
        ObjectMap<String, String> comments = new ObjectMap<>();
        StringBuilder curComment = new StringBuilder();

        for(String line : Seq.with(bundleFi.readString().split("\n", -1))){
            if(line.startsWith("#") || line.isEmpty()){
                curComment.append(line).append("\n");
            }else if(line.contains("=")){
                String lastKey = line.substring(0, line.indexOf("=")).trim();
                if(!curComment.isEmpty()){
                    comments.put(lastKey, curComment.toString());
                    curComment.setLength(0);
                }
            }
        }

        PropertiesUtils.load(other, bundleFi.reader());

        int removals = 0;
        for(Entry<String, String> entry : other){
            String key = entry.key;

            if(!main.containsKey(key)){
                removals++;
                Log.info("&lr- Removing unused key '@'...", key);
            }
        }
        if(removals > 0){
            Log.info("&lr@ keys removed.", removals);
        }

        int added = 0;
        for(String key : mainKeys){
            if(other.get(key) == null || other.get(key).trim().isEmpty()){
                other.put(key, main.get(key));
                added++;
                Log.info("&lc- Adding missing key '@'...", key);
            }
        }
        if(added > 0){
            Log.info("&lc@ keys added.", added);
        }

        ObjectMap<String, String> translatedMap = null;
        if(translator != null){
            String bundleLanguageTag = getLanguageTag(bundleFi.name());

            Seq<String> values = main.values().toSeq().map(s -> s.replaceAll("\n", placeHolder));
            translatedMap = translator.translate(values, mainLanguageTag, bundleLanguageTag);
        }
        StringBuilder builder = new StringBuilder();
        for(String key : mainKeys){
            if(!other.containsKey(key)) continue;

            String value = other.get(key);

            if(translatedMap != null){
                value = translatedMap.get(value.replaceAll("\n", placeHolder), value);
            }

            value = uniEscape(value).replaceAll(placeHolder, "\n");

            String comment = comments.get(key, "");
            String property = (key + " = " + value).replace("\n", "\\n") + "\n";

            builder.append(comment).append(property);

            other.remove(key);
        }
        bundleFi.writeString(builder.toString());
    }

    private static void createTranslator(){
        String appId = System.getenv("BAIDU_APP_ID");
        String key = System.getenv("BAIDU_KEY");

        if(appId == null || key == null){
            Log.warn("Unable to create BaiduTranslator: @, @.",
            appId == null ? "appId is unprovided" : "",
            key == null ? "key is unprovided" : "");
            return;
        }

        translator = new BaiduTranslator(appId, key);
    }

    static String getLanguageTag(String name){
        Matcher matcher = pattern.matcher(name);

        if(matcher.find()){
            return matcher.group(1);
        }else{
            // bundle.properties 默认"en";
            return "en";
        }
    }

    static String uniEscape(String string){
        StringBuilder outBuffer = new StringBuilder();
        int len = string.length();
        for(int i = 0; i < len; i++){
            char ch = string.charAt(i);
            if((ch > 61) && (ch < 127)){
                outBuffer.append(ch == '\\' ? "\\\\" : ch);
                continue;
            }

            if(ch >= 0xE000 && ch <= 0xF8FF){
                String hex = Integer.toHexString(ch);
                outBuffer.append("\\u");

                for(int j = 0; j < 4 - hex.length(); j++){
                    outBuffer.append("0");
                }

                outBuffer.append(hex);
            }else{
                outBuffer.append(ch);
            }
        }

        return outBuffer.toString();
    }
}
