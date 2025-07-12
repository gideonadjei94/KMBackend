package com.gideon.knowmate.Security;

import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.UserDomain;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Exceptions.ExpiredAuthTokenException;
import com.gideon.knowmate.Exceptions.InvalidAuthTokenException;
import com.gideon.knowmate.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${secret-key}")
    private String SECRET_KEY;

    @Value("${jwt_token_exp}")
    private long jwtExpiration;

    @Value("${refresh_token_exp}")
    private long refreshTokenExpiration;

    private final UserRepository userRepo;

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateJwtToken(UserDetails userDetails, UserDomain userType) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }


    public String generateRefreshToken(UserDetails userDetails, UserDomain userType) {
        return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }


    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {

        String id;
        String userRole = "";

            Optional<User> userOptional = userRepo.findByEmail(userDetails.getUsername());
            if(userOptional.isEmpty()) {
                throw new EntityNotFoundException("User does not exist");
            }
            User user = userOptional.get();
            if(Objects.nonNull(user.getUserRole()))
                userRole = user.getUserRole().name();
            id = user.getId();

        return Jwts
                .builder()
                .claims(extraClaims)
                .id(String.valueOf(id))
                .claim("roles", List.of(userRole))
                .claim("id",id)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        if(!username.equals(userDetails.getUsername())) {
            throw new InvalidAuthTokenException("Invalid access token");
        }
        if(tokenIsExpired(jwt)) {
            throw new ExpiredAuthTokenException("Expired access token");
        }
        return true;
    }


    public boolean tokenIsExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
