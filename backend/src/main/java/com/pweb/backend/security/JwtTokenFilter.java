//package com.pweb.backend.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.provisioning.UserDetailsManager;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//import static org.springframework.util.ObjectUtils.isEmpty;
//
//@Component
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final UserDetailsManager userRepo;
////    private final UserRepo userRepo;
//
//    public JwtTokenFilter(JwtUtil jwtUtil, UserDetailsManager userRepo/*,
//                          UserRepo userRepo*/) {
//        this.jwtUtil = jwtUtil;
//        /*this.userRepo = userRepo;*/
//        this.userRepo = userRepo;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain) throws ServletException, IOException {
//        // Get authorization header and validate
//        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (isEmpty(header) || !header.startsWith("Bearer ")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Get jwt token and validate
//        final String token = header.split(" ")[1].trim();
//        if (!jwtUtil.validate(token)) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Get user identity and set it on the spring security context
//        UserDetails userDetails = userRepo
//                .loadUserByUsername(jwtUtil.extractUsername(token));
//
//        UsernamePasswordAuthenticationToken
//                authentication = new UsernamePasswordAuthenticationToken(
//                userDetails, null,
//                userDetails == null ?
//                        List.of() : userDetails.getAuthorities()
//        );
//
//        authentication.setDetails(
//                new WebAuthenticationDetailsSource().buildDetails(request)
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        chain.doFilter(request, response);
//    }
//
//}