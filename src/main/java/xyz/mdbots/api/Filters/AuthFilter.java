package xyz.mdbots.api.Filters;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xyz.mdbots.api.Services.JwtToken;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Value("${frontend.cookie}")
    private String cookieName;

    @Autowired
    JwtToken jwtToken;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        String cookieValue = findAuthCookieValue(cookies);

        if (cookieValue != null) {
            Claims validUser = jwtToken.decode(cookieValue);

            if (validUser != null) {
                req.setAttribute("auth", validUser);
                req.setAttribute("userId", validUser.get("sub"));
                filterChain.doFilter(req, res);
            } else
                UnAuthorizedError(res, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");

        } else
            UnAuthorizedError(res, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");

    }

    public String findAuthCookieValue(Cookie[] cookies) {
        return cookies != null ? Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null) : null;
    }

    private void UnAuthorizedError(HttpServletResponse res, int statusCode, String statusText) throws IOException {
        res.setStatus(statusCode);
        res.setContentType("application/json");
        String jsonRes = String.format("{\"statusCode\": %d, \"statusText\": \"%s\"}", statusCode, statusText);
        res.getWriter().write(jsonRes);
    }
}
