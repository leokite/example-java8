package example.java8;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;


/**
 * Optional
 * 存在するかもしれないT型の値を1つ保持するラッパクラス。
 * Optionalの各メソッドは、値がnullか否かで挙動が変わる。
 */
public class OptionalTest {

    /**
     * Mapのとあるキーで取得した値をキーとしてMapから更に値を取得する
     * 従来のやり方
     */
    @Test
    public void legacy_map() {
        Map<String, String> map = new HashMap<>();
        map.put("A", "B");
        map.put("B", "C");
        map.put("C", "D");

        String value = null;
        if (map.containsKey("A")) {
            String val1 = map.get("A");
            if (map.containsKey(val1)) {
                String val2 = map.get(val1);
                if (map.containsKey(val2)) {
                    value = map.get(val2);
                }
            }
        }
        assertThat(value, is("D"));
    }

    /**
     * Mapのとあるキーで取得した値をキーとしてMapから更に値を取得する
     * Optionalを使ったやり方
     */
    @Test
    public void optional_map() {
        OptMap<String, String> optMap = new OptHashMap<>();
        optMap.put("A", "B");
        optMap.put("B", "C");
        optMap.put("C", "D");

        // Optionalを返すので、計算途中でoptMapから値が取得できなくても実行時エラーにならない
        optMap.getWithOpt("A")
            .flatMap(optMap::getWithOpt)
            .flatMap(optMap::getWithOpt)
            .ifPresent(s -> assertThat(s, is("D")));

    }

    interface OptMap<K, V> extends Map<K, V> {
        default Optional<V> getWithOpt(K key) {
            return (this.containsKey(key))
                ? Optional.of(this.get(key))
                : Optional.empty();
        }
    }

    class OptHashMap<K, V> extends HashMap<K, V> implements OptMap<K,V>{};

}
