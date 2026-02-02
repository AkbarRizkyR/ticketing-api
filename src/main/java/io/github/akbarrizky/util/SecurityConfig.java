package io.github.akbarrizky.util;

import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.auth.LoginConfig;

@Provider
@LoginConfig(authMethod = "MP-JWT")
public class SecurityConfig {
}
