import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

public class GenerateJwtSecret {

    public static void main(String[] args) {

        String secret = Encoders.BASE64.encode(
            Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()
        );

        System.out.println(secret);
    }
}