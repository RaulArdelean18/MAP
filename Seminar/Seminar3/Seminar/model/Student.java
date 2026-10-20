public class Student {
    // Membrii clasei (atributele)
    private String nume;
    private float media;

    // Constructor cu parametri
    public Student(String nume, float media) {
        this.nume = nume;
        this.media = media;
    }

    // Suprascrierea metodei toString()
    @Override
    public String toString() {
        return "Student{nume='" + nume + "', media=" + media + "}";
    }

    // Getters și Setters (opțional, dar recomandat)
    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public float getMedia() {
        return media;
    }

    public void setMedia(float media) {
        this.media = media;
    }

    // Metodă main pentru instanțiere și testare
    public static void main(String[] args) {
        Student s1 = new Student("Dan", 4.5f);
        Student s2 = new Student("Ana", 8.5f);
        Student s3 = new Student("Dan", 4.5f);

        // Afișarea obiectelor create (folosește automat metoda toString)
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
    }
}