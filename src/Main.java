import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Main {
    private static void copyFile(File source, File target) throws IOException {
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    private static void copySequential(File src1, File dst1, File src2, File dst2) throws IOException {
        long start = System.nanoTime();
        copyFile(src1, dst1);
        copyFile(src2, dst2);
        System.out.println("Последовательное копирование: " + (System.nanoTime() - start) / 1_000_000 + " мс.");
    }
    private static void copyParallel(File src1, File dst1, File src2, File dst2) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try { copyFile(src1, dst1); } catch (IOException e) { e.printStackTrace(); }
        });
        Thread t2 = new Thread(() -> {
            try { copyFile(src2, dst2); } catch (IOException e) { e.printStackTrace(); }
        });

        long start = System.nanoTime();
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Параллельное копирование: " + (System.nanoTime() - start) / 1_000_000 + " мс.");
    }

    public static void main(String[] args) {
        File src1 = new File("File1.txt");
        File src2 = new File("File2.txt");

        if (!src1.exists() || !src2.exists()) {
            System.out.println("Исходные файлы не найдены.");
            return;
        }

        try {
            copySequential(src1, new File("File1Copy_seq.txt"),
                    src2, new File("File2Copy_seq.txt"));

            copyParallel(src1, new File("File1Copy_par.txt"),
                    src2, new File("File2Copy_par.txt"));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
