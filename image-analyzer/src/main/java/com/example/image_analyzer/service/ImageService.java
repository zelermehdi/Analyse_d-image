package com.example.image_analyzer.service;

import com.example.image_analyzer.model.Image;
import com.example.image_analyzer.repository.ImageRepository;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final CascadeClassifier faceDetector;
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
        this.faceDetector = new CascadeClassifier("src/main/resources/haarcascade_frontalface_alt.xml");
    }

    /**
     * Sauvegarde une image dans la base de données.
     */
    public Image saveImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setData(file.getBytes());
        Image savedImage = imageRepository.save(image);
        logger.info("Image sauvegardée avec l'ID: " + savedImage.getId());
        return savedImage;
    }

    /**
     * Détecte les visages dans une image et sauvegarde les résultats dans la base de données.
     */
    public Image detectFaces(Image image) {
        Mat mat = opencv_imgcodecs.imdecode(new Mat(image.getData()), opencv_imgcodecs.IMREAD_COLOR);
        if (mat.empty()) {
            logger.error("Échec du chargement de l'image pour la détection des visages");
            return null;
        }

        RectVector faceDetections = new RectVector();
        faceDetector.detectMultiScale(mat, faceDetections);

        int numberOfFaces = (int) faceDetections.size();
        logger.info("Nombre de visages détectés: " + numberOfFaces);

        for (int i = 0; i < faceDetections.size(); i++) {
            Rect rect = faceDetections.get(i);
            opencv_imgproc.rectangle(mat, new Point(rect.x(), rect.y()),
                    new Point(rect.x() + rect.width(), rect.y() + rect.height()),
                    new Scalar(0, 255, 0, 0));
        }

        ByteBuffer buffer = ByteBuffer.allocate((int) (mat.total() * mat.elemSize()));
        mat.data().get(buffer.array());
        image.setData(buffer.array());
        image.setNumberOfFaces(numberOfFaces); // Définit le nombre de visages détectés sur l'objet Image

        imageRepository.save(image); // Sauvegarde les données mises à jour dans la base de données
        logger.info("Image mise à jour avec les visages détectés pour l'ID: " + image.getId());

        return image; // Retourne l'objet Image mis à jour
    }

    /**
     * Récupère une image par son ID.
     */
    public Optional<Image> getImageById(Long id) {
        logger.info("Récupération de l'image avec l'ID: " + id);
        return imageRepository.findById(id);
    }

    /**
     * Récupère toutes les images.
     */
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }
}
