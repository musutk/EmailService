import com.Email;

public class TestEmail {


    public static void main(String[] args) {
        System.setProperty("smtp.properties.path", "src/test/resources/smtp.properties");
        new Email().sendEmail("<p>Simple Text Message </p>", "PUTYOUREMAIL@HERE.COM", "Testing email service");
    }
}
