package example.java8;


public class Lambda {
    public static class Student {
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
