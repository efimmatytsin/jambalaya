/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhokhov.jambalaya.mapstruct.processor;

import org.mapstruct.ap.spi.AccessorNamingStrategy;
import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.MapStructProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;

/**
 * The implementation of {@link AccessorNamingStrategy} service provider interface for Protocol Buffers (protobuf)
 * and jOOQ generated classes.
 *
 * @author Alexey Zhokhov
 */
public class JambalayaAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    private static final String PROTOBUF_MESSAGE_LITE_OR_BUILDER = "com.google.protobuf.MessageLiteOrBuilder";
    private static final String JOOQ_RECORD = "org.jooq.Record";

    /**
     * The list of special utilities methods automatically generated by the protoc compiler.
     */
    private static final List<String> PROTOBUF_SPECIAL_METHODS = Arrays.asList(
            // merge* methods
            "mergeFrom", "mergeUnknownFields",
            // has* methods
            "hasField", "hasOneof",
            // clear* methods
            "clearField", "clearOneof",
            // set* methods
            "setUnknownFields",
            // get* methods
            "getInitializationErrorString", "getAllFields", "getUnknownFields", "getDescriptorForType",
            "getDefaultInstanceForType", "getSerializedSize", "getParserForType",
            // is* methods
            "isInitialized"
    );

    protected TypeMirror protobufMessageLiteOrBuilder;
    protected TypeMirror jooqRecord;

    @Override
    public void init(MapStructProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        TypeElement typeElement = elementUtils.getTypeElement(PROTOBUF_MESSAGE_LITE_OR_BUILDER);
        if (typeElement != null) {
            protobufMessageLiteOrBuilder = typeElement.asType();
        }

        typeElement = elementUtils.getTypeElement(JOOQ_RECORD);
        if (typeElement != null) {
            jooqRecord = typeElement.asType();
        }
    }

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        boolean isGetterMethod = super.isGetterMethod(method);

        if (isGetterMethod) {
            if (isProtobufMessageLiteOrBuilder(method)) {
                String methodName = method.getSimpleName().toString();

                if (PROTOBUF_SPECIAL_METHODS.contains(methodName)) {
                    return false;
                }
            }
        }

        return isGetterMethod;
    }

    @Override
    public boolean isSetterMethod(ExecutableElement method) {
        boolean isSetterMethod = super.isSetterMethod(method);

        if (isSetterMethod) {
            if (isProtobufMessageLiteOrBuilder(method)) {
                String methodName = method.getSimpleName().toString();

                if (PROTOBUF_SPECIAL_METHODS.contains(methodName)) {
                    return false;
                } else if (methodName.endsWith("Bytes") && method.getParameters().size() == 1) {
                    TypeMirror param = method.getParameters().get(0).asType();

                    if (param.toString().equals("com.google.protobuf.ByteString")) {
                        return false;
                    }
                } else if (methodName.endsWith("Value") && method.getParameters().size() == 1) {
                    TypeMirror param = method.getParameters().get(0).asType();

                    if (param.getKind().isPrimitive() && param.toString().equals("int")) {
                        return false;
                    }
                } else if (methodName.startsWith("merge") && method.getParameters().size() == 1) {
                    TypeMirror param = method.getParameters().get(0).asType();

                    if (param.getKind() == TypeKind.DECLARED && typeUtils.isAssignable(param, protobufMessageLiteOrBuilder)) {
                        return false;
                    }
                }
            } else if (isJooqRecord(method)) {
                String methodName = method.getSimpleName().toString();

                if (methodName.startsWith("value")) {
                    return false;
                }
            }
        }

        return isSetterMethod;
    }

    @Override
    protected boolean isFluentSetter(ExecutableElement method) {
        boolean isFluentSetter = super.isFluentSetter(method);

        if (isFluentSetter) {
            if (isProtobufMessageLiteOrBuilder(method)) {
                String methodName = method.getSimpleName().toString();

                if (PROTOBUF_SPECIAL_METHODS.contains(methodName)) {
                    return false;
                }
            }
        }

        return isFluentSetter;
    }

    @Override
    public boolean isAdderMethod(ExecutableElement method) {
        boolean isAdderMethod = super.isAdderMethod(method);

        if (isAdderMethod) {
            if (isProtobufMessageLiteOrBuilder(method)) {
                String methodName = method.getSimpleName().toString();

                if (PROTOBUF_SPECIAL_METHODS.contains(methodName)) {
                    return false;
                }
            }
        }

        return isAdderMethod;
    }

    @Override
    public boolean isPresenceCheckMethod(ExecutableElement method) {
        boolean isPresenceCheckMethod = super.isPresenceCheckMethod(method);

        if (isPresenceCheckMethod) {
            if (isProtobufMessageLiteOrBuilder(method)) {
                String methodName = method.getSimpleName().toString();

                if (PROTOBUF_SPECIAL_METHODS.contains(methodName)) {
                    return false;
                }
            }
        }

        return isPresenceCheckMethod;
    }

    private boolean isProtobufMessageLiteOrBuilder(ExecutableElement method) {
        Element receiver = method.getEnclosingElement();
        return protobufMessageLiteOrBuilder != null
                && receiver != null
                && typeUtils.isAssignable(receiver.asType(), protobufMessageLiteOrBuilder);
    }

    private boolean isJooqRecord(ExecutableElement method) {
        Element receiver = method.getEnclosingElement();
        return jooqRecord != null
                && receiver != null
                && typeUtils.isAssignable(receiver.asType(), jooqRecord);
    }

}
