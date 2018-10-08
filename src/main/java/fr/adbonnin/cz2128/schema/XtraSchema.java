package fr.adbonnin.cz2128.schema;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.xtra.predicate.Predicate;

import static fr.adbonnin.xtra.base.XtraDates.parse;
import static fr.adbonnin.xtra.predicate.XtraPredicates.or;
import static java.util.Objects.requireNonNull;

public final class XtraSchema {

    public static SchemaType arrayType() {
        return BaseSchema.ARRAY_TYPE;
    }

    public static SchemaType booleanType() {
        return BaseSchema.BOOLEAN_TYPE;
    }

    public static SchemaType objectType() {
        return BaseSchema.OBJECT_TYPE;
    }

    public static SchemaType numberType() {
        return BaseSchema.NUMBER_TYPE;
    }

    public static SchemaType stringType() {
        return BaseSchema.STRING_TYPE;
    }

    public static SchemaType dateType() {
        return numberType();
    }

    public static SchemaType dateType(final String datePattern) {
        return toSchemaType(or(numberType(), new DateSchemaType(datePattern)));
    }

    public static SchemaType toSchemaType(final Predicate<JsonNode> predicate) {
        return new SchemaType() {
            @Override
            public boolean evaluate(JsonNode value) {
                return predicate.evaluate(value);
            }
        };
    }

    public static Validator toValidator(final Predicate<JsonNode> predicate) {
        return new Validator() {
            @Override
            public boolean evaluate(JsonNode value) {
                return predicate.evaluate(value);
            }
        };
    }

    private static class DateSchemaType implements SchemaType {

        private final String datePattern;

        DateSchemaType(String datePattern) {
            this.datePattern = requireNonNull(datePattern);
        }

        @Override
        public boolean evaluate(JsonNode node) {
            return node == null || (node.isTextual() && parse(node.asText(), datePattern) != null);
        }
    }

    enum BaseSchema implements SchemaType {
        /** @see XtraSchema#arrayType() */
        ARRAY_TYPE {
            @Override
            public boolean evaluate(JsonNode node) {
                return node == null || node.isArray();
            }
        },
        /** @see XtraSchema#booleanType() */
        BOOLEAN_TYPE {
            @Override
            public boolean evaluate(JsonNode node) {
                return node == null || node.isBoolean();
            }
        },
        /** @see XtraSchema#objectType() */
        OBJECT_TYPE {
            @Override
            public boolean evaluate(JsonNode node) {
                return node == null || node.isObject();
            }
        },
        /** @see XtraSchema#numberType() */
        NUMBER_TYPE {
            @Override
            public boolean evaluate(JsonNode node) {
                return node == null || node.isNumber();
            }
        },
        /** @see XtraSchema#stringType() */
        STRING_TYPE {
            @Override
            public boolean evaluate(JsonNode node) {
                return node == null || node.isTextual();
            }
        }
    }

    private XtraSchema() { /* Cannot be instantiated */ }
}

