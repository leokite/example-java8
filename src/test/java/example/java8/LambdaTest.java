package example.java8;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;

import org.junit.Test;

public class LambdaTest {

    /**
     * ラムダ式導入前の書き方:
     * 匿名クラスを生成しメソッドの実装を記述する
     */
    @Test
    public void legacy_anonymouse_class_style() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(50));
        students.add(new Student(80));
        students.add(new Student(30));

                                   // 匿名クラス
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return Integer.compare(s1.getScore(), s2.getScore());
            }
        });

        assertEquals(students.get(0).getScore(), 30);
        assertEquals(students.get(1).getScore(), 50);
        assertEquals(students.get(2).getScore(), 80);
    }

    /**
     * ラムダ式導入後の書き方:
     * 関数型インターフェースComparatorにラムダ式を渡す
     */
    @Test
    public void java8_lambda_style() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(50));
        students.add(new Student(80));
        students.add(new Student(30));

        // ラムダ式の基本構文: (Type parameter, Type parameter) -> { return (process) }
        Collections.sort(students, (Student s1, Student s2) -> {
            return Integer.compare(s1.getScore(), s2.getScore());
        });

        // 引数の型は型推論により省略可能: (parameter, parameter) -> { return (process) }
        Collections.sort(students, (s1, s2) -> {
            return Integer.compare(s1.getScore(), s2.getScore());
        });

        // 処理が1行の場合'return' や '{}' は省略可能: (parameter, parameter) -> process
        Collections.sort(students, (s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()));

        // 引数が1つの場合'()' は省略可能: parameter -> process
        // name -> "Hello " + name;

        assertEquals(students.get(0).getScore(), 30);
        assertEquals(students.get(1).getScore(), 50);
        assertEquals(students.get(2).getScore(), 80);
    }

    /**
     * メソッド参照：
     * 関数型インターフェースの変数にメソッドを代入すること
     */
    @Test
    public void method_reference_param0() {
        String string = "abc";

        // メソッド参照
        // 呼び出したいメソッド名の直前を':'にしメソッドの引数(丸括弧)を除去
        IntSupplier supplier = string::length;
        assertEquals(supplier.getAsInt(), 3);

        // ラムダ式
        IntSupplier supplier2 = () -> string.length();
        assertEquals(supplier2.getAsInt(), 3);

    }

    @Test
    public void method_reference_param1() {
        // メソッド参照
        Consumer<String> consumer1 = System.out::println;
        consumer1.accept("abc");

        // ラムダ式
        Consumer<String> consumer2 = (String s) -> System.out.println(s);
        consumer2.accept("abc");

    }

    @Test
    public void lambda_param_scope() {
        int n = 123;
        Runnable runner = () -> {
            // ラムダ式の外側で定義された変数を参照可能
            System.out.println(n);
            // ただし変数の値は変えられない
            // n++;
        };
        // ラムダ式の外側で値の再代入はできない
        // n++;
        runner.run();
    }

    @Test
    public void lambda_primitive_wrapper() {
        Consumer<Integer> consumerForInteger = (Integer n) -> System.out.println(n);

        // コンパイルエラー オートボクシングはされない
        // consumerForInteger = (int n) -> System.out.println(n);

        // 引数を省略していればOK
        consumerForInteger = (n) -> System.out.println(n);
        consumerForInteger.accept(123); // オートボクシングされる
    }

    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    @Test
    public void lambda_cast() {
        // 通常のキャスト
        Runnable run = (Runnable) () -> System.out.println("cast");

        // 交差型キャスト
        Function f = (Function<Integer, Integer> & Serializable) i -> i + 4;

        assertEquals(f.apply(4), 8);
        assertThat(f, instanceOf(Function.class));
        assertThat(f, instanceOf(Serializable.class));
    }

    private static class Student {
        private final int score;
        public Student(int score) {
            this.score = score;
        }
        public int getScore() {
            return this.score;
        }

        @Override
        public String toString() {
            return String.valueOf(this.score);
        }
    }

}
