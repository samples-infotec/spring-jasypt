package mx.infotec.sample.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * JPA Attribute Converter for encrypting and decrypting String attributes. This
 * converter uses the Jasypt library for encryption and decryption operations.
 * It is configured with an environment variable for the encryption password.
 *
 * Note: Ensure that the Jasypt library is correctly configured in your project.
 * The encryption password should be provided through the
 * "jasypt.encryptor.password" property.
 *
 * @author rahul.chauhan
 * @see https://www.geeksforgeeks.org/spring-boot-enhancing-data-security-column-level-encryption/
 */
@Converter
public class StringCryptoConverter implements AttributeConverter<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringCryptoConverter.class);

    // Property name for the encryption algorithm
    private static final String ENCRYPTION_ALGORITHM_PROPERTY = "jasypt.encryptor.algorithm";

    // Property name for the encryption password
    private static final String ENCRYPTION_PASSWORD_PROPERTY = "jasypt.encryptor.password";

    // Jasypt StringEncryptor for performing encryption and decryption
    private final StandardPBEStringEncryptor encryptor;

    /**
     * Constructor for StringCryptoConverter.
     *
     * @param environment The Spring Environment used to access properties.
     */
    public StringCryptoConverter(Environment environment) {
        var algorithm = environment.getProperty(ENCRYPTION_ALGORITHM_PROPERTY);

        // Initialize the encryptor with the encryption password from the environment
        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setAlgorithm(algorithm);
        this.encryptor.setPassword(environment.getProperty(ENCRYPTION_PASSWORD_PROPERTY));

        LOGGER.debug("StringCryptoConverter init with {} algorithm", algorithm);

        if (LOGGER.isDebugEnabled()) {
            var tmp = this.encryptor.encrypt(ENCRYPTION_ALGORITHM_PROPERTY);
            LOGGER.debug("Encrypt {}: {}", ENCRYPTION_ALGORITHM_PROPERTY, tmp);
            LOGGER.debug("Decrypt: {} ", this.encryptor.decrypt(tmp));
        }
    }

    /**
     * Converts the attribute value to the encrypted form.
     *
     * @param attribute The original attribute value to be encrypted.
     * @return The encrypted form of the attribute.
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptor.encrypt(attribute);
    }

    /**
     * Converts the encrypted database value to its decrypted form.
     *
     * @param dbData The encrypted value stored in the database.
     * @return The decrypted form of the database value.
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptor.decrypt(dbData);
    }
}
