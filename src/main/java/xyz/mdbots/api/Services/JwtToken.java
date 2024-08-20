package xyz.mdbots.api.Services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import xyz.mdbots.api.Services.Models.DiscordTokenModel;
import xyz.mdbots.api.Services.Models.DiscordUserModel;

@Service
public class JwtToken {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.issuer}")
    private String JWT_ISSUER;

    @Value("${jwt.audience}")
    private String JWT_AUDIENCE;

    public String encode(DiscordTokenModel discordTokenModel, DiscordUserModel discordUserModel) {
        try {
            byte[] keyBytes = JWT_SECRET.getBytes();
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Map<String, Object> claimsMap = new HashMap<>();
            claimsMap.put("refresh_token", discordTokenModel.getRefreshToken());
            claimsMap.put("access_token", discordTokenModel.getAccessToken());
            claimsMap.put("token_type", discordTokenModel.getTokenType());
            claimsMap.put("expires_in", discordTokenModel.getExpiresIn());
            claimsMap.put("scope", discordTokenModel.getScope());

            return Jwts.builder()
                    .subject(discordUserModel.getId())
                    .claims(claimsMap)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 518400000L)) // 6 days
                    .audience().add(JWT_AUDIENCE).and()
                    .issuer(JWT_ISSUER)
                    .signWith(key)
                    .compact();
        } catch (JwtException e) {
            throw new RuntimeException("Falha ao codificar JWT", e);
        }
    }

    public Claims decode(String discordToken) {
        try {
            byte[] keyBytes = JWT_SECRET.getBytes();
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            return Jwts.parser()
                    .verifyWith(key)
                    .requireAudience(JWT_AUDIENCE)
                    .requireIssuer(JWT_ISSUER)
                    .build()
                    .parseSignedClaims(discordToken)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Falha ao decodificar JWT", e);
        }
    }
}
