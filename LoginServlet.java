package servlet;

import dao.UserDAO;
import dto.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Login Servlet (with debug logs)
 */
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        System.out.println("✅ [INIT] LoginServlet initialized successfully!");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set encoding
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        System.out.println("\n=== [LOGIN REQUEST] ===");
        
        // Get parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        System.out.println("📩 Email: " + email);
        System.out.println("🔑 Password: " + password);
        
        // Validate input
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            
            System.out.println("❌ [ERROR] Missing email or password!");
            request.setAttribute("error", "Email và mật khẩu không được để trống!");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }
        
        // Authenticate user
        User user = null;
        try {
            System.out.println("🔍 Checking user in database...");
            user = userDAO.loginUser(email, password);
        } catch (Exception ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("💥 [EXCEPTION] " + ex.getMessage());
            
            request.setAttribute("error", "Lỗi hệ thống! Vui lòng thử lại sau.");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }
        
        if (user != null) {
            // Login successful
            System.out.println("✅ [SUCCESS] Login successful for user: " + user.getEmail());
            System.out.println("🧑‍💼 Role: " + user.getRole());
            
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            
            // Redirect to dashboard (fix: giữ context path)
            String redirectURL = request.getContextPath() + "/dashboard.jsp";
            System.out.println("➡️ Redirecting to: " + redirectURL);
            response.sendRedirect(redirectURL);
            
        } else {
            // Login failed
            System.out.println("❌ [ERROR] Login failed! Invalid credentials for: " + email);
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("/index.html").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔁 [GET] Redirecting to index.html");
        response.sendRedirect("index.html");
    }
}
