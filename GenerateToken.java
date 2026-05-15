import com.badminton.config.JwtUtil;
import java.lang.reflect.Method;

public class GenerateToken {
    public static void main(String[] args) throws Exception {
        JwtUtil util = new JwtUtil();
        String token = util.generateToken(26, "siyu_lin", "MEMBER");
        System.out.println(token);
    }
}
