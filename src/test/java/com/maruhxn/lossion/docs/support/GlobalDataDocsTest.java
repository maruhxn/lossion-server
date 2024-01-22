package com.maruhxn.lossion.docs.support;

import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalDataDocsTest extends RestDocsSupport {

    private static final String ERROR_SNIPPET_FILE = "errorcode-response";

    @Test
    public void validationErrorSample() throws Exception {
        GlobalDataDocsController.SampleRequest req = new GlobalDataDocsController.SampleRequest("", "");
        postAction("/test/validation-error", req, false)
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("code").description("Error Code"),
                                        fieldWithPath("message").description("에러 메시지"),
                                        fieldWithPath("errors").description("Error 값 배열 값"),
                                        fieldWithPath("errors[0].field").description("문제 있는 필드"),
                                        fieldWithPath("errors[0].value").description("문제가 있는 값"),
                                        fieldWithPath("errors[0].reason").description("문재가 있는 이유")
                                )
                        )
                )
        ;
    }

    @Test
    public void internalServerExceptionSample() throws Exception {
        getAction("/test/internal-server-exception", false, null)
                .andExpect(status().isInternalServerError())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("code").description("Error Code"),
                                        fieldWithPath("message").description("에러 메시지"),
                                        fieldWithPath("errors").description("에러 정보 리스트")
                                )
                        )
                )
        ;
    }

    @Test
    void errorCode() throws Exception {
        getAction("/test/error-code", false, null)
                .andDo(
                        restDocs.document(
                                customResponseFields(ERROR_SNIPPET_FILE, fieldDescriptors()
                                )
                        ));
    }

    private List<FieldDescriptor> fieldDescriptors() {
        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        for (ErrorCode errorCode : ErrorCode.values()) {
            FieldDescriptor attributes = fieldWithPath(errorCode.name())
                    .type(OBJECT)
                    .attributes(
                            key("code").value(errorCode.name()),
                            key("message").value(errorCode.getMessage()),
                            key("httpStatus").value(String.valueOf(errorCode.getHttpStatus().value())));
            fieldDescriptors.add(attributes);
        }
        return fieldDescriptors;
    }

    // 커스텀 템플릿 사용을 위한 함수
    // 일반적으로 사용하는 responseFields()를 커스텀해서 사용하기 위함
    public static CustomResponseFieldsSnippet customResponseFields(String snippetFilePrefix, List<FieldDescriptor> fieldDescriptors) {
        return new CustomResponseFieldsSnippet(snippetFilePrefix, fieldDescriptors, true);
    }
}
