package com.maruhxn.lossion.docs.support;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.io.IOException;
import java.util.List;

/**
 * default 템플릿이 아닌 custom 템플릿을 사용하기 위한 클래스
 * type을 보고 template에서 맞는 템플릿을 선택해서 동작
 * e.g. custom-response-fields.snippet을 사용하기 위해서는 type의 값으로 "custom-response"를 주면 됩니다
 */
public class CustomResponseFieldsSnippet extends AbstractFieldsSnippet {
    public CustomResponseFieldsSnippet(
            String type, // snippet file prefix
            List<FieldDescriptor> descriptors, // 우리가 커스텀할 response의 fields를 문서화 하기 위한 Descriptors..  code, message, httpStatus 를 key 생성자인 descriptor로 받아서 문서화 하기 위함
            boolean ignoreUndocumentedFields) {
        super(type, descriptors, null, ignoreUndocumentedFields);
    }

    @Override
    protected MediaType getContentType(Operation operation) {
        return operation.getResponse().getHeaders().getContentType();
    }

    @Override
    protected byte[] getContent(Operation operation) throws IOException {
        return operation.getResponse().getContent();
    }
}