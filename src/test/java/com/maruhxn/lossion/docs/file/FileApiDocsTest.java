package com.maruhxn.lossion.docs.file;

import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Docs] - FileAPIDocs")
public class FileApiDocsTest extends RestDocsSupport {

    @DisplayName("파일명을 전달하여 해당 파일을 조회한다.")
    @Test
    void getFile() throws Exception {
        // Given
        String storedName = fileService.storeOneFile(getMockMultipartFile());

        // When / Then
        getAction("/api/files/{fileName}", false, null, storedName)
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("fileName").description("서버에 저장된 파일 이름")
                                )
                        )
                );
    }

    private MockMultipartFile getMockMultipartFile() throws IOException {
        final String originalFileName = "defaultProfileImage.jfif";
        final String filePath = "src/test/resources/static/img/" + originalFileName;

        return new MockMultipartFile(
                "images", //name
                originalFileName,
                "image/jpeg",
                new FileInputStream(filePath)
        );
    }
}
