package com.prestamos.sistema_prestamos.modules.prestamos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class ArchivoService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String guardarPdf(MultipartFile archivo) {
        if (archivo.isEmpty())
            throw new RuntimeException("El archivo está vacío");

        if (!archivo.getContentType().equals("application/pdf"))
            throw new RuntimeException("Solo se permiten archivos PDF");

        if (archivo.getSize() > 10 * 1024 * 1024)
            throw new RuntimeException("El archivo no puede superar 10MB");

        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath))
                Files.createDirectories(dirPath);

            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            Path rutaArchivo = dirPath.resolve(nombreArchivo);
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            log.info("PDF guardado: {}", nombreArchivo);
            return nombreArchivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    public byte[] obtenerPdf(String nombreArchivo) {
        try {
            Path rutaArchivo = Paths.get(uploadDir).resolve(nombreArchivo);
            if (!Files.exists(rutaArchivo))
                throw new RuntimeException("Archivo no encontrado");
            return Files.readAllBytes(rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo: " + e.getMessage());
        }
    }

    public void eliminarPdf(String nombreArchivo) {
        try {
            Path rutaArchivo = Paths.get(uploadDir).resolve(nombreArchivo);
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            log.error("Error al eliminar archivo: {}", e.getMessage());
        }
    }
}