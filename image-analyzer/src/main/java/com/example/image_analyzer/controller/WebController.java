package com.example.image_analyzer.controller;

import com.example.image_analyzer.model.Image;
import com.example.image_analyzer.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class WebController {

    private final ImageService imageService;

    public WebController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("images", imageService.getAllImages());
        return "index";
    }

    @PostMapping("/process-image")
    public String processImage(@RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            Image image = imageService.saveImage(file);
            image = imageService.detectFaces(image); // Mettre à jour l'image avec la détection des visages
            redirectAttributes.addFlashAttribute("message", "Image téléchargée et analysée avec succès : " + image.getName() + (image.getNumberOfFaces() > 0 ? ". Nombre de visages détectés : " + image.getNumberOfFaces() : ""));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Erreur lors du traitement de l'image.");
        }
        return "redirect:/";
    }
}

