package in.ac.iitm.shaili;

/**
 * Created by Awanish Raj on 20/05/16.
 */
public class WordEntry {
    public int count = 0;
    public String word;

    public WordEntry(String word, Integer count) {
        this.word = word;
        this.count = count;
    }

    public void increment() {
        this.count++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordEntry wordEntry = (WordEntry) o;

        return word.equals(wordEntry.word);

    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }
}
