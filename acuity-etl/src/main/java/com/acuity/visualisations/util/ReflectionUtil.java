/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.util;

import com.acuity.visualisations.model.output.FieldDelegatingEntity;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformer;
import com.acuity.visualisations.transform.parser.AbstractParser;
import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.acuity.visualisations.data.util.Util.getParserName;

public final class ReflectionUtil {

    private static final String ENTITY_PACKAGE = "com.acuity.visualisations.model.output.entities";
    private static final String PARSER_PACKAGE = "com.acuity.visualisations.transform.parser";
    private static final String FUNCTION_PACKAGE = "com.acuity.visualisations.transform.function";

    private ReflectionUtil() {
    }

    private static String getFullEntityClassName(String packageName, String shortName) {
        return packageName + "." + shortName;
    }

    private static String getFullEntityClassName(String shortName) {
        return getFullEntityClassName(ENTITY_PACKAGE, shortName);
    }

    private static String getFullParserClassName(String shortName) {
        return getFullEntityClassName(PARSER_PACKAGE, shortName);
    }

    private static String getCanonicalSetterName(String fieldName) {
        if (SETTERS_CACHE.containsKey(fieldName)) {
            return SETTERS_CACHE.get(fieldName);
        } else {
            String getterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            SETTERS_CACHE.put(fieldName, getterName);
            return getterName;
        }
    }

    private static String getCanonicalGetterName(String fieldName) {
        if (GETTERS_CACHE.containsKey(fieldName)) {
            return GETTERS_CACHE.get(fieldName);
        } else {
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            GETTERS_CACHE.put(fieldName, getterName);
            return getterName;
        }
    }

    public static Object getFieldValue(Object entity, String fieldName) throws Exception {
        String getterName = getCanonicalGetterName(fieldName);
        Method checkFieldGetter = entity.getClass().getMethod(getterName);
        return checkFieldGetter.invoke(entity);
    }

    public static void setFieldValue(Object entity, String fieldName, Object value, boolean isCompound)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = null;

        Class<?> entityClass = entity.getClass();

        while (entityClass != null && field == null) {
            try {
                field = entityClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                entityClass = entityClass.getSuperclass();
            }
        }

        if (entityClass == null) {
            entityClass = entity.getClass();
        }

        if (field == null) {
            if (FieldDelegatingEntity.class.isAssignableFrom(entityClass)) {
                FieldDelegatingEntity delegatingEntity = (FieldDelegatingEntity) entity;
                delegatingEntity.setField(fieldName, value);
                return;
            } else {
                String message = String.format("No such field. Entity: %s, Field: %s", entity.getClass().getSimpleName(), fieldName);
                throw new NoSuchFieldException(message);
            }
        }

        field.setAccessible(true);

        if (value == null) {
            field.set(entity, null);
        } else {
            if (field.getType().isArray()) {
                if (value.getClass().isArray()) {
                    field.set(entity, value);
                } else {
                    field.set(entity, new Object[]{value});
                }
            } else if (field.getType() == String.class) {
                field.set(entity, value.toString());
            } else {

                if (field.isAnnotationPresent(AcuityField.class)) {
                    AcuityField acuityField = field.getAnnotation(AcuityField.class);
                    if (acuityField.transform() != null) {
                        value = AcuityFieldTransformer.transform(value, acuityField.transform(), isCompound);
                    }
                    field.set(entity, value);
                } else {
                    field.set(entity, value);
                }
            }
        }
    }

    public static void setFieldValue(OutputEntity entity, String fieldName, String value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String setterName = getCanonicalSetterName(fieldName);
        Method fkFieldSetter = entity.getClass().getMethod(setterName, String.class);
        fkFieldSetter.setAccessible(true);
        fkFieldSetter.invoke(entity, value);
    }

    private static final Map<String, String> SETTERS_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> GETTERS_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, AbstractParser<?>> PARSER_CACHE = new ConcurrentHashMap<>();

    public static AbstractParser<?> getParser(Long jobId, String fileName, String colName, String type, Mapper mapper, ParserRule helper)
            throws Exception {
        if (mapper == null) {
            String key = jobId.toString() + "|" + fileName + "|" + colName + "|" + type + "|" + helper.value();
            AbstractParser<?> parser = PARSER_CACHE.get(key);
            if (parser == null) {
                String parserName = getParserName(type);
                Class<?> parserClass = Class.forName(ReflectionUtil.getFullParserClassName(parserName));
                Constructor<?> parserConstructor = parserClass.getConstructor(new Class[]{ParserRule.class});
                parser = (AbstractParser<?>) parserConstructor.newInstance(helper);
                PARSER_CACHE.put(key, parser);
                return parser;
            } else {
                return parser;
            }
        } else {
            // ???do we really need a mapper???
            String parserName = getParserName(type);
            Class<?> parserClass = Class.forName(ReflectionUtil.getFullParserClassName(parserName));
            Constructor<?> parserConstructor = parserClass.getConstructor(new Class[]{Mapper.class, ParserRule.class});
            return (AbstractParser<?>) parserConstructor.newInstance(mapper, helper);
        }
    }

    public static Class<?> getEntityClass(String entityName) throws ClassNotFoundException {
        Class<?> clazz = ENTITY_CLASS_CACHE.get(entityName);
        if (clazz == null) {
            clazz = Class.forName(ReflectionUtil.getFullEntityClassName(entityName));
            ENTITY_CLASS_CACHE.put(entityName, clazz);
            return clazz;
        } else {
            return clazz;
        }
    }
}
