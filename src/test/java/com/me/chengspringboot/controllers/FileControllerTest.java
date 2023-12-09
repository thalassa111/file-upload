package com.me.chengspringboot.controllers;

import com.me.chengspringboot.services.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application-test.properties")
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void testUploadFile() throws Exception {
        //given - mock data
        String token = "test-token";
        int folderId = 1;
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", MediaType.TEXT_PLAIN_VALUE, "some test text".getBytes());

        //when - mock upload file method
        when(fileService.uploadFile(token, 1, file)).thenReturn(ResponseEntity.ok("File uploaded successfully"));
        //mocked http request to the endpoint under test
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/file/upload-file/{folderId}", folderId)
                .file(file)
                .header("Authorization", token);

        //then - do the http request and assertions
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File uploaded successfully"));
    }
}