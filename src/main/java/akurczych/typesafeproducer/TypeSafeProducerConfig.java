package akurczych.typesafeproducer;

import static java.util.function.Predicate.not;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SecurityConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serializer;

public final class TypeSafeProducerConfig {

    @SuppressWarnings("serial")
    public static final class UnsupportedPropertyException extends RuntimeException {
        private UnsupportedPropertyException(String message) {
            super(message);
        }
    }

    @SuppressWarnings("serial")
    public static final class ConflictingPropertyException extends RuntimeException {
        private ConflictingPropertyException(String message) {
            super(message);
        }
    }

    private String bootstrapServers;
    private Class<? extends Serializer<?>> keySerializerClass;
    private Class<? extends Serializer<?>> valueSerializerClass;
    private final Map<String, Object> customEntries = new HashMap<>();

    public TypeSafeProducerConfig withBootstrapServer(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
        return this;
    }

    public TypeSafeProducerConfig withKeySerializerClass(Class<? extends Serializer<?>> keySerializerClass) {
        this.keySerializerClass = keySerializerClass;
        return this;
    }

    public TypeSafeProducerConfig withValueSerializerClass(Class<? extends Serializer<?>> valueSerializerClass) {
        this.valueSerializerClass = valueSerializerClass;
        return this;
    }

    public TypeSafeProducerConfig withCustomEntry(String propertyName, Object value) {
        Objects.requireNonNull(propertyName, "Property name cannot be null");
        customEntries.put(propertyName, value);
        return this;
    }

    public Map<String, Object> mapify() {
        final var stagingConfig = new HashMap<String, Object>();
        if (!customEntries.isEmpty()) {
            final var supportedKeys = scanClassesForPropertyNames(SecurityConfig.class, 
                                                                  SslConfigs.class,
                                                                  SaslConfigs.class,
                                                                  ProducerConfig.class,
                                                                  CommonClientConfigs.class);
            
            final var unsupportedKeys = customEntries.keySet()
                                                     .stream()
                                                     .filter(not(supportedKeys::contains))
                                                     .findAny();
            
            if (unsupportedKeys.isPresent()) {
                throw new UnsupportedPropertyException("Unsupported property: " + unsupportedKeys.get());
            }
            
            stagingConfig.putAll(customEntries);
        }
        
        Objects.requireNonNull(bootstrapServers, "Bootstrap servers not set");
        tryInsertEntry(stagingConfig, CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        Objects.requireNonNull(keySerializerClass, "Key serializer not set");
        tryInsertEntry(stagingConfig, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass.getName());
        Objects.requireNonNull(valueSerializerClass, "Value serializer not set");
        tryInsertEntry(stagingConfig, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass.getName());
        
        return stagingConfig;
    }
    
    private static void tryInsertEntry(Map<String, Object> staging, String key, Object value) {
        staging.compute(key, (__key, existingValue) -> {
          if (existingValue == null) {
            return value;
          } else {
            throw new ConflictingPropertyException("Property " + key + " conflicts with an expected property");
          }
        });
      }

    private Set<String> scanClassesForPropertyNames(Class<?>... classes) {
        return Arrays.stream(classes)
                     .map(Class::getFields)
                     .flatMap(Arrays::stream)
                     .filter(TypeSafeProducerConfig::isFieldConstant)
                     .filter(TypeSafeProducerConfig::isFieldStringType)
                     .filter(not(TypeSafeProducerConfig::isFieldDoc))
                     .map(TypeSafeProducerConfig::retrieveField)
                     .collect(Collectors.toSet());
    }
    
    private static boolean isFieldConstant(Field field) {
        return Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers());
      }
      
      private static boolean isFieldStringType(Field field) {
        return field.getType().equals(String.class);
      }
      
      private static boolean isFieldDoc(Field field) {
        return field.getName().endsWith("_DOC");
      }
      
      private static String retrieveField(Field field) {
        try {
          return (String) field.get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
}
