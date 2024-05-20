package com.example.ipfsdemon;

import com.example.ipfsdemon.kafka.KafkaProducerIpfs;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IPFSController {

    private static final Logger logger = LoggerFactory.getLogger(IPFSController.class);

    @Autowired
    private IPFSService ipfsService;

    @Autowired
    private KafkaProducerIpfs kafkaProducerIpfs;

    @GetMapping(value = "")
    public ResponseEntity<String> saveText(@RequestParam("filepath") String filepath) {
        try {
            String result = ipfsService.saveFile(filepath);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error saving file from path: {}", filepath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file: " + e.getMessage());
        }
    }

    @PostMapping(value = "upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String hashCode = ipfsService.saveFile(file);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> messageData = new HashMap<>();
            messageData.put("fileName", originalFileName);
            messageData.put("hashCode", hashCode);

            String messageJson = objectMapper.writeValueAsString(messageData);
            kafkaProducerIpfs.sendMessage("HashCode_Topic", messageJson);

            return ResponseEntity.ok(hashCode);
        } catch (Exception e) {
            logger.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping(value = "file/{hash}")
    public ResponseEntity<byte[]> getFile(@PathVariable("hash") String hash) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-type", MediaType.ALL_VALUE);
            byte[] bytes = ipfsService.loadFile(hash);
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(bytes);
        } catch (Exception e) {
            logger.error("Error retrieving file with hash: {}", hash, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
