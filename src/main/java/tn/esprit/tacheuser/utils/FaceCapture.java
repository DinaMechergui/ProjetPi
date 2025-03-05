package tn.esprit.tacheuser.utils;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import java.io.File;

public class FaceCapture {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static String captureImage() {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Erreur : Impossible d’ouvrir la caméra !");
            return null;
        }

        String filename = "captured.jpg";
        Imgcodecs.imwrite(filename, new Mat());
        camera.release();

        File file = new File(filename);
        return file.exists() ? filename : null;
    }
}

