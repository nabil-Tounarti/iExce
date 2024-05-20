package com.example.ipfsdemon;

import com.example.ipfsdemon.IPFSController;
import com.example.ipfsdemon.IPFSService;
import com.example.ipfsdemon.kafka.KafkaProducerIpfs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IPFSControllerTest {

    @Mock
    private IPFSService ipfsService;

    @Mock
    private KafkaProducerIpfs kafkaProducerIpfs;

    @InjectMocks
    private IPFSController ipfsController;

    public IPFSControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTextTest() {
        String expectedHashCode = "mockedHashCode";
        when(ipfsService.saveFile(anyString())).thenReturn(expectedHashCode);

        ResponseEntity<String> responseEntity = ipfsController.saveText("testFilePath");

        verify(ipfsService).saveFile("testFilePath");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedHashCode, responseEntity.getBody());
    }


    @Test
    void uploadFileTest() throws Exception {
        String expectedHashCode = "mockedHashCode";
        when(ipfsService.saveFile(any(MultipartFile.class))).thenReturn(expectedHashCode);

        String fileName = "testFile.txt";
        byte[] fileContent = "test file content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent);

        ResponseEntity<String> responseEntity = ipfsController.uploadFile(mockFile);

        verify(ipfsService).saveFile(mockFile);

        // Verify that KafkaProducerIpfs.sendMessage was called
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaProducerIpfs).sendMessage(topicCaptor.capture(), messageCaptor.capture());
        assertEquals("HashCode_Topic", topicCaptor.getValue()); // Corrected expected value

        // Verify the message sent to Kafka
        String expectedMessageJson = "{\"fileName\":\"testFile.txt\",\"hashCode\":\"mockedHashCode\"}";
        assertEquals(expectedMessageJson, messageCaptor.getValue());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedHashCode, responseEntity.getBody());
    }
    @Test
    void getFileTest() {
        // Mock IPFSService to return some byte array
        byte[] mockBytes = "mockedFileContent".getBytes();
        when(ipfsService.loadFile("testHash")).thenReturn(mockBytes);

        ResponseEntity<byte[]> responseEntity = ipfsController.getFile("testHash");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("mockedFileContent", new String(responseEntity.getBody()));
    }
}