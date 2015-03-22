package example.java8;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.junit.Test;


/**
 * Stream API:
 * 複数の値（オブジェクト）に対して何らかの処理（変換や集計）を行うAPI
 */
public class StreamTest {

    /**
     * Streamを使わない場合
     * Stringクラスのメソッド一覧取得（重複は排除）
     */
    @Test
    public void getStringMethods_legacy() {
        List<String> mlist = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (Method method : String.class.getMethods()) {
            String name = method.getName();
            if (!set.contains(name)) {
                mlist.add(name);
                set.add(name);
            }
        }
        System.out.println(mlist);
    }

    /**
     * Streamを使う場合
     * Stringクラスのメソッド一覧取得（重複は排除）
     */
    @Test
    public void getStringMethods_stream() {
        List<String> mlist =
            Arrays.stream(String.class.getMethods())
                .map(method -> method.getName())
                .distinct()
                .collect(Collectors.toList());
        System.out.println(mlist);

    }

    /**
     * ソース
     * Stream型のオブジェクトを生成する。
     * Stream系のインターフェースは、newでインスタンス生成できないのでインスタンスを生成するメソッドを使う。
     * Streamの種類
     *  オブジェクト型: Stream<T>
     *  プリミティブ型: IntStrem, LongStremなど
     */
    @SuppressWarnings("unused")
    @Test
    public void source_stremInstantiation() {
        // of(): 初期値を指定した生成
        Stream<String> s1 = Stream.of("abc", "def");

        IntStream is1 = IntStream.of(123, 456);

        // empty(): 空のStreamを生成
        Stream<String> s2 = Stream.empty();

        IntStream is2 = IntStream.empty();

        // builder(); ビルダーによる生成
        Builder<String> builder = Stream.builder();
        Stream<String> s3 = builder.add("abc").add("def").build();

        IntStream is3 = IntStream.builder().add(123).add(456).build();

        // generate(); 値を返す関数(Supplier)を指定 このStreamは無限に続くのでlimit()等で終了する必要あり
        Stream<String> s4 = Stream.generate(() -> "abc");

        Random rnd = new Random();
        IntStream is4 = IntStream.generate(() -> rnd.nextInt() & 7);

        // concat(): 2つのStreamを結合する
        Stream<String> sab = Stream.of("a", "b");
        Stream<String> sxy = Stream.of("x", "y");
        Stream<String> sabxy = Stream.concat(sab, sxy);

        IntStream s12 = IntStream.of(1, 2);
        IntStream s89 = IntStream.of(8, 9);
        IntStream s1289 = IntStream.concat(s12, s89);

        // コレクションからストリームを生成
        List<String> list = Arrays.asList("abc", "def");
        Stream<String> s6  = list.stream(); //直列ストリーム

        List<String> list2 = Arrays.asList("abc", "def");
        Stream<String> s7 = list2.parallelStream(); //並列ストリーム

        // 配列からストリームを生成
        String[] ss = {"a", "b", "c", "d", "e", "f"};
        Stream<String> s8 = Arrays.stream(ss, 2, 5); //c, d, eのみストリーム化
    }

    /**
     * 中間操作
     * 絞り込みや写像などの操作を指定して、新しいStreamを返す。
     * 中間操作では処理方法を指定するだけで実際には処理しない。終端操作が呼ばれた段階で複数の中間処理
     * をまとめてループ処理する。
     */
    @Test
    public void intermediateOperations() {
        // filter: 条件に一致したオブジェクトだけ抽出
        List<String> list = Arrays.asList("a", "bb", "ccc");
        List<String> list2 = list.stream()
            .filter(s -> s.length() == 2)
            .collect(Collectors.toList());
        assertThat(list2, is(Arrays.asList("bb")));

        // map: 写像。保持している値を変換する(値を変更した新しいStreamを返す)
        List<String> list3 = Arrays.asList("a.txt", "b.txt");
        List<Path> list4 = list3.stream()
            .map(Paths::get)
            .collect(Collectors.toList());
        assertThat(list4.get(0), instanceOf(Path.class));
        assertThat(list4.get(0).toString(), is("a.txt"));
        assertThat(list4.get(1), instanceOf(Path.class));
        assertThat(list4.get(1).toString(), is("b.txt"));

        // distinct: 重複した値を排除（重複はObject#equals()で判定）
        List<String> list5 = Arrays.asList("b", "a", "b", "c", "a");
        List<String> list6 = list5.stream()
            .distinct()
            .collect(Collectors.toList());
        assertThat(list6, is(Arrays.asList("b", "a", "c"))); // 順序のあるStreamでは順序が保障される

        // sorted: ソートする（保持されたデータがComparableを実装している必要あり）
        List<String> list7 = Arrays.asList("aa", "b", "ccc", "dd");
        List<String> list8 = list7.stream()
            .sorted((s1, s2) -> s1.length() - s2.length()) // 文字列長さでソート
            .collect(Collectors.toList());
        assertThat(list8, is(Arrays.asList("b", "aa", "dd", "ccc")));

        // limit: 指定された個数までに限定する。最大個数を渡す。
        Stream<String> s = Stream.iterate("a", c -> c + "b");
        List<String> list9 = s.limit(4).collect(Collectors.toList());
        assertThat(list9, is(Arrays.asList("a", "ab", "abb", "abbb")));
    }

    /**
     * 終端操作
     * Streamパイプラインを実行して、何らかの結果を取得する処理。
     * forEachは戻り値を返さない。
     */
    @Test
    public void terminalOperations() {
        // forEach: 値を処理する(Consumer)を渡す
        List<String> list1 = Arrays.asList("a", "b", "c");
        list1.stream().forEach(s -> {
            System.out.printf("value=%s%n", s);
        });

        // reduce: 値を集約する。第1引数に初期値、第2引数で集約関数(BinaryOperator)を渡す
        List<String> list2 = Arrays.asList("a", "b", "c");
        String s = list2.stream().reduce( "1", (i, t) -> i + t);
        assertThat(s, is("1abc"));

        // collect: 結果を作成する。
        List<String> list3 = Arrays.asList("a", "b", "c", "d");
        StringBuilder sb = list3.stream().collect(
            StringBuilder::new,  // supplier () -> R: 結果を入れるオブジェクトを作成する関数
            (b, t) -> b.append(t), // accumulator (R, T) -> void: Stream内の値を結果オブジェクトに入れる関数
            (b1, b2) -> b1.append(b2)); // combiner (R, R) -> void: 結果オブジェクト同士をつないで1つの結果にする関数
        assertThat(sb.toString(), is("abcd"));

        // TODO その他の操作
    }

}
