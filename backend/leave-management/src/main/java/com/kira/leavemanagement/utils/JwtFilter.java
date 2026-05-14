package com.kira.leavemanagement.utils;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.repository.EmployeeRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final AuthUtil authUtil;
	private final EmployeeRepository employeeRepository;

	public JwtFilter(AuthUtil authUtil, EmployeeRepository employeeRepository) {
		this.authUtil = authUtil;
		this.employeeRepository = employeeRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);
		Claims claims;
		try {
			claims = authUtil.validate(token);
		} 
        catch (ExpiredJwtException e) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Token expired\"}");
            return;
        } catch (MalformedJwtException e) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }

		Integer employeeId = Integer.parseInt(claims.getSubject());
		Integer tokenSessionVersion = claims.get("session_version", Integer.class);
		Employee employee = employeeRepository.findById(employeeId).orElse(null);
		if (employee == null || !employee.getSession_version().equals(tokenSessionVersion)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Session expired. Please login again\"}");
			return;
		}

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				employee.getId(),
				null,
				java.util.Collections.emptyList());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
}
