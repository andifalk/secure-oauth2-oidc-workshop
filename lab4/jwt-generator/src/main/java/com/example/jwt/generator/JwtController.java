package com.example.jwt.generator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class JwtController {

  private JWSSigner signer;

  @PostConstruct
  public void initSigningKey() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, JOSEException {
    KeyStore keyStore = KeyStore.getInstance("jks");
    keyStore.load(new ClassPathResource("jwt_keys.jks").getInputStream(), "secret".toCharArray());
    RSAKey rsaKey = RSAKey.load(keyStore, "library-server", "secret".toCharArray());
    this.signer = new RSASSASigner(rsaKey);
  }

  @GetMapping("/")
  public String createJwtForm(Model model) {
    model.addAttribute("user", new User());
    return "jwt-form";
  }

  @PostMapping("/generate")
  public String generateJwt(User user, Model model) throws JOSEException {

    Map<String, Object> payload = new HashMap<>();

    payload.put("iss", "test_issuer");
    payload.put("exp", Math.abs(System.currentTimeMillis() / 1000) + (5 * 60));
    payload.put("aud", new String[] {"library-service"});
    payload.put("sub", user.getEmail() != null ? user.getEmail() : "bruce.wayne@example.com");
    payload.put("scope", "openid email profile");
    payload.put("groups", StringUtils.isNotBlank(user.getRole()) ? new String[] {user.getRole()} : new String[] {"library_user"});
    payload.put("preferred_username", StringUtils.isNotBlank(user.getUsername()) ? user.getUsername() : "bwayne");
    payload.put("given_name", StringUtils.isNotBlank(user.getFirstName()) ? user.getFirstName() : "Bruce");
    payload.put("family_name", StringUtils.isNotBlank(user.getLastName()) ? user.getLastName() : "Wayne");
    payload.put("email", StringUtils.isNotBlank(user.getEmail()) ? user.getEmail() : "bruce.wayne@example.com");
    payload.put("email_verified", true);
    payload.put("name", StringUtils.isNotBlank(user.getFirstName())
            && StringUtils.isNotBlank(user.getLastName()) ? user.getFirstName() + " " + user.getLastName() : "Bruce Wayne");

    String jwt = createJwt(payload);
    model.addAttribute("jwt", jwt);

    return "result";
  }

  private String createJwt(Map<String, Object> payload) throws JOSEException {


    JSONObject jsonObject = new JSONObject(payload);

    JWSObject jwsObject = new JWSObject(
            new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
            new Payload(jsonObject));

    // Compute the RSA signature
    jwsObject.sign(signer);

    // To serialize to compact form, produces something like
    // eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
    // mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
    // maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
    // -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
    return jwsObject.serialize();
  }
}
